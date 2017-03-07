package org.odfi.wsb.fwapp.assets.generator

import org.odfi.wsb.fwapp.assets.AssetsSource
import org.odfi.wsb.fwapp.views.FWappView
import java.io.ByteArrayOutputStream
import com.idyria.osi.tea.hash.HashUtils
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import java.nio.ByteBuffer
import com.idyria.osi.wsb.webapp.mime.MimeTypes

class AssetsGenerator extends AssetsSource("/") {

  //tlogEnableFull[AssetsGenerator]
  
  var generatedResources = Map[String, (String,GeneratedOutputStream)]()

  def generateFile(view: FWappView, name: String): GeneratedOutputStream = {

    //-- Create ID
    var generatedId = HashUtils.hashBytesAsBase64((view.getUniqueId + name).getBytes, "MD5")

    //-- Create Generator
    var generator = new GeneratedOutputStream(generatedId)

    //-- Save with Name
    generatedResources = generatedResources.updated(generatedId, (name,generator))

    logFine[AssetsGenerator](s"Registered resource $name under $generatedId")
    
    generator

  }

  this.onDownMessage {
    req =>

      //-- Path is ID
      //-- Path contains extension, so split
      var splited =  req.path.split('.').filter(_.length()>0)
      var (generatedId,extension) =  (splited(0),splited.drop(1).mkString("."))
      
   
      this.generatedResources.get(generatedId) match {

        //-- Generator is closed so ready to go
        case Some((name,generator)) if (generator.isClosed) =>

          var resp = HTTPResponse()
          resp.content = ByteBuffer.wrap(generator.toByteArray())
          resp.contentType = MimeTypes.nameToMime(name) match {
            case Some(mime) => mime
            case None => 
              sys.error("Unsupported resource extension")
          }
          
          response(resp,req)
          
        //-- Generator not closed
        case Some(generator) =>

          var resp = HTTPResponse.c503
          resp.setTextContent("The resource is generated, but the generator was not closed. Data might be incomplete, so returning an error")

          response(resp,req)
        case None =>
      }
  }

}

class GeneratedOutputStream(val id:String) extends ByteArrayOutputStream {

  var isClosed = false

  override def close() = {
    super.close()
    isClosed = true
  }

}