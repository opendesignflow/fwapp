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
