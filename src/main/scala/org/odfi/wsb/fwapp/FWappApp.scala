package org.odfi.wsb.fwapp

import com.idyria.osi.wsb.core.WSBEngine
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import org.odfi.indesign.core.module.IndesignModule
import org.odfi.wsb.fwapp.assets.AssetsResolver
import org.odfi.wsb.fwapp.assets.AssetsManager
import org.odfi.wsb.fwapp.assets.ResourcesAssetSource
import org.odfi.wsb.fwapp.security.session.SessionIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import org.apache.http.HttpResponse
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse

trait FWappApp extends IndesignModule with org.odfi.wsb.fwapp.FWappTreeBuilder {

  override def getDisplayName = getClass.getName.replace("$", "")

  /**
   * WSB Engine used for operations
   */
  var engine = new WSBEngine

  /**
   * The PreTree is added to the broker before the main tree
   */
  var preTree = new SessionIntermediary

  /**
   * Main tree
   */
  var tree: Option[FWappIntermediary] = None

  /**
   * Assets resolver declared in app...useful to create dynamic paths
   */
  var assetsResolver: Option[AssetsResolver] = None

  /**
   * use provided tree as Application tree
   */
  def mergeTree(t: FWappIntermediary) = {

    //-- Save
    t.deriveFrom(this)
    tree = Some(t)
    preTree <= t

    //-- Find Assets Resolver to add default framework
    t.on("updated") {
      t.findChildOfType[AssetsResolver] match {
        case Some(resolver) =>
          println(s"Found AssetsResolver")
          AssetsManager.addAssetsSource("fwapp", new ResourcesAssetSource("")).addFilesSource("fwapp")
          assetsResolver = Some(resolver)

        case None =>

          println(s"Cannot find AssetsResolver")
      }
    }

    //-- Return
    t
  }
  // var appTree = new FWappTree

  def listen(port: Int) = {

    var conn = new HTTPConnector(port)
    engine.network.addConnector(conn)
    conn

  }

  // Lifecycle
  //-----------
  this.onInit {
    this.engine.lInit
  }
  this.onStart {

    println("Starting FWApp")
    //AssetsManager
    this.engine.lStart
  }
  
  this.onStop {
    this.engine.lStop
  }

  this.onClean {
    //println("Removing from processing")
  }

}

/**
 * Site is just a semantic extension
 */
class Site(basePath: String) extends FWappIntermediary(basePath) with FWappApp {

  this.engine.broker <= this
  
  //-- Local Builder adds to pretree
  this <= this.preTree
  setTopBuildIntermediary(this.preTree)
  
  this.onIntermediaryAdded[FWappIntermediary] {
    i => 
      println(s"Added intermediary to Site: "+i)
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

          logInfo[Site]("Dispatching to  pretree: "+this.intermediaries.size)
          //this.preTree.down(req)
          down(req)

      }

  }
}

