/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * Vinicius Rigoni (Eldorado)
 * Marcel Gorri (Eldorado)
 * 
 * Contributors:
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.implementation.generic;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.PreferencesManager;
import org.eclipse.sequoyah.localization.tools.managers.TranslatorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

/**
 * Asks for: - the name of a new language, and - source language, and - target
 * language
 * 
 * These data will be used for translation purposes
 */
@SuppressWarnings("restriction")
public class TranslateColumnInputDialog extends InputDialog {

	private IProject project = null;

	private ILocalizationSchema localizationSchema;

	private final String initialValue;

	// Translators
	private String translator;

	private Combo translatorsCombo = null;

	private Label translatorBrandingImage = null;

	// "From" information
	private Combo fromCombo = null;

	private String fromLanguage;

	private Text columnName = null;

	private String selectedColumn = null;

	// "To" information
	private String toLanguage;

	private Combo toCombo = null;

	private Button automaticallyAddLangID;

	private PreferenceDialog networkPreferencesDialog;

	private PreferenceManager prefMan;

	private static final String PROXY_PREFERENCE_PAGE_ID = "org.eclipse.ui.net.NetPreferences"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public TranslateColumnInputDialog(Shell parentShell, IProject project,
			String selectedColumn, String dialogTitle, String dialogMessage,
			String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.project = project;
		this.initialValue = initialValue;
		this.selectedColumn = selectedColumn;
		this.localizationSchema = LocalizationManager.getInstance()
				.getLocalizationSchema(project);
	}

	/**
	 * Get the original language
	 * 
	 * @return the original language of the word(s) being translated
	 */
	public String getFromLanguage() {
		return LanguagesUtil.getLanguageID(fromLanguage);
	}

	/**
	 * 
	 * @return
	 */
	public String getToLanguage() {
		return LanguagesUtil.getLanguageID(toLanguage);
	}

	/**
	 * Get the selected translator
	 * 
	 * @return the selected translator name
	 */
	public String getTranslator() {
		return translator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.InputDialog#createDialogArea(org.eclipse.swt
	 * .widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite languagesComposite = (Composite) super
				.createDialogArea(parent);
		this.columnName = this.getText();

		ComboListener listener = new ComboListener();

		Group translationDetailsGroup = new Group(languagesComposite,
				SWT.SHADOW_ETCHED_OUT);
		translationDetailsGroup
				.setText(Messages.TranslationDialog_LanguageAreaLabel);
		translationDetailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));
		translationDetailsGroup.setLayout(new GridLayout(3, false));

		/*
		 * Translators
		 */
		Label translatorText = new Label(translationDetailsGroup, SWT.NONE);
		translatorText.setText(Messages.Translator_Text);

		translatorsCombo = LanguagesUtil
				.createTranslatorsCombo(translationDetailsGroup);
		translatorsCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		translatorsCombo.addSelectionListener(listener);

		/*
		 * From Area
		 */
		Label fromText = new Label(translationDetailsGroup, SWT.NONE);
		fromText.setText(Messages.TranslationDialog_FromLanguage);

		String selectedLanguage = this.localizationSchema
				.getISO639LangFromID(this.selectedColumn);
		// check if ISO639 exists
		selectedLanguage = ((LanguagesUtil.getLanguageName(selectedLanguage) != null) ? selectedLanguage
				: null);
		String defaultLanguage = PreferencesManager.getInstance()
				.getProjectPreferencesManager(this.project)
				.getDefaultLanguageForColumn(this.selectedColumn);

		fromCombo = LanguagesUtil.createLanguagesCombo(translationDetailsGroup,
				selectedLanguage, defaultLanguage, this.localizationSchema);
		fromCombo
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fromCombo.addSelectionListener(listener);

		LanguagesUtil.createImageStatus(translationDetailsGroup,
				selectedLanguage);

		/*
		 * To Area
		 */
		Label toText = new Label(translationDetailsGroup, SWT.NONE);
		toText.setText(Messages.TranslationDialog_ToLanguage);

		toCombo = LanguagesUtil.createLanguagesCombo(translationDetailsGroup,
				null, null, this.localizationSchema);
		toCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		toCombo.addSelectionListener(listener);

		LanguagesUtil.createImageStatus(translationDetailsGroup, null);

		Label empty = new Label(translationDetailsGroup, SWT.NONE);

		// Automatic language ID
		automaticallyAddLangID = new Button(translationDetailsGroup, SWT.CHECK);
		automaticallyAddLangID.setText(Messages.TranslateColumnInputDialog_0);
		automaticallyAddLangID.setSelection(true);

		createNetworkGroup(languagesComposite);

		/*
		 * Translator Branding Area
		 */
		Composite brandingComposite = new Composite(languagesComposite,
				SWT.RIGHT);
		brandingComposite.setLayout(new GridLayout(1, false));
		brandingComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false));

		translatorBrandingImage = new Label(brandingComposite, SWT.NONE);
		translatorBrandingImage.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, true));

		setInitialValues();

		return languagesComposite;
	}

	/**
	 * Populate attributes with their initial values
	 */
	private void setInitialValues() {
		fromLanguage = fromCombo.getText();
		toLanguage = toCombo.getText();
		translator = translatorsCombo.getText();
		TranslatorManager.getInstance().setTranslatorBranding(translator,
				translatorBrandingImage);
	}

	/**
	 * Selection Listener that will be responsible for monitoring changes in
	 * combo selections (from language, to language and translator)
	 */
	class ComboListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() == fromCombo) {
				fromLanguage = fromCombo.getText();
				if (!fromLanguage.equals(LanguagesUtil.getComboSeparator())) {
					// update default language
					PreferencesManager
							.getInstance()
							.getProjectPreferencesManager(project)
							.setDefaultLanguageForColumn(selectedColumn,
									LanguagesUtil.getLanguageID(fromLanguage));
				} else {
					fromLanguage = null;
				}
			} else if (e.getSource() == toCombo) {
				toLanguage = toCombo.getText();
				if (!toLanguage.equals(LanguagesUtil.getComboSeparator())) {
					if (automaticallyAddLangID.getSelection()) {
						columnName.setText(localizationSchema
								.getIDforLanguage(LanguagesUtil
										.getLanguageID(toLanguage)));
					}

				} else {
					toLanguage = null;
				}
			} else if (e.getSource() == translatorsCombo) {
				translator = translatorsCombo.getText();
				TranslatorManager.getInstance().setTranslatorBranding(
						translator, translatorBrandingImage);
			}
			validateSelection();
		}
	}

	/**
	 * Validate the current selection and enable/disable OK button
	 */
	private void validateSelection() {

		boolean result = true;

		if (fromLanguage == null) {
			result = false;
		}
		if (toLanguage == null) {
			result = false;
		}

		getButton(IDialogConstants.OK_ID).setEnabled(result);
	}

	/**
	 * Create Network group
	 * 
	 * @param parent
	 *            parent composite
	 */
	@SuppressWarnings("unchecked")
	private void createNetworkGroup(Composite parent) {

		// Makes the network preferences dialog manager
		PreferenceManager manager = PlatformUI.getWorkbench()
				.getPreferenceManager();

		IPreferenceNode networkNode = null;

		for (IPreferenceNode node : (List<IPreferenceNode>) manager
				.getElements(PreferenceManager.PRE_ORDER)) {
			if (node.getId().equals(PROXY_PREFERENCE_PAGE_ID)) {
				networkNode = node;
				break;
			}
		}
		prefMan = new PreferenceManager();
		if (networkNode != null) {
			prefMan.addToRoot(networkNode);
		}

		Link downloadText = new Link(parent, SWT.WRAP);
		downloadText.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// Do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				openNetworkPreferencesPage();
			}
		});

		String linkText = Messages.bind(Messages.NetworkLinkText,
				Messages.NetworkLinkText, Messages.NetworkLinkLink);

		downloadText.setText(linkText);
		downloadText.update();
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		downloadText.setLayoutData(gridData);

	}

	/**
	 * Centralizes a window on the screen
	 * 
	 * @param shell
	 *            the window
	 */
	private static void centralizeShell(Shell shell) {
		int width = shell.getSize().x;
		int height = shell.getSize().y;
		int x = (shell.getDisplay().getClientArea().width - width) / 2;
		int y = (shell.getDisplay().getClientArea().height - height) / 2;
		shell.setLocation(x, y);
	}

	/**
	 * Opens the network preferences page
	 * 
	 * @return true if the user has confirmed the network configurations or
	 *         false otherwise
	 */
	@SuppressWarnings("restriction")
	private boolean openNetworkPreferencesPage() {
		// Creates the dialog every time, because it is disposed when it is
		// closed.
		networkPreferencesDialog = new WorkbenchPreferenceDialog(
				this.getShell(), prefMan);
		networkPreferencesDialog.create();
		centralizeShell(networkPreferencesDialog.getShell());
		networkPreferencesDialog.open();
		return networkPreferencesDialog.getReturnCode() == WorkbenchPreferenceDialog.OK;
	}

}
