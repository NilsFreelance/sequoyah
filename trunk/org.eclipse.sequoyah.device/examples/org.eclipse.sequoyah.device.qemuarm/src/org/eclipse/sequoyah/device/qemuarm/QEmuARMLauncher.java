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
 * 							  other plugin access these constants values
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 ********************************************************************************/

package org.eclipse.tml.device.qemuarm;

import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.framework.device.model.IConnection;
import org.eclipse.tml.framework.device.model.IDeviceLauncher;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.service.start.launcher.DefaultConnection;

public class QEmuARMLauncher implements IDeviceLauncher {
	public static final String SLASH = "\\"; //$NON-NLS-1$
	public IConnection connection;
	private int pid;
	
	public QEmuARMLauncher(IInstance instance){
		// get instance and generates connection;
		pid=0;
		connection = new DefaultConnection();
		connection.setHost(instance.getProperties().getProperty(IPropertyConstants.HOST));
		connection.setDisplay(instance.getProperties().getProperty(IPropertyConstants.DISPLAY));
		connection.setPort(Integer.parseInt(instance.getProperties().getProperty(IPropertyConstants.PORT)));
	}
	
	public int getPID(){
		return pid;
	}
	
	public void setPID(int pid){
		this.pid=pid;
	}
	
	public String getFileId() {
		return PluginUtils.getPluginInstallationPath(QEmuARMPlugin.getDefault())
		.getAbsolutePath().concat(SLASH).concat(QEmuARMPlugin.EMULATOR_NAME).concat(SLASH).concat(QEmuARMPlugin.EMULATOR_FILE_ID);
	}
	
	public IConnection getConnection() {
		return connection;
	}

	public String getLocation() {
		return PluginUtils.getPluginInstallationPath(QEmuARMPlugin.getDefault())
		.getAbsolutePath().concat(SLASH).concat(QEmuARMPlugin.EMULATOR_NAME).concat(SLASH).concat(QEmuARMPlugin.EMULATOR_BIN);

	}

	public String getToolArguments() {
		return QEmuARMPlugin.EMULATOR_PARAMS+connection.getStringHost();
	}

	public String getWorkingDirectory() {
		return PluginUtils
		.getPluginInstallationPath(QEmuARMPlugin.getDefault())
		.getAbsolutePath().concat(SLASH).concat(QEmuARMPlugin.EMULATOR_NAME).concat(SLASH);
	}

}
