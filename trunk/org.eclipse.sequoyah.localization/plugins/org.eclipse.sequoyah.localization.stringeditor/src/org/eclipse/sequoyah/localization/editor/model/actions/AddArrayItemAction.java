/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * <name> (<company>) - Bug [<bugid>] - <bugDescription>
 * Matheus Lima (Eldorado) - Bug [326793] -  Fixed action description
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.model.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sequoyah.localization.editor.StringEditorPlugin;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;
import org.eclipse.sequoyah.localization.editor.i18n.Messages;
import org.eclipse.sequoyah.localization.editor.model.StringEditorPart;
import org.eclipse.sequoyah.localization.editor.model.operations.AddArrayItemOperation;
import org.eclipse.sequoyah.localization.editor.model.operations.AddArrayItemsOperation;

/**
 * Action to add a new key (array item)
 */
public class AddArrayItemAction extends Action {
	/**
	 * 
	 */
	private final StringEditorPart stringEditorPart;
	int quantity = 1;
	public static final String ID = Messages.StringEditorPart_AddArrayItemActionName
	.substring(4);

	public AddArrayItemAction(StringEditorPart stringEditorPart) {
		super(Messages.StringEditorPart_AddArrayItemActionName);
		this.stringEditorPart = stringEditorPart;
		this.setImageDescriptor(StringEditorPlugin.imageDescriptorFromPlugin(
				StringEditorPlugin.PLUGIN_ID, "icons/string_array_item.png")); //$NON-NLS-1$
		this.setId(Messages.StringEditorPart_AddArrayItemActionName
				.substring(4));
		this.setDescription(Messages.AddArrayItemAction_DescriptionPrefix
				+ getId());
		this.setEnabled(false);
	}

	public void setQuantity(int i) {
		quantity = i;
	}

	@Override
	public boolean isEnabled() {
		boolean atLeastOneLineSelected = false;
		if ((this.stringEditorPart.getEditorViewer() != null)
				&& (this.stringEditorPart.getEditorViewer().getSelection() != null)) {
			StructuredSelection selection = (StructuredSelection) this.stringEditorPart
					.getEditorViewer().getSelection();
			final Object[] selectedRows = selection.toArray();
			atLeastOneLineSelected = (selectedRows != null && selectedRows.length > 0);
		}
		RowInfo[] rowInfo = getSelectedRowInfo();
		return (atLeastOneLineSelected && (rowInfo != null) && (rowInfo[0] != null));
	}

	private RowInfo[] getSelectedRowInfo() {
		RowInfo[] rowInfo = new RowInfo[1];
		if ((this.stringEditorPart.getEditorViewer() != null)
				&& (this.stringEditorPart.getEditorViewer().getSelection() != null)) {
			StructuredSelection selection = (StructuredSelection) this.stringEditorPart
					.getEditorViewer().getSelection();
			final Object[] selectedRows = selection.toArray();
			for (int i = 0; i < selectedRows.length; i++) {
				RowInfo info = (RowInfo) selectedRows[i];
				if (info instanceof RowInfoLeaf) {
					// string or array item
					RowInfoLeaf leaf = (RowInfoLeaf) info;
					if (leaf.getParent() != null) {
						// array item
						rowInfo[0] = leaf.getParent();
					}
				} else {
					// array
					rowInfo[0] = (RowInfo) selectedRows[i];
				}
			}
		}
		return rowInfo;
	}

	@Override
	public void run() {
		RowInfo[] rowInfo = new RowInfo[quantity];
		for (int i = 0; i < quantity; i++) {
			RowInfo[] rowInfoTemp = getSelectedRowInfo();
			rowInfo[i] = rowInfoTemp[0];
		}

		// add new key only if the key isn't null and the new key does not
		// exists
		if ((rowInfo != null) && (rowInfo[0] != null)) {
			if (rowInfo.length > 1) {
				AddArrayItemsOperation operation = new AddArrayItemsOperation(
						Messages.StringEditorPart_AddArrayItemOperationName,
						this.stringEditorPart, rowInfo, quantity);
				operation.addContext(this.stringEditorPart.getUndoContext());
				this.stringEditorPart.executeOperation(operation);
			} else {
				String arrayKey = rowInfo[0].getKey();
				if (rowInfo[0] instanceof RowInfo) {
					// array
					if (this.stringEditorPart.getModel().getRow(arrayKey) != null) {
						// existing array

						RowInfo existingRowArray = this.stringEditorPart
								.getModel().getRow(rowInfo[0].getKey());
						/*
						 * Map<Integer, RowInfoLeaf> childrenExistentArray =
						 * existingRowArray .getChildren(); int lastIndex =
						 * childrenExistentArray.size();
						 * 
						 * RowInfoLeaf rowInfoLeaf = new RowInfoLeaf(arrayKey,
						 * existingRowArray, lastIndex, null);
						 */
						AddArrayItemOperation operation = new AddArrayItemOperation(
								Messages.StringEditorPart_AddArrayItemOperationName,
								this.stringEditorPart, existingRowArray);
						/*
						 * AddKeyOperation operation = new AddKeyOperation(
						 * Messages .StringEditorPart_AddArrayItemOperationName,
						 * StringEditorPart.this, rowInfoLeaf);
						 */
						operation.addContext(this.stringEditorPart
								.getUndoContext());
						this.stringEditorPart.executeOperation(operation);
					}
				}
			}
		}
		this.stringEditorPart.refreshButtonsEnabled();
	}
}