/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marcel Augusto Gorri (Eldorado) - Bug [323036] - Add support to other localizable resources
 *  
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;

/**
 * @author wmg040
 *
 */
public class LocalizationFileBean {

	/*
	 * Specifies if the bean relates to a certain type of LocalizationFile
	 */
	private int type;
	
	/*
	 * A reference to the file being represented
	 */
	private IFile file;

	/*
	 * The information about the locale represented by the localization file
	 */
	private LocaleInfo locale;

	/*
	 * The list of StringArrays which are part of the file
	 */
	private List<StringArray> stringArrays;

	/*
	 * The list of StringNodes which are part of the file
	 */
	private List<StringNode> stringNodes;

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the file
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * @return the locale
	 */
	public LocaleInfo getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(LocaleInfo locale) {
		this.locale = locale;
	}	
	
	/**
	 * @return the stringArrays
	 */
	public List<StringArray> getStringArrays() {
		return stringArrays;
	}

	/**
	 * @param stringArrays
	 *            the stringArrays to set
	 */
	public void setStringArrays(List<StringArray> stringArrays) {
		this.stringArrays = stringArrays;
	}

	/**
	 * @return the stringNodes
	 */
	public List<StringNode> getStringNodes() {
		return stringNodes;
	}

	/**
	 * @param stringNodes
	 *            the stringNodes to set
	 */
	public void setStringNodes(List<StringNode> stringNodes) {
		this.stringNodes = stringNodes;
	}

	/**
	 * Default constructor
	 * 
	 */
	public LocalizationFileBean() {
	}

	/**
	 * Constructor receiving parameters
	 * 
	 * @param stringArrays
	 * @param stringNodes
	 * @param file
	 * @param locale
	 */
	public LocalizationFileBean(int type, List<StringArray> stringArrays,
			List<StringNode> stringNodes, IFile file, LocaleInfo locale) {
		this.type = type;
		this.stringArrays = stringArrays;
		this.stringNodes = stringNodes;
		this.file = file;
		this.locale = locale;
	}
}
