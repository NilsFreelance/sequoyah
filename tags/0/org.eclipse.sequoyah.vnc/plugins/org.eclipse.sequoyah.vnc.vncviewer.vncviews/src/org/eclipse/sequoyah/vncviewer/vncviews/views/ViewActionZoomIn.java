/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Barboza Franco (Eldorado Research Institute) -  [243167] - Zoom mechanism not working properly 
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.vncviewer.vncviews.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ViewActionZoomIn implements IViewActionDelegate {

	
	private IViewPart targetPart;
	
	
	
	public void init(IViewPart view) {

		this.targetPart = view;

	}

	
	public void run(IAction action) {

		VNCViewerView.zoomIn();
		
	}

	
	public void selectionChanged(IAction action, ISelection selection) {
	

	}

}
