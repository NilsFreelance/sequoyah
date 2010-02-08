/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.persistence;

import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationProject;

/**
 * 
 */
public class ProjectPersistenceManager {

	private LocalizationProject project;

	public ProjectPersistenceManager(LocalizationProject project) {
		this.project = project;
	}

	private boolean dataLoaded;

	/**
	 * 
	 */
	public void saveData() {

	}

	/**
	 * 
	 */
	public void loadData() {

	}

	/**
	 * @param file
	 */
	public void loadDataForFile(LocalizationFile file) {

	}

	/**
	 * @return
	 */
	public boolean hasData() {
		return false;
	}

	/**
	 * @return
	 */
	public boolean hasMetadata() {
		return false;
	}

	/**
	 * @return
	 */
	public boolean hasExtraInfo() {
		return false;
	}

	/**
	 * 
	 */
	public void clearAllData() {

	}

	/**
	 * @param file
	 * @return
	 */
	public LocalizationFile clearDataForFile(LocalizationFile file) {
		return null;
	}

	/**
	 * @param _class
	 */
	public void clearClassReferences(Class _class) {

	}

	/**
	 * @param file
	 */
	public void removeOrphanKeysForFile(LocalizationFile file) {

	}

	/**
	 * 
	 */
	public void removeAllOrphanKeys() {

	}

	/**
	 * @return
	 */
	public boolean isAllDataLoaded() {
		return false;
	}

	/**
	 * @param file
	 * @return
	 */
	public boolean isFileDataLoaded(LocalizationFile file) {
		return false;
	}

}
