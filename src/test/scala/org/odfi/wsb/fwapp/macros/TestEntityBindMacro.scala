package org.odfi.wsb.fwapp.macros

import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import fmacros.EntityUtilsMTrait
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer

class TestModel extends ElementBuffer {
  
  var id : XSDStringBuffer = ""
  
}

class TestEntityBindMacro extends EntityUtilsMTrait{
  
  val m = new TestModel
  bindToEntity(m.id)
  
  
}