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

package org.eclipse.tml.framework.device.internal.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IStatus;

public class MobileStatus implements IStatus {
	private eStatus status;
	private IInstance parent;

	public MobileStatus(eStatus status){
		this.status = status;
	}
	
	public ImageDescriptor getImage() {
		if (status.equals(eStatus.STARTED)) {
			return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_START);
		} else if (status.equals(eStatus.REFRESHING)) {
			return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_REFRESH);
		} else if (status.equals(eStatus.STOPPED)) {
			return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_STOP);
		} else {
			return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_INACTIVE);
		}
	}
	
	public eStatus getStatus() {
		return status;
	}
	
	public void setStatus(eStatus status) {
		this.status = status;
	}
	
	public String toString(){
		return "[Status=" + (status.name()==null?"":status.name())+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	public IInstance getParent() {
		return parent;
	}

	public void setParent(IInstance instance) {
		this.parent = instance;
	}
	
	public Object clone(){
		return new MobileStatus(this.status);
	}
}
