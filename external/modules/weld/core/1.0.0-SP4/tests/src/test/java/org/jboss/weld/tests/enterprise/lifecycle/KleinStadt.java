package org.jboss.weld.tests.enterprise.lifecycle;

import javax.ejb.Local;

@Local
public interface KleinStadt
{
   public void begruendet();
   
   public void zustandVergessen();
   
   public void zustandVerloren();
   
}
