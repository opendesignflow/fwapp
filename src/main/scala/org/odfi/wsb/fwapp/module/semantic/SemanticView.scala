package org.odfi.wsb.fwapp.module.semantic

import org.odfi.wsb.fwapp.views.LibraryView
import com.idyria.osi.vui.html.HTMLNode
import org.w3c.dom.html.HTMLElement
import org.odfi.wsb.fwapp.assets.AssetsManager
import java.net.URI
import org.odfi.wsb.fwapp.views.ui.SemanticUIImplView
import com.idyria.osi.vui.html.Form
import org.odfi.wsb.fwapp.assets.generator.AssetsGenerator

trait SemanticView extends LibraryView with SemanticUIImplView {

  this.addLibrary("semantic") {
    case (Some(source), target) =>
      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/semantic/semantic.min.css")) {

        }

        script(createAssetsResolverURI(s"/semantic/semantic.min.js")) {

        }
      }
    case (None, target) =>

      onNode(target) {
        stylesheet(createAssetsResolverURI(s"/fwapp/external/semantic/semantic.min.css")) {

        }

        script(createAssetsResolverURI(s"/fwapp/external/semantic/semantic.min.js")) {

        }
      }

  }

  def ribbonHeaderDiv(color: String, name: String)(cl: => Any) = {

    "ui segment" :: div {
      classes("ribbon-header")
      s"ui $color ribbon label" :: a("#") { text(name) }
      div {
        cl
      }

    }

  }
  
  // Form validation
  //--------------
  override def form(cl: => Any) = {
    
    println("Semantic form")
    //-- Create Form
    var form = super.form(cl)
    
    //-- Make sire and id is present
    form.id match {
      case "" => 
        form.id = "semantic-"+form.hashCode()
      case other => 
    }
    
    //-- Take Validation and generate script
    
    getAssetsResolver match {
      case Some(resolver) => 
        resolver.findChildOfType[AssetsGenerator] match {
          case Some(generator) => 
            
            var generatorOut = generator.generateFile(this, "form-validation-"+form.id)
            
            generatorOut.write("consol.info('From generated')".getBytes)
            
            generatorOut.close()
            
          case None => 
            semanticLocalError("Code generation required, no assets generator found")
        }
      case None => 
        semanticLocalError("Code generation required, no assets resolver found")
    }
    
    //-- Return
    form
    
  }
  def semanticFieldRequire = {
    currentNode.findParentOfType[Form[HTMLElement,_]] match {
      case Some(parent) => 
        
        
        
      case None => 
        semanticLocalError("Field Require not possible if not in Form")
    }
  }

  // Local Errors
  //-----------------
  def semanticLocalError(err:String) = {
    +@("local-error" -> err)
  }
  
  // Popup
  //-------------

  //-- Preset components
  //---------------
  def semanticIconClickPopup(iconName: String)(cl: => Any) {
    s"$iconName icon" :: i {
      semanticPopupOnClick
    }
    semanticFlowingPopup {
      cl
    }
  }

  //-- Low LEvel utils
  //-----------------------
  def semanticPopupOnClick = {
    classes("popup-activate")
  }
  def semanticFlowingPopup(cl: => Any) = {
    "ui flowing popup hidden" :: div {
      cl
    }
  }
  def semanticTopLeft = classes("top left")
  def semanticTopRight = classes("top right")
  def semanticEffectTransition = classes("transition")

}