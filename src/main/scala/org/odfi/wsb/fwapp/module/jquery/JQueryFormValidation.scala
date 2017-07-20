package org.odfi.wsb.fwapp.module.jquery

import com.idyria.osi.vui.html.Form
import org.w3c.dom.html.HTMLElement
import com.idyria.osi.vui.html.HTMLNode
import com.idyria.osi.vui.html.Input

trait JQueryFormValidation extends JQueryView {

  this.addLibrary("jquery-formvalidation") {
    case (_, targetNode) =>
      onNode(targetNode) {

        script(createAssetsResolverURI("/fwapp/external/form-validator/jquery.form-validator.min.js")) {

        }
        script(createAssetsResolverURI("/fwapp/lib/form-validator/form-validator.js")) {

        }
        stylesheet(createAssetsResolverURI("/fwapp/external/form-validator/theme-default.min.css")) {

        }

      }
  }

  def jqueryFormValidatorForm(cl: => Any) = {
    form {
      cl
    }
  }
  def jqueryFormValidatorEnable = {
    currentNode match {
      case sform: Form[HTMLElement, _] =>
        //-- Take Validation and generate script
        this.jqueryGenerateOnLoad("form-validator-" + sform.getId) match {
          case Some(generator) =>

            generator.println("console.info('Generated Jquery validation');")

            //-- look for required
            /*var contraints = sform.onSubNodesMap {
              case node: HTMLNode[HTMLElement, _] =>

                // prepare constaints
                var nodeContraints = List[String]()

                if (node.hasAttribute("required"))
                  nodeContraints = nodeContraints :+ "empty"

                node.onDataOfType[String]("semantic-validation-type") {
                  ftype =>
                    nodeContraints = nodeContraints :+ ftype
                }
                node.onDataOfType[List[String]]("semantic-validation-not") {
                  nots =>
                    nodeContraints = nodeContraints ++ (nots.map { n => s"not[$n]" })

                }

                // If no constraints, return empty
                nodeContraints.size match {
                  case 0 =>
                    ""
                  case other =>
                    // Make constraints string
                    val nodeConstraintsString = nodeContraints.mkString("['", "','", "']")

                    s"'" + node.attribute("name") + s"' : $nodeConstraintsString"
                }

            }.filterNot(_.isEmpty())*/

            //generator.println(s"""$$("#${sform.getId}").form({ on: 'blur',fields: { ${contraints.mkString(",")} }});""")
            generator.println(s"""$$.validate({ form: '#${sform.getId}'})""")
            generator.close()

          case None =>
        }
      case other =>
    }
  }
  def jqueryFormValidatorRequire = {
    data("validation" -> "required")
  }

  def jqueryFormValidatorDependsOnValue(source: String, values: String*) = {
    data("validation-depends-on" -> source)
    data("validation-depends-on-value" -> values.mkString(","))
  }

  /**
   * Sets a depend on values on all inputs found as descendant of current node after running the closure
   */
  def jqueryFormValidatorAllInputsDependsOnValue(source: String, values: String*)(cl: => Any) = {
    
    // Run closure
    cl
    
    // Update inputs
    currentNode.findDescendantsOfType[Input[HTMLElement,_]].foreach {
      input => 
        input.+@("data-validation-depends-on" -> source)
        input.+@("data-validation-depends-on-value" -> values.mkString(","))
    }
    
  }
  
}