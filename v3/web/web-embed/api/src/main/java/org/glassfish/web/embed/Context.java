/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 */

package org.glassfish.web.embed;

import java.util.*;
import javax.servlet.*;
import org.apache.catalina.Valve;

/**
 * Representation of a web application.
 *
 * @author Rajiv Mordani
 * @author Jan Luehe
 */
// TODO: Add support for configuring environment entries
// Security realms support
public interface Context extends ServletContext, Lifecycle {

    /**
     * 
     */
    public <T extends Valve> void addValve(T t);

    /**
     * Registers the given listener with this context.
     * 
     * <p>The given listener must be an instance of one or more of the
     * following interfaces:
     * <ul>
     * <li><tt>javax.servlet.ServletContextAttributeListener</tt>
     * <li><tt>javax.servlet.ServletRequestAttributeListener</tt>
     * <li><tt>javax.servlet.ServletRequestListener</tt>
     * <li><tt>javax.servlet.ServletContextListener</tt>
     * <li><tt>javax.servlet.http.HttpSessionAttributeListener</tt>
     * <li><tt>javax.servlet.http.HttpSessionListener</tt>
     * </ul>
     *
     * @param t the listener to be registered with this context
     *
     * @throws IllegalArgumentException if the given listener is not
     * an instance of any of the above interfaces
     * @throws IllegalStateException if this context has already been
     * initialized and started
     */
    public <T extends EventListener> void addListener(T t);

    /**
     * Instantiates the given listener Class and registers it with this
     * context.
     * 
     * <p>The given listener must be an instance of one or more of the
     * following interfaces:
     * <ul>
     * <li><tt>javax.servlet.ServletContextAttributeListener</tt>
     * <li><tt>javax.servlet.ServletRequestAttributeListener</tt>
     * <li><tt>javax.servlet.ServletRequestListener</tt>
     * <li><tt>javax.servlet.ServletContextListener</tt>
     * <li><tt>javax.servlet.http.HttpSessionAttributeListener</tt>
     * <li><tt>javax.servlet.http.HttpSessionListener</tt>
     * </ul>
     *
     * @param listenerClass the listener Class to be instantiated and 
     * registered with this context
     *
     * @throws IllegalArgumentException if the given class does not
     * implement any of the above interfaces
     * @throws IllegalStateException if this context has already been
     * initialized and started
     */
    public void addListener(Class <? extends EventListener> listenerClass);
}
