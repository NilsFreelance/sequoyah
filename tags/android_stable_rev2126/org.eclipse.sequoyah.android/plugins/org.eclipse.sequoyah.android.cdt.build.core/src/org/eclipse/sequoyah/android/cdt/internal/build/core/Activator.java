package org.eclipse.sequoyah.android.cdt.internal.build.core;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.sequoyah.android.cdt.build.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		// register the NDK service
		context.registerService(INDKService.class.getName(), new NDKService(), null);
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static IEclipsePreferences getPreferenceStore() {
		return new InstanceScope().getNode(PLUGIN_ID);
	}

	public static void log(Exception e) {
		if (e instanceof CoreException)
			plugin.getLog().log(((CoreException)e).getStatus());
		else
			plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e));
	}
	
	public static URL getFile(IPath path) {
		return FileLocator.find(plugin.getBundle(), path, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> clazz) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference ref = context.getServiceReference(clazz.getName());
		try {
			return (ref != null) ? (T)context.getService(ref) : null;
		} finally {
			if(ref != null)
				context.ungetService(ref);
		}
	}

}
