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

import org.odfi.indesign.core.module.IndesignModule
import com.idyria.osi.ooxoo.core.buffers.structural.AnyXList
import com.idyria.osi.ooxoo.core.buffers.datatypes.DateTimeBuffer

object SecurityLibModule extends IndesignModule {

  // Configs
  //------------------

  def getUsers = {
    this.config.get.custom.content.collect {
      case u: User => u
    }
  }

  /**
   * Save user
   */
  def saveUser(user: User) = synchronized {

    user.creationDate match {
      case null =>  user.creationDate = new DateTimeBuffer
      case other =>
    }
   
    user.lastUpdate = new DateTimeBuffer

    this.config.get.custom.content.contains(user) match {
      case true =>
      case false => 
        this.config.get.custom.content += user
    }
    

    this.config.get.resyncToFile

  }

  // Lifecycle
  //----------------
  this.onStart {

    AnyXList(classOf[User])

  }

}
