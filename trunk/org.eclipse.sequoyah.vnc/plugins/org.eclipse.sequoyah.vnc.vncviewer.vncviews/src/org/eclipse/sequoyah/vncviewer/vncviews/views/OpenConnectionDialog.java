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
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 * Daniel Barboza Franco - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233121] - There is no support for proxies when connecting the protocol 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [246585] - VncViewerService is not working anymore after changes made in ProtocolHandle
 ********************************************************************************/

package org.eclipse.tml.vncviewer.vncviews.views;

import java.io.IOException;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;




public class OpenConnectionDialog extends TitleAreaDialog {


	
	private Text hostText;
	private Text portText;
	private Combo protocolVersion;
	private Text passwordText;
	private Button bypassProxyButton;
	
	public OpenConnectionDialog(Shell parent) {
		super(parent);
		

	}
	
	

	protected Point getInitialSize() {

		return new Point(250, 330);
		//return super.getInitialSize();
	}
	
	
	
	
	protected Control createDialogArea(Composite parent) {
	
	
		Composite external = createDefaultComposite(parent, 1, 17);
		Composite fields = createDefaultComposite(external, 2, 0);
		
		
		int width, height, cols;

		setTitle("New VNC connection");
		setMessage("Enter values for your VNC connection");
		
		cols = 20;

		Label hostLabel = new Label(fields, SWT.RIGHT);
		hostText = new Text(fields, parent.getStyle() | SWT.BORDER);
		
		Label portLabel = new Label(fields, SWT.RIGHT);
		portText = new Text(fields, parent.getStyle() | SWT.BORDER);
		
		Label passwordLabel = new Label(fields, SWT.RIGHT);
		passwordText= new Text(fields, parent.getStyle() | SWT.BORDER | SWT.PASSWORD);

		GC gc = new GC(hostText.getDisplay());
		width = gc.getFontMetrics().getAverageCharWidth() * cols;
		height = gc.getFontMetrics().getHeight();

		GridData gridData = new GridData();
		gridData.heightHint = height;
		gridData.widthHint = width;

		hostLabel.setText("Host:");
		hostText.setLayoutData(gridData);
		hostText.setSize(hostText.computeSize(width, height));
		
		portLabel.setText("Port:");
		portText.setSize(portText.computeSize(width, height));
		portText.setLayoutData(gridData);
		
		passwordLabel.setText("Password:");
		passwordText.setSize(passwordText.computeSize(width, height));
		passwordText.setLayoutData(gridData);

		createCombo(external);
		
		
		
		
        Composite bypassComposite = new Composite(external, SWT.NULL);
        GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, false);
        bypassComposite.setLayoutData(gdata);
        GridLayout glayout = new GridLayout();

        bypassComposite.setLayout(glayout);
		
		bypassProxyButton = new Button(bypassComposite, SWT.CHECK);
		Point p = bypassProxyButton.getLocation();
		bypassProxyButton.setLocation(p.x, p.y+100);
		
		bypassProxyButton.setText("Bypass proxy settings");
		
		
		return external;
	
	}
	
	
	protected void okPressed() {
	
				
		String host = hostText.getText();
		String version;

		int port = Integer.valueOf(portText.getText()).intValue();
		version = protocolVersion.getItem(protocolVersion.getSelectionIndex());
		String password = passwordText.getText();

		try {
			VNCViewerView.stopProtocol();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VNCViewerView.start(host, port, version, password, bypassProxyButton.getSelection());
		
		
		super.okPressed();
		
	}
	
	
	
	@Override
	protected void cancelPressed() {
	
		VNCViewerView.stop();
		super.cancelPressed();
	}
	
	
	/**
	 * @param parent
	 * @param columns
	 * @return
	 */
	private Composite createDefaultComposite(Composite parent, int columns, int left) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();

		if (columns > 0) {
			layout.numColumns = columns;
		}


		layout.marginLeft = left + 5;
		layout.marginTop = 5;
		//layout.marginBottom = 15;

		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}
	
	
	
	
	
	
	
	
	
	/**

     * Creates the widgets that will be used to manipulate the devices and configuration at the view
     *
     * @param composite The parent composite of the device widgets
     */
    private void createCombo(Composite composite)

    {

    	
        Composite comboComposite = new Composite(composite, SWT.NULL);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);

        comboComposite.setLayoutData(data);
        GridLayout gridLayout = new GridLayout();

        comboComposite.setLayout(gridLayout);

        //comboComposite.setBackground(BACKGROUND_COLOR);

 

        Label configListLabel = new Label(comboComposite, SWT.NONE);

        configListLabel.setText("VNC Protocol used as base:");

        //configListLabel.setBackground(BACKGROUND_COLOR);


        
        Combo protocolCombo = new Combo(comboComposite, SWT.READ_ONLY);
        data  = new GridData(SWT.FILL, SWT.CENTER, true, false);
        protocolCombo.setLayoutData(data);
 
        protocolCombo.add("VNC 3.3");
        protocolCombo.add("VNC 3.7");
        protocolCombo.add("VNC 3.8");
        protocolCombo.select(2);
        
        this.protocolVersion = protocolCombo;
    
    }
    


}
