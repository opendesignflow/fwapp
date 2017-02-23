package org.odfi.wsb.fwapp.views

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.wsb.fwapp.FWappApp

import com.idyria.osi.vui.html.basic.BasicHTMLView
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import java.net.URI
import org.odfi.wsb.fwapp.FWappIntermediary
import java.net.URLEncoder
import org.odfi.wsb.fwapp.Site


trait FWappView extends BasicHTMLView with HarvestedResource {
  
  def getId = getClass.getCanonicalName
  
  def getUniqueId = getId+""+hashCode()
  
  //def getApp = this.findUpchainResource[FWappApp]
  def getApp = this.findUpchainResource[FWappIntermediary] match {
    case Some(baseIntermediary) => 
      baseIntermediary.findParentOfType[Site] 
    case None => 
      None
  }
  
  // Requests
  //--------
  var request  : Option[HTTPRequest] = None
  
  // URI/Path Utils
  //-----------
  def createAppURI(path:String) = getApp match {
    case Some(site : Site) if(path.startsWith("/")) =>   new URI((site.fullURLPath+"/"+path).replaceAll("//+","/"))
    case other => new URI(path.replaceAll("//+","/"))
  }
  
  def createAssetsResolverURI(path:String) = {
    new URI(getApp match {
      case Some(app) if (app.assetsResolver.isDefined) => 
        
        //-- Found assets resolver and use full URL
        
        
       // println(s"Assets are in : "+app.assetsResolver.get.fullURLPath)
        
        ("/"+app.assetsResolver.get.fullURLPath+"/"+path).replaceAll("//+","/")
      case other =>
        println(s"No app/assets resolver in upchain")
        path.replaceAll("//+","/")
    })
  }
  
  def getAssetsResolver = getApp match {
      case Some(app) => 
        app.assetsResolver
      case other => 
        None
  }
  
  /**
   * Create a link to current view, with associated URL Arguments
   */
  def createCurrentViewLink(args:(String,String)*) = {
    
    args.size match {
      case 0  => 
        getViewPath
      case other => 
        getViewPath+"?"+args.map { case (name,value) => s"$name=${URLEncoder.encode(value,"UTF8")}"}.mkString("&")
    }
    
    
  }
  
  def getViewPath = {
    this.findUpchainResource[FWappIntermediary].get.fullURLPath
  }
  
  // Session Utils
  //----------------
  def ensureSession = request match {
    case Some(req) => 
      req.getSession
    case None => 
  }
  
  // HTML Overrides
  //---------------------
  
  /**
   * Make Links automatically adapted to application
   */
  override def a(target:String)(cl: => Any) = {
    
   // println(s"OVERRIDEN A LINK")
    super.a(createAppURI(target).toString())(cl)
    
  }
  
  
  
}