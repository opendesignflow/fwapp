package org.odfi.wsb.fwapp.lib.files.semantic

import org.odfi.wsb.fwapp.module.semantic.SemanticView

trait FileSemanticUpload extends SemanticView {

  this.addLibrary("filesemantic") {
    case (source, node) =>
      onNode(node) {
        script(createAssetsResolverURI("fwapp/lib/files/semantic-files.js")) {

        }
       
        defaultAssetsResolverScripts("fwapp/external/jquery-fileupload/9.19.1/js/",List("vendor/jquery.ui.widget.js","jquery.iframe-transport.js","jquery.fileupload.js")) 
        stylesheet(createAssetsResolverURI("fwapp/external/jquery-fileupload/9.19.1/css/jquery.fileupload.css")) {
          
        }
      }
  }

  this.registerNamedAction("file.put") {
    req =>

      println("Put Action")

  }

  def semanticFilesInput(fieldId: String,validationText:String) = {

    div {
      val finput = s"#fileupload-$fieldId fileupload" :: input {
        +@("type" -> "file")
        fieldName("uploadedFile")
        +@("multiple" -> "true")
        +@("style" -> "display:none")
        currentNode.getId
        //+@("onchange" -> "fwapp.lib.files.updateFilesList($(this).parent(),this.files)")
      }
      
      "ui buttons" :: div {
        "ui primary button" :: div {
          text("Select Files")
          +@("onclick" -> "$(this).parent().parent().find('input').click()")
        }
        "or" :: div {
          data("text" -> "then")
        }
        "ui success button disabled upload-button" :: div {
          text("Upload All")
          +@("onclick" -> "fwapp.lib.files.uploadAll(this)")
        }
        
        "ui primary button disabled validate-button" :: div {
          text(validationText)
          +@("onclick" -> "fwapp.lib.files.submitUploadForm(this)")
          
        }
      }

      "files-content" :: div {
       
        "ui divided items" :: div {
          
        }
        
      }
    }
    
    /*semanticOnSubmitButton("Upload") {
      println("Upload done:"+withRequestParameter("uploadedFile"))
    }*/
   

  }

}