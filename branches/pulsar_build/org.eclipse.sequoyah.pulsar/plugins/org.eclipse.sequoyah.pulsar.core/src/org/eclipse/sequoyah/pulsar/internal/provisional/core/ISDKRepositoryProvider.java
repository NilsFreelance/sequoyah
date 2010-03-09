/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *
 */

package org.eclipse.sequoyah.pulsar.internal.provisional.core;

import java.util.Collection;

/**
 * An extension that provides SDK repositories
 * 
 */
public interface ISDKRepositoryProvider {
	
	/**
	 * Return a collection of ISDKRepository
	 * 
	 * @return Collection
	 */
	public Collection<ISDKRepository> getRepositories();

}
