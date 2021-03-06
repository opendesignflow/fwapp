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
package org.odfi.wsb.fwapp.errors

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

class Handle404  extends HTTPIntermediary {
  
  this.acceptDown[HTTPRequest] {
    req => 
      //println("Testing 404 against: "+req.path+s", upped=${req.upped} && errors=${req.hasErrors}")
      req.upped==false && req.hasErrors==false
  }
  
  
  this.onDownMessage {
    req => 
      println("404 for : "+req.path)
      var r = HTTPResponse.c404
      r.contentType = "text/plain"
      r.setTextContent("Not Found: "+req.originalPath)
      response(r,req) 
  }
}
