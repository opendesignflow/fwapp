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
package org.odfi.wsb.fwapp.lib.indesign

import org.odfi.wsb.fwapp.framework.FWAppValueBufferView
import scala.reflect.ClassTag
import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.heart.DefaultHeartTask
import org.odfi.indesign.core.heart.Heart
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

trait FWappResourceValueBindingView extends FWAppValueBufferView {

  // Bind Delays
  //------------------
  def inputBindAfter500MS[V: ClassTag](cl: V => Unit) = inputBindAfterDelay(500)(cl)

  def inputBindAfterDelayInit[V: ClassTag](i:V)(delayMs: Int = 1000)(cl: V => Unit) = {
    
    onNode(inputBindAfterDelay(delayMs)(cl)) {
      
      i match {
        case null => 
        case i if (i.isInstanceOf[Boolean] && i.asInstanceOf[Boolean] == true) => 
           +@("checked" -> true)
           +@("selected" -> true)
        case i if (i.isInstanceOf[Boolean]) => 
        case i => 
          +@("value" -> i.toString())
      }
     
    }
    
  }
  def inputBindAfterDelay[V: ClassTag](delayMs: Int = 1000)(cl: V => Unit) = {

    val task = new DefaultHeartTask {
      this.scheduleDelay = delayMs
      var value: Option[V] = None

      def doTask = {
        value match {
          case Some(v) =>
            println("Running Value Update closure")
            cl(v)
          case None =>
        }
      }
    }

    // Make input and bind
    inputBind[V] {
      value =>
        println("Rescheduling task")
        task.value = Some(value)
        Heart.repump(task)
    }
  }

  // Utils to work with buffers
  //-------------
  
  def inputToBufferAfter500MS(b:XSDStringBuffer) = {
    inputBindAfter500MSInit(b) {
      str => 
        b.set(str)
    }
  }
  
  def inputBindAfter500MSInit(init: XSDStringBuffer)(cl: String => Unit) = {
    val realInit = init match {
      case null => null
      case other => other.toString()
    }
    inputBindAfterDelayInit(realInit)(500)(cl)
  }

}
