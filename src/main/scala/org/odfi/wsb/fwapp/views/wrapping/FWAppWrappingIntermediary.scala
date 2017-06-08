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
package org.odfi.wsb.fwapp.views.wrapping

import org.odfi.wsb.fwapp.views.FWAppViewIntermediary
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.views.FWAppCatchAllView
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import org.jsoup.Jsoup
import com.idyria.osi.vui.html.jsoup.JSoupElementNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.views.FWappView
import org.odfi.wsb.fwapp.FWappIntermediary

class FWAppWrappingIntermediary extends FWappIntermediary("/") with FWAppCatchAllView {


  //-- Content to part
  var sourceIDToPartMapping = Map[String, String]()

  def mapSourceIDToPart(t: (String, String)) = {
    sourceIDToPartMapping = sourceIDToPartMapping + t
  }

  //-- Use Resource Resolver to get files
  var resourceResolver = new ResourcesAssetSource
  this <= resourceResolver

  var __htmlView: Option[Class[_ <: FWappView]] = None

  def htmlView_=(cl: Class[_ <: FWappView]) = {
    __htmlView = Some(cl)
  }
  def htmlView = __htmlView

  this.acceptAllDown
  this.onDownMessage {
    case req if (req.path.endsWith(".html")) =>

      // Resolve
      //resourceResolver.down(req)

      println(s"Got message: " + req)
      println("Searching in: " + this.resourceResolver.fileSources)

    case req =>
  }

  this.onUpMessage[HTTPResponse] {
    case msg if (msg.relatedMessage.get.asInstanceOf[HTTPRequest].path.endsWith(".html")) =>

      try {
        val p = msg.relatedMessage.get.asInstanceOf[HTTPRequest].path
        println(s"Transform HTML here: " + p)
        var str = new String(msg.content.array())

        //println(str)

        var htmlDocument = Jsoup.parse(str)

        //-- Create target view
        //-- Create target view
        this.htmlView match {
          case Some(viewClass) =>
            println("Found View class")

            // prepare view
            var viewInstance = viewClass.newInstance()
            viewInstance.request = Some(msg.relatedMessage.get.asInstanceOf[HTTPRequest])
            viewInstance.deriveFrom(this)

            // Map sources to  target parts
            this.sourceIDToPartMapping.foreach {
              case (source, target) =>

                var elements = htmlDocument.select("#" + source)
                elements.size() match {
                  case 0 =>

                  case other =>
                    println(s"Found Element: " + source)

                    var foundElement = elements.first()
                    foundElement.remove()

                    viewInstance.definePart(target) {
                      new JSoupElementNode[HTMLElement, JSoupElementNode[_, _]](foundElement)
                    }
                }

            }

            //-- Render and set output
            msg.htmlContent = viewInstance.rerender

            viewInstance.clean

          case None =>
        }

      } catch {
        case e: Throwable =>
          e.printStackTrace()
      }

    case msg =>
  }

}
