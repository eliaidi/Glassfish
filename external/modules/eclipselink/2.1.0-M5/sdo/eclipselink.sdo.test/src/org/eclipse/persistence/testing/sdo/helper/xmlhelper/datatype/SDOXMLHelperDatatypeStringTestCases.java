/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
* 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeStringTestCases extends SDOXMLHelperDatatypeTestCase {
    
	public SDOXMLHelperDatatypeStringTestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeStringTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
    	return String.class;
    }    
    
    protected SDOType getValueType() {
    	return SDOConstants.SDO_STRING;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myString-1.xml");
    }

    protected String getControlRootURI() {
        return "myString-NS";
    }

    protected String getControlRootName() {
        return "myString";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myString.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myString-builtin.xsd";
    }

}
