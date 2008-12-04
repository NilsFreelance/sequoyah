/*******************************************************************************
 * Copyright (c) 2008 MontaVista Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yu-Fen Kuo (MontaVista) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tml.framework.device.ui.wizard;

import java.util.Properties;

/*
 * Interface used by custom new device instance wizard pages. If the custom
 * wizard page implements this interface, during wizard perform finish the
 * properties will be saved in the newly created instance.
 */
public interface IInstanceProperties {
	public Properties getProperties();
}
