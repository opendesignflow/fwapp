package org.odfi.wsb.fwapp.lib.security

import org.odfi.indesign.core.harvest.HarvestedResource
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import java.util.UUID
import com.idyria.osi.ooxoo.core.buffers.datatypes.id.UUIDBuffer

@xelement(name = "User")
class User extends UserTrait {

  // ID and federation
  //------------------

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

  def federate(providerID: String, token: String) : UserTraitFederatedIdentity = synchronized {

    
    
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