package org.odfi.wsb.fwapp.framework

import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Input

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
  def bindBufferValue(vb: DoubleBuffer): Unit = {

    +@("value" -> vb.toString())
    
    this.bindValue {
      v: Double =>
       // println(s"Updating bound value")
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
        vb.set(v)
        
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
        vb.set(v)

    }

  }
  
  def inputToBufferWithlabel(name:String,vb:DoubleBuffer)(cl: => Any) : Input[HTMLElement,_]  = {
    input {
      label(name) {
        
      }
      bindBufferValue(vb)
      cl
    }
  }
  
  def inputToBufferWithlabel(name:String,vb:IntegerBuffer)(cl: => Any) : Input[HTMLElement,_] = {
    input {
      label(name) {
        
      }
      bindBufferValue(vb)
      cl
    }
  }
  
  def inputToBufferWithlabel(name:String,vb:BooleanBuffer)(cl: => Any) : Input[HTMLElement,_] = {
    input {
      label(name) {
        
      }
      bindBufferValue(vb)
      cl
    }
  }
  
}