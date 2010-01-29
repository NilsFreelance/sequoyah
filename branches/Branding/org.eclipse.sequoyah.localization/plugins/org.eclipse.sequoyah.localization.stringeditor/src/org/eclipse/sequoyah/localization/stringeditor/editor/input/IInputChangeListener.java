/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * Vinicius Rigoni Hernandes (Eldorado)
 * 
 * Contributors:
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.localization.stringeditor.editor.input;

/**
 * Listener that aim to be notified when there is a change in the editor
 * contents
 */
public interface IInputChangeListener {

	public void columnChanged(String columnID);

}
