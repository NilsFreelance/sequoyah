/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - Package was changed to make able to any
 * 							  other plugin access these constants values.
 ********************************************************************************/
package org.eclipse.tml.common.utilities;

public interface IPropertyConstants {
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String DISPLAY = "display";

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final String DEFAULT_PORT = "5900";
	public static final String DEFAULT_DISPLAY = ":0.0";
	
}

