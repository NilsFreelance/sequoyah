/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. and others.  All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute)
 * [244951] Implement listener/event mechanism at device framework
 *
 * Contributors:
 * Fabio Rigo (Eldorado Research Institute) - Bug [287995] - Provide an instance is about to transition event
 ********************************************************************************/

package org.eclipse.tml.framework.device.events;

public interface IInstanceListener
{
    void instanceCreated(InstanceEvent e);
    void instanceDeleted(InstanceEvent e);
    void instanceUpdated(InstanceEvent e);
    void instanceLoaded(InstanceEvent e);
    void instanceUnloaded(InstanceEvent e);
    void instanceTransitioned(InstanceEvent e);
    void instanceAboutToTransition(InstanceEvent e);
}
