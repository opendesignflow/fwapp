package org.odfi.wsb.fwapp.views

import com.idyria.osi.vui.html.Html
import com.idyria.osi.vui.html.Head
import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Body
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.assets.AssetsSource
import scala.collection.mutable.LinkedHashMap

trait LibraryView extends FWappView {

  var libraries = new LinkedHashMap[String, List[(Option[AssetsSource], HTMLNode[HTMLElement, Any]) => Any]]();
  var librariesPlaced = false

  /*def addLibrary(cl: HTMLNode[HTMLElement, Any] => Any) = {
    
  }*/

  def addLibrary(name: String)(cl: (Option[AssetsSource], HTMLNode[HTMLElement, Any]) => Any): Unit = {

    //-- Look for list
    var closures = libraries.get(name) match {
      case Some(lst) => lst
      case None => List()
    }

    //-- Update
    closures = closures :+ cl

    //-- Save
    libraries += (name -> closures)
  }

  def placeLibraries = {

    // Check Libraries
    this.libraries.foreach {
      case (lib, builders) =>

        getAssetsResolver match {
          case Some(resolver) if (resolver.findAssetsSource(lib).isDefined) =>
            builders.foreach(_(Some(resolver.findAssetsSource(lib).get), currentNode))
          case other =>
            builders.foreach(_(None, currentNode))
        }

      /*AssetsManager.findAssetSource(lib) match {
          case Some(source) =>
            builders.foreach(_(Some(source), currentNode))

          case None =>
            builders.foreach(_(None, currentNode))
        }*/
    }
    librariesPlaced = true
  }

  override def render: HTMLNode[HTMLElement, HTMLNode[HTMLElement, _]] = {

    // Let Main Rendereing Chain happen
    var result = super.render

    //println("Library view main render done:" +result)

    if (!librariesPlaced) {
      // Add Scripts/Stylesheet depending on result
      var (contentNode, targetNode) = result match {

        // HTML: Look for Head; if none; add to result node
        case h: Html[_, _] =>

          var target = h.children.find {
            case n if (classOf[Head[HTMLElement, _]].isInstance(n)) => true
            case _ => false
          } match {
            case Some(head) => head.asInstanceOf[Head[HTMLElement, _]]
            case None =>
              result
          }

          var content = h.children.find {
            case n if (classOf[Body[HTMLElement, _]].isInstance(n)) => true
            case _ => false
          } match {
            case Some(body) => body.asInstanceOf[Body[HTMLElement, _]]
            case None =>
              result
          }

          (content, target)

        // Others: Add to result node
        case _ => (result, result)

      }

      // Add
      // Reverse and add to first nodes
      this.libraries.foreach {

        case (lib, builders) =>

          getAssetsResolver match {
            case Some(resolver) if (resolver.findAssetsSource(lib).isDefined) =>
              builders.foreach(_(Some(resolver.findAssetsSource(lib).get), currentNode))
            case other =>
              builders.foreach(_(None, currentNode))
          }

        /*AssetsManager.findAssetSource(lib) match {
            case Some(source) =>
              builders.foreach(_(Some(source), currentNode))

            case None =>
              builders.foreach(_(None, currentNode))

              onNode(contentNode) {
                "warning" :: div {
                  text(s"Library $lib cannot be provided through assets manager, maybe a configuration is missing")
                }
              }

          }*/
      }
    }
    //  Add scripts to target
    // externalAdd(targetNode)

    /*switchToNode(targetNode, {
      
      stylesheet(new URI(s"${viewPath}/resources/semantic/semantic.min.css".noDoubleSlash)) {

      }
  
      script(new URI(s"${viewPath}/resources/semantic/semantic.min.js".noDoubleSlash)) {

      }
    })*/

    // Return
    result
  }

}