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