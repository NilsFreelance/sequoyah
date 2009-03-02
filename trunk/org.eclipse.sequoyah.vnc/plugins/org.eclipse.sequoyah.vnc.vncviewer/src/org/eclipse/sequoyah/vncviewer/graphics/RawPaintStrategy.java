/********************************************************************************
 * Copyright (c) 2008 MontaVista Software. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 *
 * Contributors:
 * Daniel Barboza Franco (Motorola) - Bug [242129] - Raw enconding not implemented correctly
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 ********************************************************************************/
package org.eclipse.tml.vncviewer.graphics;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.DataInput;

import org.eclipse.tml.vncviewer.network.RectHeader;

public class RawPaintStrategy extends AbstractPaintStrategy {

	public RawPaintStrategy(IPainterContext context) {
		super(context);
	}
	
	public void processRectangle(RectHeader rh, DataInput in) throws Exception{
		int width = rh.getWidth();
		int height = rh.getHeight();
		int pixels[] = new int[width*height];
		IPainterContext pc = getContext();
		
		log(RawPaintStrategy.class).debug("Processing rectangle defined by: w=" 
		        + width + "; h=" + height + ".");
		
		pixels = pc.readpixels(in, width, height);
		pc.setPixels(rh.getX(), rh.getY(), width, height, pixels, 0);
	}

}
