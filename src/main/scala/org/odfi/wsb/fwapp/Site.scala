
package org.odfi.wsb.fwapp

import com.idyria.osi.wsb.core.WSBEngine
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import org.odfi.indesign.core.module.IndesignModule
import org.odfi.wsb.fwapp.assets.AssetsResolver
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import org.odfi.wsb.fwapp.session.SessionIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import org.apache.http.HttpResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.osi.wsb.core.broker.tree.Intermediary
import org.odfi.wsb.fwapp.assets.AssetsSource

/**
 * Site is just a semantic extension
 */
class Site(basePath: String) extends FWappIntermediary(basePath) with FWappApp {

  var siteName: Option[String] = None

  def setSiteName(str: String) = siteName = Some(str)

  override def getDisplayName = siteName match {
    case Some(name) => name
    case None       => getClass.getSimpleName.replace("$", "")
  }

  this.engine.broker <= this

  //-- Local Builder adds to pretree
  this <= this.preTree
  //this <= this.preTree
  setTopBuildIntermediary(this.preTree)

  override def <=[I <: Intermediary](i: I) = i match {
    case pt if (pt == this) =>
      i
    case pt if (pt == this.preTree) =>
      super.<=(i)
    case other =>
      this.preTree <= i
  }

  this.onIntermediaryAdded[FWappIntermediary] {
    i =>
      println(s"Added intermediary to Site: " + i)
  }

  this.onClean {
    println("Removing Site from parent")
    this.parentIntermediary match {
      case null =>
      case other =>
        other -= this
      // other.intermediaries -= this
    }

  }

  this.onDownMessage {
    req =>

      logInfo[Site]("Got request for site: " + getId + " -> " + req.path + " under: " + this.parentIntermediary)

      this.parentIntermediary match {
        case p: FWappIntermediary =>

          logInfo[Site]("Dispatching to subtree skipping pretree ")
          down(req)

        /*this.tree match {

            case Some(tree) =>

              logInfo[Site]("Dispatching " + getId)

            case None =>

              logInfo[Site]("NO tree was defined..." + getId)
              var resp = HTTPResponse.c404
              resp.setTextContent(s"The site at ${basePath} has no application handlers defined")
              response(resp,req)

          }*/

        case other =>

          logInfo[Site]("Dispatching to  pretree: " + this.intermediaries.size)
          //this.preTree.down(req)
          down(req)

      }

  }

  // Assets Helper
  //----------------
  def useDefaultAssets = {
    "/assets" uses new AssetsResolver
  }

  // Error Helper
  //------------------

  // Lifecycle
  //-----------
  this.onInit {

    this.findParentOfType[Site] match {
      case Some(found) =>
      case None =>
        this.engine.lInit
    }

  }
  this.onStart {

    this.findParentOfType[Site] match {
      case Some(found) =>
      //println("Found parent site: "+found)
      case None =>
        this.engine.lStart

        this.engine.network.connectors.foreach {
          case hc: HTTPConnector =>

            println("Website " + getDisplayName + s" available 2 at: http://localhost:${hc.port}${this.basePath}")

          case other =>

            println("Website " + getDisplayName + "is master with no HTTP Connector, did you forget a call to listen(port) ?")
        }
    }

    /* this.parentIntermediary match {
      case null =>
        println("Starting FWApp")
        //AssetsManager
        this.engine.lStart
      case other =>
        println("Parent i: "+this.parentIntermediary)

    }*/

  }

  this.onStop {
    this.engine.lStop
  }
}
