/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial Version
 */
package org.eclipse.mtj.tfm.sign.core.extension;

import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.osgi.framework.Version;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class ExtensionImpl implements IExtension {

    protected String id = "DefaulSignExtensionId";
    protected String vendor = "DSDP - Eclipse.org";
    protected Version version = Version.emptyVersion;
    protected String description = "Defaul Sign Extension Description";
    protected ExtensionType type = ExtensionType.SECURITY_MANAGEMENT;
    protected boolean active = false;

    /**
     * Creates a new instance of ExtensionImpl.
     */
    public ExtensionImpl() {

    }
    
    

    /**
     * Creates a new instance of ExtensionImpl.
     * @param id
     * @param vendor
     * @param version
     * @param description
     * @param type
     */
    public ExtensionImpl(String id, String vendor, Version version,
            String description, ExtensionType type) {
        this.id = id;
        this.vendor = vendor;
        this.version = version;
        this.description = description;
        this.type = type;
    }



    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#getType()
     */
    public ExtensionType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#getVendor()
     */
    public String getVendor() {
        return vendor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#getVersion()
     */
    public Version getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setActive(boolean)
     */
    public void setActive(boolean value) {
        active = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setDescription(java.lang.String)
     */
    public void setDescription(String value) {
        description = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setId(java.lang.String)
     */
    public void setId(String value) {
        id = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setType(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)
     */
    public void setType(ExtensionType value) {
        type = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setVendor(java.lang.String)
     */
    public void setVendor(String value) {
        vendor = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.IExtension#setVersion(org.osgi.framework.Version)
     */
    public void setVersion(Version value) {
        version = value;
    }

}
