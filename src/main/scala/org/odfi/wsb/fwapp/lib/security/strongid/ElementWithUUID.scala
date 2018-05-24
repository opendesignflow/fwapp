package org.odfi.wsb.fwapp.lib.security.strongid

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.id.UUIDBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute

trait ElementWithUUID extends ElementBuffer {
  
  @xattribute(name="UUID")
  var uuid : UUIDBuffer= _
  
  def uuidOption = uuid match {
    case null => None
    case other => Some(other)
  }
  
  def initUUID = {
    this.uuid = UUIDBuffer()
  }
  
}