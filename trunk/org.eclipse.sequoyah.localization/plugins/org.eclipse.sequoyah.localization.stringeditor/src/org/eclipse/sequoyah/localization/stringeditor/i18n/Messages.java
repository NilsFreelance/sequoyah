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
package org.eclipse.tml.localization.stringeditor.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.tml.localization.stringeditor.i18n.messages"; //$NON-NLS-1$
	public static String DefaultOperationProvider_NewColumnDescription;
	public static String DefaultOperationProvider_NewColumnErrorNotEmpty;
	public static String DefaultOperationProvider_NewColumnTitle;
	public static String DefaultOperationProvider_NewRowDescription;
	public static String DefaultOperationProvider_NewRowErrorNotEmpty;
	public static String DefaultOperationProvider_NewRowTitle;
	public static String StringEditorPart_AddColumnActionName;
	public static String StringEditorPart_AddColumnOperationName;
	public static String StringEditorPart_AddKeyActionName;
	public static String StringEditorPart_AddKeyOperationName;
	public static String StringEditorPart_HideAllColumnsActionName;
	public static String StringEditorPart_HighlightChangesLabel;
	public static String StringEditorPart_KeyAlreadyExistsErrorMessage;
	public static String StringEditorPart_KeyLabel;
	public static String StringEditorPart_KeyTooltip;
	public static String StringEditorPart_OptionsText;
	public static String StringEditorPart_RemoveColumnActionName;
	public static String StringEditorPart_RemoveColumnOperationName;
	public static String StringEditorPart_RemoveColumnQuestionMessage;
	public static String StringEditorPart_RemoveKeyActionName;
	public static String StringEditorPart_RemoveKeyOperationName;
	public static String StringEditorPart_RevertColumnActionName;
	public static String StringEditorPart_RevertColumnActionOperationName;
	public static String StringEditorPart_RevertColumnActionTooltip;
	public static String StringEditorPart_SearchLabel;
	public static String StringEditorPart_ShowAllColumnsActionName;
	public static String StringEditorPart_ShowCellCommentsButtonText;
	public static String StringEditorPart_ShowColumnsSubmenuLabel;
	public static String StringEditorPart_TranslateActionName;
	public static String StringEditorPart_FilterByKeyLabel;
	public static String StringEditorPart_EditorTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
