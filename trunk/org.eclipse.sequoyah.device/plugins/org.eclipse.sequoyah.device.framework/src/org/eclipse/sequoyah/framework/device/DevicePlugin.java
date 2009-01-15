/**
 * * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [247333] - New Icons for Start and Stop
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/
package org.eclipse.tml.framework.device;

import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.status.StatusManager;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DevicePlugin extends BasePlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.tml.framework.device"; //$NON-NLS-1$
	public static final String DEVICE_TYPES_EXTENSION_POINT_ID = "org.eclipse.tml.deviceTypes"; //$NON-NLS-1$	
	
	@Deprecated
	public static final String DEVICE_ID = "org.eclipse.tml.device"; //$NON-NLS-1$ 
	
	public static final String SERVICE_ID = "org.eclipse.tml.service"; //$NON-NLS-1$
	public static final String STATUS_ID = "org.eclipse.tml.status"; //$NON-NLS-1$
	public static final String SERVICE_DEF_ID = "org.eclipse.tml.serviceDefinition"; //$NON-NLS-1$
	public static final String ICON_DEVICE= "ICON_DEVICE"; //$NON-NLS-1$
	public static final String ICON_SERVICE = "ICON_SERVICE"; //$NON-NLS-1$
	public static final String ICON_MOVING = "ICON_MOVING"; //$NON-NLS-1$
	public static final String ICON_BOOK = "ICON_BOOK"; //$NON-NLS-1$
	public static final String ICON_GAMEBOARD = "ICON_GAMEBOARD"; //$NON-NLS-1$
	public static final String ICON_NEW_BOOK = "ICON_NEW_BOOK"; //$NON-NLS-1$
	public static final String ICON_REMOVE = "ICON_REMOVE"; //$NON-NLS-1$
	public static final String ICON_START = "ICON_START"; //$NON-NLS-1$
	public static final String ICON_STOP = "ICON_STOP"; //$NON-NLS-1$
	public static final String ICON_REFRESH = "ICON_REFRESH"; //$NON-NLS-1$
	public static final String ICON_INACTIVE = "ICON_INACTIVE"; //$NON-NLS-1$
	public static final String ICON_PROPERTY = "ICON_PROPERTY"; //$NON-NLS-1$
	public static final String TML_STATUS_UNAVAILABLE = "UNAVAILABLE"; //$NON-NLS-1$
	public static final String TML_STATUS_IDLE="IDLE"; //$NON-NLS-1$
	public static final String TML_STATUS_OFF="OFF"; //$NON-NLS-1$
	public static final String TML_STATUS_INACTIVE ="INACTIVE"; //$NON-NLS-1$

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
		putImageInRegistry(ICON_START, path+"full/obj16/start.png"); //$NON-NLS-1$
		putImageInRegistry(ICON_STOP, path+"full/obj16/stop.png"); //$NON-NLS-1$
		putImageInRegistry(ICON_REFRESH, path+"full/obj16/refresh.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_INACTIVE, path+"full/obj16/inactive.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_MOVING, path+"movingBox.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_BOOK, path+"book.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_GAMEBOARD, path+"gameboard.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_NEW_BOOK, path+"newbook.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_REMOVE, path+"remove.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_PROPERTY, path+"property.gif"); //$NON-NLS-1$
	}

	public void earlyStartup() {
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.HOST, IPropertyConstants.DEFAULT_HOST);
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.DISPLAY, IPropertyConstants.DEFAULT_DISPLAY);
		DEFAULT_PROPERTIES.setProperty(IPropertyConstants.PORT, IPropertyConstants.DEFAULT_PORT);
		StatusManager.getInstance().listStatus();
	}
	/*
	 * get image with specified iconPath relative to the bundleName.
	 * 
	 * @param bundleName - plugin name @param iconPath - icon path relative to
	 * the plugin bundle name specified @return image
	 */
	public Image getImageFromRegistry(String bundleName, String iconPath) {
		String key = bundleName + ":" + iconPath; //$NON-NLS-1$
		Image image = getImageRegistry().get(key);
		if (image == null) {
			ImageDescriptor descriptor = null;
			Bundle bundle = Platform.getBundle(bundleName);
			URL url = bundle.getResource(iconPath);
			if (url != null) {
				descriptor = ImageDescriptor.createFromURL(url);
			}

			if (descriptor == null) {
				descriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			getImageRegistry().put(key, descriptor);
			image = getImageRegistry().get(key);
		}
		return image;
	}

}
