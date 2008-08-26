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

package org.glassfish.web.admin.monitor.telemetry;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.enterprise.util.i18n.StringManager;

import java.lang.reflect.Method;
import org.glassfish.flashlight.datatree.TreeNode;
import org.glassfish.flashlight.datatree.factory.TreeNodeFactory;
import org.glassfish.flashlight.statistics.Counter;
import org.glassfish.flashlight.statistics.factory.CounterFactory;
import org.glassfish.web.admin.monitor.statistics.V2JVMStats;

public class JVMMemoryStatsTelemetry {
    
    private TreeNode jvmNode;
    private MemoryUsage heapUsage;
    private MemoryUsage nonheapUsage;
    private MemoryMXBean bean;
    private Counter commitHeapSize = CounterFactory.createCount();
    private Logger logger;
    private boolean isEnabled = true;
    
    /** Creates a new instance of JVMMemoryStatsTelemetry */
    public JVMMemoryStatsTelemetry(TreeNode server, boolean jvmMonitoringEnabled, Logger logger) {
        try {
            this.logger = logger;

            // jvm node
            jvmNode = TreeNodeFactory.createTreeNode("jvm", null, "jvm");
            server.addChild(jvmNode);

            bean = ManagementFactory.getMemoryMXBean();

            // heap memory usage
            heapUsage = bean.getHeapMemoryUsage();
            Method m = heapUsage.getClass().getMethod("getCommitted", (Class[]) null);
            logger.finest("heapUsage.getCommitted() = " + heapUsage.getCommitted());
            logger.finest("Method m.invoke() = " + m.invoke(heapUsage, (Object[]) null));
            TreeNode committedHeapSize = 
                    TreeNodeFactory.createMethodInvoker("committedHeapSize", heapUsage, "jvm", m);
            jvmNode.addChild(committedHeapSize);

            // non-heap memory usage
            nonheapUsage = bean.getNonHeapMemoryUsage();
            m = nonheapUsage.getClass().getMethod("getCommitted", (Class[]) null);
            TreeNode nonHeapNode =
                TreeNodeFactory.createMethodInvoker("non-heap-memory", nonheapUsage, "jvm", m);
            jvmNode.addChild(nonHeapNode);

            // v2 compatible jvm stats node
            V2JVMStats v2jvmStats = new V2JVMStats();
            m = v2jvmStats.getClass().getMethod("getUptime", (Class[]) null);
            TreeNode v2JVMNode = 
                TreeNodeFactory.createMethodInvoker("jvm", v2jvmStats, "jvm", m);
            jvmNode.addChild(v2JVMNode);

            //enable/disable node
            jvmNode.setEnabled(jvmMonitoringEnabled);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JVMMemoryStatsTelemetry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JVMMemoryStatsTelemetry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JVMMemoryStatsTelemetry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(JVMMemoryStatsTelemetry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JVMMemoryStatsTelemetry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }            
    
    public void enableMonitoring(boolean flag) {
        if (isEnabled != flag) {
            isEnabled = flag;
            jvmNode.setEnabled(flag);
        }
    }
    
    public Counter getCommittedHeapSize() {
        commitHeapSize.setCount(heapUsage.getCommitted());
        return commitHeapSize;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
    
    /*
    public Counter getCommittedNonHeapSize() {
        commitNonHeapSize.setCount(nonheapUsage.getCommitted());
        return (Counter)commitNonHeapSize.unmodifiableView();
    }
    
    public Counter getInitHeapSize() {
        initHeapSize.setCount(heapUsage.getInit());
        return (CountStatistic)initHeapSize.unmodifiableView();
    }
    
    public CountStatistic getInitNonHeapSize() {
        initNonHeapSize.setCount(nonheapUsage.getCommitted());
        return (CountStatistic)initNonHeapSize.unmodifiableView();
    }
    
    public CountStatistic getMaxHeapSize() {
        maxHeapSize.setCount(heapUsage.getMax());
        return (CountStatistic)maxHeapSize.unmodifiableView();
    }
    
    public CountStatistic getMaxNonHeapSize() {
        maxNonHeapSize.setCount(nonheapUsage.getInit());
        return (CountStatistic)maxNonHeapSize.unmodifiableView();
    }
    
    public CountStatistic getObjectPendingFinalizationCount() {
        objPendingCount.setCount(bean.getObjectPendingFinalizationCount());
        return (CountStatistic)objPendingCount.unmodifiableView();
    }
    
    public CountStatistic getUsedHeapSize() {
        usedHeapSize.setCount(heapUsage.getUsed());
        return (CountStatistic)usedHeapSize.unmodifiableView();
    }
    
    public CountStatistic getUsedNonHeapSize() {
        usedNonHeapSize.setCount(nonheapUsage.getUsed());
        return (CountStatistic)usedNonHeapSize.unmodifiableView();
    }
    */
}
