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
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.sequoyah.device.service.start;

import org.eclipse.osgi.util.NLS;

/**
 * Resources for externalized Strings of the TmL Emulator Core.
 */
public class StartServiceResources extends NLS {
	

	private static String BUNDLE_NAME = "org.eclipse.sequoyah.device.service.start.StartServiceResources";//$NON-NLS-1$

	public static String TML_Start_Service_Plugin_Name;
	public static String TML_Error;
	public static String TML_Resource_Not_Available;
	public static String TML_Start_Service;
	public static String TML_Start_Service_Update;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, StartServiceResources.class);
	}

}
