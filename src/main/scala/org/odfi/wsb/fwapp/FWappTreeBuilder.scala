package org.odfi.wsb.fwapp

import scala.collection.mutable.ArrayStack
import com.idyria.osi.vui.html.basic.BasicHTMLView
import org.odfi.wsb.fwapp.views.FWappView
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.wsb.core.broker.tree.Intermediary
import com.idyria.osi.wsb.webapp.http.message.HTTPIntermediary
import org.odfi.wsb.fwapp.errors.Handle404
import java.net.URLEncoder

trait FWappTreeBuilder extends TLogSource {

  def reset = {
    this.fwappIntermediariesStack.clear()
    this.lastCurrentIntermediaryStack.clear()
  }

  class IWrapper[I <: FWappIntermediary](val fwappIntermediary: I) {

    def ::(server: FWappApp) = {
      server.mergeTree(fwappIntermediary)
      this
    }

    /**
     * Call closure and come back to previous state
     */
    def is(cl: => Unit): IWrapper[I] = {
      onIntermediary(this.fwappIntermediary) {
        cl
      }
      // cl
      fwappIntermediary.@->("updated")
      stackBackToLastIntermediary
      this
    }

    def view[V <: FWappView](v: Class[V]): IWrapper[I] = {
      fwappIntermediary.htmlView = v
      stackBackToLastIntermediary
      this
    }

    /*  def uses(tree: IWrapper[I]) : I = {
      
      //-- Derive Resource first, to make sure insertion in Intermediary tree can work with resources
      tree.fwappIntermediary.deriveFrom(this.fwappIntermediary.parentIntermediary.asInstanceOf[FWappIntermediary])
      
      //-- Replace local intermediary with the one being used
      this.fwappIntermediary.parentIntermediary <= tree.fwappIntermediary
      this.fwappIntermediary.parentIntermediary -= this.fwappIntermediary
      
      //-- Noew intermediary uses path
      tree.fwappIntermediary.basePath = this.fwappIntermediary.basePath
      
      
      
     // this.fwappIntermediary <= tree.fwappIntermediary
      stackBackToLastIntermediary
      tree.fwappIntermediary
    }*/

    def uses[I <: FWappIntermediary](i: I): I = {

      //-- Derive Resource first, to make sure insertion in Intermediary tree can work with resources
      i.deriveFrom(this.fwappIntermediary.parentIntermediary.asInstanceOf[FWappIntermediary])

      //-- Replace local intermediary with the one being used
      this.fwappIntermediary.parentIntermediary <= i
      this.fwappIntermediary.parentIntermediary -= this.fwappIntermediary

      //-- Noew intermediary uses path
      i.basePath = this.fwappIntermediary.basePath

      // this.fwappIntermediary <= tree.fwappIntermediary
      stackBackToLastIntermediary
      i
    }

  }

  // Stacks
  //--------
  var fwappIntermediariesStack = ArrayStack[IWrapper[FWappIntermediary]]()
  var lastCurrentIntermediaryStack = ArrayStack[IWrapper[FWappIntermediary]]()

  //-- Set Top Intermediary to new "/" if necessary
  this.isInstanceOf[FWappIntermediary] match {
    case true =>

      println("Tree builder is an Intermediary, so add a wrapper for this")
      fwappIntermediariesStack.push(new IWrapper(this.asInstanceOf[FWappIntermediary]))
    case false =>
    //fwappIntermediariesStack.push(new IWrapper(new FWappIntermediary("/")))
  }

  //def setBuild
  def setTopBuildIntermediary(i: FWappIntermediary) = {
    fwappIntermediariesStack.clear()
    fwappIntermediariesStack.push(new IWrapper(i))
  }

  /**
   * Go back to last top to ensure consistency after adding a sub tree
   */
  def stackBackToLastIntermediary = {
    this.lastCurrentIntermediaryStack.headOption match {
      case Some(lastCurrent) =>
        fwappIntermediariesStack.clear
        fwappIntermediariesStack.push(lastCurrent)
        lastCurrentIntermediaryStack.pop()
      case None =>
    }
  }

  /**
   * Split path, create tree if necessary
   */
  def path(path: String) = {

    // Start Back with Stack at the beginning
    // Save current head first
    /*lastCurrentIntermediaryStack.push(fwappIntermediariesStack.head)
    
    var topPath = fwappIntermediariesStack.head
    fwappIntermediariesStack.clear()
    fwappIntermediariesStack.push(topPath)*/

    logFine[FWappTreeBuilder]("Processing path: " + path)

    fwappIntermediariesStack.headOption match {
      case Some(top) =>
        logFine[FWappTreeBuilder]("Current top: " + top.fwappIntermediary.basePath)
        lastCurrentIntermediaryStack.push(top)
      case None =>
        logFine[FWappTreeBuilder]("No current top")
    }
    // println("Current top path: "+topPath.fwappIntermediary.urlPath)

    //-- Split path and create tree
    //var currentIntermediaryStack = ArrayStack[IWrapper]()
    path.replaceAll("/+", "/").split("/").filter(_.trim.length() > 0).map(p => URLEncoder.encode(p, "UTF8")).foreach {
      pathComponent =>

        //-- Look for path component in current
        //-- If no current top on stack; create
        fwappIntermediariesStack.headOption match {
          case None =>

            logFine[FWappTreeBuilder](s"--> Creating and setting $pathComponent as top")
            var intermediary = new IWrapper(new FWappIntermediary(pathComponent))
            fwappIntermediariesStack.push(intermediary)
            lastCurrentIntermediaryStack.push(fwappIntermediariesStack.head)

          // IF current top has the path component, push it on top of stack
          // If not; create and push
          case Some(top) =>

            top.fwappIntermediary.getIntermediaryFor(pathComponent) match {

              // Found Sub Intermediary is now the current top
              case Some(subIntermediary) =>

                logFine[FWappTreeBuilder](s"Found $pathComponent under " + top.fwappIntermediary.basePath)

                fwappIntermediariesStack.push(new IWrapper(subIntermediary))
              case None =>

                logFine[FWappTreeBuilder](s"Not Found $pathComponent under " + top.fwappIntermediary.basePath)

                // Create new Intermediary and Add it to current top
                var intermediary = new IWrapper(new FWappIntermediary(pathComponent))
                top.fwappIntermediary <= intermediary.fwappIntermediary
                intermediary.fwappIntermediary.deriveFrom(top.fwappIntermediary)
                fwappIntermediariesStack.push(intermediary)
            }
        }

    }

    //-- If no Head is present, maybe we created the empty path
    fwappIntermediariesStack.headOption match {
      case Some(actualTop) => actualTop
      case None =>
        var intermediary = new IWrapper(new FWappIntermediary(""))
        fwappIntermediariesStack.push(intermediary)
        lastCurrentIntermediaryStack.push(fwappIntermediariesStack.head)
        intermediary
    }

  }

  // Views
  //--------------
  def `the view is`[V <: FWappView](v: Class[V]) = {

  }
  def view[V <: FWappView](v: Class[V]) = {

    this.fwappIntermediariesStack.head.fwappIntermediary.htmlView = v

  }

  // Intermediaries
  //-------------------
  def useIntermediary(i: HTTPIntermediary) = {
    logFine[FWappTreeBuilder](s"Adding intermediary $i to: " + this.fwappIntermediariesStack.head.fwappIntermediary.fullURLPath)
    this.fwappIntermediariesStack.head.fwappIntermediary <= i
  }

  def add404Intermediary = {
    useIntermediary(new Handle404)
  }

  def withCurrentIntermediary(cl: FWappIntermediary => Any) = {
    cl(this.fwappIntermediariesStack.head.fwappIntermediary)
  }

  def onIntermediary(i: FWappIntermediary)(cl: => Any) = {
    lastCurrentIntermediaryStack.push(fwappIntermediariesStack.head)
    this.fwappIntermediariesStack.push(new IWrapper(i))
    cl
    stackBackToLastIntermediary
  }

  // Conversions
  //------------------

  implicit def pathStringToIntermediary(path: String) = {
    this.path(path)
  }

  implicit def fwintermediaryToIntermediaryWrapper[I <: FWappIntermediary](i: I) = {
    new IWrapper[I](i)
  }

}