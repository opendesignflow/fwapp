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

import org.odfi.indesign.core.harvest.HarvestedResource
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import java.util.UUID
import com.idyria.osi.ooxoo.core.buffers.datatypes.id.UUIDBuffer

@xelement(name = "User")
class User extends UserTrait with CommonSecurityUser {

  // Parameters
  //-----------------
  def setEmail(str:String) = {
    this.email = str
  }
  
  def getEmail = this.email
  
  
  // ID and federation
  //------------------

  def getUUID : String = this.ID.toString
  
  /**
   * Find a valid Identity, and cleanup at the same time
   */
  def findValidIdentity(providerID: String, token: String) = synchronized {

    val res = this.federatedIdentities.find {
      case id if (id.validity != null && id.validity.isAfterNow) =>
        id.providerID.toString == providerID && id.token != null && id.token.equals(token)
      case id if (id.validity != null && id.validity.isBeforeNow) =>
        this.federatedIdentities.remove(this.federatedIdentities.indexOf(id))
        false
      case other =>
        false
    }
    
    res

  }

  def federate(providerID: String, token: String)  : Unit = synchronized {

    
    
    //-- Merge or Create new ID
    var identity = this.federatedIdentities.find {
      id => id.providerID != null && id.providerID === providerID
    } match {
      case Some(id) =>
        id.token.set(token)
        id
      case None =>

        var id = this.federatedIdentities.add
        id.providerID = providerID
        id.token = token
        id

    }


    identity
  }
  
  def federate(providerID: String, token: UserTraitFederatedIdentity) : UserTraitFederatedIdentity = synchronized {

    
    //-- Clean existing
    this.federatedIdentities.find {
      id => id.providerID != null && id.providerID === providerID
    } match {
      case Some(id) =>
        this.federatedIdentities.remove(this.federatedIdentities.indexOf(id))
      case None =>

    }
    
    this.federatedIdentities += token


    token
  }
  
  def cleanTokensFor(providerID:String,save:Boolean = false) = synchronized {
    
    //-- Remove Provider's tokens
    this.federatedIdentities.find {
      id => id.providerID != null && id.providerID === providerID
    } match {
      case Some(id) =>
        id.token = null
      case other => 
    }
    
    //-- Save
    if (save)
      this.save
  }
  
  def hasProvider(providerID:String) = synchronized {
    
     this.federatedIdentities.find {
      id => id.providerID != null && id.providerID === providerID
    }.isDefined
    
  }
  
  // Roles
  //---------
  def hasRole(roleID:String) = {
    
    this.roles.find {
      r => 
        r.ID.toString() == roleID
    }.isDefined
    
  }

  // Registratin utils
  //--------------------

  /**
   * If no creation date, the user can't be permanent
   */
  def isPermanent = {

    this.creationDate match {
      case null => false
      case other => true
    }

  }
  def isValid = this.isPermanent

  // Save ustils
  def save = {
    SecurityLibModule.saveUser(this)
  }

}

object User {

  def apply() = {
    var u = new User
    u.ID = UUIDBuffer()
    u
  }
}
