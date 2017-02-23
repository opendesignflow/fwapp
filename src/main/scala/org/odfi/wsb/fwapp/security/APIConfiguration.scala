package org.odfi.wsb.fwapp.security

import javax.security.auth.login.AppConfigurationEntry

import scala.collection.JavaConversions._

class APIConfiguration extends javax.security.auth.login.Configuration {
  
  
  var configuredApplications = Map[String,Array[AppConfigurationEntry]]()
  
  def addApplicationConfiguration(name: String,loginModule:Class[_ <: javax.security.auth.spi.LoginModule],required:AppConfigurationEntry.LoginModuleControlFlag, parameters : Map[String,_]) = {
    
    var entry = new AppConfigurationEntry(loginModule.getCanonicalName,required,parameters);
    configuredApplications = configuredApplications.get(name) match {
      case Some(array) => configuredApplications.updated(name, array :+ entry)
      case None => configuredApplications.updated(name, Array(entry))
    }
    
  }
  
  def getAppConfigurationEntry(name: String) = configuredApplications.get(name) match {
    case Some(arr) => arr
    case None => Array[AppConfigurationEntry]()
  }
  
}