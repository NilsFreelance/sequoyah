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

package org.eclipse.sequoyah.pulsar.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;

/**
 * A class of utilities for P2 common operations
 */
@SuppressWarnings("restriction")
public class P2Utils {

    public static final String PROP_PULSAR_PROFILE = "org.eclipse.pulsar.profile"; //$NON-NLS-1$

    /**
     * @param iu
     * @return
     */
    public static boolean isInstalled(IInstallableUnit iu) {
        for (IProfile profile : ProvisioningHelper.getProfiles()) {
            IQueryResult<IInstallableUnit> available = profile.available(new InstallableUnitQuery(iu.getId(), iu
                    .getVersion()), new NullProgressMonitor());
            if (!available.isEmpty())
                return true;
        }
        return false;
    }

    /**
     * @param id
     * @param installFolder
     * @return
     * @throws ProvisionException
     */
    public static IProfile createProfile(String id, IPath installFolder)
            throws ProvisionException {
        IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper
                .getService(Activator.getContext(), IProfileRegistry.class
                        .getName());
        Map<String, String> propMap = new HashMap<String, String>();
        if (installFolder != null)
        	propMap.put(IProfile.PROP_INSTALL_FOLDER, installFolder
                    .toOSString());
        propMap.put(PROP_PULSAR_PROFILE, Boolean.TRUE.toString());
        return profileRegistry.addProfile(id, propMap);
    }

    /**
     * @param id
     */
    public static void deleteProfile(String id) {
        IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper
                .getService(Activator.getContext(), IProfileRegistry.class
                        .getName());
        profileRegistry.removeProfile(id);
    }

    /**
     * @param sdk
     * @param installFolder
     * @return
     * @throws ProvisionException
     */
    public static IProfile createProfileForSDK(ISDK sdk, IPath installFolder)
            throws ProvisionException {
        StringBuilder sb = new StringBuilder();
        sb.append("org.eclipse.pulsar.profile."); //$NON-NLS-1$
        sb.append(((SDK) sdk).getInstallableUnit().getId());
        sb.append("."); //$NON-NLS-1$
        sb.append(System.currentTimeMillis());

        return createProfile(sb.toString(), installFolder);
    }

    /**
     * @param profile
     * @return
     */
    public static boolean isSupportedProfile(IProfile profile) {
        return profile.getProperty(PROP_PULSAR_PROFILE) != null;
    }

    /**
     * @return
     */
    public static Collection<IProfile> getProfiles() {
        Collection<IProfile> profiles = new ArrayList<IProfile>();
        for (IProfile profile : ProvisioningHelper.getProfiles()) {
            if (isSupportedProfile(profile))
                profiles.add(profile);
        }

        return profiles;
    }
}
