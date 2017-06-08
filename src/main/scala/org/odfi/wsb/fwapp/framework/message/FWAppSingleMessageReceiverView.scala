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
package org.odfi.wsb.fwapp.framework.message

import org.odfi.wsb.fwapp.views.FWappView
import com.idyria.osi.wsb.core.broker.tree.single.SingleMessage
import scala.reflect.ClassTag
import com.idyria.osi.wsb.core.broker.tree.single.SingleMessageIntermediary

trait FWAppSingleMessageReceiverView extends FWappView {

  def singleMessageReceive[MT <: SingleMessage](onlyLast: Boolean = false, delayMS: Long = 100)(cl: (MT => Unit))(implicit tag: ClassTag[MT]) = {

    //println("********** Preparing Single Message Receiver")
    
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

        //println("********** Found Intermediary Tree Top to add to: "+top)
        top <= int

      //-- If not top parent found, add it iself
      case other =>
        //println("********** Not Found Intermediary Tree Top to add to: ")
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
