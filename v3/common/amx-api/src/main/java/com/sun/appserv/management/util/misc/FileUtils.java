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
 
/*
 * $Header: /cvs/glassfish/appserv-api/src/java/com/sun/appserv/management/util/misc/FileUtils.java,v 1.2 2007/05/05 05:31:05 tcfujii Exp $
 * $Revision: 1.2 $
 * $Date: 2007/05/05 05:31:05 $
 */
 

package com.sun.appserv.management.util.misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
	The API that should be used to output from a Cmd running within the framework.
 */
public final class FileUtils
{
	private	FileUtils()	{}
	
	
		public static String
	fileToString( final File src )
		throws FileNotFoundException, IOException
	{
	    return fileToString( src, 32 * 1024 );
	}
	
		public static String
	fileToString( final File src, final int readBufferSize )
		throws FileNotFoundException, IOException
	{
		final FileReader	in	= new FileReader( src );

		final long  length  = src.length();
		if ( length > 1024 * 1024 * 1024 )
		{
		    throw new IllegalArgumentException();
		}
		
		final char[]	readBuffer	= new char[ readBufferSize ];
		
		final StringBuilder	result	= new StringBuilder( (int)length );
		try
		{
			while ( true )
			{
				final int numRead	= in.read( readBuffer, 0, readBufferSize );
				if ( numRead < 0 )
					break;
				
				result.append( readBuffer, 0, numRead );
			}
		}
		finally
		{
			in.close();
		}
		
		return( result.toString() );
	}
	
		public static void
	stringToFile( final String s, final File dest )
		throws IOException
	{
		final FileWriter	out	= new FileWriter( dest );
		
		try
		{
			out.write( s );
		}
		finally
		{
			out.close();
		}
	}
};


