package org.odfi.wsb.fwapp.security

import org.odfi.wsb.fwapp.rules.NavigationRule
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPCodes

class AuthenticatedRule extends NavigationRule {

  this.onDownMessage {
    req =>

      println(s"Running Authenticated rule for upped=${req.upped}:  "+req.originalPath)
      
      var res = req.hasSession match {

        case true if (req.getSession.get.findValueOfType[UserTrait].isDefined) => Some(req.getSession.get.findValueOfType[UserTrait].get._2)
        case other => None
      }
      res match {

        //-- Found User
        case Some(user) =>

        //-- Return error
        case other =>
          
          println(s"Denying access: "+req.originalPath)
          
          responseToRedirect(req)
          
         /* var resp = HTTPResponse()
          resp.code = HTTPCodes.Unauthorized // Un
          resp.setTextContent("Denied Access")
          
          response(resp,req)*/

      }

  }

}