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
  
   def inputToBuffer(vb:DoubleBuffer)(cl: => Any) : Input[HTMLElement,_]  = {
    input {
      bindBufferValue(vb)
      cl
    }
  }
  
  def inputToBuffer(vb:IntegerBuffer)(cl: => Any) : Input[HTMLElement,_] = {
    input {
      bindBufferValue(vb)
      cl
    }
  }
  
  def inputToBuffer(vb:BooleanBuffer)(cl: => Any) : Input[HTMLElement,_] = {
    input {
      bindBufferValue(vb)
      cl
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
  
  def selectToBuffer(values: List[(String,String)],vb:XSDStringBuffer)(cl: => Any)  = {
    
    
    // If actual value not in range, set to first
    values.find {
      case (name,v) => v==vb.toString()
    } match {
      case None => 
        vb.set(values(0)._1)
      case other => 
    }
    
    // Create Select
    select {
      
      //-- Set options
      values.foreach {
        case (name,v) => 
          
          option(v) {
            textContent(name)
            
            //-- Selected
            if (v==vb.toString()) {
              +@("selected"->true)
            }
          }
 
      }
      
      //-- Bind
      bindValue { sv : String => vb.set(sv) }
      
      //-- Config closure
      cl
      
    }
    
    
  }
  
}
