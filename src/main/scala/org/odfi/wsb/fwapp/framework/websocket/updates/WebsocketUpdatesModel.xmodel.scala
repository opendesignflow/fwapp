package org.odfi.wsb.fwapp.framework.websocket.updates


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
object WebsocketUpdatesModel extends ModelBuilder {
  
  
  val common = "UpdateMessage" is {
    isTrait
    "TargetID" ofType("string")
  }
  
  "UpdateText" is {
    withTrait(common)
    "Text" ofType("cdata")
  }
  
  "UpdateHTML" is {
    withTrait(common)
    "HTML" ofType("cdata")
  }
  
  "UpdateAttribute" is {
    withTrait(common)
    "Name" ofType("string")
    "Value" ofType("string")
  }
  
}