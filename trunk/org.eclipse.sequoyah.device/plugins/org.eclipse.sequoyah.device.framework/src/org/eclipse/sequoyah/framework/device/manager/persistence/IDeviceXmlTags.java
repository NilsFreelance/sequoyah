/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute) 
 * [245114] Enhance persistence policies
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.framework.device.manager.persistence;

interface IDeviceXmlTags
{
    String TML_DEVICE_DATAFILE = "tml_devices.xml"; //$NON-NLS-1$
    
    String TML_XML_DEVICES = "devices"; //$NON-NLS-1$
    String TML_XML_DEVICE = "deviceType"; //$NON-NLS-1$
    String TML_XML_DEVICE_ID = "id"; //$NON-NLS-1$
    String TML_XML_DEVICE_PLUGIN = "plugin"; //$NON-NLS-1$
    String TML_XML_INSTANCES = "instances"; //$NON-NLS-1$
    String TML_XML_INSTANCE = "instance"; //$NON-NLS-1$
    String TML_XML_INSTANCE_NAME = "name"; //$NON-NLS-1$
    String TML_XML_INSTANCE_DEVICE_ID = "deviceType_id"; //$NON-NLS-1$
    String TML_XML_ROOT = "tml"; //$NON-NLS-1$
}
