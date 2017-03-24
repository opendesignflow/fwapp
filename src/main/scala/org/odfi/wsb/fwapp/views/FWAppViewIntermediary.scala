package org.odfi.wsb.fwapp.views

import org.odfi.wsb.fwapp.FWappIntermediary
import com.idyria.osi.wsb.webapp.http.session.Session
import java.time.chrono.IsoChronology
import java.time.Instant
import java.time.ZoneId
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import org.odfi.wsb.fwapp.framework.FWAppFrameworkView
import org.odfi.wsb.fwapp.framework.ActionsResultActionResult
import java.io.StringWriter
import java.io.PrintWriter
import java.nio.ByteBuffer
import org.odfi.wsb.fwapp.framework.ActionsResult
import org.odfi.wsb.fwapp.framework.Errors
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

class FWAppViewIntermediary extends FWappIntermediary("/") {
  
  
  override def getDisplayName = htmlView match {
    case Some(v) if(viewPool.size>0) => viewPool.head._2.getDisplayName 
    case other => super.getDisplayName
  }
  
  this.acceptDown[HTTPRequest] {
    req => 
      //println(s"Checking acceptance: ${req.originalPath} <- ${req.path}")
      //req.path=="/" || req.path=="" || req.path.split("/").filter(_.length()>0).length==1
      req.path=="/" || req.path=="" || (htmlView.isDefined && classOf[FWAppCatchAllView].isAssignableFrom(htmlView.get))
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
    case req  =>

      //println(s"Rendering for: "+req.originalPath)
      logInfo[FWAppViewIntermediary](s"Paths are identical for ${req.originalPath} in ${basePath} -> render view")

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
            //-- If Session is present after rendering, add to pool
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

    case other => 
      

  }
  
}