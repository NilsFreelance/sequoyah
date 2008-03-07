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
package org.eclipse.tml.framework.device;

import java.util.Properties;

import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.status.StatusManager;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DevicePlugin extends BasePlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.tml.framework.device";
	public static final String DEVICE_ID = "org.eclipse.tml.device";
	public static final String SERVICE_ID = "org.eclipse.tml.service";
	public static final String STATUS_ID = "org.eclipse.tml.status";
	public static final String SERVICE_DEF_ID = "org.eclipse.tml.serviceDefinition";
	public static final String ICON_DEVICE= "ICON_DEVICE";
	public static final String ICON_SERVICE = "ICON_SERVICE";
	public static final String ICON_MOVING = "ICON_MOVING";
	public static final String ICON_BOOK = "ICON_BOOK";
	public static final String ICON_GAMEBOARD = "ICON_GAMEBOARD";
	public static final String ICON_NEW_BOOK = "ICON_NEW_BOOK";
	public static final String ICON_REMOVE = "ICON_REMOVE";
	public static final String ICON_START = "ICON_START";
	public static final String ICON_STOP = "ICON_STOP";
	public static final String ICON_REFRESH = "ICON_REFRESH";
	public static final String ICON_INACTIVE = "ICON_INACTIVE";
	public static final String ICON_PROPERTY = "ICON_PROPERTY";
	public static final String TML_STATUS_UNAVAILABLE = "UNAVAILABLE";
	public static final String TML_STATUS_IDLE="IDLE";
	public static final String TML_STATUS_OFF="OFF";
	public static final String TML_STATUS_INACTIVE ="INACTIVE";
	public static final Properties DEFAULT_PROPERTIES = new Properties();	

	// The shared instance
	private static DevicePlugin plugin;
	
	/**
	 * The constructor
	 */
	public DevicePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DevicePlugin getDefault() {
		return plugin;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#initializeImageRegistry()
	 */
	@Override
	protected void initializeImageRegistry() {
		String path = getIconPath();
		putImageInRegistry(ICON_DEVICE, path+"full/obj16/device.gif"); //$NON-NLS-1$	
		putImageInRegistry(ICON_SERVICE, path+"full/obj16/service.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_START, path+"full/obj16/start.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_STOP, path+"full/obj16/stop.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_REFRESH, path+"full/obj16/refresh.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_INACTIVE, path+"full/obj16/inactive.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_MOVING, path+"movingBox.gif");
		putImageInRegistry(ICON_BOOK, path+"book.gif");
		putImageInRegistry(ICON_GAMEBOARD, path+"gameboard.gif");
		putImageInRegistry(ICON_NEW_BOOK, path+"newbook.gif");
		putImageInRegistry(ICON_REMOVE, path+"remove.gif");
		putImageInRegistry(ICON_PROPERTY, path+"property.gif");
	}

	public void earlyStartup() {
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.HOST, IPropertyConstants.DEFAULT_HOST);
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.DISPLAY, IPropertyConstants.DEFAULT_DISPLAY);
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.PORT, IPropertyConstants.DEFAULT_PORT);
		StatusManager.getInstance().listStatus();
	}


}
