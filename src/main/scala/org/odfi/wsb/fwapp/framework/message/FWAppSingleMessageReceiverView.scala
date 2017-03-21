package org.odfi.wsb.fwapp.framework.message

import org.odfi.wsb.fwapp.views.FWappView
import com.idyria.osi.wsb.core.broker.tree.single.SingleMessage
import scala.reflect.ClassTag
import com.idyria.osi.wsb.core.broker.tree.single.SingleMessageIntermediary

trait FWAppSingleMessageReceiverView extends FWappView {

  def singleMessageReceive[MT <: SingleMessage](onlyLast: Boolean = false, delayMS: Long = 100)(cl: (MT => Unit))(implicit tag: ClassTag[MT]) = {

    println("********** Preparing Single Message Receiver")
    
    val int = new SingleMessageIntermediary[MT] {

      this.onDownMessage {
        message =>

          if (onlyLast == false || (onlyLast && message.lastInTrain)) {

            cl(message)

            Thread.sleep(delayMS)

          }

      }
    }

    this.getIntermediaryTreeTop match {
      case Some(top) if(top!=this) =>

        println("********** Found Intermediary Tree Top to add to: "+top)
        top <= int

      //-- If not top parent found, add it iself
      case other =>
        println("********** Not Found Intermediary Tree Top to add to: ")
        this.onParentResourceAdded {
          getIntermediaryTreeTop match {
            case Some(top) =>
              top <= int
            case None =>
          }
        }
    }

  }

  /* LocalWebEngine.broker <= new SingleMessageIntermediary[MT] {
      this.onDownMessage {
        message =>

          LocalWebHTMLVIew.this.request match {
            case Some(_) if (onlyLast == false || (onlyLast && message.lastInTrain)) =>

              cl(message)

              Thread.sleep(delayMS)

            case None =>
          }

      }
    }*/

}