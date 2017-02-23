package org.odfi.wsb.fwapp.security.provider.kerberos

import org.odfi.indesign.core.harvest.HarvestedResource

class KerberosAuthProvider(val realm : String) extends HarvestedResource {
  
  def getId = s"${getClass.getCanonicalName}@$realm"
  
}