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
 * Matheus Tait Lima (Eldorado) - Adapting localization plugins to accept automatic translations
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.localization.stringeditor.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.TranslationInfo;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * Implementers should have in mind that callers will do:
 * 
 * 
 */

public interface IOperationProvider
{

    public void init(IProject project) throws Exception;

    /**
     * When creating a new column, providers will be asked to provide the ID If
     * no id is provided, a simple dialog will be opened asking user
     * 
     * @return the new column
     */
    public ColumnInfo getNewColumn();

    /**
     * When creating a new row, providers will be asked to provide the key of
     * this row.
     * 
     * @return the new row
     */
    public RowInfo[] getNewRow();

    /**
     * When creating a new column, based on a translation, 
     * providers will be asked to provide information 
     * about the new column.
     * 
     * @return the new column
     */
    public TranslationInfo getTranslatedColumnInfo(String selectedColumn);

    /**
     * When creating a new column, based on a translation, 
     * providers will be asked to provide information 
     * about the new column.
     * 
     * @return the new columns
     */
    public TranslationInfo[] getTranslatedColumnsInfo(String selectedColumn, String[] selectedKeys,
            String[] selectedCells, TableColumn[] columns);

}
