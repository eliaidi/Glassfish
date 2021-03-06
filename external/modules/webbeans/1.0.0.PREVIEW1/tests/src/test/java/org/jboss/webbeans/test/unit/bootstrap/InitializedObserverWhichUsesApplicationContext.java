package org.jboss.webbeans.test.unit.bootstrap;

import javax.event.Observes;
import javax.inject.Current;
import javax.inject.manager.Initialized;
import javax.inject.manager.Manager;

class InitializedObserverWhichUsesApplicationContext
{
   
   @Current Cow cow;
   
   public void observeInitialized(@Observes @Initialized Manager manager)
   {
      cow.moo();
   }
   
}
