package org.odfi.wsb.fwapp.framework

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.wsb.core.message.soap.Fault
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait


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
    withTrait("org.odfi.wsb.fwapp.framework.json.JSONIOTrait")
    "ActionResult" multiple {
      "ID" ofType "string"
      "Success" ofType "boolean"
      "Result" ofType "string"
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