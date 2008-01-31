//  
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-04/12/2007 12:08 AM(kohsuke)-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2007.08.24 at 01:25:15 PM PDT
//
    
   
package com.sun.enterprise.config.serverbeans;

import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.Element;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.component.Injectable;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.List;


/**
 *
 */

/* @XmlType(name = "", propOrder = {
    "description",
    "securityMap",
    "property"
}) */
@Configured
public interface ConnectorConnectionPool extends ConfigBeanProxy, Injectable, Resource {

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute(required = true)
    public String getName();

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) throws PropertyVetoException;

    /**
     * Gets the value of the resourceAdapterName property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute(required = true)
    public String getResourceAdapterName();

    /**
     * Sets the value of the resourceAdapterName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setResourceAdapterName(String value) throws PropertyVetoException;

    /**
     * Gets the value of the connectionDefinitionName property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute(required = true)
    public String getConnectionDefinitionName();

    /**
     * Sets the value of the connectionDefinitionName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setConnectionDefinitionName(String value) throws PropertyVetoException;

    /**
     * Gets the value of the steadyPoolSize property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getSteadyPoolSize();

    /**
     * Sets the value of the steadyPoolSize property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSteadyPoolSize(String value) throws PropertyVetoException;

    /**
     * Gets the value of the maxPoolSize property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getMaxPoolSize();

    /**
     * Sets the value of the maxPoolSize property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMaxPoolSize(String value) throws PropertyVetoException;

    /**
     * Gets the value of the maxWaitTimeInMillis property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getMaxWaitTimeInMillis();

    /**
     * Sets the value of the maxWaitTimeInMillis property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMaxWaitTimeInMillis(String value) throws PropertyVetoException;

    /**
     * Gets the value of the poolResizeQuantity property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getPoolResizeQuantity();

    /**
     * Sets the value of the poolResizeQuantity property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPoolResizeQuantity(String value) throws PropertyVetoException;

    /**
     * Gets the value of the idleTimeoutInSeconds property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getIdleTimeoutInSeconds();

    /**
     * Sets the value of the idleTimeoutInSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIdleTimeoutInSeconds(String value) throws PropertyVetoException;

    /**
     * Gets the value of the failAllConnections property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getFailAllConnections();

    /**
     * Sets the value of the failAllConnections property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFailAllConnections(String value) throws PropertyVetoException;

    /**
     * Gets the value of the transactionSupport property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getTransactionSupport();

    /**
     * Sets the value of the transactionSupport property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTransactionSupport(String value) throws PropertyVetoException;

    /**
     * Gets the value of the isConnectionValidationRequired property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getIsConnectionValidationRequired();

    /**
     * Sets the value of the isConnectionValidationRequired property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIsConnectionValidationRequired(String value) throws PropertyVetoException;

    /**
     * Gets the value of the validateAtmostOncePeriodInSeconds property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getValidateAtmostOncePeriodInSeconds();

    /**
     * Sets the value of the validateAtmostOncePeriodInSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValidateAtmostOncePeriodInSeconds(String value) throws PropertyVetoException;

    /**
     * Gets the value of the connectionLeakTimeoutInSeconds property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getConnectionLeakTimeoutInSeconds();

    /**
     * Sets the value of the connectionLeakTimeoutInSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setConnectionLeakTimeoutInSeconds(String value) throws PropertyVetoException;

    /**
     * Gets the value of the connectionLeakReclaim property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getConnectionLeakReclaim();

    /**
     * Sets the value of the connectionLeakReclaim property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setConnectionLeakReclaim(String value) throws PropertyVetoException;

    /**
     * Gets the value of the connectionCreationRetryAttempts property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getConnectionCreationRetryAttempts();

    /**
     * Sets the value of the connectionCreationRetryAttempts property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setConnectionCreationRetryAttempts(String value) throws PropertyVetoException;

    /**
     * Gets the value of the connectionCreationRetryIntervalInSeconds property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getConnectionCreationRetryIntervalInSeconds();

    /**
     * Sets the value of the connectionCreationRetryIntervalInSeconds property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setConnectionCreationRetryIntervalInSeconds(String value) throws PropertyVetoException;

    /**
     * Gets the value of the lazyConnectionEnlistment property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getLazyConnectionEnlistment();

    /**
     * Sets the value of the lazyConnectionEnlistment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLazyConnectionEnlistment(String value) throws PropertyVetoException;

    /**
     * Gets the value of the lazyConnectionAssociation property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getLazyConnectionAssociation();

    /**
     * Sets the value of the lazyConnectionAssociation property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLazyConnectionAssociation(String value) throws PropertyVetoException;

    /**
     * Gets the value of the associateWithThread property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getAssociateWithThread();

    /**
     * Sets the value of the associateWithThread property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAssociateWithThread(String value) throws PropertyVetoException;

    /**
     * Gets the value of the matchConnections property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getMatchConnections();

    /**
     * Sets the value of the matchConnections property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMatchConnections(String value) throws PropertyVetoException;

    /**
     * Gets the value of the maxConnectionUsageCount property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getMaxConnectionUsageCount();

    /**
     * Sets the value of the maxConnectionUsageCount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMaxConnectionUsageCount(String value) throws PropertyVetoException;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Attribute
    public String getDescription();

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) throws PropertyVetoException;

    /**
     * Gets the value of the securityMap property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the securityMap property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecurityMap().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link SecurityMap }
     */
    @Element
    public List<SecurityMap> getSecurityMap();

    /**
     * Gets the value of the property property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     */
    @Element("property")
    public List<Property> getProperty();



}
