package org.odfi.wsb.fwapp.lib.security.rules

import org.odfi.wsb.fwapp.navigation.NavigationRule
import org.odfi.wsb.fwapp.lib.security.views.SecurityHelper

class AuthenticatedRule extends NavigationRule with SecurityHelper {

  this.onDownMessage {
    req =>

      //println(s"Running Authenticated rule for upped=${req.upped}:  " + req.originalPath)

      securitySessionGetUser(Some(req)) match {
        case Some(user) =>
          //println(s"Authenticated ok")
        case None =>
          
          //println(s"Denying access: " + req.originalPath)
          responseToRedirect(req)
      }
  }

}