/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.flashlight.impl.core;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Mahesh Kannan
 * @author Byron Nevins
 * Date: Jun 25, 2008
 *
 * Byron Nevins says:  Note this class is used in one and only one place in all of
 * GF --> FlashlightProbeProviderFactory.java
 * Apparently it is used exclusively for making sure that 2 probes with the same name are not allowed?!?
 * look at the putIfAbsent for clues
 * This class has no business having any public members and should be moved to the only user's package
 *
 * I'm suspicious.  Maybe it is called by reflection from somewhere?!?
 */
public class ProbeProviderRegistry {
    private ProbeProviderRegistry() {
    }

    public static ProbeProviderRegistry getInstance() {
        return _me;
    }

    public Collection<FlashlightProbeProvider> getAllProbeProviders() {
        return Collections.unmodifiableCollection(providerMap.values());
    }

    public FlashlightProbeProvider getProbeProvider(String moduleProviderName, String moduleName, String probeProviderName) {
        if(probeProviderName == null)
            probeProviderName = "";

        return providerMap.get(makeName(moduleProviderName, moduleName, probeProviderName));
    }

    public FlashlightProbeProvider registerProbeProvider(FlashlightProbeProvider provider, Class clz) {

        String qname = makeName(provider, clz);

        // if there is an entry in the map for qname already -- it is an error
        // ConcurrentMap allows us to check and put with thread-safety!
        // putIfAbsent returns null iff there was no value already in there.

        if (providerMap.putIfAbsent(qname, provider) != null) {
            throw new IllegalStateException("Provider already mapped " + qname);
        }

        return provider;
    }

    private String makeName(FlashlightProbeProvider provider, Class clazz) {
        String ppName = provider.getProbeProviderName();

        if(ppName == null)
            ppName = clazz.getName();

        return makeName(provider.getModuleProviderName(),
                        provider.getModuleName(),
                        ppName);
    }

    private String makeName(String a, String b, String c) {
        StringBuilder sb = new StringBuilder();

        sb.append(a);
        sb.append(":");
        sb.append(b);
        sb.append(":");
        sb.append(c);

        return sb.toString();
    }

    private static ProbeProviderRegistry _me =
            new ProbeProviderRegistry();

    private ConcurrentMap<String, FlashlightProbeProvider> providerMap =
            new ConcurrentHashMap<String, FlashlightProbeProvider>();
}
