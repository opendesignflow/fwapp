package org.odfi.wsb.fwapp.lib.security

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer

@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object SecurityModel extends ModelBuilder {

  // user
  //--------------
  "User" is {
    makeTraitAndUseCustomImplementation

    //-- ID
    "ID" ofType("uuid")
    
    //-- Attributes
    "CreationDate" ofType("datetime")
    "LastUpdate" ofType("datetime")
    "Email" ofType("string")
    
    //-- Federated tokens
    "FederatedIdentity" multiple {
      "Validity" ofType "datetime"
      "ProviderID" ofType "string"
      "Token" ofType "sha256string"
    }

    //-- Roles
    "Role" multiple {
      "Validity" ofType "datetime"
      "ID" ofType "string"
    }
  }

}