/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Matheus Tait Lima (Eldorado) - Adapting to work with automatic translation
 * Marcel Gorri (Eldorado) - Add new attribute - branding
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.classes;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sequoyah.localization.tools.datamodel.node.TranslationResult;
import org.eclipse.swt.graphics.Image;

public abstract class ITranslator {

	/*
	 * The name of the translator
	 */
	private String name;

	/*
	 * An image with the translator branding, if any
	 */
	private Image brandingImg;

	/**
	 * Translate a string
	 * 
	 * @param word
	 *            the string to be translated
	 * @param fromLanguage
	 *            original language
	 * @param toLanguage
	 *            target language
	 * @return a TranslationResult object with the translation result
	 * @throws Exception
	 */
	public abstract TranslationResult translate(String word,
			String fromLanguage, String toLanguage) throws Exception;

	/**
	 * Translate a list of strings
	 * 
	 * @param words
	 *            the strings to be translated
	 * @param fromLanguage
	 *            original language
	 * @param toLanguage
	 *            target language
	 * @return a list of TranslationResult objects with the translation results
	 * @throws Exception
	 */
	public abstract List<TranslationResult> translateAll(List<String> words,
			String fromLanguage, String toLanguage, IProgressMonitor monitor)
			throws Exception;

	/**
	 * Get the name of the translator
	 * 
	 * @return the name of the translator
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the translator
	 * 
	 * @param name
	 *            the name of the translator
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the branding image
	 * 
	 * @return the branding image
	 */
	public Image getBrandingImg() {
		return brandingImg;
	}

	/**
	 * Set the branding image
	 * 
	 * @param brandingImg
	 *            the branding image
	 */
	public void setBrandingImg(Image brandingImg) {
		this.brandingImg = brandingImg;
	}

	/**
	 * Translate a single string from a given language to several other
	 * languages.
	 * 
	 * @param word
	 *            the string to be translated
	 * @param fromLanguage
	 *            the original language of the word
	 * @param toLanguages
	 *            list of languages for the string to be translated for
	 */
	public abstract List<TranslationResult> translate(String word,
			String fromLanguage, List<String> toLanguages) throws Exception;

	/**
	 * Translates a list of strings from a list of given languages to other
	 * given languages (given by a list, or course), using google Ajax API's for
	 * that.
	 * 
	 * This comment feels like the
	 * "Three Swatch watch switching witches watched switched Swatch watch witches switch"
	 * , but I'll let it here anyway.
	 * 
	 * @param words
	 *            the strings to be translated
	 * @param fromLanguage
	 *            the list of original languages
	 * @param toLanguages
	 *            list of languages for the strings to be translated for
	 */
	public abstract List<TranslationResult> translateAll(List<String> words,
			List<String> fromLanguage, List<String> toLanguage,
			IProgressMonitor monitor) throws Exception;

}
