package org.odfi.wsb.fwapp.lib.security.provider.kerberos

import org.odfi.wsb.fwapp.framework.FWAppFrameworkView

trait KerberosAuthView extends FWAppFrameworkView {
  
  def authenticate = {
    println("Authentication for Kerberos")
  }
  
}