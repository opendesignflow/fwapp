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
package org.odfi.wsb.fwapp.lib.security

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import org.odfi.wsb.fwapp.FWappIntermediary
import scala.reflect.ClassTag

class Authenticator extends FWappIntermediary("/") {
  
  def withProvider[AP <: AuthenticationProvider : ClassTag](cl: AP => Any) = {
    
    this.getSite match {
      case Some(site) =>
        site.findDerivedResourceOfType[AP] match {
          case Some(ap) =>
            
            cl(ap)
            
          case None => 
        }
        
      case None => 
    }
    
  }
  
}
