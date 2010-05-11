/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 * Marcelo Marzola Bossoni (Eldorado) - Bug (289282) - NullPointer adding keyNullPointer adding key
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Matheus Tait Lima (Eldorado) - Bug [300351] - Optimizing performance when translating a whole column at once
 * Marcelo Marzola Bossoni (Eldorado) - Fix erroneous externalized strings/provide implementation to the new methods
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahExceptionStatus;
import org.eclipse.sequoyah.localization.stringeditor.datatype.CellInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.stringeditor.editor.input.AbstractStringEditorInput;
import org.eclipse.sequoyah.localization.stringeditor.editor.input.IEditorChangeListener;
import org.eclipse.sequoyah.localization.tools.LocalizationToolsPlugin;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.StringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.StringNodeComment;
import org.eclipse.sequoyah.localization.tools.datamodel.TranslationResult;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ITranslator;
import org.eclipse.sequoyah.localization.tools.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.ProjectLocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.TranslatorManager;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager.IFileChangeListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;

/***
 * 
 * This class implements a IStringEditorInput and provides the necessary methods
 * for the Localization Files Editor to work with (edit, save, translate, etc)
 * localization files.
 * 
 */
public class StringEditorInput extends AbstractStringEditorInput {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #getTitle()
	 */
	@Override
	public String getTitle() {
		return projectLocalizationManager.getLocalizationProject().getProject()
				.getName();
	}

	/*
	 * The Project Localization Manager used as a source to get all information
	 * provided by this class
	 */
	private ProjectLocalizationManager projectLocalizationManager = null;

	private final IFileChangeListener fileChangeListener = new IFileChangeListener() {

		public IProject getProject() {
			return projectLocalizationManager.getLocalizationProject()
					.getProject();
		}

		public void fileChanged(IFile file) {
			notifyInputChanged(getColumnID(file));
		}
	};

	private final IEditorChangeListener editorChangeListener = new IEditorChangeListener() {

		public void editorContentChanged(IEditorInput input, String newContent) {
			IFileEditorInput fileInput = input instanceof IFileEditorInput ? (IFileEditorInput) input
					: null;
			if (fileInput != null) {
				LocalizationFile locFile = projectLocalizationManager
						.getLocalizationProject().getLocalizationFile(
								fileInput.getFile());
				try {
					projectLocalizationManager.getProjectLocalizationSchema()
							.updateLocalizationFileContent(locFile, newContent);
					notifyInputChanged(getColumnID(locFile.getFile()));
				} catch (SequoyahException e) {
					BasePlugin.logError(
							"Impossible to update file content for file: "
									+ locFile.getFile().getFullPath(), e);
				}
			}
		}
	};

	/*
	 * Localization icon
	 */
	private final String LOCALIZATION_ICON = "icons/loc_icon.png"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #addRow(org
	 * .eclipse.sequoyah.stringeditor.datatype.RowInfo)
	 */
	@Override
	public RowInfo addRow(RowInfo row) {

		RowInfo rowInfo = row;

		String key = row.getKey();
		boolean isArray = row.isArray();
		Map<String, CellInfo> cells = row.getCells();
		Set<String> columns = cells.keySet();

		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();

		// add at least to the default column
		if (columns.size() == 0) {
			LocaleInfo locale = schema.getLocaleInfoFromID(schema
					.getDefaultID());
			if (locale != null) {
				LocalizationFile mainFile = projectLocalizationManager
						.getLocalizationProject().getLocalizationFile(locale);
				StringNode stringNode = mainFile.getStringNodeByKey(row
						.getKey(), isArray);
				// arrays have different keys that are generated on the fly
				rowInfo.setKey(stringNode.getKey());
			}
		}

		for (Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
			String column = iterator.next();

			LocaleInfo info = schema.getLocaleInfoFromID(column);
			LocalizationFile file = projectLocalizationManager
					.getLocalizationProject().getLocalizationFile(info);
			String value = (cells.get(column)).getValue();
			String comment = (cells.get(column)).getComment();
			StringNode newNode = new StringNode(key, value);
			newNode.setArray(isArray);
			StringNodeComment commentNode = new StringNodeComment();
			commentNode.setComment(comment);
			newNode.setStringNodeComment(commentNode);
			newNode = file.addStringNode(newNode);
			rowInfo.setKey(newNode.getKey());
		}

		return rowInfo;
	}

	/**
	 * Returns the project localization manager associated with this editor
	 * input.
	 * 
	 * @return the project localization manager
	 */
	public ProjectLocalizationManager getProjectLocalizationManager() {
		return projectLocalizationManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #removeRow (java.lang.String)
	 */
	@Override
	public void removeRow(String key) {
		List<LocalizationFile> files = projectLocalizationManager
				.getLocalizationProject().getLocalizationFiles();

		for (Iterator<LocalizationFile> iterator = files.iterator(); iterator
				.hasNext();) {
			LocalizationFile localizationFile = iterator.next();
			localizationFile.removeStringNode(localizationFile
					.getStringNodeByKey(key));
		}
	}

	/**
	 * Instantiate the Project Localization Manager
	 * 
	 * @throws SequoyahException
	 * 
	 * @see org.eclipse.sequoyah.localization.stringeditor.editor.input.AbstractStringEditorInput#init(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void init(final IProject project) throws SequoyahException {
		try {
			projectLocalizationManager = LocalizationManager.getInstance()
					.getProjectLocalizationManager(project, true);

			LocalizationManager.getInstance().addFileChangeListener(
					fileChangeListener);

			addEditorChangeListener(editorChangeListener);

			if (projectLocalizationManager == null) {

				Status status = new Status(Status.ERROR,
						LocalizationToolsPlugin.PLUGIN_ID,
						Messages.StringEditorInput_ErrorInitializingEditor);
				throw new SequoyahException(new SequoyahExceptionStatus(status));
			}
		} catch (IOException ioe) {
			Status status = new Status(Status.ERROR,
					LocalizationToolsPlugin.PLUGIN_ID,
					Messages.StringEditorInput_FileMalformed);
			throw new SequoyahException(new SequoyahExceptionStatus(status));
		}
	}

	private String getColumnID(IFile file) {
		LocaleInfo info = projectLocalizationManager.getLocaleInfoForFile(file);

		return projectLocalizationManager.getProjectLocalizationSchema()
				.getColumnIDFromLocaleInfo(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #getColumns()
	 */
	@Override
	public List<ColumnInfo> getColumns() {
		List<LocalizationFile> files = projectLocalizationManager
				.getLocalizationProject().getLocalizationFiles();
		List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();
		String defaultID = projectLocalizationManager
				.getProjectLocalizationSchema().getDefaultID();
		for (Iterator<LocalizationFile> iterator = files.iterator(); iterator
				.hasNext();) {
			LocalizationFile localizationFile = iterator.next();

			String columnID = getColumnID(localizationFile.getFile());
			String toolTip = schema.getLocaleToolTip(localizationFile.getFile()
					.getFullPath());

			List<StringNode> localizationNodes = localizationFile
					.getStringNodes();
			Map<String, CellInfo> cells = new HashMap<String, CellInfo>();
			for (Iterator<StringNode> nodes = localizationNodes.iterator(); nodes
					.hasNext();) {
				StringNode stringNode = nodes.next();
				String comment = ((stringNode.getStringNodeComment() != null) ? stringNode
						.getStringNodeComment().getComment()
						: ""); //$NON-NLS-1$
				CellInfo info = new CellInfo(stringNode.getValue(), comment);
				cells.put(stringNode.getKey(), info);
			}

			columns.add(new ColumnInfo(columnID, toolTip, cells, columnID
					.equals(defaultID) ? false : true));

		}

		return columns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #translate (java.lang.String, TranslatedColumnInfo)
	 */
	@Override
	public boolean translateColumn(String srcColumnID,
			TranslationInfo destColumnInfo, IProgressMonitor monitor) {

		boolean result = true;
		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();

		LocaleInfo info = schema.getLocaleInfoFromID(srcColumnID);

		LocalizationFile existingFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(info);

		if (existingFile != null) {

			LocaleInfo infoTarget = schema.getLocaleInfoFromID(destColumnInfo
					.getId());
			String path = schema.getPathFromLocaleInfo(infoTarget);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
					new Path(path));
			LocalizationFile newFile = projectLocalizationManager
					.getProjectLocalizationSchema()
					.createLocalizationFile(file, infoTarget,
							new ArrayList<StringNode>(), null);
			newFile.setLocalizationProject(projectLocalizationManager
					.getLocalizationProject());

			result = translateColumn(existingFile, newFile, destColumnInfo,
					monitor);

			if (result) {
				projectLocalizationManager.getLocalizationProject()
						.addLocalizationFile(newFile);
				newFile.setDirty(true);
			}

		} else {
			monitor.setCanceled(true);
			BasePlugin.logError("Error translating from file '" + srcColumnID //$NON-NLS-1$
					+ ". File does not exist."); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * @param srcColumnID
	 * @param newColumnsInfo
	 * @param monitor
	 * @return
	 */
	@Override
	public boolean translateCells(String srcColumnID,
			TranslationInfo[] newColumnsInfo, IProgressMonitor monitor) {

		try {

			monitor.beginTask(Messages.TranslationProgress_Connecting,
					newColumnsInfo.length);

			ITranslator translator = TranslatorManager.getInstance()
					.getTranslatorByName(newColumnsInfo[0].getTranslator());

			ArrayList<String> words = new ArrayList<String>();
			ArrayList<String> fromLanguages = new ArrayList<String>();
			ArrayList<String> toLanguages = new ArrayList<String>();

			for (TranslationInfo translationInfo : newColumnsInfo) {
				words.add(translationInfo.getFromWord());
				fromLanguages.add(translationInfo.getFromLang());
				toLanguages.add(translationInfo.getToLang());
				monitor.worked(1);
			}

			monitor.beginTask(Messages.TranslationProgress_FetchingInformation,
					100);
			List<TranslationResult> translationResults = translator
					.translateAll(words, fromLanguages, toLanguages, monitor);

			int i = 0;
			monitor.done();

			monitor
					.beginTask(Messages.ParsingAnswer,
							newColumnsInfo.length * 3);

			for (TranslationInfo translationInfo : newColumnsInfo) {
				monitor.worked(1);
				String translatedString = translationResults.get(i++)
						.getTranslatedWord();
				monitor.worked(1);
				translationInfo.setToWord(translatedString);
				monitor.worked(1);
			}
			monitor.done();
		} catch (Exception e) {
			BasePlugin.logError(e.getMessage());
			monitor.setCanceled(true);
			return false;
		}

		return true;
	}

	/**
	 * Call the translator for each cell, populating the new column
	 */
	private boolean translateColumn(LocalizationFile source,
			LocalizationFile target, TranslationInfo destColumnInfo,
			IProgressMonitor monitor) {

		List<StringNode> originalNodes = source.getStringNodes();

		monitor.beginTask(Messages.TranslationProgress_Connecting,
				originalNodes.size());

		ArrayList<String> strings = new ArrayList<String>();
		for (Iterator<StringNode> iterator = originalNodes.iterator(); iterator
				.hasNext();) {
			StringNode originalNode = iterator.next();
			strings.add(originalNode.getValue());
			monitor.worked(1);
		}
		monitor.done();

		List<TranslationResult> translationResults;
		try {
			ITranslator translator = TranslatorManager.getInstance()
					.getTranslatorByName(destColumnInfo.getTranslator());
			monitor.beginTask(Messages.TranslationProgress_FetchingInformation,
					100);
			translationResults = translator.translateAll(strings,
					destColumnInfo.getFromLang(), destColumnInfo.getToLang(),
					monitor);
		} catch (Exception e) {
			BasePlugin.logError(e.getMessage());
			monitor.setCanceled(true);
			return false;
		}
		monitor.done();

		int i = 0;
		monitor.beginTask(Messages.ParsingAnswer, translationResults.size());
		for (Iterator<TranslationResult> iterator = translationResults
				.iterator(); iterator.hasNext();) {
			monitor.worked(1);
			TranslationResult translationResult = iterator.next();
			String translatedString = translationResult.getTranslatedWord();
			StringNode newNode = new StringNode(originalNodes.get(i).getKey(),
					translatedString);
			newNode.setArray(originalNodes.get(i).isArray());
			i++;
			target.addStringNode(newNode);
			destColumnInfo.addCell(newNode.getKey(), new CellInfo(newNode
					.getValue(), "")); //$NON-NLS-1$
		}

		monitor.done();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #addColumn (java.lang.String)
	 */
	@Override
	public void addColumn(String ID) {

		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();

		LocaleInfo info = schema.getLocaleInfoFromID(ID);

		LocalizationFile existingFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(info);

		if (existingFile == null) {
			String path = schema.getPathFromLocaleInfo(info);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
					new Path(path));
			LocalizationFile newFile = projectLocalizationManager
					.getProjectLocalizationSchema().createLocalizationFile(
							file, info, new ArrayList<StringNode>(), null);
			newFile.setLocalizationProject(projectLocalizationManager
					.getLocalizationProject());
			newFile.setDirty(true);
			projectLocalizationManager.getLocalizationProject()
					.addLocalizationFile(newFile);

		} else {
			existingFile.setToBeDeleted(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #removeColumn (java.lang.String)
	 */
	@Override
	public void removeColumn(String columnID) {
		LocaleInfo locale = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);
		LocalizationFile file = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(locale);
		projectLocalizationManager.markFileForDeletion(file);
		projectLocalizationManager.getLocalizationProject().setDirty(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #setValue (java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setValue(String columnID, String key, String value)
			throws SequoyahException {

		if (projectLocalizationManager == null) {
			Status status = new Status(Status.ERROR,
					LocalizationToolsPlugin.PLUGIN_ID,
					Messages.StringEditorInput_ErrorManagerNotInitialized);
			throw new SequoyahException(new SequoyahExceptionStatus(status));
		}
		LocaleInfo locale = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);
		LocalizationFile file = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(locale);
		file.getStringNodeByKey(key).setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #getValue (java.lang.String, java.lang.String)
	 */
	@Override
	public CellInfo getValue(String columnID, String key) {
		LocaleInfo localeInfo = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);
		LocalizationFile localizationFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(localeInfo);
		StringNode stringNode = localizationFile.getStringNodeByKey(key);
		return new CellInfo(stringNode.getValue(), ((stringNode
				.getStringNodeComment() != null) ? stringNode
				.getStringNodeComment().getComment() : null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #getValues (java.lang.String)
	 */
	@Override
	public Map<String, CellInfo> getValues(String columnID) {
		LocaleInfo localeInfo = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);
		Map<String, CellInfo> keyValueMap = new HashMap<String, CellInfo>();
		keyValueMap.keySet();
		LocalizationFile localizationFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(localeInfo);
		List<StringNode> stringNodes = localizationFile.getStringNodes();
		for (StringNode stringNode : stringNodes) {
			String comment = ""; //$NON-NLS-1$
			if (stringNode.getStringNodeComment() != null) {
				comment = stringNode.getStringNodeComment().getComment();
			}
			keyValueMap.put(stringNode.getKey(), new CellInfo(stringNode
					.getValue(), comment));
		}
		return keyValueMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.stringeditor.editor.input.IStringEditorInput#
	 * getAvailableKeysForColumn(java.lang.String)
	 */
	@Override
	public List<CellInfo> getAvailableKeysForColumn(String columnID) {
		LocaleInfo localeInfo = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);

		List<CellInfo> keysForColumn = new ArrayList<CellInfo>();
		LocalizationFile localizationFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(localeInfo);
		List<StringNode> stringNodes = localizationFile.getStringNodes();

		for (StringNode stringNode : stringNodes) {
			keysForColumn.add(new CellInfo(stringNode.getKey(), stringNode
					.getStringNodeComment().getComment()));
		}

		return keysForColumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #canSave()
	 */
	@Override
	public boolean canSave() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #save()
	 */
	@Override
	public boolean save() {
		boolean result = projectLocalizationManager.saveProject();
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #isDirty()
	 */
	@Override
	public boolean isDirty() {
		boolean returnValue = false;
		if (projectLocalizationManager != null) {
			returnValue = projectLocalizationManager.getLocalizationProject()
					.isDirty();
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		if (projectLocalizationManager.getAvailableLocales().size() > 0) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return LocalizationToolsPlugin.imageDescriptorFromPlugin(
				LocalizationToolsPlugin.PLUGIN_ID, LOCALIZATION_ICON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		String name = ""; //$NON-NLS-1$
		// Human readable name for this editor (for instance,
		// usually it is the file being edited). For now I'll just
		// return the same text as the tooltip
		if (projectLocalizationManager != null) {
			name = projectLocalizationManager.getProjectLocalizationSchema()
					.getEditorName();
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.stringeditor.editor.input.IStringEditorInput#
	 * canRevertByColumn()
	 */
	@Override
	public boolean canRevertByColumn() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #revert()
	 */
	@Override
	public boolean revert() {
		LocalizationManager.getInstance().unloadProjectLocalizationManager(
				projectLocalizationManager.getLocalizationProject()
						.getProject());
		try {
			LocalizationManager.getInstance().getProjectLocalizationManager(
					projectLocalizationManager.getLocalizationProject()
							.getProject(), false);
		} catch (IOException e) {

		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #revert(java .lang.String)
	 */
	@Override
	public boolean revert(String columnID) throws IOException {

		boolean found = false;
		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();
		LocaleInfo localeInfo = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);

		if (localeInfo != null) {

			LocalizationFile file = projectLocalizationManager
					.getLocalizationProject().getLocalizationFile(localeInfo);

			LocalizationFile newFile = schema.loadFile(file.getFile());

			projectLocalizationManager.getLocalizationProject()
					.removeLocalizationFile(file);

			projectLocalizationManager.getLocalizationProject()
					.addLocalizationFile(newFile);

			found = true;
		}

		return found;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return projectLocalizationManager.getProjectLocalizationSchema()
				.getEditorName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #dispose()
	 */
	@Override
	public void dispose() {
		if (projectLocalizationManager != null) {
			LocalizationManager.getInstance().removeFileChangeListener(
					fileChangeListener);
			LocalizationManager.getInstance().unloadProjectLocalizationManager(
					projectLocalizationManager.getLocalizationProject()
							.getProject());
			projectLocalizationManager = null;
		}
	}

	public static void stopListening() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #removeCell (java.lang.String, java.lang.String)
	 */
	@Override
	public void removeCell(String key, String columnID) {
		ILocalizationSchema schema = projectLocalizationManager
				.getProjectLocalizationSchema();
		LocaleInfo locale = schema.getLocaleInfoFromID(columnID);
		LocalizationFile file = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(locale);

		// avoid total key deletion
		if ((schema.getDefaultID() != null)
				&& schema.getDefaultID().equals(columnID)) {
			try {
				setValue(columnID, key, ""); //$NON-NLS-1$
			} catch (SequoyahException e) {
				// do nothing
			}
		} else {
			file.removeStringNode(file.getStringNodeByKey(key));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #setCellTooltip (java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setCellTooltip(String columnID, String key, String tooltip)
			throws SequoyahException {
		if (projectLocalizationManager == null) {
			Status status = new Status(Status.ERROR,
					LocalizationToolsPlugin.PLUGIN_ID,
					Messages.StringEditorInput_ErrorManagerNotInitialized);
			throw new SequoyahException(new SequoyahExceptionStatus(status));
		}
		LocaleInfo locale = projectLocalizationManager
				.getProjectLocalizationSchema().getLocaleInfoFromID(columnID);
		LocalizationFile file = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(locale);

		StringNodeComment comment = file.getStringNodeByKey(key)
				.getStringNodeComment();

		if (comment == null) {
			comment = new StringNodeComment();
		}
		file.getStringNodeByKey(key).setStringNodeComment(comment);
		comment.setComment(tooltip);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #validate()
	 */
	@Override
	public IStatus validate() {
		IStatus result = new Status(Status.OK,
				LocalizationToolsPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
		;
		// The project is considered as in a warning state if there is no
		// default localization file
		String defaultID = projectLocalizationManager
				.getProjectLocalizationSchema().getDefaultID();
		if (defaultID != null) {
			LocaleInfo info = projectLocalizationManager
					.getProjectLocalizationSchema().getLocaleInfoFromID(
							defaultID);
			LocalizationFile localizationFile = projectLocalizationManager
					.getLocalizationProject().getLocalizationFile(info);

			if (localizationFile == null) {
				result = new Status(Status.WARNING,
						LocalizationToolsPlugin.PLUGIN_ID,
						Messages.Warning_NoDefaultFile);
			} else {
				if (localizationFile.isToBeDeleted()) {
					result = new Status(Status.WARNING,
							LocalizationToolsPlugin.PLUGIN_ID,
							Messages.Warning_NoDefaultFile);
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.stringeditor.editor.input.
	 * IStringEditorInput #canHandle (org.eclipse.core.resources.IFile)
	 */
	@Override
	public boolean canHandle(IFile file) {
		boolean canHandle = false;
		ILocalizationSchema localizationSchema = LocalizationManager
				.getInstance().getLocalizationSchema(file.getProject());
		if (localizationSchema != null) {
			canHandle = localizationSchema.isLocalizationFile(file);
		}
		return canHandle;
	}

	@Override
	public List<IFile> getFiles() {
		List<IFile> files = new ArrayList<IFile>();
		for (LocalizationFile locFile : projectLocalizationManager
				.getLocalizationProject().getLocalizationFiles()) {
			files.add(locFile.getFile());
		}
		return files;
	}

	@Override
	public String getSourcePageNameForFile(IFile file) {
		LocalizationFile locFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(file);
		return projectLocalizationManager.getProjectLocalizationSchema()
				.getSourcePageNameForFile(locFile);
	}

	@Override
	public String getContentForFileAsText(IFileEditorInput editorInput) {
		LocalizationFile locFile = projectLocalizationManager
				.getLocalizationProject().getLocalizationFile(
						editorInput.getFile());
		return projectLocalizationManager.getProjectLocalizationSchema()
				.getLocalizationFileContent(locFile);
	}
}
