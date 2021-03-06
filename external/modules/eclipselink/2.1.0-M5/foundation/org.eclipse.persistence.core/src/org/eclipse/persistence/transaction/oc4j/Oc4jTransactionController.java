/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.transaction.oc4j;

import javax.transaction.TransactionManager;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for OC4J JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA
 * transactions in OC4J. The JTA TransactionManager must be set on the instance.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class Oc4jTransactionController extends JTATransactionController {
    public static final String JNDI_TRANSACTION_MANAGER_NAME = "java:comp/pm/TransactionManager";

    public Oc4jTransactionController() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        return (TransactionManager)jndiLookup(JNDI_TRANSACTION_MANAGER_NAME);
    }
}
