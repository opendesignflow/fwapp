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
    s"""$$("#$toId").val($$("#$fromId").val());$$("#$toId").trigger("change")"""
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