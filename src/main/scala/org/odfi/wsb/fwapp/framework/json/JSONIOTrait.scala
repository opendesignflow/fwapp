package org.odfi.wsb.fwapp.framework.json

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.lib.json.JsonIO
import java.io.CharArrayWriter

trait JSONIOTrait extends ElementBuffer {
  
  def toJSonString = {
    
    var out = new CharArrayWriter
    var io = new JsonIO(outputArray=out)
    this.appendBuffer(io)
    this.streamOut()
    
    var res = io.finish
    
    this.cleanIOChain
    
    res
  }
  
}