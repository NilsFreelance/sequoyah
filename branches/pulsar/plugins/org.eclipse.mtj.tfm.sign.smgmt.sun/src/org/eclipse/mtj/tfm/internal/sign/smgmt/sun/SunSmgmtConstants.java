/**
 * Copyright (c) 2006,2009 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation         - initial API and implementation
 *     Diego Sandin (Motorola) - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.mtj.tfm.internal.sign.smgmt.sun;

/**
 * @since 1.0
 */
public interface SunSmgmtConstants {

	public static String SECURITY_TOOL_LOCATION = "SecurityToolLocationPreference"; //$NON-NLS-1$

	// keytool primary command options
	public static final String GENERATE_KEY 	   = "-genkey"; //$NON-NLS-1$
	public static final String DELETE_KEY 		   = "-delete"; //$NON-NLS-1$
	public static final String IMPORT_CERT 		   = "-import"; //$NON-NLS-1$
	public static final String GENERATE_CSR 	   = "-export"; //$NON-NLS-1$
	public static final String LIST 			   = "-list"; //$NON-NLS-1$
	public static final String CHANGE_STORE_PASSWD = "-storepasswd"; //$NON-NLS-1$
	
	// keytool secondary command options
	public static final String ALIAS 			= "-alias"; //$NON-NLS-1$
	public static final String DNAME 			= "-dname"; //$NON-NLS-1$
	public static final String KEYPASS 			= "-keypass"; //$NON-NLS-1$
	public static final String VALIDITY 		= "-validity"; //$NON-NLS-1$
	public static final String STORETYPE 		= "-storetype"; //$NON-NLS-1$
	public static final String KEYSTORE 		= "-keystore"; //$NON-NLS-1$
	public static final String STOREPASS 		= "-storepass"; //$NON-NLS-1$
	public static final String PROVIDER 		= "-provider"; //$NON-NLS-1$
	public static final String FILE 			= "-file"; //$NON-NLS-1$
	public static final String NEWSTOREPASS 	= "-new"; //$NON-NLS-1$
	public static final String KEYALG 			= "-keyalg"; //$NON-NLS-1$
	public static final String SIGALG 			= "-sigalg"; //$NON-NLS-1$
	public static final String KEYSIZE 			= "-keysize"; //$NON-NLS-1$
	public static final String NOPROMPT 		= "-noprompt"; //$NON-NLS-1$

    // Dname prefix options

}
