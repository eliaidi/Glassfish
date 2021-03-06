/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 20/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

//@XmlRegistry
public class ObjectFactory2 {
    //@XmlElementDecl(scope=FooBar.class,name="foo")
    public JAXBElement<String> createFooBarFoo(String s) {
        return new JAXBElement<String>(new QName("foo"), String.class, FooBar.class, s);
    }
 
    //@XmlElementDecl(scope=FooBar.class,name="bar")
    public JAXBElement<String> createFooBarBar(String s) {
        return new JAXBElement<String>(new QName("bar"), String.class, FooBar.class, s);
    }
 
    //@XmlElementDecl(name="foo")
    public JAXBElement<Integer> createFoos(Integer i) {
        return new JAXBElement<Integer>(new QName("foos"), Integer.class, i);
    }
}
