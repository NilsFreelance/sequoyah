/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial implementation
 */
package org.eclipse.sequoyah.tfm.sign.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.sequoyah.tfm.sign.internal.core.extension.SignExtensionManager;
import org.eclipse.sequoyah.tfm.sign.internal.core.extension.permission.PermissionGroupProviderRegistry;
import org.eclipse.sequoyah.tfm.sign.core.extension.IExtensionManager;
import org.eclipse.sequoyah.tfm.sign.core.extension.permission.IPermissionGroupProviderRegistry;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SignCore extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.sequoyah.tfm.sign.core";

    // The shared instance
    private static SignCore plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static SignCore getDefault() {
        return plugin;
    }

    /**
     * @return
     */
    public static IExtensionManager getSignExtensionManager() {
        return new SignExtensionManager();
    }

    /**
     * @return
     */
    public static IPermissionGroupProviderRegistry getPermissionGroupProviderRegistry() {
        return PermissionGroupProviderRegistry.getInstance();
    }

    /**
     * The constructor
     */
    public SignCore() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

}
