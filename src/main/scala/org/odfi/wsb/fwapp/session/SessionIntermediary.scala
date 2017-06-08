/*-
 * #%L
 * FWAPP Framework
 * %%
 * Copyright (C) 2016 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
