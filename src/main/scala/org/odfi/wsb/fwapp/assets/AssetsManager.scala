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

import org.odfi.indesign.core.module.IndesignModule
import org.odfi.indesign.core.config.ConfigSupport

object AssetsManager extends IndesignModule with ConfigSupport {

  // Sources
  var assetsSources = Map[String, AssetsSource]()

  def addAssetsSource[T <: AssetsSource](name:String,source:T) : T = {
    this.assetsSources = this.assetsSources + (name -> source)
    source
  }
  
  def onNoAssetSource(name: String)(cl: => Any) = {
    this.findAssetSource(name) match {
      case None => cl
      case _ => 
    }
  }
  
  def findAssetSource(name: String): Option[AssetsSource] = {

    assetsSources.get(name) match {
      case Some(source) => Some(source)
      case None =>
        this.config.get.getKey(name) match {

          case Some(key) if (key.keyType.toString() == "file") =>

            var source = new ResourcesAssetSource(key.values.head.toString())
            source.addFilesSource(key.values(1).toString())

            assetsSources = assetsSources + (name -> source)
            
            Some(source)

          case None => None
        }
    }

  }

}
