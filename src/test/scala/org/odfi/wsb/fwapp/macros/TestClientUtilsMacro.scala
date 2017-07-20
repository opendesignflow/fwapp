package org.odfi.wsb.fwapp.macros

import org.scalatest.FunSuite
import org.odfi.wsb.fwapp.views.FWappView
import fmacros.FormClientUtilsMTrait
import org.odfi.wsb.fwapp.module.clientutils.FormClientUtils

class TestClientUtilsMacro extends FunSuite {

  test("Form Switch Test") {

    val impl = new FWappView with FormClientUtils {
      /*formInputMatch("nameoffield") {
        case "a" =>
        case "b" =>
      }*/
    }

   /* impl.formInputMatch("nameoffield") {
      case "a" =>
        println("Test a")
        println("Test a")
        println("Test a")
      case "b" =>
        println(s"Test b")
    }
    
    impl.formInputMatchList("test")(List(
      
        "a" ->  {
            () =>
              
        }
    
    ))*/
      
    
    
   

  }

}