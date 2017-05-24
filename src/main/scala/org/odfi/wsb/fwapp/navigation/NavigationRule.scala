package org.odfi.wsb.fwapp.navigation

import org.odfi.wsb.fwapp.FWappIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import com.idyria.osi.wsb.webapp.http.message.HTTPCodes

class NavigationRule extends FWappIntermediary("/") with NavigationUtils {
  
  
  var redirectionLocation : Option[String] = None
  
  def redirection(str:String) = {
    this.redirectionLocation = Some(str)
    this
  }
  
  // Response
  //-----------
  def responseToRedirect(req:HTTPRequest) = redirectionLocation match {
    case Some(r) => 
      
      
      // Save Current View location
      //--------------
      stackViewLocation(req)
      
     // req.getSession.get("navigation.lastview")
      
      var resp = HTTPResponse()
      resp.code = HTTPCodes.Temporary_Redirect
      resp.addParameter("Location", createAppPath(r))
      
      
      
      
      response(resp,req)
      
    case None => 
      sys.error("Cannot call response to redirect if no redirection location is set")
  }
  
  
}