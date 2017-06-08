package org.odfi.wsb.fwapp.lib.security

import javax.security.auth.login.AppConfigurationEntry
import scala.collection.convert.AsJavaConverters
import scala.collection.convert.DecorateAsJava



class APIConfiguration extends javax.security.auth.login.Configuration with DecorateAsJava {
  
  
  var configuredApplications = Map[String,Array[AppConfigurationEntry]]()
  
  def addApplicationConfiguration(name: String,loginModule:Class[_ <: javax.security.auth.spi.LoginModule],required:AppConfigurationEntry.LoginModuleControlFlag, parameters : Map[String,_]) = {
    
    var entry = new AppConfigurationEntry(loginModule.getCanonicalName,required,parameters.asJava);

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
