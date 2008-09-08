/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute) 
 *
 * Contributors:
 * Daniel Barboza Franco (Motorola) - Integration with code from bug 227793 to correctly deal with the redesigned painting process.
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 *******************************************************************************/
package org.eclipse.tml.vncviewer.network.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.tml.protocol.lib.IMessageFieldsStore;
import org.eclipse.tml.protocol.lib.IRawDataHandler;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;
import org.eclipse.tml.vncviewer.network.IVNCPainter;
import org.eclipse.tml.vncviewer.network.RectHeader;
import org.eclipse.tml.vncviewer.network.VNCProtocolData;
import org.eclipse.tml.vncviewer.registry.VNCProtocolRegistry;

/**
 * DESCRIPTION: This class consists of the reader for the framebuffer
 * rectangles, which is part of the Framebuffer Update message.<br>
 * 
 * 
 * RESPONSIBILITY: Provide the protocol framework with framebuffer data.<br>
 * 
 * COLABORATORS: None<br>
 * 
 * USAGE: This class is intended to be used by Eclipse.<br>
 * 
 */
public class FramebufferRectanglesHandler implements IRawDataHandler {

	private int current_rect = 1;
	private int x0, y0 = Integer.MAX_VALUE;
	private int x1, y1 = 0;

	public Map<String, Object> readRawDataFromStream(ProtocolHandle handle,
			InputStream dataStream, IMessageFieldsStore currentlyReadFields,
			boolean isBigEndian) throws IOException,
			ProtocolRawHandlingException {

		// Determine the number of pixels using the width and height already
		// read
		int w = (Integer) currentlyReadFields.getFieldValue("width");
		int h = (Integer) currentlyReadFields.getFieldValue("height");

		Map<String, Object> fieldsMap = new HashMap<String, Object>();

		VNCProtocolData protocolData = VNCProtocolRegistry.getInstance().get(
				handle);

		if (protocolData != null) {
			// Collects the painter where the rectangles will be processed
			// from the protocol data instance
			IVNCPainter painter = protocolData.getVncPainter();

			int x = (Integer) currentlyReadFields.getFieldValue("x-position");
			int y = (Integer) currentlyReadFields.getFieldValue("y-position");
			int width = (Integer) currentlyReadFields.getFieldValue("width");
			int height = (Integer) currentlyReadFields.getFieldValue("height");
			int encoding = (Integer) currentlyReadFields
					.getFieldValue("encodingType");
			// byte[] data = (byte[])
			// currentlyReadFields.getFieldValue("pixelsData");

			// Process the rectangle data into the painter

			try {
				painter.processRectangle(new RectHeader(x, y, width, height,
						encoding), protocolData.getInputStream());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int numRect = (Integer) currentlyReadFields
					.getFieldValue("numberOfRectangles");

			x0 = Math.min(x0, x);
			y0 = Math.min(y0, y);
			x1 = Math.max(x1, x + w);
			y1 = Math.max(y1, y + h);

			if (current_rect == numRect) {
				current_rect = 1;

				painter.updateRectangle(x0, y0, x1, y1);

				x0 = y0 = Integer.MAX_VALUE;
				x1 = y1 = 0;

			} else {
				current_rect += 1;
			}

		}

		return fieldsMap;
	}

	public void writeRawDataToStream(ProtocolHandle handle,
			ByteArrayOutputStream dataStream,
			ProtocolMessage messageToGetInformationFrom, boolean isBigEndian)
			throws ProtocolRawHandlingException {

		// No implementation. This is a client plugin only
	}
}
