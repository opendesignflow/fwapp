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
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.fs.FileBuffer
import java.io.File
import com.idyria.osi.ooxoo.core.buffers.datatypes.URIBuffer
import java.net.URI
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer

trait FWappResourceValueBindingView extends FWAppValueBufferView {

    // Bind Delays
    //------------------
    def inputBindAfter500MS[V: ClassTag](cl: V => Unit) = inputBindAfterDelay(500)(cl)

    def inputBindAfterDelayInit[V: ClassTag](i: V)(delayMs: Int = 1000)(cl: V => Unit) = {

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

    /**
     * Updated buffer after 500ms and runs provided closure
     */
    def inputToBufferAfter500MS(b: XSDStringBuffer) = {
        inputBindAfter500MSInit(b) {
            str =>
                b.set(str)
        }
    }

    def inputToBufferAfter500MS(b: IntegerBuffer) = {
        inputBindAfter500MSInit(b) {
            v =>
                b.set(v)
        }
    }
    
    def inputToBufferAfter500MS(b: LongBuffer) = {
        inputBindAfter500MSInit(b) {
            v =>
                b.set(v)
        }
    }

    def inputToBufferAfter500MS(b: DoubleBuffer) = {
        inputBindAfter500MSInit(b) {
            v =>
                b.set(v)
        }
    }

    def inputToBufferAfter500MS(b: URIBuffer) = {
        inputBindAfter500MSInit(b) {
            v =>
                println("Updating URI Buffer")
                b.set(v)
        }
    }

    def inputToBufferAfter500MS(b: FileBuffer) = {
        inputBindAfter500MSInit(b) {
            f =>
                println("Setting FIle bufdder: " + f)
                b.set(f)
        }
    }

    /**
     * Updated buffer after 500ms and runs provided closure
     */
    def inputToBufferAfter500MSAnd(b: XSDStringBuffer)(cl: => Any) = {
        inputBindAfter500MSInit(b) {
            str =>
                b.set(str)
                cl
        }
    }

    def inputToBufferAfter500MSAnd(b: FileBuffer)(cl: => Any) = {
        inputBindAfter500MSInit(b) {
            str =>
                b.set(str)
                cl
        }
    }
    
     def inputToBufferAfter500MSAnd(b: DoubleBuffer)(cl: => Any) = {
        inputBindAfter500MSInit(b) {
            str =>
                b.set(str)
                cl
        }
    }

    def inputBindAfter500MSInit(init: XSDStringBuffer)(cl: String => Unit) = {
        val realInit = init match {
            case null  => null
            case other => other.toString()
        }
        inputBindAfterDelayInit(realInit)(500)(cl)
    }

    def inputBindAfter500MSInit(init: URIBuffer)(cl: URI => Unit) = {
        val realInit = init.toString
        inputBindAfterDelayInit(realInit)(500) {
            v =>
                cl(new URI(v))
        }
    }

    def inputBindAfter500MSInit(init: IntegerBuffer)(cl: Int => Unit) = {
        val realInit = init.data

        inputBindAfterDelayInit(realInit)(500)(cl)
    }
    
    def inputBindAfter500MSInit(init: LongBuffer)(cl: Long => Unit) = {
        val realInit = init.data

        inputBindAfterDelayInit(realInit)(500)(cl)
    }

    def inputBindAfter500MSInit(init: DoubleBuffer)(cl: Double => Unit) = {
        val realInit = init.data

        inputBindAfterDelayInit(realInit)(500)(cl)
    }

    def inputBindAfter500MSInit(init: FileBuffer)(cl: File => Unit) = {
        val realInit = init.data

        inputBindAfterDelayInit(realInit)(500)(cl)
    }

}
