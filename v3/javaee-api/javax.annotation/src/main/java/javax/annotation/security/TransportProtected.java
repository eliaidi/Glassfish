/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the "License").  You may not use this file except 
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt or 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html. 
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * HEADER in each file and include the License file at 
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable, 
 * add the following below this CDDL HEADER, with the 
 * fields enclosed by brackets "[]" replaced with your 
 * own identifying information: Portions Copyright [yyyy] 
 * [name of copyright owner]
 */

/*
 *
 * Copyright 2005-2009 Sun Microsystems, Inc. All Rights Reserved.
 */


package javax.annotation.security;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * This annotation indicates whether a secure transport, such as TLS 
 * or SSL, is required to access the annotated methods. The value of the
 * annotation is a boolean that indicates, when true, that a secure
 * transport is required, and when false, that a secure transport is not
 * requried. 
 *
 * @since Common Annotations 1.1
 */

@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface TransportProtected {
     boolean value() default true;
}
