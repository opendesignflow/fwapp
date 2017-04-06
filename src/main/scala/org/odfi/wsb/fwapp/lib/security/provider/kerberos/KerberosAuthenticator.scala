package org.odfi.wsb.fwapp.lib.security.provider.kerberos

import org.odfi.wsb.fwapp.lib.security.Authenticator

class KerberosAuthenticator extends Authenticator {
  
  
  this.onDownMessage {
    req => 
      (req.getURLParameter("kerberos.principal"),req.getURLParameter("kerberos.password")) match {
        case (Some(login),Some(pw)) => 
          
          //-- Look for Provider
          withProvider[KerberosAuthProvider] {
            provider => 
              
              provider.authenticate(login, pw)
          }
          
          
        case other => 
      }
  }
}