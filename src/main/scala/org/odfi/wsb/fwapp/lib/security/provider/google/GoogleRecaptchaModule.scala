package org.odfi.wsb.fwapp.lib.security.provider.google

import org.odfi.indesign.core.module.IndesignModule
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import com.idyria.osi.wsb.core.network.connectors.tcp.TCPNetworkContext
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait
import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import java.net.URL
import java.net.HttpURLConnection
import com.idyria.osi.tea.io.TeaIOUtils
import com.google.api.client.json.jackson2.JacksonParser
import com.google.api.client.json.jackson2.JacksonParser
import com.google.api.client.json.jackson2.JacksonFactory

object GoogleRecaptchaModule extends IndesignModule {

  class GoogleRecaptchaResponse extends ElementBuffer with JSonUtilTrait {
    
    @xelement(name = "success")
    var success : BooleanBuffer = null
    
    
    @xelement(name = "error-codes")
    var errorCodes = new XList[XSDStringBuffer](_.value)
    
  }
  
  
  def verify(req: HTTPRequest, resp: String) = GoogleRecaptchaModule.config.get.getString("serverSiteKey") match {
    case Some(serverKey) =>

      var userIp = req.networkContext.get.asInstanceOf[TCPNetworkContext].getRemoteHostIP

      println("Verifying Key for: " + userIp)
      var url = s"""https://www.google.com/recaptcha/api/siteverify?secret=${serverKey}&response=$resp&remoteip=$userIp"""
      
      //-- Call
      var urlConn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
      urlConn.getResponseCode match {
        case 200 => 
          
          var is = urlConn.getInputStream
          var jsonResp = new String(TeaIOUtils.swallowStream(is))
          println("Resp: "+jsonResp)
          """"success"\s*:\s*true""".r.findFirstIn(jsonResp).isDefined match {
            case true => 
              
            case false => 
              sys.error("Canot validate JSON TOken")
          }
           /*
          var parser = JacksonFactory.getDefaultInstance.createJsonParser(is)
          parser.get
          var p = new JacksonParser*/
          
          
          // var resp = (new GoogleRecaptchaResponse).fromJSONString(new String(TeaIOUtils.swallowStream(is)))
           //println("REs: "+resp.toJSONString)
           
           
           
        case other => 
          sys.error("Cannot verify Recaptcha, server returned: "+other)
      }
     
      

    case None =>
      sys.error("Cannot verify recaptcha, no server key defined")
  }

}