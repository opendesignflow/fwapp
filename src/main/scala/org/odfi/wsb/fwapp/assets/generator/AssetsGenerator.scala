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
package org.odfi.wsb.fwapp.assets.generator

import org.odfi.wsb.fwapp.assets.AssetsSource
import org.odfi.wsb.fwapp.views.FWappView
import java.io.ByteArrayOutputStream
import com.idyria.osi.tea.hash.HashUtils
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import java.nio.ByteBuffer
import com.idyria.osi.wsb.webapp.mime.MimeTypes
import java.net.URLEncoder
import java.net.URLDecoder
import java.io.PrintStream
import com.idyria.osi.tea.listeners.ListeningSupport

class AssetsGenerator extends AssetsSource("/") {

 /// tlogEnableFull[AssetsGenerator]
  
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
      //-- Remember it starts with "/ID" , so drop first character
      var generatedId = URLDecoder.decode(req.path.drop(1), "US-ASCII")
      
      logFine[AssetsGenerator](s"Generated Resource: "+generatedId)
     // var splited =  generatedId.split('.').filter(_.length()>0)
     // var (generatedId,extension) =  (splited(0),splited.drop(1).mkString("."))
      
   
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

class GeneratedOutputStream(val id:String) extends ByteArrayOutputStream with ListeningSupport {

  var printStream = new PrintStream(this)
  
  var isClosed = false

  def getURLId = URLEncoder.encode(id, "US-ASCII")
  
  override def close() = {
    this.@->("close")
    super.close()
    isClosed = true
  }
  
  def println(str:String) = this.printStream.println(str)
  def print(str:String) = this.printStream.print(str)

  // Events
  def onClose(cl: => Unit) = {
    this.on("close")(cl)
  }
  
}
