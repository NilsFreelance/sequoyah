/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * Vinicius Rigoni Hernandes (Eldorado)
 * 
 * Contributors:
 *  Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 *  Marcelo Marzola Bossoni (Eldorado) - Bug [294445] - Localization Editor remains opened when project is deleted
 ********************************************************************************/
package org.eclipse.tml.localization.stringeditor.editor.input;

/**
 * Listener that aim to be notified when there is a change in the editor
 * contents
 */
public interface IInputChangeListener {

	public void columnChanged(String columnID);

	public void projectRemoved();

}
