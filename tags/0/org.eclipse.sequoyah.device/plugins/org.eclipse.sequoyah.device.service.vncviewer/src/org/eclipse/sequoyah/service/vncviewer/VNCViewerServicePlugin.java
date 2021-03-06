/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Franco (Motorola)
 * 
 * Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [247333] - New Icons for Start and Stop
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer;

import org.eclipse.tml.common.utilities.BasePlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VNCViewerServicePlugin extends BasePlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.tml.service.vncviewer"; //$NON-NLS-1$
	public static final String ICON_SERVICE_START = "ICON_SERVICE_START"; //$NON-NLS-1$

	// The shared instance
	private static VNCViewerServicePlugin plugin;
	
	/**
	 * The constructor
	 */
	public VNCViewerServicePlugin() {
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#stop(org.osgi.framework.BundleContext)
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
	public static VNCViewerServicePlugin getDefault() {
		return plugin;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#initializeImageRegistry()
	 */
	@Override
	protected void initializeImageRegistry() {
		String path = getIconPath();
		putImageInRegistry(ICON_SERVICE_START, path+"full/obj16/start.png"); //$NON-NLS-1$	
	}

}
