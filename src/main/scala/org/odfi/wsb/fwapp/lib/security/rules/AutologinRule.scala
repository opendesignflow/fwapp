package org.odfi.wsb.fwapp.lib.security.rules


import org.odfi.wsb.fwapp.lib.security.views.SecurityHelper
import org.odfi.wsb.fwapp.navigation.NavigationRule

class AutologinRule(user:String) extends NavigationRule with SecurityHelper {
  
  this.onDownMessage {
    req => 
      
      //securitySessionSignout(Some(req))
      securitySessionGetUser(Some(req)) match {
        case Some(user) => 
        case None => 
          securityGetUserByEmail(user) match {
            case Some(user) =>
              securitySessionSetUser(Some(req), user)
            case None => 
          }
      }
      
  }
  
}