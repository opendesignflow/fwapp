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
package org.odfi.wsb.fwapp.views

import com.idyria.osi.vui.html.Html
import com.idyria.osi.vui.html.Head
import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.Body
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.assets.AssetsSource
import scala.collection.mutable.LinkedHashMap
import com.idyria.osi.vui.html.Script
import com.idyria.osi.vui.html.Stylesheet

trait LibraryView extends FWappView {

  var libraries = new LinkedHashMap[String, List[(Option[AssetsSource], HTMLNode[HTMLElement, Any]) => Any]]();


 var librariesPlaced = false
  var librariesPlacedLocation: Option[HTMLNode[HTMLElement, _]] = None

  /*def addLibrary(cl: HTMLNode[HTMLElement, Any] => Any) = {
    
  }*/

  def hasLibrary(name: String) = libraries.keySet.contains(name)

  def addLibrary(name: String)(cl: (Option[AssetsSource], HTMLNode[HTMLElement, Any]) => Any): Unit = {

    //val  targetView = getTopViewOrSelfAs[LibraryView] 
    val targetView = this

    //-- Look for list
    var closures = targetView.libraries.get(name) match {
      case Some(lst) => lst
      case None      => List()
    }

    //-- Update
    closures = closures :+ cl

    //println("Adding CL for: "+name)

    //-- Save
    targetView.libraries += (name -> closures)
  }

  /**
   * If the placement location is defined, remove duplicate scripts and such
   */
  def cleanupLibraries = this.librariesPlacedLocation match {
    case Some(location) =>

      val scriptsAndStyle = location.children.filter {
        case s: Script[_, _]     => s.hasAttribute("src")
        case s: Stylesheet[_, _] => s.hasAttribute("href")
        case other               => false
      }

      // group by location and only keep one of duplicates
      scriptsAndStyle.groupBy {
        case s: Script[_, _]     => s.attribute("src")
        case s: Stylesheet[_, _] => s.attribute("href")
      }.foreach {
        case (loc, nodes) => nodes.drop(1).foreach(_.detach)
      }

    case None =>
  }

  def placeLibraries = {

    librariesPlacedLocation = Some(currentNode)

    // Check Libraries
    try {

      // Real target node cna be local or in the parent's placement location
      ///val realTargetNode = currentNode
      val realTargetNode = getTopView[LibraryView] match {
        case Some(parentLibView) if (parentLibView.librariesPlacedLocation.isDefined) =>
          //println("Found Top View")
          parentLibView.librariesPlacedLocation.get
        case other =>
          currentNode

      }

      //println(s"calling place libraries on: " + realTargetNode.hashCode() + " -> " + currentNode.children.size)

      onNode(realTargetNode) {
        this.libraries.foreach {
          case (lib, builders) =>

            // println("Placing: "+lib)
            getAssetsResolver match {
              case Some(resolver) if (resolver.findAssetsSource(lib).isDefined) =>
                builders.foreach(_(Some(resolver.findAssetsSource(lib).get), realTargetNode))
              case other =>
                builders.foreach(_(None, realTargetNode))
            }

          /*AssetsManager.findAssetSource(lib) match {
          case Some(source) =>
            builders.foreach(_(Some(source), currentNode))

          case None =>
            builders.foreach(_(None, currentNode))
        }*/
        }
      }

      // Cleanup libraries to avoid duplicates
      getTopViewOrSelfAs[LibraryView].cleanupLibraries

    } catch {
      case e: Throwable => e.printStackTrace()
    }
   // println(s"done calling place libraries on: " + currentNode.hashCode() + " -> " + currentNode.children.size)
    librariesPlaced = true
  }

  override def render: HTMLNode[HTMLElement, HTMLNode[HTMLElement, _]] = {

    // Let Main Rendereing Chain happen
    var result = super.render

    //println(s"Library view main render on ${getClass} done: " + result.hashCode())

    if (!librariesPlaced) {

      //println(s"Library view on ${getClass} placing libraries")

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
      onNode(targetNode) {
        placeLibraries
      }
      /*this.libraries.foreach {

        case (lib, builders) =>

          getAssetsResolver match {
            case Some(resolver) if (resolver.findAssetsSource(lib).isDefined) =>
              builders.foreach(_(Some(resolver.findAssetsSource(lib).get), currentNode))
            case other =>
              builders.foreach(_(None, currentNode))
          }
      }*/
    }

    // Return
    result
  }

}
