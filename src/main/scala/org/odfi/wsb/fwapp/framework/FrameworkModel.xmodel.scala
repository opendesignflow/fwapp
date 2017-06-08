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
package org.odfi.wsb.fwapp.framework

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.wsb.core.message.soap.Fault
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait

@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object FrameworkModel extends ModelBuilder {

  "CallActions" is {
    "Action" multiple {

    }
  }

  "ActionsResult" is {
    withTrait(classOf[STAXSyncTrait])
    withTrait(classOf[JSonUtilTrait])
    withTrait("org.odfi.wsb.fwapp.framework.json.JSONIOTrait")
    
    "ActionResult" multiple {
      makeTraitAndUseSameNameImplementation
      withTrait(classOf[JSonUtilTrait])
      
      "ID" ofType "string"
      
      "Success" ofType "boolean"
      
      "Result" multiple {
        
        "Hint" ofType "string"
        "Text" ofType "cdata"
        "Values" multiple {
          ofType("string")
        }
        
      }
      
      "Error" multiple {
        "Message" ofType "string"
        "Stack" ofType "cdata"
      }
      

    }
  }

  "Errors" is {
    withTrait(classOf[STAXSyncTrait])
    withTrait("org.odfi.wsb.fwapp.framework.json.JSONIOTrait")
    importElement(classOf[Fault].getCanonicalName).setMultiple
  }
}
