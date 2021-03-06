/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - Bug 323036 - Add support to other Localizable Resources
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android.datamodel;

import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.SoundLocalizationFile;

/**
 * This class represents a real Android localization file in the project and
 * contains information about XML file saved
 * 
 */
public class AndroidSoundLocalizationFile extends SoundLocalizationFile {

	/*
	 * Static code to add file type to factory's hashmap
	 */
	static {
		LocalizationFileFactory.getInstance().addFileType(
				AndroidSoundLocalizationFile.class.getName(),
				AndroidSoundLocalizationFile.class);
	}

	/**
	 * Default constructor.
	 */
	public AndroidSoundLocalizationFile() {
	}

	/**
	 * 
	 */
	public AndroidSoundLocalizationFile(LocalizationFileBean bean) {
		super(bean);
	}

	/**
	 * ImageLocalizationFile knows how to create itself.
	 * 
	 * @param bean
	 *            Bean containing all information necessary to create any type
	 *            of LocalizationFile.
	 * @return LocalizationFile created (if the parameter received is not null).
	 */
	public static LocalizationFile create(LocalizationFileBean bean) {
		LocalizationFile locFile;
		if (bean != null) {
			locFile = new AndroidSoundLocalizationFile(bean);
		} else {
			locFile = null;
		}
		return locFile;
	}
}
