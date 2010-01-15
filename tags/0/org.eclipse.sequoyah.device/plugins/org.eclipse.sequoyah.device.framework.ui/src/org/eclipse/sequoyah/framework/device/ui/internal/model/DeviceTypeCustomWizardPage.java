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
 *******************************************************************************/
package org.eclipse.tml.framework.device.ui.internal.model;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.tml.framework.device.ui.model.IDeviceTypeCustomWizardPage;

public class DeviceTypeCustomWizardPage implements IDeviceTypeCustomWizardPage {
	private String id;
	private IWizardPage pageClass;
	private IRunnableWithProgress operationClass;

	public DeviceTypeCustomWizardPage(String id, IWizardPage pageClass,
			IRunnableWithProgress operationClass) {
		super();
		this.id = id;
		this.pageClass = pageClass;
		this.operationClass = operationClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IWizardPage getPageClass() {
		return pageClass;
	}

	public void setPageClass(IWizardPage pageClass) {
		this.pageClass = pageClass;
	}

	public IRunnableWithProgress getOperationClass() {
		return operationClass;
	}

	public void setOperationClass(IRunnableWithProgress operationClass) {
		this.operationClass = operationClass;
	}
}
