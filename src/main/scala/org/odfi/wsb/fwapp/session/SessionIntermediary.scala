package org.odfi.wsb.fwapp.session

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.odfi.wsb.fwapp.FWappIntermediary
import com.idyria.osi.wsb.webapp.http.session.Session


class SessionIntermediary extends FWappIntermediary("/") {
  
  //tlogEnableFull[SessionIntermediary]
  //tlogEnableFull[Session]
  
  this.onDownMessage {
    req => 
      logFine[SessionIntermediary]("Checking Session....")
      //req.getSession
  }
  
  //-- ON UP, make sure session is kept
  this.onUpMessage[HTTPResponse] {
    resp => 
      
      resp.relatedMessage match {
        case Some(req : HTTPRequest) if(req.hasSession) => 
          logFine[SessionIntermediary]("Propagating Session....")
          resp.session = req.session
        case other => 
      }
      
  }
  
  
}