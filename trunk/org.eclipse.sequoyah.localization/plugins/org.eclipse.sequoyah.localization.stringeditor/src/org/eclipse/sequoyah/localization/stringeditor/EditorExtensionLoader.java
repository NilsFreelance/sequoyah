/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.stringeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.localization.stringeditor.editor.input.IStringEditorInput;
import org.eclipse.tml.localization.stringeditor.providers.ContentProvider;
import org.eclipse.tml.localization.stringeditor.providers.DefaultOperationProvider;
import org.eclipse.tml.localization.stringeditor.providers.ICellValidator;
import org.eclipse.tml.localization.stringeditor.providers.IOperationProvider;

public class EditorExtensionLoader {

	private static final String EXTENSION_POINT_ID = "contentprovider";

	private static final String PROVIDER_ELEMENT_ID = "provider";

	private static final String PROVIDER_EDITOR_CONTEXT_HELP_ID = "contextHelpID";

	private static final String PROVIDER_EDITOR_INPUT_ID = "editorInput";

	private static final String PROVIDER_OPERATION_PROV_ID = "operationsProvider";

	private static final String PROVIDER_CELL_VALIDATOR_ID = "cellValidator";

	private final Map<IFile, IConfigurationElement> knownProviders;

	private final List<IConfigurationElement> availableElements;

	private static EditorExtensionLoader instance;

	private EditorExtensionLoader() {
		knownProviders = new HashMap<IFile, IConfigurationElement>();
		availableElements = new ArrayList<IConfigurationElement>();
		loadExtensions();
	}

	/**
	 * Load the extensions for the editor input extension point
	 */
	private void loadExtensions() {

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(StringEditorPlugin.PLUGIN_ID,
						EXTENSION_POINT_ID);
		for (IConfigurationElement element : elements) {
			if (element.getName().equals(PROVIDER_ELEMENT_ID)) {
				availableElements.add(element);
			}
		}
	}

	/**
	 * Return the available editor input for a given nature
	 * 
	 * @param nature
	 *            the nature
	 * @return the editor content provider
	 */
	public ContentProvider getContentProviderForFileInput(IFile file) {

		ContentProvider provider = null;
		IConfigurationElement iConfigurationElement = knownProviders.get(file);
		if (iConfigurationElement == null) {
			iConfigurationElement = searchElementForFile(file);
		}
		if (iConfigurationElement != null) {
			provider = createContentProviderFromElement(iConfigurationElement);
		}

		return provider;
	}

	private IConfigurationElement searchElementForFile(IFile file) {
		IConfigurationElement elementResult = null;

		int i = 0;
		while (elementResult == null && i < availableElements.size()) {
			IConfigurationElement element = availableElements.get(i);
			ContentProvider prov = createContentProviderFromElement(element);
			if (prov.getEditorInput().canHandle(file)) {
				elementResult = element;
				knownProviders.put(file, element);
				prov = null;
			}
			i++;
		}
		System.gc();
		return elementResult;
	}

	private static ContentProvider createContentProviderFromElement(
			IConfigurationElement element) {
		ContentProvider contentProvider = null;
		String contextHelpID = null;
		Object inputClazz = null;
		Object opClazz = null;
		Object cellValidator = null;

		try {
			inputClazz = element
					.createExecutableExtension(PROVIDER_EDITOR_INPUT_ID);

			opClazz = element
					.createExecutableExtension(PROVIDER_OPERATION_PROV_ID);
			cellValidator = element
					.createExecutableExtension(PROVIDER_CELL_VALIDATOR_ID);
			contextHelpID = element
					.getAttribute(PROVIDER_EDITOR_CONTEXT_HELP_ID);

		} catch (CoreException e) {
			BasePlugin.logWarning("No operation provider found for "
					+ element.getDeclaringExtension().getUniqueIdentifier()
					+ ". Using a default operation provider.");
		}
		IStringEditorInput input = null;
		IOperationProvider provider = null;
		ICellValidator validator = null;

		if (inputClazz instanceof IStringEditorInput) {
			input = (IStringEditorInput) inputClazz;
		}
		if (opClazz instanceof IOperationProvider) {
			provider = (IOperationProvider) opClazz;
		} else {
			provider = new DefaultOperationProvider();
		}
		if (cellValidator instanceof ICellValidator) {
			validator = (ICellValidator) cellValidator;
		}
		if (input != null) {
			contentProvider = new ContentProvider(input, provider, validator,
					contextHelpID.length() > 0 ? contextHelpID : null);
		}
		return contentProvider;
	}

	/**
	 * Get a instance of this loader
	 * 
	 * @return
	 */
	public final static EditorExtensionLoader getInstance() {
		if (instance == null) {
			instance = new EditorExtensionLoader();
		}
		return instance;
	}

}
