/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.android;

import java.awt.Dimension;
import java.util.HashMap;

import org.eclipse.tml.localization.android.i18n.Messages;
import org.eclipse.tml.localization.tools.datamodel.LocaleAttribute;

public class AndroidLocaleAttribute extends LocaleAttribute {

	public final static int INDEX_COUNTRY_CODE = 0;

	public final static int INDEX_NETWORK_CODE = 1;

	public final static int INDEX_LANGUAGE = 2;

	public final static int INDEX_REGION = 3;

	public final static int INDEX_SCREEN_ORIENTATION = 4;

	public final static int INDEX_PIXEL_DENSITY = 5;

	public final static int INDEX_TOUCH_TYPE = 6;

	public final static int INDEX_KEYBOARD_STATE = 7;

	public final static int INDEX_TEXT_INPUT_METHOD = 8;

	public final static int INDEX_NAVIGATION_METHOD = 9;

	public final static int INDEX_SCREEN_DIMENSION = 10;

	public final static int INDEX_COUNT = 11;

	private int androidType;

	private String stringValue = "";

	protected boolean isSet = false;

	/***
	 * Creates a new AndroidLocaleAttribute
	 * 
	 * @param value
	 * @param androidType
	 */
	public AndroidLocaleAttribute(Object value, int androidType) {
		super("", LocaleAttribute.STRING_TYPE, 0, 0, null, "", "");
		this.androidType = androidType;
		setAndroidValue(value);
		isSet = false;
	}

	/***
	 * Creates an Android Language Attribute based on a android resource
	 * qualifier.
	 * 
	 * @param qualifier
	 */
	public AndroidLocaleAttribute(String qualifier, int type) {
		super("", LocaleAttribute.STRING_TYPE, 0, 0, null, "", "");

		androidType = type;
		Object value = getValueAndTypeFromQualifier(qualifier);
		setAndroidValue(value);

	}

	/***
	 * Gets the Android type.
	 * 
	 * @return
	 */
	public int getAndroidType() {
		return androidType;
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getCountryCodeFolder(String value) {
		return "mcc" + value;
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getLanguageFolder(String value) {
		return value.toLowerCase();
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getNetworkCodeFolder(String value) {
		return "mnc" + value;
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getPixelFolder(String value) {
		return value + "dpi";
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getRegionCodeFolder(String value) {
		return "r" + value.toUpperCase();
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	public String getStringValue() {
		return stringValue;
	}

	/***
	 * - Parse the qualifier in order to retrieve necessary information to be
	 * used for other methods of this class.
	 * 
	 * - Sets the attribute type according to the qualifier type.
	 * 
	 * @param qualifier
	 * @return
	 */
	private Object getValueAndTypeFromQualifier(String strValue) {
		Object result = null;

		switch (androidType) {
		case INDEX_COUNTRY_CODE:
			result = strValue.substring(3);
			break;

		case INDEX_NETWORK_CODE:
			result = strValue.substring(3);
			break;

		case INDEX_LANGUAGE:
			result = strValue;
			break;

		case INDEX_REGION:
			result = strValue.substring(1);
			break;

		case INDEX_SCREEN_ORIENTATION:
			result = strValue;
			break;

		case INDEX_PIXEL_DENSITY:
			int index = strValue.indexOf("dpi");
			result = strValue.substring(0, index);
			break;

		case INDEX_TOUCH_TYPE:
			result = strValue;
			break;

		case INDEX_KEYBOARD_STATE:
			result = strValue;
			break;

		default:
			break;

		case INDEX_TEXT_INPUT_METHOD:
			result = strValue;
			break;

		case INDEX_NAVIGATION_METHOD:
			result = strValue;
			break;

		case INDEX_SCREEN_DIMENSION:
			String[] numbers = strValue.split("x");
			int x = Integer.parseInt(numbers[0]);
			int y = Integer.parseInt(numbers[1]);
			result = new Dimension(x, y);
			break;
		}

		return result;
	}

	/***
	 * Checks if this attribute is set.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public boolean isSet() {
		return isSet;
	}

	/***
	 * Sets the values of this attribute.
	 * 
	 * The object received as param will be parsed according to the android type
	 * of the attribute.
	 * 
	 * So, this method WILL fail if you pass an attribute type that is not the
	 * one expected according to the type of the attribute. For instance, if the
	 * atribute type is Dimension, so the object of the parameter MUST be a
	 * Dimension.
	 * 
	 * @param value
	 */
	public void setAndroidValue(Object value) {
		switch (androidType) {
		case INDEX_COUNTRY_CODE:
			setCountryCodeNode(value);
			break;

		case INDEX_NETWORK_CODE:
			setNetworkCodeNode(value);
			break;

		case INDEX_LANGUAGE:
			setLanguageNode(value);
			break;

		case INDEX_REGION:
			setRegionNode(value);
			break;

		case INDEX_SCREEN_ORIENTATION:
			setOrientationNode(value);
			break;

		case INDEX_PIXEL_DENSITY:
			setPixelNode(value);
			break;

		case INDEX_TOUCH_TYPE:
			setTouchNode(value);
			break;

		case INDEX_KEYBOARD_STATE:
			setKeyboardNode(value);
			break;

		case INDEX_TEXT_INPUT_METHOD:
			setTextInputNode(value);
			break;

		case INDEX_NAVIGATION_METHOD:
			setNavigationNode(value);
			break;

		case INDEX_SCREEN_DIMENSION:
			setDimensionNode(value);
			break;

		default:
			throw new IllegalArgumentException(Messages.Unknown_Andr_Type);

		}
		isSet = true;
	}

	/***
	 * Sets this attribute.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public void setAttribute() {
		isSet = true;
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setCountryCodeNode(Object value) {
		displayName = "Country Code";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 3;
		maximumSize = 3;
		allowedValues = null;
		setIntValue(value);
		folderValue = getCountryCodeFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setDimensionNode(Object value) {
		if (!(value instanceof Dimension)) {
			throw new IllegalArgumentException(Messages.Invalid_Andr_Value);
		}
		displayName = "Screen Dimension";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 0;
		maximumSize = 0;
		allowedValues = null;
		double y = ((Dimension) value).getWidth();
		double x = ((Dimension) value).getHeight();
		displayValue = (int) x + "x" + (int) y;
		folderValue = displayValue;
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	public void setIntValue(Object value) {

		Integer intValue = -1;
		if (value instanceof Integer) {
			intValue = (Integer) value;
		} else if (value instanceof String) {
			intValue = Integer.parseInt((String) value);
		} else {
			throw new IllegalArgumentException(Messages.Invalid_Andr_Value);
		}

		if (fixedSize > 0) {
			if (intValue.toString().length() != fixedSize) {
				throw new IllegalArgumentException(
						Messages.Invalid_Andr_Value_Size + fixedSize);
			}
		}

		if (maximumSize > 0) {
			if (intValue.toString().length() > maximumSize) {
				throw new IllegalArgumentException(
						Messages.Invalid_Andr_Value_Size + maximumSize);
			}
		}

		displayValue = intValue.toString();
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setKeyboardNode(Object value) {
		displayName = "Keyboard State";
		type = LocaleAttribute.FIXED_TEXT_TYPE;
		fixedSize = 0;
		maximumSize = 0;
		allowedValues = new HashMap<String, String>();
		setValuesBasedOnDisplayValue((String) value);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setLanguageNode(Object value) {
		displayName = "Language";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 2;
		maximumSize = 2;
		allowedValues = null;
		setStringValue(value);
		folderValue = getLanguageFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setNavigationNode(Object value) {
		displayName = "Navigation Method";
		type = LocaleAttribute.FIXED_TEXT_TYPE;
		fixedSize = 0;
		allowedValues = new HashMap<String, String>();
		setValuesBasedOnDisplayValue((String) value);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setNetworkCodeNode(Object value) {
		displayName = "Network Code";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 0;
		maximumSize = 3;
		allowedValues = null;
		setIntValue(value);
		folderValue = getNetworkCodeFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setOrientationNode(Object value) {
		displayName = "Screen Orientation";
		type = LocaleAttribute.FIXED_TEXT_TYPE;
		fixedSize = 0;
		maximumSize = 0;
		allowedValues = new HashMap<String, String>();
		setValuesBasedOnDisplayValue((String) value);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setPixelNode(Object value) {
		displayName = "Pixel Density";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 0;
		maximumSize = 0;
		allowedValues = null;
		setIntValue(value);
		folderValue = getPixelFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setRegionNode(Object value) {
		displayName = "Region";
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 2;
		maximumSize = 2;
		allowedValues = null;
		setStringValue(value);
		folderValue = getRegionCodeFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setStringValue(Object value) {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException(Messages.Invalid_Andr_Value);
		}

		if (type == FIXED_TEXT_TYPE) {
			setValuesBasedOnDisplayValue((String) value);
		} else {
			if (fixedSize > 0) {
				if (((String) value).length() != fixedSize) {
					throw new IllegalArgumentException(
							Messages.Invalid_Andr_Value_Size + fixedSize);
				}
			}

			if (maximumSize > 0) {
				if (((String) value).length() > maximumSize) {
					throw new IllegalArgumentException(
							Messages.Invalid_Andr_Value_Size + maximumSize);
				}
			}

			displayValue = (String) value;
		}

	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setTextInputNode(Object value) {
		displayName = "Text Input Method";
		type = LocaleAttribute.FIXED_TEXT_TYPE;
		fixedSize = 0;
		setValuesBasedOnDisplayValue((String) value);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setTouchNode(Object value) {
		displayName = "Touch Screen Type";
		type = LocaleAttribute.FIXED_TEXT_TYPE;
		fixedSize = 0;
		maximumSize = 0;
		setValuesBasedOnDisplayValue((String) value);
	}

	/***
	 * Unsets this attribute.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public void unsetAttribute() {
		isSet = false;
	}

}
