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

package org.eclipse.mtj.internal.pulsar.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvUIImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class InstallersLabelProvider extends ColumnLabelProvider {
	
	private Device device;
	private Map<Object, Image> imageCache;
	
	public InstallersLabelProvider(Device device) {
		this.device = device;
	}

	private Image getRepositoryImage(ISDKRepository repository) {
		ensureImageCache();
		if (!imageCache.containsKey(repository))
			imageCache.put(repository, repository.getImageDescriptor().createImage(device));
		
		return imageCache.get(repository);
	}

	private void ensureImageCache() {
		if (imageCache == null)
			imageCache = new HashMap<Object, Image>();
	}
	
	private Image getProvUIImage(String key) {
		ensureImageCache();
		if (!imageCache.containsKey(key)) {
			ImageDescriptor imageDescriptor = 
				ProvUIActivator.getDefault().getImageRegistry().getDescriptor(key);
			imageCache.put(key, imageDescriptor.createImage(device));
		}
		
		return imageCache.get(key);
	}

	@Override
	public Image getImage(Object element) {
		Object object = ((TreeNode) element).getValue();
		if (object instanceof ISDKRepository) {
			return getRepositoryImage((ISDKRepository) object);
		}
		else if (object instanceof ISDK) {
			return getProvUIImage(ProvUIImages.IMG_IU);
		}
		else if (object instanceof String) { // category
			return getProvUIImage(ProvUIImages.IMG_CATEGORY);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		Object object = ((TreeNode) element).getValue();
		if (object instanceof ISDK) {
			return ((ISDK) object).getName();
		}
		else if (object instanceof ISDKRepository) {
			return ((ISDKRepository) object).getName();
		}
		else if (object instanceof String) {
			return (String) object;
		}
		
		return null;
	}

	public void dispose() {
		for (Image image : imageCache.values()) {
			image.dispose();
		}
		super.dispose();
	}
}
