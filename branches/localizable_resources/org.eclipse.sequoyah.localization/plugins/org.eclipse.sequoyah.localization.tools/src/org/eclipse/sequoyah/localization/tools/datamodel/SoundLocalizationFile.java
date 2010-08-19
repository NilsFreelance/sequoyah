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
package org.eclipse.sequoyah.localization.tools.datamodel;

import org.eclipse.core.resources.IFile;

/**
 * This class represents a real localization file for sounds
 * 
 */
public class SoundLocalizationFile extends LocalizationFile {

	/**
	 * @param file
	 * @param localeInfo
	 */
	public SoundLocalizationFile(IFile file, LocaleInfo localeInfo) {
		super(file, localeInfo);
	}

	/**
	 * @param bean
	 * @return
	 */
	public static LocalizationFile create(LocalizationFileBean bean) {
		LocalizationFile locFile;
		if (bean != null) {
			locFile = new SoundLocalizationFile(bean.getFile(),
					bean.getLocale());
		} else {
			locFile = null;
		}
		return locFile;
	}

}
