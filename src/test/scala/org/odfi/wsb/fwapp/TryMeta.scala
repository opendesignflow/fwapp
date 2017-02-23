package org.odfi.wsb.fwapp

import scala.reflect.macros.whitebox.Context


object TryMeta extends App{
  
  //TryMacro2.hello
  
 

  println("Hello")
  
    import reflect.runtime.universe._
  
  var tm = new TryMacro
  
/*  tm.testM {
    str : String => 
      
      println("Running Test")  
  }*/
  
  /*
  tm.test {
    str : String => 
      println("Running Test")
       
  }
  
  tm.test2(reify{
    str : String => 
      println("Running Test")
       
  })
  
  tm.test2 {
    str : String => 
      println("Running Test 2 ")
       
  }
  implicit def closureToExpr[V](cl : V => Any) : Expr[V=>Any] = {
    reify(cl)
  }
  */
  
  
  
  
  
}