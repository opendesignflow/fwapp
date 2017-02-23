package org.odfi.wsb.fwapp.security.session

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.odfi.wsb.fwapp.FWappIntermediary


class SessionIntermediary extends FWappIntermediary("/") {
  
  
  //-- ON UP, make sure session is kept
  this.onUpMessage[HTTPResponse] {
    resp => 
      
      resp.relatedMessage match {
        case Some(req : HTTPRequest) if(req.hasSession) => 
          resp.session = req.session
        case other => 
      }
      
  }
  
  
}