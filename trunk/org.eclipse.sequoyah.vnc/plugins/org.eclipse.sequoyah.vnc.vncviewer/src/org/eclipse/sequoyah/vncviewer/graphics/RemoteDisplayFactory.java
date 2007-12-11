/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.vncviewer.config.VNCConfiguration;
import org.eclipse.tml.vncviewer.graphics.swt.SWTRemoteDisplay;


public class RemoteDisplayFactory {

	
	/**
	 * This factory returns an IRemoteDisplay instance.
	 * 
	 * @param display the String that represents the desired instance. 
	 * @param parent the Composite to be used as the GUI components parent.
	 * @return the instantiated IRemoteDisplay
	 */
	public static IRemoteDisplay getDisplay(String display, Composite parent){
	
		
		//Properties config = VNCConfiguration.getConfigurationProperties("C:/temp/vnc_viewer.conf");
		Properties config = VNCConfiguration.getConfigurationProperties();
	
		if (display.equals("SWTDisplay")){
			return new SWTRemoteDisplay(parent, config);
		}
	
		return null;
	}
	
	
}
