/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,  
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package javax.event;

import javax.inject.ExecutionException;

/**
 * Wraps any checked exceptions which occur during the notification of an
 * observer
 * 
 * @author Pete Muir
 */

public class ObserverException extends ExecutionException
{

   private static final long serialVersionUID = -801836224808304381L;

   public ObserverException()
   {
      
   }

   public ObserverException(String message)
   {
      super(message);
   }

   public ObserverException(Throwable cause)
   {
      super(cause);
   }

   public ObserverException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
