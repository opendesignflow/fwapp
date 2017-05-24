package org.odfi.wsb.fwapp

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach

class IntermediaryLanguageTest extends FunSuite with FWappTreeBuilder with BeforeAndAfterEach {

 
  
  override def beforeEach = {
    this.reset
  }
  

  test("Create Intermediary tree") {

    var wres = ("/a" is {

      "/b/c" is {

      }

      "/d" is {
        "/e" is {

        }
      }

    }).fwappIntermediary

    // Test content
    //-------------------

    println("Path: " + wres.basePath)
    assertResult(2, "Top intermediary has 2 children: B and D")(wres.intermediaries.size)

  }

  test("Intermediary Tree - Repeating path") {

    //tlogEnableFull[FWappTreeBuilder]
    
    //-- Create
     var aIntermediary = ("/a" is {

      "/b/c" is {

      }
      "/b/c/d" is {
        
      }
      "/b" is {
        
        "/c/d/e" is {
          
        }
        
      }

    }).fwappIntermediary
    
    
    //-- Check
    assertResult(1,"A -> B")(aIntermediary.intermediaries.size)
    assertResult(1,"B -> C")(aIntermediary.intermediaries(0).intermediaries.size)
    assertResult(1,"C -> D")(aIntermediary.intermediaries(0).intermediaries(0).intermediaries.size)
  }

}