package org.odfi.wsb.fwapp.lib.security

trait CommonSecurityUser {
  
  def isValid : Boolean
  
  def hasRole(r:String) : Boolean
  
  def isPermanent : Boolean
  
  def hasProvider(name:String) : Boolean
  
  def cleanTokensFor(name:String, save:Boolean) : Unit
  
  def getUUID : String
  
  def setEmail(str:String) : Unit
  
  def getEmail : String
  
  def federate(provider:String,uuid:String) : Unit
  
  def findValidIdentity(provider:String,uuid:String) : Option[CommonFederatedIdentity]
  
}