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
  def inputBindAfter500MSInit(init: XSDStringBuffer)(cl: String => Unit) = {
    val realInit = init match {
      case null => null
      case other => other.toString()
    }
    inputBindAfterDelayInit(realInit)(500)(cl)
  }

}