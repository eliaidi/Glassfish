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
package org.glassfish.jms.admin.cli;

import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.ActionReport;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.annotations.Inject;
import org.jvnet.hk2.component.PerLookup;
import com.sun.enterprise.config.serverbeans.*;
import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.enterprise.util.LocalStringManagerImpl;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Create JMS Resource Command
 *
 */
@Service(name="create-jms-resource")
@Scoped(PerLookup.class)
@I18n("create.jms.resource")
public class CreateJMSResource implements AdminCommand {

    @Param(name="restype")
    String resourceType;

    @Param(optional=true, defaultValue="true")
    Boolean enabled;

    @Param(name="property", optional=true)
    Properties props;

    @Param(optional=true)
    String target = SystemPropertyConstants.DEFAULT_SERVER_INSTANCE_NAME;

    @Param(name="description", optional=true)
    String description;

    @Param(name="jndi_name", primary=true)
    String jndiName;

    @Inject
    CommandRunner commandRunner;

    @Inject
    ConnectorConnectionPool[] connPools;

    private static final String QUEUE = "javax.jms.Queue";
    private static final String TOPIC = "javax.jms.Topic";
    private static final String QUEUE_CF = "javax.jms.QueueConnectionFactory";
    private static final String TOPIC_CF = "javax.jms.TopicConnectionFactory";
    private static final String UNIFIED_CF = "javax.jms.ConnectionFactory";
    private static final String DEFAULT_JMS_ADAPTER = "jmsra";
    private static final String DEFAULT_OPERAND="DEFAULT";

    //JMS destination resource properties
    private static final String NAME = "Name";
    private static final String IMQ_DESTINATION_NAME = "imqDestinationName";

    final private static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(CreateJMSResource.class);


    /**
     * Executes the command with the command parameters passed as Properties
     * where the keys are the paramter names and the values the parameter values
     *
     * @param context information
     */
    public void execute(AdminCommandContext context) {
        final ActionReport report = context.getActionReport();

         if (resourceType == null) {
            report.setMessage(localStrings.getLocalString("create.jms.resource.noResourceType",
                            "No Resoruce Type specified for JMS Resource."));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }

        if (jndiName == null) {
            report.setMessage(localStrings.getLocalString("create.jms.resource.noJndiName",
                            "No JNDI name specified for JMS Resource."));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }

        if (!(resourceType.equals(TOPIC_CF) || resourceType.equals(QUEUE_CF) || resourceType.equals(UNIFIED_CF) || resourceType.equals(TOPIC)  || resourceType.equals(QUEUE))) {
             report.setMessage(localStrings.getLocalString("create.jms.resource.InvalidResourceType",
                            "Invalid Resource Type specified for JMS Resource."));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;

        }
        //Populate the JMS RA map
        populateJmsRAMap();


        /* Map MQ properties to Resource adapter properties */
        if (props != null) {
            Enumeration en = props.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String raKey = getMappedName(key);
                if (raKey == null) raKey = key;
                props.put(raKey, (String) props.get(key));
                if(! raKey.equals(key))
                    props.remove(key);
            }
         }

        ActionReport subReport = report.addSubActionsReport();

      if (resourceType.equals(TOPIC_CF) || resourceType.equals(QUEUE_CF) || resourceType.equals(UNIFIED_CF)) {
          ConnectorConnectionPool cpool = null;
             for (ConnectorConnectionPool cp : connPools) {
                 if (cp.getName().equals(jndiName))
                     cpool = cp;
            }
          boolean createdPool = false;
           // If pool is already existing, do not try to create it again
          if (cpool == null) {
                // Add connector-connection-pool.
              Properties parameters = populateConnectionPoolParameters();
              commandRunner.doCommand("create-connector-connection-pool", parameters, subReport);
              createdPool= true;
              if (ActionReport.ExitCode.FAILURE.equals(subReport.getActionExitCode())){
                    report.setMessage(localStrings.getLocalString("create.jms.resource.cannotCreateConnectionPool",
                            "Unable to create connection pool."));
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                    return;
              }
          }
              Properties params = populateConnectionResourceParameters();
              commandRunner.doCommand("create-connector-resource", params, subReport);

              if (ActionReport.ExitCode.FAILURE.equals(subReport.getActionExitCode())){
                    report.setMessage(localStrings.getLocalString("create.jms.resource.cannotCreateConnectorResource",
                            "Unable to create connection resource."));
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);

                //rollback the connection pool ONLY if we created it...
                  if (createdPool)
                     commandRunner.doCommand("delete-connector-connection-pool", populateConnectionPoolParameters(), subReport);

                    return;
              }
      } else if (resourceType.equals(TOPIC) ||
                    resourceType.equals(QUEUE))
            {
                Properties aoAttrList = new Properties();
                try{
                 //validate the provided properties and modify it if required.
                    Properties properties =  validateDestinationResourceProps(props, jndiName);
                    //aoAttrList.put("property", properties);
                    String propString = "";
                    for (java.util.Map.Entry<Object, Object>prop : properties.entrySet()) {
                            propString += prop.getKey() + "=" + prop.getValue() + ":";
                    }
                    propString = propString.substring(0, propString.length());
                    aoAttrList.put("property", propString); 
                }catch (Exception e)
                {
                    if (ActionReport.ExitCode.FAILURE.equals(subReport.getActionExitCode())){
                    report.setMessage(localStrings.getLocalString("create.jms.resource.cannotCreateConnectorResource",
                            "Unable to create connector resource."));
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                    return;
                 }
                }
                // create admin object
                aoAttrList.setProperty(DEFAULT_OPERAND,  jndiName);
                aoAttrList.setProperty("restype",  resourceType);
                aoAttrList.setProperty("raname",  DEFAULT_JMS_ADAPTER);
                if(enabled!=null)
                    aoAttrList.put("enabled",  enabled);

                commandRunner.doCommand("create-admin-object", aoAttrList, subReport);

                if (ActionReport.ExitCode.FAILURE.equals(subReport.getActionExitCode())){
                    report.setMessage(localStrings.getLocalString("create.jms.resource.cannotCreateAdminObject",
                            "Unable to create admin object."));
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                    return;
                }

            }

        ActionReport.ExitCode ec = ActionReport.ExitCode.SUCCESS;

        report.setActionExitCode(ec);
    }

    Hashtable mapping = null;
    private void populateJmsRAMap() {
        mapping = new Hashtable();
        mapping.put("imqDestinationName","Name");
        mapping.put("imqDestinationDescription","Description");
        mapping.put("imqConnectionURL","ConnectionURL");
        mapping.put("imqDefaultUsername","UserName");
        mapping.put("imqDefaultPassword","Password");
        mapping.put("imqConfiguredClientID","ClientId");
        mapping.put("imqAddressList","AddressList");
        mapping.put("MessageServiceAddressList","AddressList");
    }

    public String getMappedName(String key){
        return (String) mapping.get(key);
    }

    private Properties populateConnectionPoolParameters(){

            String steadyPoolSize = null;
            String maxPoolSize = null;
            String poolResizeQuantity = null;
            String idleTimeoutInSecs = null;
            String maxWaitTimeInMillis = null;
	        String failAllConnections = null;
	        String transactionSupport = null;
            Properties parameters = new Properties();

            if(props != null){
            Enumeration keys =  props.keys();
            Properties tmpProps = new Properties();

            while(keys.hasMoreElements())
            {
                String propKey = (String) keys.nextElement();

                if ("steady-pool-size".equals(propKey))
                    steadyPoolSize = props.getProperty(propKey);
                else if ("max-pool-size".equals(propKey))
                    maxPoolSize = props.getProperty(propKey);
                else if ("pool-resize-quantity".equals(propKey))
                    poolResizeQuantity = props.getProperty(propKey);
                else if ("idle-timeout-in-seconds".equals(propKey))
                    idleTimeoutInSecs = props.getProperty(propKey);
                else if ("max-wait-time-in-millis".equals(propKey))
                    maxWaitTimeInMillis = props.getProperty(propKey);
		        else if ("transaction-support".equals(propKey))
		            transactionSupport = props.getProperty(propKey);
		        else if("fail-all-connections".equals(propKey))
		            failAllConnections = props.getProperty(propKey);
                else
                    tmpProps.setProperty(propKey, props.getProperty(propKey));
            }
               if (tmpProps.size() >0)
               {
               String propString = "";
                for (java.util.Map.Entry<Object, Object>prop : tmpProps.entrySet()) {
                        propString += prop.getKey() + "=" + prop.getValue() + ":";
                }
                propString = propString.substring(0, propString.length());

                parameters.put("property", propString);

               }
         }
        //parameters.setProperty("restype", resourceType);

        parameters.setProperty("poolname", jndiName);
        if(description != null)
            parameters.setProperty("description", description);

        // Get the default res adapter name from Connector-runtime
        String raName = DEFAULT_JMS_ADAPTER;
        parameters.setProperty("raname", raName);

        parameters.setProperty("connectiondefinition", resourceType);
        parameters.setProperty("maxpoolsize",  (maxPoolSize == null) ? "250" : maxPoolSize);
        parameters.setProperty("steadypoolsize", (steadyPoolSize == null) ? "1" : steadyPoolSize);
        if (poolResizeQuantity != null) {
             parameters.setProperty("poolresize", poolResizeQuantity);
        }
         if (idleTimeoutInSecs != null) {
             parameters.setProperty("idletimeout", idleTimeoutInSecs);
        }

        if (maxWaitTimeInMillis != null) {
             parameters.setProperty("maxwait", maxWaitTimeInMillis);
        }

        if (failAllConnections != null) {
            parameters.setProperty("failconnection",failAllConnections);
        }
        if (transactionSupport != null) {
            parameters.setProperty("transactionsupport", transactionSupport);
        }

        return parameters;
    }

    private Properties populateConnectionResourceParameters()
    {
        Properties parameters = new Properties();
        parameters.setProperty("jndi_name", jndiName);
        parameters.put("enabled", enabled);
        parameters.setProperty("poolname", jndiName);
        if(description != null)
            parameters.setProperty("description", description);

        return parameters;
    }

       /**
     * Validates the properties specified for a Destination Resource
     * and returns a validated Properties list.
     *
     * NOTE: When "Name" property has not been specified by the user,
     * the properties object is updated with a computed Name.
     */
    private Properties validateDestinationResourceProps(Properties props,
    		String jndiName) throws Exception {
        String providedDestinationName = null;
        if(props != null)
             providedDestinationName = getProvidedDestinationName(props);
        else
            props = new Properties();
        //sLogger.fine("provided destination name =  "	+ providedDestinationName);
        if (providedDestinationName != null) {
        	//check validity of provided JMS destination name
            if (!isSyntaxValid(providedDestinationName)) {
                throw new Exception(localStrings.getLocalString(
                      "admin.mbeans.rmb.destination_name_invalid",
                      jndiName, providedDestinationName));
            }
	} else {
            //compute a valid destination name from the JNDI name.
            String newDestName = computeDestinationName(jndiName);
            //sLogger.log(Level.WARNING, "admin.mbeans.rmb.destination_name_missing",new Object[]{jndiName, newDestName});
            props.put(NAME, newDestName);
            //sLogger.fine("Computed destination name" + newDestName  + " and updated props");
        }
    	return props;
    }

    /**
     * Get the physical destination name provided by the user. The "Name"
     * and "imqDestinationName" properties are used to link a JMS destination
     * resource to its physical destination in SJSMQ.
     */
    private String getProvidedDestinationName(Properties props) {
        for (Enumeration e = props.keys() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String value = (String)props.get(key);
            if(NAME.equals(key) || IMQ_DESTINATION_NAME.equals(key)){
                if (value != null && value.length() != 0) return value;
            }
        }
        return null;
    }
  //Modified this method to support wildcards in MQ destinations...
    private boolean isSyntaxValid(String name) {
        char[] namechars = name.toCharArray();
        if (Character.isJavaIdentifierStart(namechars[0]) || namechars[0] == '*' || namechars[0] == '>') {
            for (int i = 1; i<namechars.length; i++) {
                if (!Character.isJavaIdentifierPart(namechars[i]) && ! (namechars[i] == '.' || namechars[i] == '*' || namechars[i] == '>')) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Derive a destination name, valid as per MQ destination naming rules,
     * from the JNDI name provided for the JMS destination resource.
     *
     * Scheme: merely replace all invalid identifiers in the JNDI name with
     * an 'underscore'.
     */
    private String computeDestinationName(String providedJndiName) {
    	char[] jndiName = providedJndiName.toCharArray();
        char[] finalName = new char[jndiName.length];
        finalName[0] = Character.isJavaIdentifierStart(jndiName[0]) ? jndiName[0] : '_';
        for (int i = 1; i < jndiName.length; i++) {
        	finalName[i] = Character.isJavaIdentifierPart(jndiName[i])? jndiName[i] : '_';
        }
        return new String(finalName);
    }
}





















