package org.odfi.wsb.fwapp.framework

import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer

trait FWAppValueBufferView extends FWAppValueBindingView {
  
  /**
   * BindValue with Buffers
   */
  def bindBufferValue(vb: IntegerBuffer): Unit = {

    +@("value" -> vb.toString())
    
    this.bindValue {
      v: Int =>
        vb.set(v)
    }
  
  }

  /**
   * BindValue with Buffers
   */
  def bindBufferValue(vb: XSDStringBuffer): Unit = {

    +@("value" -> vb.toString())
    this.bindValue {
      v: String =>
        vb.data = v
        
    }

  }

  def bindBufferValue(vb: BooleanBuffer): Unit = {

    vb.data.booleanValue() match {
      case true =>
        +@("checked" -> "true")
      case false =>
    }

    this.bindValue {
      v: Boolean =>
        vb.data = v

    }

  }
  
}