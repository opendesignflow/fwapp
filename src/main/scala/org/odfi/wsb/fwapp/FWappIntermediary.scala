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
package org.odfi.wsb.fwapp

import java.time.Instant
import java.time.ZoneId
import java.time.chrono.IsoChronology

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.wsb.fwapp.views.FWappView

import com.idyria.osi.wsb.core.broker.tree.IntermediaryLanguage
import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import com.idyria.osi.wsb.webapp.http.session.Session
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import java.io.PrintStream
import java.io.StringWriter
import java.io.PrintWriter
import com.idyria.osi.wsb.core.message.soap.Fault
import org.odfi.wsb.fwapp.framework.Errors
import java.nio.ByteBuffer
import org.odfi.wsb.fwapp.framework.ActionsResult


/**
 * 
 */
class FWappIntermediary(urlPath: String) extends HTTPPathIntermediary(urlPath) with HarvestedResource {

  def getId = basePath

  def ::(server: FWappApp) = {
    server.mergeTree(this)
    this
  }

  def getIntermediaryFor(str: String) = {
    this.intermediaries.collect { case fw: FWappIntermediary => fw }.find { iw => iw.basePath == str || iw.basePath == "/"+str }
  }
  
  /**
   * Look for a/b/c path in sub intermediaries
   */
  def findIntermediaryForPath(path:String) =  {
    
    
    var currentIntermediary : Option[FWappIntermediary]= Some(this)
    
    //println(s"Searching for: "+path+" starting in :"+this)
    //println(s"Finding Intermediary: "+path)
    
    this.findChild {
      case i : FWappIntermediary  =>
       
        i.fullURLPathInSite==path
      case other => false
    }.asInstanceOf[Option[FWappIntermediary]]
  
  }
  
  // Path in application
  //----------

  var __fullURLPath: Option[String] = None
  
  /**
   * Returns the FULL URL Path
   */
  def fullURLPath = { 
    
    var mapped = this.mapParentIntermediaries {
      case i: FWappIntermediary => i.basePath
      case other => ""
    }.filter(_.length()>0)
    
    var fp = (mapped.mkString("/") + "/" + basePath).replaceAll("//+", "/") 
   //println(s"Returning fill path: "+fp)
    
    fp
  }
    
  
  /**
   * Returns FUll URL path withing the application
   */
  def fullURLPathInSite = {
    var appFound = false
    var mapped = this.mapParentIntermediaries {
      case i: Site if(!appFound) =>
        appFound=true
        ""
      case i: FWappIntermediary if(!appFound) => 
        i.basePath
      case other => ""
    }
    
    (mapped.mkString("/") + "/" + basePath).replaceAll("//+", "/") 
  }
  
  def getSite =  this.findParentOfType[Site]
  
  def createAppPath(path:String) = {

    this.findParentOfType[Site] match {
      case Some(site) =>
        (site.fullURLPath+"/"+path).replaceAll("//+", "/") 
      case None => 
        path
    }
    
    
  }
    

}

object FWappIntermediary {

}
