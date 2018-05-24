package org.odfi.wsb.fwapp.module.pdfjs

import org.odfi.wsb.fwapp.views.LibraryView
import java.net.URI
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.vui.html.Canvas
import org.w3c.dom.html.HTMLElement


@xelement
trait PDFJSMessage extends ElementBuffer with JSonUtilTrait {
  
  @xelement(name="targetId")
  var targetId : XSDStringBuffer = ""
  
}

@xelement
class PDFJSPageForward extends PDFJSMessage

@xelement
class PDFJSPageBackward extends PDFJSMessage

trait PDFJSView extends LibraryView {
  
  var pdfjsversion = "1.9.426"
  var pdfPage = 1
  
  this.addLibrary("pdfjs") {
    case (_,node) =>
      
      
      stylesheet(createDefaultAssetsResolverURI(s"/fwapp/external/pdfjs/$pdfjsversion/web/locale/locale.properties")) {
        +@("rel"->"resource")
        +@("type"->"application/l10n")
      }
      /*script(createDefaultAssetsResolverURI(s"/fwapp/external/pdfjs/$pdfjsversion/web/l10n.js")) {
        
      }*/
     /* script(createDefaultAssetsResolverURI(s"/fwapp/external/pdfjs/$pdfjsversion/web/compatibility.js")) {
        
      }*/
      script(createDefaultAssetsResolverURI(s"/fwapp/external/pdfjs/$pdfjsversion/build/pdf.js")) {
        
      }
      script(createDefaultAssetsResolverURI(s"/fwapp/lib/pdfjs/pdfjs.js")) {
        
      }
      
      script {s"""
      fwapp.pdfjs.appPath="${getApp.get.basePath}";
"""}
      
  }
  
  
  def pdfCanvas(id: String) : Canvas[HTMLElement,Canvas[_,_]] = {
     var c = canvas {

      this.id("pdfjs-" + id)
      /*+@("width" -> "600")
      +@("height" -> "300")*/

    }
   
    c
  }
   def pdfCanvas(url: URI, id: String = "canvas") : Canvas[HTMLElement,Canvas[_,_]]  = {
   
    var c = canvas {

      this.id("pdfjs-" + id)
      +@("data-url" -> url.toASCIIString())
      +@("page" -> pdfPage.toString)
      /*+@("width" -> "600")
      +@("height" -> "300")*/

    }
   
    c
   
  }
}