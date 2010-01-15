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
package org.eclipse.tml.framework.device.model;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

public interface IInstanceRegistry {
	public List<IInstance> getInstances();
	public void setInstances(List<IInstance> instances);
	public void addInstance(IInstance instance);
	public void removeInstance(IInstance instance);
	public ImageDescriptor getImage();
	public void clear();
}
