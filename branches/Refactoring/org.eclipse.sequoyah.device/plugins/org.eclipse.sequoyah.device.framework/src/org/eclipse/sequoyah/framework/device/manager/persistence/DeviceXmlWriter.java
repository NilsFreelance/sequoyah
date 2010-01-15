/********************************************************************************
 * Copyright (c) 2008, 2009 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute) 
 * [245114] Enhance persistence policies
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [271695] - Support to non-persistent instances of devices
 * Mauren Brenner (Eldorado) - Bug [280813] - Support saving instance info outside the workspace
 * Fabio Rigo (Eldorado) - Bug [288006] - Unify features of InstanceManager and InstanceRegistry
 * Daniel Barboza Franco (Eldorado) - Bug [287187] -Save device instance information in a directory defined in runtime.
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [288301] - Device view crashes when there is a device plug-in missing.
 ********************************************************************************/

package org.eclipse.tml.framework.device.manager.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.factory.DeviceTypeRegistry;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstance;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class DeviceXmlWriter implements IDeviceXmlTags
{
    /**
     * Stores the instance data on a XML file located in the workspace root.
     */
    public static void saveInstances() {
        File path = DevicePlugin.getDeviceXmlLocation();
        File file = new File(path, TML_DEVICE_DATAFILE);

        Document document = createDocument(file);

        if (document != null) {

            try {
                Element root = document.getDocumentElement();
                Element instancesRoot = createInstancesElement(document);
                root.appendChild(instancesRoot);

                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$

                file.getParentFile().mkdirs();
                // initialize FileOutputStream with File object to save to file
                FileOutputStream outputStream = new FileOutputStream(file
                        .getAbsoluteFile());
                StreamResult result = new StreamResult(outputStream);
                DOMSource source = new DOMSource(document);
                transformer.transform(source, result);
                outputStream.close();

            } catch (IOException ie) {
                ie.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerFactoryConfigurationError e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Part of saveInstances
     * 
     * @param file -
     *            The File object representing the XML file.
     * @return The DOM document representing the XML file.
     */
    private static Document createDocument(File file) {    	
        Document document = null;
        if (file != null) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                DOMImplementation impl = builder.getDOMImplementation();

                // Create the document
                document = impl.createDocument(null,
                        TML_XML_ROOT, null);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return document;
    }

    /**
     * Creates DOM Element which represents all the instances defined. called by
     * saveInstances().
     */
    private static Element createInstancesElement(Document document) {
        Element instancesRoot = document
                .createElement(TML_XML_INSTANCES);
        InstanceRegistry registry = InstanceRegistry.getInstance();
        Iterator<IInstance> iterator = registry.getInstances().iterator();
   
        while (iterator.hasNext()) {
   
        	IInstance iIInst = iterator.next();
            IDeviceType device = DeviceTypeRegistry.getInstance().getDeviceTypeById(iIInst.getDeviceTypeId());

            /* 
             * Check if this instance should be persisted
             * If device == null, it means that this particular instance does not 
             * have a plug-in that declares its device, so we persist it anyway.
             */ 
            if (device == null || device.isPersistent()) {
           
	            Element element = document
	                    .createElement(TML_XML_INSTANCE);
	            element.setAttribute(TML_XML_INSTANCE_NAME, iIInst
	                    .getName());
	            String xml_device_id = iIInst.getDeviceTypeId();
	
	            element.setAttribute(TML_XML_INSTANCE_DEVICE_ID,
	                    xml_device_id);
	            if (element != null)
	                instancesRoot.appendChild(element);
	            Properties propProp = iIInst.getProperties();
	
	            for (Enumeration<?> e = propProp.keys(); e.hasMoreElements();) {
	                String propStr = (String) e.nextElement();
	                String propValStr = propProp.getProperty(propStr);
	
	                Element propElement = document.createElement(propStr);
	                Text propNode = document.createTextNode(propValStr);
	                propElement.appendChild(propNode);
	                element.appendChild(propElement);
	
	            }
            }

        }
        return instancesRoot;
    }
}
