/*******************************************************************************
 * Copyright (c) 2008 MontaVista Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Yu-Fen Kuo (MontaVista) - initial API and implementation
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [271695] - Support to non-persistent instances of devices
 * Mauren Brenner (Eldorado) - [281377] Support device types whose instances cannot be created by user
 *******************************************************************************/

package org.eclipse.tml.framework.device.model;

import java.util.List;
import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

public interface IDeviceType {

	public String getId();

	public void setId(String id);

	public String getLabel();

	public void setLabel(String label);

	public boolean isAbstract();

	public boolean isPersistent();
	
	public boolean supportsUserInstances();
	
	public void setAbstract(boolean isAbstract);

	public String getSuperClass();

	public void setSuperClass(String superClass);

	public Properties getProperties();

	public void setProperties(Properties properties);

	public void addProperty(String name, String value);

	public String getBundleName();

	public void setBundleName(String bundleName);

	public Image getImage();

	public IDeviceHandler getHandler();

	public void setHandler(IDeviceHandler handler);	
	
	public List<IService> getServices();

	public void setServices(List<IService> services);
}
