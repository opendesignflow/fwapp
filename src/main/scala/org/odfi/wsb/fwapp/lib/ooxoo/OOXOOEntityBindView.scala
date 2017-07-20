package org.odfi.wsb.fwapp.lib.ooxoo

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.tea.listeners.ListeningSupport
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import com.idyria.osi.ooxoo.core.utils.ReflectUtilsTrait



trait EntityBindBuffer extends ElementBuffer with ListeningSupport with ReflectUtilsTrait {
  
  // Utilities
  //-------------
  def entityListFields = {
    
    this.listElementAndAttributeFields
    //ScalaReflectUtils.listElementAndAttributeFields(this)
  }
  
  // Submission
  //----------------
  def triggerEntitySubmit = {
    this.@->("entity.submit")
  }
  def onEntitySubmit(cl: => Any) = {
     this.on("entity.submit") {
       cl
     }
  }
}