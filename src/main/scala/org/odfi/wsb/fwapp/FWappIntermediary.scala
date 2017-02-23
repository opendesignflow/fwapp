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
import org.odfi.wsb.fwapp.framework.ActionsResultActionResult
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
    this.intermediaries.collect { case fw: FWappIntermediary => fw }.find { iw => iw.basePath == str }
  }
  
  /**
   * Look for a/b/c path in sub intermediaries
   */
  def findIntermediaryForPath(path:String) =  {
    
    
    var currentIntermediary : Option[FWappIntermediary]= Some(this)
    
    println(s"Searching for: "+path+" starting in :"+this)
    //println(s"Finding Intermediary: "+path)
    
    this.findChild {
      case i : FWappIntermediary if (i.fullURLPathInSite==path) =>
        true
      case other => false
    }.asInstanceOf[Option[FWappIntermediary]]
    /*
    var resultMap = path.split("/").filter(_.length()>0).map {
      case subPath if(currentIntermediary.isDefined) => 
        
        println(s"-> Searching for: "+subPath+" starting in :"+currentIntermediary)
        
        //println(s"Searching for $subPath in ${currentIntermediary.get.basePath}")
        
        currentIntermediary.get.intermediaries.collect {case i : FWappIntermediary => i }.find { i => i.basePath=="/"+subPath } match {
          case Some(found)  => 
            currentIntermediary = Some(found)
            Some(found)
          case None =>
            currentIntermediary = None
            None
            
        }
      case other => None
    }
    
    resultMap.last*/
    
    
    
    /*
    println(s"Finding Intermediary: "+path)
    
    path.split("/").filter(_.length()>0).scanLeft(z)(op)
    path.split("/").filter(_.length()>0).reduceLeftOption[String] {
      case (l,r) => 
        
    }*/
    
    /*path.split("/").filter(_.length()>0).foreach {
      subPath => 
        currentIntermediary.intermediaries.collect {case i : HTTPPathIntermediary => i }.find { i => i.basePath==subPath } match {
          case => 
        }
    }*/
    
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
    }
    
    (mapped.mkString("/") + "/" + basePath).replaceAll("//+", "/") 
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
    
  // View Support
  //----------------
  var __htmlView: Option[Class[_ <: FWappView]] = None

  def htmlView_=(cl: Class[_ <: FWappView]) = {
    __htmlView = Some(cl)
  }
  def htmlView = __htmlView

  def instanciateView = htmlView match {
    case Some(cl) =>
      var view = cl.newInstance()

      view.deriveFrom(this)

      Some(view)
    case None => None
  }

  //-- view pool
  var viewPool = Map[Session, FWappView]()

  def viewFromPool(session: Session) = {

    //-- Get Current time
    var time = IsoChronology.INSTANCE.zonedDateTime(Instant.now(), ZoneId.of("GMT"))

    //-- Get List of views for the 
    this.viewPool.get(session) match {
      case Some(cachedView) => cachedView
      case other =>
        var view = this.instanciateView.get
        this.viewPool = this.viewPool.updated(session, view)
        view
    }
  }

  def viewToPool(session: Session, view: FWappView) = {
    this.viewPool = this.viewPool.updated(session, view)
  }

  def removeViewFromPool(view: FWappView) = {
    this.viewPool = this.viewPool - this.viewPool.find { case (k, v) => v == view }.get._1
  }

  // Processing
  //---------------
  this.onDownMessage {
    // When under a HTTPPathIntermediary, the current level is "/". If not "/", message is supposed to go down
    case req if (req.path == "/") =>

      logInfo[FWappIntermediary]("Paths are identical -> render view")

      //-- View Rendering
      //-- If a session is set, get from session pool or create

      htmlView match {
        case Some(viewClass) =>

          //-- Catch global errors
          var resp = new HTTPResponse
          try {

            // Parameters
            //-----------
            val isJSONFormat = req.getURLParameter("_format").isDefined && req.getURLParameter("_format").get == "json"

            // Create a view
            //-----------------------

            //-- Take from Session pool or create
            var isSessionBeforeRender = req.hasSession
            var view = isSessionBeforeRender match {
              case true => viewFromPool(req.getSession.get)
              case false => instanciateView.get
            }

            //-- View needs current request
            view.request = Some(req)

           // println("Render view, session is: " + isSessionBeforeRender + " , view instance: " + view.hashCode())

            // Actions
            //-----------------

            view match {
              case frview: FWAppFrameworkView =>
                frview.cleanActionResults
                req.getURLParameter("_action") match {
                  case Some(actionName) =>

                    logInfo[FWappIntermediary]("Calling Action "+actionName)
                    
                    var res = new ActionsResultActionResult
                    res.ID = actionName
                    frview.saveActionResult(actionName, res)

                    frview.actions.get(actionName) match {
                      case Some(action) =>

                        try {

                          res.result = action._2(action._1).toString()
                          res.success = true
                          
                          //println("Action run")
                        } catch {
                          case e: Throwable =>

                            //println("Error fro, actino: " + e.getLocalizedMessage)

                            res.success = false
                            var error = res.errors.add
                            error.message = e.getLocalizedMessage
                            var stackString = new StringWriter
                            e.printStackTrace(new PrintWriter(stackString))
                            error.stack = stackString.toString()
                          //req.addError(e)
                        }
                      case None =>

                        res.success = false
                        var error = res.errors.add
                        error.message = "Action Not Registed for view"

                    }

                  case None =>
                }
              case other =>
            }
          
            // Render type: 
            // - Normal to get the HTML for example
            // - partial to get only part of the view
            //-----------

            //-- Render view

            //-- Render
            req.getURLParameter("_render") match {
              case None =>
                //logInfo[FWappIntermediary]("Rerender full")
                resp.htmlContent = view.rerender
               // logFine[FWappIntermediary]("Result: "+resp.htmlContent.get.toString()) 

              case Some("partial") =>
                resp.htmlContent = view.rerender

              case Some("full") =>
                resp.htmlContent = view.rerender

              //-- Other values don't render view
              case other =>
                resp.code = 200
                resp.contentType = "text/plain"
                resp.content = ByteBuffer.wrap("".getBytes)
            }

            //-- Check errors
            //---------------

            req.hasErrors match {

              //-- For JSON return, send back JSON report
              case true if (isJSONFormat) =>

                //-- Set Code
                resp.code = 500

                //-- Return errors in JSON
                var errors = new Errors
                req.consumeErrors {
                  err =>
                    var fault = errors.faults.add
                    fault.reason.text = err.getLocalizedMessage
                }

                resp.contentType = "application/json"
                resp.content = ByteBuffer.wrap(errors.toJSonString.getBytes)

              // var fault = new Fault

              //-- For normal render, return error page
              case true =>

                //-- Set Code
                resp.code = 500
                resp.contentType = "text/plain"
                var stackString = new StringWriter
                req.errors(0).printStackTrace(new PrintWriter(stackString))
                resp.content = ByteBuffer.wrap(stackString.toString.getBytes)

              case other =>
            }

            // View Result
            //-----------------
           // println("Checking view action results")
            view match {

              //-- JSon Result for actions
              case frview: FWAppFrameworkView if (frview.hasActionResult && isJSONFormat) =>

               // println("Found action results")

                //-- Set Code
                resp.code = frview.hasActionErrors match {
                  case true => 500
                  case false => 200
                }

                //-- Set action results as content
                resp.contentType = "application/json"
                var results = new ActionsResult
                frview.actionResults.foreach {
                  case (code, r) =>
                    results.actionResults += r
                }
                resp.content = ByteBuffer.wrap(results.toJSonString.getBytes)

              //-- Add Results

              //-- Error Page
              //-- NOthing otherwise
              case other =>
            }

            //-- Check session
            (isSessionBeforeRender, req.hasSession) match {
              //-- New Session
              case (false, true) =>

                //println("View now has session save to pool")
                viewToPool(req.getSession.get, view)

              //-- Clear from session
              case (true, false) => removeViewFromPool(view)

              ///-- Others change nothing
              case _ =>

            }

          } catch {

            //-- Uncaught errors
            case e: Throwable =>

              e.printStackTrace()
              
              //-- Set Code
              resp.code = 500
              resp.contentType = "text/plain"
              var stackString = new StringWriter
              e.printStackTrace(new PrintWriter(stackString))
              resp.content = ByteBuffer.wrap(stackString.toString.getBytes)

          } finally {

            //-- Send Response
            //println("Sending response")
            response(resp, req)

          }

        case None =>
      }

    case req =>
      
      logInfo[FWappIntermediary](s"${basePath} cannot render ${req.path}")

  }

}

object FWappIntermediary {

}