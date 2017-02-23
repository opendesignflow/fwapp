package org.odfi.wsb.fwapp.assets

import org.odfi.indesign.core.harvest.HarvestedResource
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import java.nio.ByteBuffer
import org.odfi.wsb.fwapp.FWappIntermediary

abstract class AssetsSource(basePath: String) extends FWappIntermediary(basePath) with HarvestedResource {
  
 override def getId = getClass.getCanonicalName
  
  
  /**
   * Find the asset based on name
   * @return tuple with type and bytes
   */
  //def resolveAsset(name:String) : Option[(String,ByteBuffer)] 
  
}