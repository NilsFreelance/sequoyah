/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.vncviewer.standalone;


import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.tml.vncviewer.graphics.swt.SWTRemoteDisplay;


/**
 * This class is a standalone implementation of a VNC Viewer.
 * It's an example of how to use the VNC Display component.
 * This component is implemented within the SWTDisplay class and it is completely independent
 * from Eclipse, thus it can be used without the need of an Eclipse plugin, or even Eclipse. 
 */
public class StandaloneVNCViewer {

	private static Canvas canvas;
	private static int WINDOW_WIDTH = 700;
	private static int WINDOW_HEIGHT = 500;
	private static String host = "127.0.0.1";
	private static int port = 5900;
	
	
	/**
	 * Adds a key event listener to the GUI and handles key events.
	 * @param parent the parent Composite to use.
	 * */
	public static void addKeyEventListener(Composite parent) {
		
		final SWTRemoteDisplay vncswt = new SWTRemoteDisplay(parent, new Properties(), null);
		final Display display = parent.getDisplay();

		try {
			//vncswt.start(host, port, null);
			vncswt.start(null);
		} catch (Exception e){
			log(StandaloneVNCViewer.class).error("The viewer can not be started.");
		}
	
		canvas = vncswt.getCanvas();
		canvas.setFocus();
		
		Listener listener = new Listener(){
			public void handleEvent(Event event){
				final Event e = event;
				
				display.syncExec(new Runnable(){
					public void run(){
					
						synchronized (vncswt.getCanvas()) {

							try {
								vncswt.keyEvent(e);
								vncswt.updateScreen();
							} catch (Exception e1) {
								log(StandaloneVNCViewer.class).error("VNC View error on key event.");
							}
							
							canvas.redraw();

						}
					}
				});
			}
		
		};
		
		canvas.addListener(SWT.KeyDown, listener );
	}

	
	/**
	 * Stays in a loop performing updates on the screen buffer.
	 */
	public static void updateScreenLoop(Composite parent) {
		
		final SWTRemoteDisplay vncswt = new SWTRemoteDisplay(parent, new Properties(), null);
	
		try {
			//vncswt.start(host, port, null);
			vncswt.start(null);
		} catch (Exception e){
			log(StandaloneVNCViewer.class).error("The viewer can not be started.");
		}
		
		while (!parent.isDisposed()) {
		
			try {
				vncswt.updateScreen();
				vncswt.wait(500);
			}
			catch (Exception e){
				log(StandaloneVNCViewer.class).error("Error on Loop test.");
			}

		}
		
	}
	
	
	public static void main(String [] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		
		if (args.length == 2) {
			host = args[0];
			port = (Integer.valueOf(args[1])).intValue();
		}
		else{
			log(StandaloneVNCViewer.class).error("[host] [port] expected.");
			return;
		}
		
		shell.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		shell.open();
	
		updateScreenLoop(shell);
		display.dispose();
	}

}


