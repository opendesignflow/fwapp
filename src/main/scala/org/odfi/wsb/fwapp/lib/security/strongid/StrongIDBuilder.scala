package org.odfi.wsb.fwapp.lib.security.strongid

import com.idyria.osi.ooxoo.model.ModelBuilder

trait StrongIDBuilder extends ModelBuilder {
  
  
  def withUUID = {
    withTrait(classOf[ElementWithUUID])
  }
}