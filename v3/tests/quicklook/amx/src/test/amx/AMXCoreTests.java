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
package amxtest;

import javax.management.ObjectName;
import javax.management.Attribute;
import javax.management.AttributeList;

import org.testng.annotations.*;
import org.testng.Assert;


import java.util.Set;
import java.util.Map;
import java.util.List;

import  org.glassfish.admin.amx.core.AMXProxy;
import org.glassfish.admin.amx.config.AMXConfigProxy;
import org.glassfish.admin.amx.base.DomainRoot;
import org.glassfish.admin.amx.base.SystemStatus;
import org.glassfish.admin.amx.core.Extra;
import org.glassfish.admin.amx.core.Util;
import org.glassfish.admin.amx.util.CollectionUtil;
import org.glassfish.admin.amx.util.TimingDelta;
import org.glassfish.admin.amx.util.SetUtil;
import org.glassfish.admin.amx.util.jmx.JMXUtil;
import org.glassfish.admin.amx.intf.config.JDBCConnectionPool;

/** 
Basic AMXProxy tests that verify connectivity and ability to
traverse the AMXProxy hierarchy and fetch all attributes.
 */
//@Test(groups={"amx"}, description="AMXProxy tests", sequential=false, threadPoolSize=5)
@Test(groups =
{
    "amx"
}, description = "Core AMX tests")
public final class AMXCoreTests extends AMXTestBase
{
    public AMXCoreTests()
    {
    }

    //@Test(timeOut=15000)
    public void bootAMX() throws Exception
    {
        final DomainRoot domainRoot = getDomainRootProxy();

        // one basic call to prove it's there...
        domainRoot.getAppserverDomainName();
    }

    @Test(dependsOnMethods = "bootAMX")
    public void iterateAllSanityCheck()
    {
        try
        {
        final Set<AMXProxy> all = getAllAMX();
        assert all.size() > 20;
        for (final AMXProxy amx : all)
        {
            final Set<AMXProxy> children = amx.childrenSet();
            assert children != null;
        }
        }
        catch( final Throwable t )
        {
            System.out.println( "Test iterateAllSanityCheck() IGNORED, see issue #9355" );
            t.printStackTrace();
        }
    }
    
    @Test
    public void testAMXComplianceMonitorFailureCount()
    {
        try
        {
        final int failureCount = getDomainRootProxy().getNumComplianceFailures();
        
        assert failureCount == 0 :
            "Server indicates that there are non-compliant AMX MBean validator failures, failure count = " + failureCount + ", examine the server log for failures";
        }
        catch( final Throwable t )
        {
            System.out.println( "Test testAMXComplianceMonitorFailureCount() IGNORED, see issue #9355" );
            t.printStackTrace();
        }
     }

}





























