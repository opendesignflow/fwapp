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

trait FWappApp extends IndesignModule with org.odfi.wsb.fwapp.FWappTreeBuilder {

  override def getDisplayName = getClass.getName.replace("$", "")

  /**
   * WSB Engine used for operations
   */
  val engine = new WSBEngine

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
    /*t.deriveFrom(this)
    tree = Some(t)
    preTree <= t*/

    //-- Find Assets Resolver to add default framework
    /*t.on("updated") {
      t.findChildOfType[AssetsResolver] match {
        case Some(resolver) =>
          println(s"Found AssetsResolver for: "+resolver.fullURLPath)
         
          //AssetsManager.addAssetsSource("fwapp", new ResourcesAssetSource("")).addFilesSource("fwapp")
         
          assetsResolver = Some(resolver)

        case None =>

          println(s"Cannot find AssetsResolver")
      }
    }*/

    //-- Return
    t
  }
  // var appTree = new FWappTree

  def listen(port: Int) = {

    var conn = new HTTPConnector(port)
    engine.network.addConnector(conn)
    conn

  }

}

