package org.odfi.wsb.fwapp.assets

import org.odfi.wsb.fwapp.FWappIntermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.FWappApp
import org.odfi.wsb.fwapp.Site

class AssetsResolver(path: String = "/") extends FWappIntermediary(path) {

  // Add Defaults
  addAssetsSource("/fwapp",new ResourcesAssetSource).addFilesSource("fwapp")
  /*var fwappSource = new ResourcesAssetSource("/fwapp")
  fwappSource.addFilesSource("fwapp")
  this <= fwappSource*/
  
  
   def addAssetsSource[T <: AssetsSource](name:String,source:T) : T = {
    source.basePath = name
    this <= source
   // this.assetsSources = this.assetsSources + (name -> source)
    source
  }
  
  this.onNewParentIntermediary {
    println("Assets Resolver, add to application")
    this.findParentOfType[Site]  match {
      case Some(app) if (app.assetsResolver.isEmpty) =>
        println("Found app with no resolver: "+app)
        app.assetsResolver = Some(this)
      case other => 
    }
    /*this.findUpchainResource[FWappApp] match {
      case Some(app) =>
        println("Found")
        app.assetsResolver = Some(this)
      case None => 
    }*/
  }
  
  def findAssetsSource(name:String) = {
    this.intermediaries.collectFirst {
      case s : AssetsSource if (s.basePath==("/"+name).replaceAll("//+", "/")) =>
        s
      
    }
  }
  
  this.onDownMessage {
    msg =>
      
      logFine[AssetsResolver]("Assets resolving: "+msg.path)

      // Split at "/" like "/a/b/c"
      // the "a" part is the target asset
      var splittedPath = msg.path.trim.split("/").filter(_.length()>0)
      splittedPath.length match {
        case 0 =>

          //-- Generate 503
          response(HTTPResponse.c503, msg)

        case other =>

          //-- Take asset name
          /*var assetName = splittedPath(0)

          //-- Remove from message
          msg.stripPathPrefix(s"/$assetName")
          
           logFine[AssetsResolver]("Looking source for: "+assetName)

          //-- Look for source
          AssetsManager.findAssetSource(assetName) match {
            case Some(source) =>

              logFine[AssetsResolver]("Found source for: "+assetName)
              this <= source
              source.down(msg)

            case None =>

              //-- Generate 404
              response(HTTPResponse.c404, msg)
          }*/

      }

  }

}