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
package org.glassfish.admin.amx.base;

import org.glassfish.admin.amx.monitoring.MonitoringRoot;
import org.glassfish.admin.amx.core.AMXConstants;
import org.glassfish.admin.amx.annotation.*;

import org.glassfish.admin.amx.core.AMXProxy;
import org.glassfish.admin.amx.core.Util;
import org.glassfish.api.amx.AMXMBeanMetadata;


/**
	The top-level interface for an appserver domain. Access to all other
	{@link AMX} begins here.
    
    For dependency reasons, not all children of DomainRoot have getter methods;
    use {@link AMXHelper#getChild}.
    <p>
    The 'name' property in the ObjectName of DomainRoot is the name of the
    appserver domain.  For example, appserver domains 'domain' and 'domain2' would
    have ObjectNames for DomainRoot as follows:
    <pre>
    amx:type=DomainRoot:name=domain1
    amx:type=DomainRoot:name=domain2
    </pre>
    Of course, these two MBeans would normally be found in different MBeanServers.
 */
@AMXMBeanMetadata(singleton=true)
public interface DomainRoot extends AMXProxy
{
    /** more efficient access to type */
    public static final String TYPE = "DomainRoot";
    
    public static final String PARENT_PATH = "";
    public static final String PATH = PARENT_PATH + Pathnames.SEPARATOR;
    
    /**
        Return the {@link Ext} MBean, parent of top-level utility and specialty MBeans.
     */
    @ManagedAttribute
    public Ext getExt();

    /**
        @return the singleton {@link QueryMgr}.
     */
    @ManagedAttribute
    public QueryMgr		getQueryMgr();

    /**
        @return the singleton {@link BulkAccess}.
     */
    @ManagedAttribute
    public BulkAccess		getBulkAccess();

    /**
       @return the singleton {@link UploadDownloadMgr}.
     */
    @ManagedAttribute
    public UploadDownloadMgr		getUploadDownloadMgr();

    /**
        @return the singleton {@link Pathnames}.
     */
    @ManagedAttribute
    public Pathnames		getPathnames();

    /**
        @return the singleton {@link Sample}.
     */
    @ManagedAttribute
    public Sample		getSample();

    
    /**
      Return the name of this appserver domain.  Not to be confused with the
      JMX domain name, which may be derived from this name and is
      available from any ObjectName in AMX by calling
      {@link Util#getObjectName}

      The domain name is equivalent to the name of
      the directory containing the domain configuration.  This name
      is not part of the configuration and can only be changed by
      using a different directory to house the configuration for the
      domain.
      @return the name of the Appserver domain
     */
    @ManagedAttribute
    public String	getAppserverDomainName();

    /**
        For module dependency reasons, the returned object must be cast to the appropriate type,
        as it cannot be used here.
      @return the JSR 77 J2EEDomain.
     */
    @ManagedAttribute
    public AMXProxy		getJ2EE();

    /**
        Get the DomainConfig.
        For module dependency reasons, the returned object must be cast to DomainConfig.class
        by the client, as it cannot be referenced here.
        @return the singleton DomainConfig
     */
    @ManagedAttribute
    public AMXProxy	getDomain();

    /**
        @return the singleton {@link MonitoringRoot}.  All MonitoringRoot MBeans should be
        descendants of this MBean.
     */
    @ManagedAttribute
    public MonitoringRoot		getMonitoringRoot() ;

    /**
        @return the singleton SystemInfo
     */
    @ManagedAttribute
    public SystemInfo		getSystemInfo();
    
    /**
        Notification type for JMX Notification issued when AMX MBeans are loaded
        and ready for use.  
        @see #getAMXReady
     */
    public static final String  AMX_READY_NOTIFICATION_TYPE =
        AMXConstants.NOTIFICATION_PREFIX + "DomainRoot" + ".AMXReady";
        
    /**
        Poll to see if AMX is ready for use. It is more efficient to instead listen
        for a Notification of type {@link #AMX_READY_NOTIFICATION_TYPE}.  That
        should be done  by first registering the listener, then checking
        just after registration in case the Notification was issued in the ensuing
        interval just before the listener became registered.
        
        @return true if AMX is ready for use, false otherwise.
        @see #AMX_READY_NOTIFICATION_TYPE
     */
    @ManagedAttribute
    public boolean  getAMXReady();
      
    /**
        Wait (block) until AMX is ready for use. Upon return, AMX is ready for use.
     */
    @ManagedOperation
    public void  waitAMXReady();
    
    /**
      @since Glassfish V3
     */
    @ManagedAttribute
    public String getDebugPort();
    
    /**
      @since Glassfish V3
     */
    @ManagedAttribute
    public String getApplicationServerFullVersion();  
      
    
    
    /**
       @since Glassfish V3
     */
    @ManagedAttribute
    public String getInstanceRoot();

    /**
       @return the directory for the domain
      @since Glassfish V3
     */
    @ManagedAttribute
    public String getDomainDir();
    
    /**
      @return the configuration directory, typically 'config' subdirectory of {@link #getDomainDir}
      @since Glassfish V3
     */
    @ManagedAttribute
    public String getConfigDir();

    /**
      @return the installation directory
      @since Glassfish V3
     */
    @ManagedAttribute
    public String getInstallDir();
    
    
    /**
        Return the time the domain admin server has been running.
        uptime[0] contains the time in milliseconds.  uptime[1] contains a human-readable
        string describing the duration.
     */
    @ManagedAttribute
    public Object[]     getUptimeMillis();
}














