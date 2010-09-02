/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 * Marcelo Marzola Bossoni (Eldorado) - Bug (289236) - Editor Permitting create 2 columns with same id
 * Marcelo Marzola Bossoni (Eldorado) - Bug (289282) - NullPointer adding keyNullPointer adding key
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Daniel Barboza Franco (Eldorado) - Bug [290058] - fixing NullPointerException's while listening changes made from outside Eclipse
 * Matheus Lima (Eldorado) - Adapting to accept online translation for a column
 * Paulo Faria (Eldorado) - Add option to expand rows for global maximum height (default) or let all lines with row 1 and include scroll for items with multiple lines
 * Marcelo Marzola Bossoni (Eldorado) - Fix erroneous externalized strings/make this editor a multipage one
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.editor.EditorExtensionLoader;
import org.eclipse.sequoyah.localization.editor.StringEditorPlugin;
import org.eclipse.sequoyah.localization.editor.datatype.CellInfo;
import org.eclipse.sequoyah.localization.editor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.editor.datatype.IModelChangedListener;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.editor.i18n.Messages;
import org.eclipse.sequoyah.localization.editor.model.EditorSession.PROPERTY;
import org.eclipse.sequoyah.localization.editor.model.input.AbstractStringEditorInput;
import org.eclipse.sequoyah.localization.editor.model.input.IInputChangeListener;
import org.eclipse.sequoyah.localization.editor.model.operations.AddColumnOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.AddKeyOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.AddKeysOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.CloneOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.EditCellOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.EditCellsOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.EditorOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.RemoveColumnOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.RemoveKeyOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.RevertColumnToSavedStateOperation;
import org.eclipse.sequoyah.localization.editor.providers.ContentProvider;
import org.eclipse.sequoyah.localization.editor.providers.ICellValidator;
import org.eclipse.sequoyah.localization.editor.providers.IOperationProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class StringEditorPart extends MultiPageEditorPart {

	class SearchTextModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			StringEditorPart.this.searchString = ((Text) e.widget).getText();
			updateViewer(null);
		}
	}

	class FilterByKeyTextModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			StringEditorPart.this.filterByKeyString = ((Text) e.widget)
					.getText();
		}
	}

	/**
	 * A selection listener for columns to change the sort direction
	 */
	class ColumnSelectionListener implements SelectionListener {

		private final TableViewer viewer;

		public ColumnSelectionListener(TableViewer theViewer) {
			this.viewer = theViewer;
		}

		public void widgetSelected(SelectionEvent e) {
			TableColumn column = (TableColumn) e.widget;
			this.viewer.getTable().setSortColumn(column);
			this.viewer
					.getTable()
					.setSortDirection(
							this.viewer.getTable().getSortDirection() == SWT.DOWN ? SWT.UP
									: SWT.DOWN);
			this.viewer.refresh();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}
	}

	/**
	 * A editing support for this editor cells. Only the non keys cells are
	 * editable
	 */
	class CellEditingSupport extends EditingSupport {

		private final String columnID;

		private final CellEditor editor;

		public CellEditingSupport(TableViewer viewer, String columnID) {
			super(viewer);
			this.columnID = columnID;
			this.editor = new TextCellEditor(viewer.getTable(), SWT.MULTI
					| SWT.V_SCROLL);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return !columnID
					.equalsIgnoreCase(Messages.StringEditorPart_KeyLabel);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.
		 * Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			CellInfo info = ((RowInfo) element).getCells().get(columnID);
			return ((info != null) && (info.getValue() != null)) ? info
					.getValue() : ""; //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			RowInfo theRow = (RowInfo) element;
			CellInfo oldCell = theRow.getCells().get(columnID);
			CellInfo newCell = null;

			String EOL = System.getProperty("line.separator"); //$NON-NLS-1$
			String oldValue = ((oldCell != null) ? oldCell.getValue() : ""); //$NON-NLS-1$
			String newValue = ((String) value).replaceAll(EOL, "\n"); //$NON-NLS-1$
			if (newValue.equals(oldValue)) {
				return;
			}

			/*
			 * If our new value is a valid one, we create a new cell
			 */
			if (value.toString().length() > 0) {
				/*
				 * if our old cell isn't a null one check if the values aren't
				 * the same
				 */
				if (oldCell != null) {
					/*
					 * Our old cell is different from our new one
					 */
					if (((oldCell.getValue() != null) && !oldCell.getValue()
							.equals(value.toString()))
							|| (oldCell.getValue() == null)) {
						newCell = new CellInfo(value.toString(),
								oldCell.getComment());
					}
				} else {
					newCell = new CellInfo(value.toString(), null);
				}
			} else {
				if (oldCell != null) {
					newCell = new CellInfo(null, null);
				}
			}
			if (newCell != null) {
				newCell.setDirty(true);
				EditCellOperation operation = new EditCellOperation(
						theRow.getKey(), columnID, oldCell, newCell,
						StringEditorPart.this);
				executeOperation(operation);
			}
		}
	}

	class TranslateColumnAction extends Action {
		public TranslateColumnAction() {
			super(Messages.StringEditorPart_TranslateActionName);
			this.setEnabled(activeColumn != 0);
		}

		@Override
		public void run() {

			TableColumn originalColumn = getEditorViewer().getTable()
					.getColumn(activeColumn);

			final TranslationInfo newColumnInfo = StringEditorPart.this
					.getContentProvider().getOperationProvider()
					.getTranslatedColumnInfo(originalColumn.getText());

			if (newColumnInfo != null) {
				try {
					PlatformUI.getWorkbench().getProgressService()
							.run(false, false, new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {

									monitor.setTaskName(Messages.TranslationProgress_Connecting);

									TableColumn originalColumn = getEditorViewer()
											.getTable().getColumn(activeColumn);

									if ((newColumnInfo != null)
											&& (getColumnByID(newColumnInfo
													.getId()) == null)) {

										if (getEditorInput().translateColumn(
												originalColumn.getText(),
												newColumnInfo, monitor)) {

											getEditorInput().getValues(
													newColumnInfo.getId());
											AddColumnOperation operation = new AddColumnOperation(
													Messages.StringEditorPart_TranslateActionName,
													StringEditorPart.this,
													newColumnInfo);

											executeOperation(operation);
										} else {
											monitor.setCanceled(true);
											MessageDialog
													.openInformation(
															getEditorSite()
																	.getShell(),
															Messages.StringEditorPart_TranslationError,
															Messages.StringEditorPart_TranslationErrorCheckConnetion);
										}

									} else {
										monitor.setCanceled(true);
										MessageDialog
												.openInformation(
														getEditorSite()
																.getShell(),
														Messages.StringEditorPart_TranslationError,
														Messages.StringEditorPart_TranslationErrorTargetExists);
									}

								}
							});
				} catch (InvocationTargetException e) {
					// Do nothing
				} catch (InterruptedException e) {
					// Do nothing
				}
			}
		}

	}

	class TranslateCellAction extends Action {

		public TranslateCellAction() {
			super(Messages.StringEditorPart_TranslateCellActionName);
			this.setEnabled((activeColumn != 0) && (activeRow != null));
		}

		@Override
		@SuppressWarnings("unused")
		public void run() {
			TableColumn originalColumn = getEditorViewer().getTable()
					.getColumn(activeColumn);

			StructuredSelection selection = (StructuredSelection) getEditorViewer()
					.getSelection();
			String[] selectedKeysText = new String[selection.size()];
			String[] selectedCellsText = new String[selection.size()];
			Object[] selectedRows = selection.toArray();
			for (int i = 0; i < selectedRows.length; i++) {
				RowInfo rowInfo = (RowInfo) selectedRows[i];
				CellInfo cellInfo = rowInfo.getCells().get(
						originalColumn.getText());
				selectedKeysText[i] = rowInfo.getKey();
				selectedCellsText[i] = cellInfo.getValue();
			}

			String selectedCellText = activeRow.getViewerRow()
					.getCell(activeColumn).getText();
			TableColumn[] tableColumns = getEditorViewer().getTable()
					.getColumns();

			final TranslationInfo[] newColumnsInfo = StringEditorPart.this
					.getContentProvider()
					.getOperationProvider()
					.getTranslatedColumnsInfo(originalColumn.getText(),
							selectedKeysText, selectedCellsText, tableColumns);

			if (newColumnsInfo != null) {
				try {
					PlatformUI.getWorkbench().getProgressService()
							.run(false, false, new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {

									monitor.setTaskName(Messages.TranslationProgress_Connecting);

									TableColumn originalColumn = getEditorViewer()
											.getTable().getColumn(activeColumn);

									if (getEditorInput().translateCells(
											originalColumn.getText(),
											newColumnsInfo, monitor)) {

										int size = newColumnsInfo.length;
										String[] keys = new String[size];
										String[] columns = new String[size];
										CellInfo[] oldValues = new CellInfo[size];
										CellInfo[] newValues = new CellInfo[size];

										int i = 0;
										for (TranslationInfo translationInfo : newColumnsInfo) {
											keys[i] = translationInfo
													.getFromKey();
											columns[i] = translationInfo
													.getToColumn();

											oldValues[i] = getCellInfo(
													columns[i], keys[i]);
											newValues[i] = new CellInfo(
													translationInfo.getToWord(),
													oldValues[i].getComment());

											i++;
										}

										EditCellsOperation operation = new EditCellsOperation(
												Messages.StringEditorPart_TranslateCellActionName,
												keys, columns, oldValues,
												newValues,
												StringEditorPart.this);

										executeOperation(operation);
										getEditorViewer().refresh();

									} else {
										monitor.setCanceled(true);
										MessageDialog
												.openInformation(
														getEditorSite()
																.getShell(),
														Messages.StringEditorPart_TranslationError,
														Messages.StringEditorPart_TranslationErrorCheckConnetion);
									}

								}
							});
				} catch (InvocationTargetException e) {
					// Do nothing
				} catch (InterruptedException e) {
					// Do nothing
				}
			}
		}
	}

	class CloneColumnAction extends Action {
		public CloneColumnAction() {
			super(Messages.StringEditorPart_CloneActionName);
			this.setEnabled(activeColumn != 0);
		}

		@Override
		public void run() {
			TableColumn column = getEditorViewer().getTable().getColumn(
					activeColumn);
			ColumnInfo newColumnInfo = StringEditorPart.this
					.getContentProvider().getOperationProvider().getNewColumn();
			if (newColumnInfo != null) {
				CloneOperation operation = new CloneOperation(
						StringEditorPart.this, column.getText(), newColumnInfo);
				executeOperation(operation);
			}
		}
	}

	class RevertToSavedAction extends Action {
		public RevertToSavedAction() {
			super(Messages.StringEditorPart_RevertColumnActionName);
			this.setToolTipText(Messages.StringEditorPart_RevertColumnActionTooltip);
			this.setEnabled(activeColumn != 0);
		}

		@Override
		public void run() {
			String column = viewer.getTable().getColumn(activeColumn).getText();
			RevertColumnToSavedStateOperation operation = new RevertColumnToSavedStateOperation(
					Messages.StringEditorPart_RevertColumnActionOperationName,
					StringEditorPart.this, getModel().getColumn(column));
			executeOperation(operation);
		}
	}

	/**
	 * An action to show/hide columns
	 */
	class HideShowColumnAction extends Action {
		private final TableColumn column;

		public HideShowColumnAction(String name, int style, TableColumn c) {
			super(name, style);
			column = c;
			setChecked(c.getWidth() > 0);
		}

		@Override
		public void run() {
			if (!isChecked()) {
				hideColumn(column);
			} else {
				showColumn(column);
			}
		}
	}

	/**
	 * An action to show/hide columns
	 */
	class HideShowAllColumnsAction extends Action {
		private final Table table;

		private final boolean visible;

		public HideShowAllColumnsAction(String name, int style, Table t,
				boolean visible) {
			super(name, style);
			this.table = t;
			this.visible = visible;
		}

		@Override
		public void run() {
			for (int i = 1; i < table.getColumnCount(); i++) {
				if (visible) {
					showColumn(table.getColumn(i));
				} else {
					hideColumn(table.getColumn(i));
				}
			}
		}
	}

	/**
	 * Action to add a new column
	 */
	class AddColumnAction extends Action {

		public AddColumnAction() {
			super(Messages.StringEditorPart_AddColumnActionName);
		}

		@Override
		public void run() {
			ColumnInfo info = getContentProvider().getOperationProvider()
					.getNewColumn();

			if (info != null) {
				if (getColumnByID(info.getId()) == null) {
					AddColumnOperation operation = new AddColumnOperation(
							Messages.StringEditorPart_AddColumnOperationName,
							StringEditorPart.this, info);
					executeOperation(operation);

				} else {
					MessageDialog
							.openInformation(
									getEditorSite().getShell(),
									Messages.StringEditorPart_ColumnAlreadyExistTitle,
									NLS.bind(
											Messages.StringEditorPart_ColumnAlreadyExistMessage,
											info.getId()));
				}
			}
		}
	}

	/**
	 * Action to remove a column
	 */
	class RemoveColumnAction extends Action {

		public RemoveColumnAction() {
			super(Messages.StringEditorPart_RemoveColumnActionName);
			String activeColumnID = getEditorViewer().getTable()
					.getColumn(activeColumn).getText();
			this.setEnabled((activeColumn != 0)
					&& ((getModel().getColumn(activeColumnID) != null) && getModel()
							.getColumn(activeColumnID).canRemove()));
		}

		@Override
		public void run() {
			TableColumn selectedColumn = getEditorViewer().getTable()
					.getColumn(activeColumn);
			if (!selectedColumn.getText().equals(
					Messages.StringEditorPart_KeyLabel)) {
				if (MessageDialog.openQuestion(StringEditorPart.this
						.getEditorSite().getShell(),
						Messages.StringEditorPart_RemoveColumnActionName,
						Messages.StringEditorPart_RemoveColumnQuestionMessage
								+ " \"" + selectedColumn.getText() + "\"?")) { //$NON-NLS-1$ //$NON-NLS-2$
					RemoveColumnOperation operation = new RemoveColumnOperation(
							Messages.StringEditorPart_RemoveColumnOperationName,
							StringEditorPart.this,
							((StringEditorViewerModel) getEditorViewer()
									.getInput()).getColumn(selectedColumn
									.getText()), getEditorViewer().getTable()
									.indexOf(selectedColumn), selectedColumn
									.getWidth());
					executeOperation(operation);
				}
			}
		}
	}

	/**
	 * Action to add a new key
	 */
	class AddKeyAction extends Action {

		public AddKeyAction() {
			super(Messages.StringEditorPart_AddKeyActionName);
		}

		@Override
		public void run() {

			RowInfo[] rowInfo = getContentProvider().getOperationProvider()
					.getNewRow();
			// add new key only if the key isn't null and the new key does not
			// exists
			if (rowInfo != null) {

				if (rowInfo.length > 1) {
					AddKeysOperation operation = new AddKeysOperation(
							Messages.StringEditorPart_AddKeyOperationName,
							StringEditorPart.this, rowInfo);
					operation.addContext(getUndoContext());
					executeOperation(operation);
				} else {
					if (getModel().getRow(rowInfo[0].getKey()) == null) {
						AddKeyOperation operation = new AddKeyOperation(
								Messages.StringEditorPart_AddKeyOperationName,
								StringEditorPart.this, rowInfo[0]);
						operation.addContext(getUndoContext());
						executeOperation(operation);
					} else {
						editorComposite
								.setMessage(
										Messages.StringEditorPart_KeyAlreadyExistsErrorMessage,
										IMessage.ERROR);
					}
				}
			}
		}
	}

	/**
	 * Action to remove a key
	 */
	class RemoveKeyAction extends Action {

		public RemoveKeyAction() {
			super(Messages.StringEditorPart_RemoveKeyActionName);
		}

		@Override
		public void run() {
			ISelection sel = viewer.getSelection();
			List<RowInfo> toBeDeleted = new ArrayList<RowInfo>();
			if ((sel != null) && (sel instanceof IStructuredSelection)) {
				IStructuredSelection selection = (IStructuredSelection) sel;
				for (Object o : selection.toArray()) {
					if (o instanceof RowInfo) {
						toBeDeleted.add((RowInfo) o);
					}
				}
			}
			if (toBeDeleted.size() > 0) {
				RemoveKeyOperation operation = new RemoveKeyOperation(
						Messages.StringEditorPart_RemoveKeyOperationName,
						StringEditorPart.this, toBeDeleted);
				executeOperation(operation);
			}
		}
	}

	/*
	 * This editor undo context
	 */
	private final IUndoContext undoContext;

	/*
	 * This editor history
	 */
	private final IOperationHistory operationHistory;

	/*
	 * List of columns changed in the file system
	 */
	private Set<String> changedColumns = new HashSet<String>();

	private boolean needToPromptFileSystemChange = false;

	private final IInputChangeListener inputChangeListener = new IInputChangeListener() {
		public void columnChanged(String columnID) {
			markColumnAsChanged(columnID);
		}
	};

	/*
	 * the viewer
	 */
	private TableViewer viewer;

	/*
	 * A hint for size
	 */
	private static final int SIZE_HINT = 14;

	/*
	 * The column listener
	 */
	private ColumnSelectionListener listener;

	/*
	 * the active column index
	 */
	private int activeColumn;

	/*
	 * the selected row (when a popup menu appears)
	 */
	private ViewerCell activeRow = null;

	/*
	 * the content provider for this editor instance
	 */
	private ContentProvider contentProvider;

	/*
	 * the editor configuration session
	 */
	private EditorSession session;

	/*
	 * The sorter property
	 */
	private static String SORTER_PROPERTY_NAME = "sorter"; //$NON-NLS-1$

	/*
	 * The previous width of the column property
	 */
	private static String PREVIOUS_WIDTH_PROPERTY_NAME = "previous.width"; //$NON-NLS-1$

	/*
	 * The project associated with this project
	 */
	private IProject associatedProject;

	/*
	 * Highlight user changes button
	 */
	private Button highlightChangesButton = null;

	/*
	 * Expand row button
	 */
	private Button expandRowButton = null;

	/*
	 * Highlight user changes
	 */
	private boolean highlightChanges = false;

	/*
	 * Expand rows: if row size must be expanded according to the number of
	 * lines
	 */
	private boolean expandRow = true;

	/*
	 * Search text
	 */
	private Text searchText = null;

	/*
	 * Search string
	 */
	private String searchString = ""; //$NON-NLS-1$

	/*
	 * Button to check option to show tooltips
	 */
	private Button showCellCommentsButton = null;

	/*
	 * ption to show tooltips
	 */
	private boolean showCellComments = false;

	/*
	 * Filter by key text
	 */
	private Text filterByKeyText = null;

	/*
	 * Filter by key string
	 */
	private String filterByKeyString = ""; //$NON-NLS-1$

	/*
	 * Filter by key apply button
	 */
	private Button applyFilterButton = null;

	private Form editorComposite;

	private Image errorImage;

	private Image warningImage;

	private Image okImage;

	private boolean pageChangedWithDirtySourceEditors = false;

	public StringEditorPart() {
		undoContext = new ObjectUndoContext(this);
		operationHistory = OperationHistoryFactory.getOperationHistory();
		viewer = null;
		associatedProject = null;
	}

	/**
	 * 
	 * @return this editor undo context
	 */
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	/**
	 * Get the project being edited by this editor part
	 * 
	 * @return
	 */
	public IProject getAssociatedProject() {
		return associatedProject;
	}

	/**
	 * 
	 * @return this editor operation history
	 */
	public IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		try {
			IFile file = ((IFileEditorInput) input).getFile();
			contentProvider = EditorExtensionLoader.getInstance()
					.getContentProviderForFileInput(file);

			if (contentProvider != null) {
				AbstractStringEditorInput stringEditorInput = contentProvider
						.getEditorInput();
				IOperationProvider operationProvider = contentProvider
						.getOperationProvider();
				ICellValidator cellValidator = contentProvider.getValidator();
				stringEditorInput.init(file.getProject());
				operationProvider.init(file.getProject());
				cellValidator.init(file.getProject());
				session = EditorSession.loadFromProject(file.getProject());
				associatedProject = file.getProject();
				setSite(site);
				setInput(stringEditorInput);
				setPartName(stringEditorInput.getTitle());

				stringEditorInput.addInputChangeListener(inputChangeListener);

			} else {
				throw new Exception(
						"An input provider for the given file was not found: " //$NON-NLS-1$
								+ ((IFileEditorInput) input).getFile()
										.getLocation().toOSString());
			}
			errorImage = new Image(Display.getDefault(), PlatformUI
					.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK).getImageData()
					.scaledTo(16, 16));
			warningImage = new Image(Display.getDefault(), PlatformUI
					.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK).getImageData()
					.scaledTo(16, 16));
			okImage = new Image(Display.getDefault(), StringEditorPlugin
					.imageDescriptorFromPlugin(StringEditorPlugin.PLUGIN_ID,
							"icons/obj16_ok.png").getImageData()); //$NON-NLS-1$

		} catch (Exception e) {
			handleInitFailure(e, input, site);
		}

	}

	private void handleInitFailure(Exception e, IEditorInput input,
			IEditorSite site) {
		MessageDialog.openError(new Shell(),
				"Error loading editor. Some available editor will be opened", e //$NON-NLS-1$
						.getMessage());
		IEditorDescriptor[] editors = PlatformUI
				.getWorkbench()
				.getEditorRegistry()
				.getEditors(
						"*." //$NON-NLS-1$
								+ ((IFileEditorInput) input).getFile()
										.getLocation().getFileExtension());

		if (editors.length > 0) {
			int i = 0;
			IEditorDescriptor editor = null;
			while ((i < editors.length) && (editor == null)) {
				if (!editors[i].getId().equals(site.getId())) {
					editor = editors[i];
				}
				i++;
			}
			if (editor != null) {
				try {
					site.getPage().openEditor(input, editor.getId());
				} catch (PartInitException e1) {
					// do nothing
				}
			}
			site.getPage().closeEditor(this, false);
		}

	}

	/**
	 * Get this editor progress monitor from action bars
	 * 
	 * @return the editor progress monitor
	 */
	public IProgressMonitor getProgressMonitor() {
		return getEditorSite().getActionBars().getStatusLineManager()
				.getProgressMonitor();
	}

	private void createOptionsSection(FormToolkit toolkit, final Form parent) {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 4,
				1);
		GridLayout layout = new GridLayout(5, false);
		ExpandableComposite expandableComposite = toolkit
				.createExpandableComposite(parent.getBody(),
						ExpandableComposite.TWISTIE);
		expandableComposite.setLayoutData(layoutData);
		expandableComposite.setText(Messages.StringEditorPart_OptionsText);
		expandableComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				parent.layout(true);
			}
		});

		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 6, 1);
		Composite optionsComposite = toolkit
				.createComposite(expandableComposite);
		optionsComposite.setLayoutData(layoutData);
		optionsComposite.setLayout(layout);
		expandableComposite.setClient(optionsComposite);

		expandRowButton = toolkit.createButton(optionsComposite,
				Messages.StringEditorPart_ExpandRows, SWT.CHECK);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		expandRowButton.setLayoutData(layoutData);
		expandRowButton.setSelection(new Boolean(expandRow));
		expandRowButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				expandRow = ((Button) e.widget).getSelection();

				viewer.getTable().setRedraw(true);
				viewer.getTable().layout();
				viewer.getTable().update();
				viewer.getTable().redraw();
				for (int i = 0; i < viewer.getTable().getColumnCount(); i++) {
					viewer.getTable().getColumn(i).pack();
				}
				viewer.getTable().pack();

				viewer.getTable().getParent().layout();
				viewer.getTable().getParent().setRedraw(true);
				viewer.getTable().getParent().redraw();
				viewer.refresh();

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		Label search = toolkit.createLabel(optionsComposite,
				Messages.StringEditorPart_SearchLabel + ": "); //$NON-NLS-1$
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		search.setLayoutData(layoutData);

		searchText = toolkit.createText(optionsComposite, "", SWT.BORDER); //$NON-NLS-1$
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		searchText.setLayoutData(layoutData);
		searchText.addModifyListener(new SearchTextModifyListener());

		showCellCommentsButton = toolkit
				.createButton(optionsComposite,
						Messages.StringEditorPart_ShowCellCommentsButtonText,
						SWT.CHECK);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		showCellCommentsButton.setLayoutData(layoutData);
		showCellCommentsButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				showCellComments = ((Button) e.widget).getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		Label filter = toolkit.createLabel(optionsComposite,
				Messages.StringEditorPart_FilterByKeyLabel + ": "); //$NON-NLS-1$
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		filter.setLayoutData(layoutData);

		filterByKeyText = toolkit.createText(optionsComposite, "", SWT.BORDER); //$NON-NLS-1$
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		filterByKeyText.setLayoutData(layoutData);
		filterByKeyText.addModifyListener(new FilterByKeyTextModifyListener());
		filterByKeyText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					getEditorViewer().refresh();
				}
			}
		});

		applyFilterButton = toolkit.createButton(optionsComposite,
				Messages.StringEditorPart_Apply, SWT.PUSH);
		layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		applyFilterButton.setLayoutData(layoutData);
		applyFilterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getEditorViewer().refresh();
			}
		});

		if (!Platform.getOS().equals(Platform.OS_LINUX)) {
			highlightChangesButton = toolkit.createButton(optionsComposite,
					Messages.StringEditorPart_HighlightChangesLabel, SWT.CHECK);
			layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
			highlightChangesButton.setLayoutData(layoutData);
			highlightChangesButton
					.addSelectionListener(new SelectionListener() {

						public void widgetSelected(SelectionEvent e) {
							highlightChanges = ((Button) e.widget)
									.getSelection();
							for (RowInfo info : getModel().getRows().values()) {
								viewer.update(info, null);
							}
						}

						public void widgetDefaultSelected(SelectionEvent e) {
							// do nothing
						}
					});
		}

	}

	private IEditorPart addPage(IFile inputFile) throws PartInitException {
		TextEditor editor = new TextEditor();
		int index = addPage(editor, new FileEditorInput(inputFile));
		setPageText(index, getEditorInput().getSourcePageNameForFile(inputFile));

		return editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		addPage(createMainControl(this.getContainer()));
		setPageText(0, getEditorInput().getName());
		for (IFile inputFile : getEditorInput().getFiles()) {
			try {
				addPage(inputFile);
			} catch (PartInitException e) {
				BasePlugin.logError(
						"Impossible to add a new page to the String Editor", e); //$NON-NLS-1$
			}
		}
	}

	private void registerActionHandlers() {
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(),
				new UndoActionHandler(getEditorSite(), getUndoContext()));

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(),
				new RedoActionHandler(getEditorSite(), getUndoContext()));

		getEditorSite().getActionBars()
				.setGlobalActionHandler(
						ActionFactory.SAVE.getId(),
						ActionFactory.SAVE.create(getEditorSite()
								.getWorkbenchWindow()));
	}

	public Composite createMainControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		AbstractStringEditorInput stringEditorInput = getEditorInput();
		List<ColumnInfo> infos = stringEditorInput.getColumns();

		editorComposite = toolkit.createForm(parent);
		editorComposite
				.setText((stringEditorInput.getName() != null)
						&& (stringEditorInput.getName().trim().length() > 0) ? stringEditorInput
						.getName() : Messages.StringEditorPart_EditorTitle);

		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 0;
		editorComposite.getBody().setLayout(layout);

		createOptionsSection(toolkit, editorComposite);

		Table t = toolkit.createTable(editorComposite.getBody(),
				SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
						| SWT.BORDER | SWT.HIDE_SELECTION);

		viewer = new TableViewer(t);

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		viewer.getTable().setLayoutData(layoutData);
		listener = new ColumnSelectionListener(viewer);

		/*
		 * create the "Key" column
		 */
		createColumn(Messages.StringEditorPart_KeyLabel,
				Messages.StringEditorPart_KeyTooltip, 0).getColumn()
				.setMoveable(false);

		restoreSession(infos);

		/*
		 * Configure the viewer
		 */
		ViewerComparator sorter = new TableComparator();
		viewer.setComparator(sorter);
		viewer.getTable().setSortDirection(SWT.DOWN);
		viewer.setContentProvider(new StringEditorViewerContentProvider());
		viewer.setInput(new StringEditorViewerModel(infos, this
				.getContentProvider().getValidator()));
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);

		/*
		 * Enable tooltips
		 */
		StringEditorViewerEditableTooltipSupport.enableFor(viewer,
				ToolTip.NO_RECREATE, this);

		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		viewer.getControl().addMenuDetectListener(new MenuDetectListener() {

			public void menuDetected(MenuDetectEvent e) {

				int position = viewer.getTable().toControl(e.x, e.y).x
						+ viewer.getTable().getHorizontalBar().getSelection();

				int x = 0;
				for (int i = 0; i < viewer.getTable().getColumnCount(); i++) {
					x += viewer.getTable().getColumn(i).getWidth();
					if (position <= x) {
						activeColumn = i;
						int currentX = viewer.getTable().toControl(e.x, e.y).x;
						int currentY = viewer.getTable().toControl(e.x, e.y).y;
						activeRow = viewer
								.getCell(new Point(currentX, currentY));
						break;
					}
				}
				// if the position is out of the columns bound, set the column
				// to the first one
				if (position >= x) {
					activeColumn = 0;
				}
				viewer.getControl().setMenu(
						mgr.createContextMenu(viewer.getControl()));

			}
		});

		viewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {

				return ((RowInfo) element).getKey().toLowerCase()
						.contains(getKeyFilter().toLowerCase());
			}
		});

		ColumnViewerEditorActivationStrategy activationStrategy = new ColumnViewerEditorActivationStrategy(
				viewer) {

			@Override
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				boolean activate = false;
				if ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED)
						&& (event.stateMask == 0)) {
					if ((event.character >= 32) && (event.character <= 127)) {
						activate = true;
					} else if ((event.keyCode == SWT.CR)
							|| (event.keyCode == SWT.DEL)
							|| (event.keyCode == SWT.KEYPAD_CR)) {
						activate = true;
					} else {
						activate = super.isEditorActivationEvent(event);
					}
				} else if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
					activate = true;
				}
				return activate;
			}

		};
		activationStrategy.setEnableEditorActivationWithKeyboard(true);

		FocusCellHighlighter highlighter = new StringEditorCellHighlighter(
				viewer);

		CellNavigationStrategy navigationStrategy = new CellNavigationStrategy();

		TableViewerFocusCellManager manager = new TableViewerFocusCellManager(
				viewer, highlighter, navigationStrategy);

		TableViewerEditor.create(viewer, manager, activationStrategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		viewer.getColumnViewerEditor().addEditorActivationListener(
				new StringEditorColumnViewerEditorActivationListener(viewer));

		viewer.getTable().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				session.clean();
				saveSession();
				saveOptions();
				session.save();
			}
		});
		viewer.getTable().layout(true, true);
		restoreOptions();
		session.clean();
		saveOptions();
		saveSession();
		session.save();
		setEditorStatus(getEditorInput().validate());
		getModel().addListener(new IModelChangedListener() {

			public void modelChanged(StringEditorViewerModel model) {
				setEditorStatus(getEditorInput().validate());
			}
		});

		if (contentProvider.getContextHelpID() != null) {
			PlatformUI
					.getWorkbench()
					.getHelpSystem()
					.setHelp(viewer.getTable(),
							contentProvider.getContextHelpID());
		}

		// if (expandRow) {
		// user preference is to view all the rows with the global maximum
		// number of lines
		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			final int TEXT_MARGIN = 3;

			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = viewer.getTable().getColumn(event.index)
						.getWidth() - 1;
				if (event.index == 0) {
					event.width = event.width - 3;
				}
				if (expandRow) {
					event.height = Math.max(event.height, size.y + TEXT_MARGIN);
				} else {
					Point size2 = event.gc.textExtent("A"); //$NON-NLS-1$
					event.height = size2.y + TEXT_MARGIN;
				}
			}
		});

		viewer.getTable().addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				event.detail &= ~SWT.FOREGROUND;
			}
		});

		viewer.getTable().addListener(SWT.PaintItem, new Listener() {
			final int TEXT_MARGIN = 3;

			final int FIRST_COLUM_TEXT_MARGIN = 20;

			public void handleEvent(Event event) {

				int yOffset = 0;
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				if (event.index > 0) {
					event.gc.drawText(text, event.x + TEXT_MARGIN, event.y
							+ yOffset, true);
				} else if (event.index == 0) {
					event.gc.drawImage(item.getImage(), event.x, event.y);
					event.gc.drawText(text, event.x + FIRST_COLUM_TEXT_MARGIN,
							event.y + yOffset, true);
				}

			}
		});

		/*
		 * for (int i = 0; i < viewer.getTable().getColumnCount(); i++) {
		 * viewer.getTable().getColumn(i).pack(); } viewer.getTable().pack();
		 */
		// }
		registerActionHandlers();
		return editorComposite;
	}

	private void saveSession() {
		Table t = viewer.getTable();
		for (int i = 0; i < t.getColumnCount(); i++) {
			{
				TableColumn c = t.getColumn(t.getColumnOrder()[i]);
				session.setProperty(c.getText(), PROPERTY.ORDER,
						new Integer(i).toString());
				session.setProperty(c.getText(), PROPERTY.VISIBLE, new Boolean(
						c.getResizable()).toString());
				session.setProperty(c.getText(), PROPERTY.WIDTH,
						new Integer(c.getWidth()).toString());
			}

		}
		/*
		 * Save the sort column and the sort direction
		 */
		session.setProperty(SORTER_PROPERTY_NAME, PROPERTY.SORT_BY_COLUMN,
				t.getSortColumn() != null ? t.getSortColumn().getText() : null);
		session.setProperty(SORTER_PROPERTY_NAME, PROPERTY.ORDER, t
				.getSortDirection() == SWT.UP ? new Integer(SWT.UP).toString()
				: new Integer(SWT.DOWN).toString());

	}

	private void saveOptions() {
		/*
		 * Save the search and highlight options
		 */
		String namespace = associatedProject.getName();
		session.setProperty(namespace, PROPERTY.HIGHLIGHT_CHANGES,
				Boolean.toString(highlightChanges));
		session.setProperty(namespace, PROPERTY.SEARCH_TEXT,
				searchString.length() > 0 ? searchString : null);
		session.setProperty(namespace, PROPERTY.SHOW_COMMENTS,
				Boolean.toString(showCellComments));
		session.setProperty(namespace, PROPERTY.FILTER_BY_KEY,
				filterByKeyString.length() > 0 ? filterByKeyString : null);
		session.setProperty(namespace, PROPERTY.EXPAND_ROW,
				Boolean.toString(expandRow));
	}

	private void restoreOptions() {
		/*
		 * Restore search and highlight options
		 */
		String namespace = associatedProject.getName();
		String search = session.getProperty(namespace, PROPERTY.SEARCH_TEXT);
		String highlight = session.getProperty(namespace,
				PROPERTY.HIGHLIGHT_CHANGES);
		String showComments = session.getProperty(namespace,
				PROPERTY.SHOW_COMMENTS);
		String filterByKey = session.getProperty(namespace,
				PROPERTY.FILTER_BY_KEY);
		String shouldExpandRow = session.getProperty(namespace,
				PROPERTY.EXPAND_ROW);
		if (search != null) {
			searchText.setText(search);
			searchString = searchText.getText();
		}
		if (highlight != null) {
			if (!Platform.getOS().equals(Platform.OS_LINUX)) {
				highlightChangesButton.setSelection(new Boolean(highlight));
				highlightChanges = highlightChangesButton.getSelection();
			}
		}
		if (showComments != null) {
			showCellCommentsButton.setSelection(new Boolean(showComments));
			showCellComments = showCellCommentsButton.getSelection();
		}
		if (filterByKey != null) {
			filterByKeyText.setText(filterByKey);
			filterByKeyString = filterByKeyText.getText();
		}
		if (shouldExpandRow != null) {
			expandRowButton.setSelection(new Boolean(shouldExpandRow));
			expandRow = expandRowButton.getSelection();
		}
	}

	private void restoreSession(List<ColumnInfo> infos) {

		List<ColumnInfo> reordered = new ArrayList<ColumnInfo>(infos.size());
		Map<Integer, ColumnInfo> orderMap = new HashMap<Integer, ColumnInfo>();
		List<ColumnInfo> out = new ArrayList<ColumnInfo>();

		/*
		 * Open session
		 */
		for (ColumnInfo info : infos) {
			String order = session.getProperty(info.getId(), PROPERTY.ORDER);

			if (order != null) {
				orderMap.put(Integer.parseInt(order), info);
			} else {
				out.add(info);
			}
		}

		/*
		 * exclude the key column (i = 0)
		 */
		for (int i = 1; i < infos.size() + 1; i++) {
			ColumnInfo info = orderMap.get(i);
			if (info != null) {
				reordered.add(info);
			} else if (out.size() > 0) {
				reordered.add(out.remove(0));
			}
		}

		/*
		 * Create columns
		 */
		for (ColumnInfo info : reordered) {
			String width = session.getProperty(info.getId(), PROPERTY.WIDTH);
			String visible = session
					.getProperty(info.getId(), PROPERTY.VISIBLE);

			TableViewerColumn column = createColumn(info.getId(),
					info.getTooltip(), -1);
			if (width != null) {
				column.getColumn().setWidth(Integer.parseInt(width));
			}
			if (visible != null) {
				column.getColumn().setResizable(Boolean.parseBoolean(visible));
			}
		}
		/*
		 * Set size of the key column if it has a saved one
		 */
		String width = session.getProperty(Messages.StringEditorPart_KeyLabel,
				PROPERTY.WIDTH);
		String visible = session.getProperty(
				Messages.StringEditorPart_KeyLabel, PROPERTY.VISIBLE);
		if (width != null) {
			viewer.getTable().getColumn(0).setWidth(Integer.parseInt(width));
		}
		if (visible != null) {
			viewer.getTable().getColumn(0)
					.setResizable(Boolean.parseBoolean(visible));
		}
		String sortBy = session.getProperty(SORTER_PROPERTY_NAME,
				PROPERTY.SORT_BY_COLUMN);
		String sortDirection = session.getProperty(SORTER_PROPERTY_NAME,
				PROPERTY.ORDER);
		/*
		 * Try to restore the sort by. If no sort saved, use the key column
		 */
		if ((sortBy != null) && (sortBy.length() > 0)) {
			for (TableColumn c : viewer.getTable().getColumns()) {
				if (c.getText().equals(sortBy)) {
					viewer.getTable().setSortColumn(c);
				}
			}
		} else {
			viewer.getTable().setSortColumn(viewer.getTable().getColumn(0));
		}
		if (sortDirection != null) {
			try {
				int direction = Integer.parseInt(sortDirection);
				viewer.getTable().setSortDirection(direction);
			} catch (NumberFormatException e) {
				viewer.getTable().setSortDirection(SWT.DOWN);
			}
		}
		viewer.refresh();
		saveSession();
	}

	/**
	 * Create a new column with the following id and tooltip
	 * 
	 * @param id
	 * @param tooltip
	 * 
	 * @return the created columnviewer
	 */
	private TableViewerColumn createColumn(String id, String tooltip, int index) {
		TableViewerColumn column = null;
		column = new TableViewerColumn(viewer, SWT.NONE, index);
		column.setLabelProvider(new StringEditorColumnLabelProvider(id, this));
		column.setEditingSupport(new CellEditingSupport(viewer, id));
		column.getColumn().setText(id);
		column.getColumn().setToolTipText(tooltip);
		column.getColumn().setWidth(
				SIZE_HINT * (column.getColumn().getText().length() + 1));
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().addSelectionListener(listener);
		return column;
	}

	/**
	 * hide the column
	 * 
	 * @param c
	 */
	private void hideColumn(TableColumn c) {
		c.setResizable(false);
		c.setData(PREVIOUS_WIDTH_PROPERTY_NAME,
				new Integer(c.getWidth()).toString());
		c.setWidth(0);
	}

	/**
	 * show the column with the same size
	 * 
	 * @param c
	 */
	private void showColumn(TableColumn c) {
		c.setResizable(true);
		int preferedSize = -1;

		try {
			preferedSize = Integer.parseInt((String) c
					.getData(PREVIOUS_WIDTH_PROPERTY_NAME));
		} catch (NumberFormatException e) {

		}

		c.setWidth(preferedSize > 0 ? preferedSize : 0);

		if (preferedSize == -1) {
			c.pack();
		}
	}

	/**
	 * Fill the context menu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {

		addColumnsVisibilityActions(manager);

		manager.add(new Separator());
		manager.add(getEditorSite().getActionBars().getGlobalActionHandler(
				ActionFactory.UNDO.getId()));
		manager.add(getEditorSite().getActionBars().getGlobalActionHandler(
				ActionFactory.REDO.getId()));
		manager.add(new Separator());
		manager.add(new AddKeyAction());
		manager.add(new RemoveKeyAction());
		manager.add(new AddColumnAction());
		manager.add(new RemoveColumnAction());
		manager.add(new RevertToSavedAction());
		manager.add(new CloneColumnAction());
		manager.add(new TranslateColumnAction());
		manager.add(new TranslateCellAction());

	}

	/**
	 * Add actions to show/hide columns as a submenu
	 * 
	 * @param manager
	 */
	private void addColumnsVisibilityActions(final IMenuManager manager) {

		MenuManager showColumnsMenu = new MenuManager(
				Messages.StringEditorPart_ShowColumnsSubmenuLabel);

		for (TableColumn column : viewer.getTable().getColumns()) {
			if (!column.getText().equals(Messages.StringEditorPart_KeyLabel)) {
				showColumnsMenu.add(new HideShowColumnAction(column.getText(),
						Action.AS_CHECK_BOX, column));
			}
		}

		showColumnsMenu.add(new Separator());
		showColumnsMenu.add(new HideShowAllColumnsAction(
				Messages.StringEditorPart_ShowAllColumnsActionName,
				Action.AS_PUSH_BUTTON, viewer.getTable(), true));
		showColumnsMenu.add(new HideShowAllColumnsAction(
				Messages.StringEditorPart_HideAllColumnsActionName,
				Action.AS_PUSH_BUTTON, viewer.getTable(), false));

		manager.add(showColumnsMenu);

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (promptUpdateConflicts()) {
			List<RowInfo> changed = getModel().save();
			updateViewer(changed);
			setEditorStatus(getEditorInput().validate());
			if (getActivePage() > 0) {
				notifyEditorChanged();
			}
			for (int i = 1; i < getPageCount(); i++) {
				if (getEditor(i).isDirty()) {
					if (getActivePage() == 0) {
						updatePageContent(i);
					}
					getEditor(i).doSave(monitor);
				}
			}
			getEditorInput().save();
			fireDirtyPropertyChanged();
		}
		syncPages();

	}

	public void updateViewer(List<RowInfo> rows) {
		if (rows != null) {
			for (RowInfo changedRow : rows) {
				viewer.update(changedRow, null);
			}
		} else {
			for (RowInfo changedRow : getModel().getRows().values()) {
				viewer.update(changedRow, null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return super.isDirty() || (getEditorInput()).isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false; // not allowed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	} // not allowed

	/**
	 * Get this editor viewer
	 * 
	 * @return the editor viewer associated with this editor
	 */
	public TableViewer getEditorViewer() {
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

		viewer.getTable().setFocus();

		promptFileSystemChanges();

	}

	public void markColumnAsChanged(String columnID) {
		changedColumns.add(columnID);
		needToPromptFileSystemChange = true;
	}

	public boolean unmarkColumnAsChanged(String columnID) {
		boolean result = changedColumns.remove(columnID);
		needToPromptFileSystemChange = (!changedColumns.isEmpty());
		return result;
	}

	private void clearColumnsMarkedAsChanged() {
		changedColumns.clear();
		needToPromptFileSystemChange = (!changedColumns.isEmpty());
	}

	/**
	 * Do the "RevertColumnToSavedStateOperation" without undo/redo entry This
	 * method is just used when a source page is changed --> editor notifies
	 * input --> input parses the new content --> input notifies editor that
	 * something changed with the new parsed file
	 * 
	 * Keep track of what was changed before
	 * 
	 * @param columnName
	 */
	private void untrackedRevertColumn(String columnName) {
		ColumnInfo savedState = new ColumnInfo(columnName, getModel()
				.getColumn(columnName).getTooltip(), getEditorInput()
				.getValues(columnName), getModel().getColumn(columnName)
				.canRemove());
		syncDirty(columnName, savedState);
		getModel().removeColumn(columnName);
		getModel().addColumn(savedState);
		unmarkColumnAsChanged(columnName);
		getEditorViewer().refresh();
	}

	/**
	 * Sync the dirty state from source to the destination column Dirty state as
	 * follows: Source and Destination cells values differ; Source is dirty;
	 * Destination new cells
	 * 
	 * 
	 * @param source
	 *            a name of existent column to copy from
	 * @param destination
	 *            a new {@link ColumnInfo} to copy the dirty state to
	 * 
	 *            NOTE: Only the cells that exists on both columns will be
	 *            copied
	 */
	private void syncDirty(String source, ColumnInfo destination) {
		ColumnInfo sourceColumn = getModel().getColumn(source);
		if (sourceColumn != null) {
			for (String key : destination.getCells().keySet()) {
				CellInfo sourceCell = sourceColumn.getCells().get(key);
				CellInfo destCell = destination.getCells().get(key);
				// source cell exists
				if (sourceCell != null) {
					// source cell is dirty
					if (sourceCell.isDirty()) {
						if (destCell != null) {
							destCell.setDirty(true);
						}
					}
					// values differ
					if (!sourceCell.getValue().equals(destCell.getValue())) {
						destCell.setDirty(true);
					}
				}
				// source cell does not exists: this is a new cell
				else {
					if (destCell != null) {
						destCell.setDirty(true);
					}
				}
			}
		}
	}

	private void promptFileSystemChanges() {

		if (pageChangedWithDirtySourceEditors) {
			Set<String> revertColumnNames = new HashSet<String>(changedColumns);
			for (String columnName : revertColumnNames) {
				untrackedRevertColumn(columnName);
			}
			pageChangedWithDirtySourceEditors = false;
		}

		else if ((changedColumns.size() > 0) && (needToPromptFileSystemChange)) {

			String columnNames = getChangedColumnNamesString();

			boolean replace = MessageDialog.openQuestion(StringEditorPart.this
					.getEditorSite().getShell(),
					Messages.StringEditorPart_FileChangedTitle, NLS.bind(
							Messages.StringEditorPart_FileChangedDescription,
							columnNames));

			needToPromptFileSystemChange = false;

			/*
			 * If the user chooses to replace
			 */
			if (replace) {
				Set<String> revertColumnNames = new HashSet<String>(
						changedColumns);
				for (String columnName : revertColumnNames) {
					RevertColumnToSavedStateOperation operation = new RevertColumnToSavedStateOperation(
							Messages.StringEditorPart_RevertColumnActionOperationName,
							StringEditorPart.this, getModel().getColumn(
									columnName));
					executeOperation(operation);
				}
			}

		}

	}

	private boolean promptUpdateConflicts() {

		boolean result = true;

		if (changedColumns.size() > 0) {

			String columnNames = getChangedColumnNamesString();

			boolean overwrite = MessageDialog
					.openQuestion(
							StringEditorPart.this.getEditorSite().getShell(),
							Messages.StringEditorPart_UpdateConflictTitle,
							NLS.bind(
									Messages.StringEditorPart_UpdateConflictDescription,
									columnNames));

			if (!overwrite) {
				result = false;
			} else {
				clearColumnsMarkedAsChanged();
			}

		}

		return result;

	}

	private String getChangedColumnNamesString() {

		String result = "\n"; //$NON-NLS-1$

		int i = 1;
		for (String columnName : changedColumns) {
			result += i++ + ")" + columnName + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return result;

	}

	/**
	 * @param columnID
	 * @param key
	 * @return
	 */
	private CellInfo getCellInfo(String columnID, String key) {
		CellInfo cellInfo = null;

		cellInfo = getEditorInput().getValue(columnID, key);

		return cellInfo;
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}

	/**
	 * Add a new column based in the {@link ColumnInfo}
	 * 
	 * @param info
	 * @param index
	 *            index of the column or -1 if to be the last
	 * @return the created column
	 */
	public TableColumn addColumn(ColumnInfo info, int index) {
		TableViewerColumn tableViewerColumn = createColumn(info.getId(),
				info.getTooltip(), index);
		getEditorInput().addColumn(info.getId());
		getModel().addColumn(info);
		for (String cellKey : info.getCells().keySet()) {
			try {
				CellInfo cell = info.getCells().get(cellKey);
				if (cell.getValue() != null) {
					getEditorInput().setValue(info.getId(), cellKey,
							cell.getValue());
				}
			} catch (SequoyahException e) {
				BasePlugin.logError("Error adding column: " + info.getId(), e); //$NON-NLS-1$
			}
		}

		getEditorViewer().refresh();
		fireDirtyPropertyChanged();
		syncPages();
		return tableViewerColumn.getColumn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public AbstractStringEditorInput getEditorInput() {
		return (AbstractStringEditorInput) super.getEditorInput();
	}

	/**
	 * Remove the follow column from the table
	 * 
	 * @param info
	 */
	public void removeColumn(String columnID) {
		getEditorInput().removeColumn(columnID);
		getModel().removeColumn(columnID);
		for (TableColumn column : getEditorViewer().getTable().getColumns()) {
			if (column.getText().equals(columnID)) {
				column.dispose();
			}
		}
		fireDirtyPropertyChanged();
		getEditorViewer().refresh();
		syncPages();
	}

	/**
	 * Add a new row
	 * 
	 * @param info
	 */
	public void addRow(RowInfo info) {
		info = getEditorInput().addRow(info);
		getModel().addRow(info);
		fireDirtyPropertyChanged();
	}

	/**
	 * Remove a row
	 * 
	 * @param key
	 */
	public void removeRow(String key) {
		getModel().removeRow(key);
		getEditorInput().removeRow(key);
		fireDirtyPropertyChanged();
	}

	/**
	 * Get the model associated with this editor viewer
	 * 
	 * @return the model
	 */
	public StringEditorViewerModel getModel() {
		return (StringEditorViewerModel) getEditorViewer().getInput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getEditorInput().removeInputChangeListener(inputChangeListener);
		getEditorInput().dispose();
		super.dispose();
	}

	/**
	 * Fire the dirt property updated
	 */
	public void fireDirtyPropertyChanged() {
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Get if label provider should highlight the changes
	 * 
	 * @return
	 */
	public boolean getHighlightChanges() {
		return highlightChanges;
	}

	/**
	 * get the search string
	 * 
	 * @return
	 */
	public String getSearchText() {
		return searchString;
	}

	/**
	 * get if cell comments shall be shown
	 * 
	 * @return
	 */
	public boolean getShowCellComments() {
		return showCellComments;
	}

	/**
	 * get the value to filter keys
	 * 
	 * @return
	 */
	public String getKeyFilter() {
		return filterByKeyString;
	}

	/**
	 * execute a undoable operation
	 * 
	 * @param operation
	 */
	private void executeOperation(EditorOperation operation) {
		try {
			operation.addContext(getUndoContext());
			getOperationHistory()
					.execute(operation, getProgressMonitor(), null);
		} catch (ExecutionException e) {
			BasePlugin.logError("Error executing editor operation: " //$NON-NLS-1$
					+ operation.getLabel(), e);
		}
	}

	public void setEditorStatus(IStatus status) {
		StringBuilder builder = new StringBuilder();
		int messageType = 0;
		if ((status != null) && !status.isOK()) {
			if (status.isMultiStatus()) {
				for (IStatus child : status.getChildren()) {
					builder.append(child.getMessage());
					builder.append("\n"); //$NON-NLS-1$
				}
			} else {
				builder.append(status.getMessage());
			}
			switch (status.getSeverity()) {
			case IStatus.WARNING:
				messageType = IMessage.WARNING;
				break;
			case IStatus.ERROR:
				messageType = IMessage.ERROR;
				break;
			default:
				break;
			}
			editorComposite.setMessage(builder.toString(), messageType);
		} else {
			editorComposite.setMessage(null);
		}

	}

	/**
	 * Get a column by their header text
	 * 
	 * @param columnID
	 * @return the column or null if not found
	 */
	public TableColumn getColumnByID(String columnID) {
		TableColumn theColumn = null;
		for (TableColumn column : getEditorViewer().getTable().getColumns()) {
			if (column.getText().equals(columnID)) {
				theColumn = column;
			}
		}
		return theColumn;
	}

	public Image getErrorImage() {
		return errorImage;
	}

	public Image getWarningImage() {
		return warningImage;
	}

	public Image getOKImage() {
		return okImage;
	}

	/**
	 * Find with pages need to be added to editor
	 * 
	 * @return a list with the IFile objects to add to this editor
	 */
	private List<IFile> findPagesToAdd() {
		List<IFile> toAdd = new ArrayList<IFile>();
		List<IFile> files = getEditorInput().getFiles();
		for (int i = 0; i < files.size(); i++) {
			// ignore the first page (it is always visible)
			int j = 1;
			String pageName = getEditorInput().getSourcePageNameForFile(
					files.get(i));
			boolean sync = false;
			while ((j < getPageCount()) && !sync) {
				if (getPageText(j).equals(pageName)) {
					sync = true;
				}
				j++;
			}
			if (!sync) {
				toAdd.add(files.get(i));
			}
		}
		return toAdd;
	}

	/**
	 * Find which editor pages need to be removed
	 * 
	 * @return a inverse ordered list of indexes of pages to remove
	 */
	private List<Integer> findPagesToRemove() {
		List<Integer> toRemove = new ArrayList<Integer>();
		List<IFile> files = getEditorInput().getFiles();
		// ignore the first page (it is always visible)
		for (int i = 1; i < getPageCount(); i++) {
			int j = 0;
			String pageName = getPageText(i);
			boolean sync = false;
			while ((j < files.size()) && !sync) {
				if (getEditorInput().getSourcePageNameForFile(files.get(j))
						.equals(pageName)) {
					sync = true;
				}
				j++;
			}
			if (!sync) {
				toRemove.add(i);
			}
		}
		Comparator<Integer> comparator = Collections.reverseOrder();
		Collections.sort(toRemove, comparator);
		return toRemove;
	}

	/**
	 * Syncronize the opened editor pages (remove pages of deleted input files
	 * and add pages of created files)
	 */
	private void syncPages() {
		List<Integer> pagesToRemove = findPagesToRemove();
		List<IFile> pagesToAdd = findPagesToAdd();
		for (int i : pagesToRemove) {
			removePage(i);
		}
		for (IFile file : pagesToAdd) {
			try {
				addPage(file);
			} catch (PartInitException e) {
				BasePlugin.logError(
						"Impossible to add a new page to the String Editor", e); //$NON-NLS-1$
			}
		}
	}

	private void notifyEditorChanged() {
		// exclude first page that is the main page
		for (int i = 1; i < getPageCount(); i++) {
			IEditorPart editor = getEditor(i);
			if (editor instanceof ITextEditor) {
				ITextEditor sourceEditorPart = (ITextEditor) editor;
				IEditorInput editorInput = sourceEditorPart.getEditorInput();
				if (sourceEditorPart.isDirty()) {
					pageChangedWithDirtySourceEditors = true;
					getEditorInput().notifyEditorChanged(
							editorInput,
							sourceEditorPart.getDocumentProvider()
									.getDocument(editorInput).get());
				}
			}
		}
	}

	private void updatePageContent(int newPageIndex) {
		ITextEditor editor = getEditor(newPageIndex) instanceof ITextEditor ? (ITextEditor) getEditor(newPageIndex)
				: null;
		if (editor != null) {
			IFileEditorInput input = editor.getEditorInput() instanceof IFileEditorInput ? (IFileEditorInput) editor
					.getEditorInput() : null;
			if (input != null) {
				String rawTextFileContent = getEditorInput()
						.getContentForFileAsText(input);
				if (!editor.getDocumentProvider().getDocument(input).get()
						.equals(rawTextFileContent)) {
					editor.getDocumentProvider().getDocument(input)
							.set(rawTextFileContent);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {

		// before change to the main page, notify the input about changed files
		// and their new content
		if (newPageIndex == 0) {
			registerActionHandlers();
			if (isDirty()) {
				notifyEditorChanged();

			}
		}
		/*
		 * If we are changing to the text tabs, get the raw content from the
		 * input and set to the tab.
		 */
		else {
			updatePageContent(newPageIndex);

		}
		super.pageChange(newPageIndex);
	}
}
