package org.odfi.wsb.fwapp.security

import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import org.odfi.wsb.fwapp.FWappIntermediary
import scala.reflect.ClassTag

class Authenticator extends FWappIntermediary("/") {
  
  def withProvider[AP <: AuthenticationProvider : ClassTag](cl: AP => Any) = {
    
    this.getSite match {
      case Some(site) =>
        site.findDerivedResourceOfType[AP] match {
          case Some(ap) =>
            
            cl(ap)
            
          case None => 
        }
        
      case None => 
    }
    
  }
  
}