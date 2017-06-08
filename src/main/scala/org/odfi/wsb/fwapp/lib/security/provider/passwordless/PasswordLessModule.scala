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
package org.odfi.wsb.fwapp.lib.security.provider.passwordless

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.wsb.fwapp.lib.security.SecurityLibModule
import org.odfi.wsb.fwapp.lib.security.UserTraitFederatedIdentity
import com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer
import java.util.UUID

object PasswordLessModule extends IndesignModule {

  var availableTokens = Map[String, UserTraitFederatedIdentity]()

  def generateToken(email: String) = synchronized {

    var id = new UserTraitFederatedIdentity
    id.providerID = "passwordless"
    id.validity = DateTimeBuffer()
    id.validity.addMinutes(5)
    id.token = UUID.randomUUID().toString()

    availableTokens = availableTokens.updated(email, id)

    id

  }
  def checkToken(token: String) = synchronized {

    availableTokens.find {
      case (email, id) =>
        println("PL testing: "+id.token.toString()+" against: "+token)
        id.token.toString() == token

    } match {

      /*case Some((email,id)) if (id.token.toString != token) =>
        availableTokens = availableTokens - email
        sys.error("Email and Token don't match")*/
      case Some((email, id)) if (id.validity.isBeforeNow) =>
        availableTokens = availableTokens - email
        sys.error("Token not valid anymore")
      case Some((email, id)) =>
        availableTokens = availableTokens - email
        (email, id.token.toString)
      case None =>
        sys.error("No Token available, please generate one before authentication")

    }

  }

  this.onLoad {
    requireModule(SecurityLibModule)
  }
}
