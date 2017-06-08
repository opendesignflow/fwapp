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
