package org.odfi.wsb.fwapp.views

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.wsb.fwapp.FWappApp

import com.idyria.osi.vui.html.basic.BasicHTMLView
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest
import java.net.URI
import org.odfi.wsb.fwapp.FWappIntermediary
import java.net.URLEncoder
import org.odfi.wsb.fwapp.Site
import scala.collection.convert.DecorateAsJava
import scala.collection.convert.DecorateAsScala


trait FWappView extends BasicHTMLView with HarvestedResource with DecorateAsJava with DecorateAsScala {
  
  // ID and so on
  //---------------
  def getId = getClass.getCanonicalName
  
  def getUniqueId = getId+""+hashCode()
  
  
  
  // Environment utilities -> finding main Site and so on
  //---------------

  
  def getFirstIntermediary =  this.findUpchainResource[FWappIntermediary]
  
  def getApp = this.findUpchainResource[FWappIntermediary] match {
    case Some(baseIntermediary) => 
      baseIntermediary.findParentOfType[Site] 
    case None => 
      None
  }
  
  def getIntermediaryTreeTop =  this.findUpchainResource[FWappIntermediary] match {
    case Some(baseIntermediary) => 
      Some(baseIntermediary.findTopMostIntermediary)
    case None => 
      None
  }
  
  // Requests
  //--------
  var request  : Option[HTTPRequest] = None
  
  // URI/Path Utils
  //-----------
  
  def createFullDomainURI(pathInput:String) = {
    
    s"http://${request.get.getURLParameter("Host").get}${createAppURI(pathInput)}"
    
  }
  def createAppURI(pathInput:String) = { 
    
    val path = pathInput.replaceAll("//+","/")
    
    
    //println("createAppURI for -> "+path)
    var resLink = getApp match {
    
      //-- Start with # , don't change
      case Some(site : Site) if(path.startsWith("#")) => 
        new URI(path.replaceAll("//+","/"))
      //-- Starts with @/, path based on top level app
      case Some(site : Site) if(path.startsWith("@/")) => 
        
        //println("Found @/ link;  app is: "+site)
        
        // Take app, and look for top most from it
        site.findTopMostIntermediaryOfType[Site] match {
          case Some(topSite) =>
            
             //println("Found @/ link; top app is: "+topSite+" -> "+topSite.fullURLPath)
            
             new URI( (topSite.fullURLPath+"/"+ path.stripPrefix("@").stripPrefix(topSite.fullURLPath) ).replaceAll("//+","/"))
          case otherwise => 
            // println("Found @/ link without top app, adding current app base: "+site.fullURLPath)
            
            new URI( (site.fullURLPath+"/"+path.stripPrefix("@").stripPrefix(site.fullURLPath)).replaceAll("//+","/"))
        }
        
       
        
      case Some(site : Site) if(!path.startsWith("/")) => 
        //println("Path is relative")
        new URI(getViewPath+path)
      case Some(site : Site) if(!path.startsWith(site.fullURLPath)) =>   
       // println("Path does not start with site base path: "+site.fullURLPath)
        new URI((site.fullURLPath+"/"+path).replaceAll("//+","/"))
      case other => new URI(path.replaceAll("//+","/"))
    }
    //println("Result link: "+resLink)
    resLink
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
  
  /**
   * Returns sub path of current view
   * If view is at /a/b , and the request is responded at /a/b/c/d/e
   * Return is: /c/d/e
   */
  def getViewSubPath = {
    request.get.originalPath.stripPrefix(getViewPath)
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