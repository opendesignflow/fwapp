package org.odfi.wsb.fwapp.module.jquery

import org.odfi.wsb.fwapp.views.LibraryView
import org.odfi.wsb.fwapp.assets.generator.AssetsGeneratorView

trait JQueryView extends LibraryView with AssetsGeneratorView {

  this.addLibrary("jquery") {
    case (Some(source), target) =>

    case (None, target) =>
      onNode(target) {

        script(createAssetsResolverURI("/fwapp/external/jquery/jquery-3.1.1.min.js")) {

        }

      }

  }

  def jqueryValToVal(fromId: String, toId: String) = {
    s"""$$("#$toId").val($$("#$fromId").val());

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
("#$toId").trigger("change")"""
  }

  def jqueryGenerateOnLoad(name: String) = {

    getAssetsGenerator match {
      case Some(generator) =>

        var generatorOut = generator.generateFile(this, name+".js")
        generatorOut.println("""$(function() {""")
        generatorOut.onClose {
          generatorOut.println("""});""")
        }
        
        //-- Add Script
        script(createAssetsResolverURI(generator.basePath + "/" + generatorOut.getURLId)) {

        }
        
        Some(generatorOut)
      case None =>
        None
    }
  }

}
