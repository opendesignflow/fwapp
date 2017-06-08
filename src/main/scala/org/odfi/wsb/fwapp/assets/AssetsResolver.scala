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
package org.odfi.wsb.fwapp.assets

import org.odfi.wsb.fwapp.FWappIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.FWappApp
import org.odfi.wsb.fwapp.Site

class AssetsResolver(path: String = "/") extends FWappIntermediary(path) {

  // Add Defaults
  addAssetsSource("/fwapp", new ResourcesAssetSource).addFilesSource("fwapp")

  def addAssetsSource[T <: AssetsSource](name: String, source: T): T = {
    source.basePath = ("/" + name).replaceAll("//+", "/")
    this <= source
    // this.assetsSources = this.assetsSources + (name -> source)
    source
  }
  
  /**
   * Adds a ResourcesAssetSource to path, which searches in "" , so that you can add resources to project without specific sub folders
   */
  def addDefaultResourcesAssetSource(basePath:String) = {
    addAssetsSource(basePath, new ResourcesAssetSource).addFilesSource("")
  }
  
  def addResourcesAssetSource(basePath:String) = addAssetsSource(basePath, new ResourcesAssetSource).addFilesSource(basePath.stripPrefix("/"))

  def ifNoAssetSource(name: String)(cl: => Unit) = {
    this.intermediaries.find {
      case r: AssetsSource if (r.basePath == name) => true
      case other => false
    } match {
      case Some(found) =>
      case None =>
        cl
    }
  }

  this.onNewParentIntermediary {
    logFine[AssetsResolver]("Assets Resolver, add to application")
    this.findParentOfType[Site] match {
      case Some(app) if (app.assetsResolver.isEmpty) =>
        logFine[AssetsResolver]("Found app with no resolver: " + app)
        app.assetsResolver = Some(this)
      case other =>
    }
    /*this.findUpchainResource[FWappApp] match {
      case Some(app) =>
        println("Found")
        app.assetsResolver = Some(this)
      case None => 
    }*/
  }

  def findAssetsSource(name: String) = {
    this.intermediaries.collectFirst {
      case s: AssetsSource if (s.basePath == ("/" + name).replaceAll("//+", "/")) =>
        s

    }
  }
  
  def findResourcesAssetsSource(name:String) = {
    this.intermediaries.collectFirst {
      case s: ResourcesAssetSource if (s.basePath == ("/" + name).replaceAll("//+", "/")) =>
        s

    }
  }

  this.onDownMessage {
    msg =>

      logFine[AssetsResolver]("Assets resolving: " + msg.path)

      // Split at "/" like "/a/b/c"
      // the "a" part is the target asset
      var splittedPath = msg.path.trim.split("/").filter(_.length() > 0)
      splittedPath.length match {
        case 0 =>

          //-- Generate 503
          response(HTTPResponse.c503, msg)

        case other =>

        //-- Take asset name
        /*var assetName = splittedPath(0)

          //-- Remove from message
          msg.stripPathPrefix(s"/$assetName")
          
           logFine[AssetsResolver]("Looking source for: "+assetName)

          //-- Look for source
          AssetsManager.findAssetSource(assetName) match {
            case Some(source) =>

              logFine[AssetsResolver]("Found source for: "+assetName)
              this <= source
              source.down(msg)

            case None =>

              //-- Generate 404
              response(HTTPResponse.c404, msg)
          }*/

      }

  }

}
