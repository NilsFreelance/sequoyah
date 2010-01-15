/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute) 
 * [245114] Enhance persistence policies
 ********************************************************************************/

package org.eclipse.tml.framework.device.manager.persistence;

public class TmLDevice
{
    private String id;
    private String plugin;

    public TmLDevice(String id, String plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    public String getId() {
        return id;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }
}
