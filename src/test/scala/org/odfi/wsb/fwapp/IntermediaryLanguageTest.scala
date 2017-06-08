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
