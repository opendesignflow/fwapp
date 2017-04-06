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