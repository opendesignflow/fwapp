package org.odfi.wsb.fwapp.lib.security

import org.odfi.indesign.core.harvest.HarvestedResource

class IdentityController extends HarvestedResource {
  
  def getId = getClass.getCanonicalName
  
  def addAuthenticationProvider[ AP <:AuthenticationProvider](p: AP) = {
    this.addDerivedResource(p)
    p
  }
  
}