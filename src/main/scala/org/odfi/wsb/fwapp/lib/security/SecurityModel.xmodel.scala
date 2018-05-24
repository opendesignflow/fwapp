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
package org.odfi.wsb.fwapp.lib.security

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.model.out.scala.JPAProducer

@producers(Array(
  new producer(value = classOf[JPAProducer]),
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object SecurityModel extends ModelBuilder {

  // user
  //--------------
  "User" is {
    makeTraitAndUseCustomImplementation

    //-- ID
    "ID" ofType ("uuid")

    //-- Attributes
    "CreationDate" ofType ("datetime")
    "LastUpdate" ofType ("datetime")
    "Email" ofType ("string")
    
    
    //-- Authentication
    "Authentication" is {
      
      "OTP" is {
        "IsSealed" ofType("boolean")
        "SealedHint" ofType("string")
        "SharedKey" ofType("binary") // Password protected HMAC Shared Key
        "HMAC" ofType("string")
        "Digits" ofType("int")
      }
    }

    //-- Federated tokens
    "FederatedIdentity" multiple {
      makeTraitAndUseCustomImplementation
      withTrait("org.odfi.wsb.fwapp.lib.security.CommonFederatedIdentity")
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
