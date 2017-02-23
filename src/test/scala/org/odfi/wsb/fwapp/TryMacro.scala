package org.odfi.wsb.fwapp

import scala.reflect.macros.blackbox
import scala.language.experimental.macros
import scala.reflect.reify.phases.Reify

import reflect.runtime.universe._
import scala.reflect.ClassTag

class TryMacro {
  
  def testM[V](expr : V => Any) : Unit = macro TryMacro.testMImpl[V]
  
  
  
  
  def test(cl : String => Any) : Unit = {
  
    
    val test = reify {
      cl
    }
    
    println("Test:" +test)
  }

  
   def test2[V](exp: Expr[V=>Any])(implicit tag: ClassTag[V]) : Unit = {
      println("Go test2: "+exp) 
      
      // Second elemen in tree is the input arg
      var inputArg = exp.tree.children(0)
      var inputName = inputArg
      
      println("Name: "+inputName.productElement(1))
      
      println("Calling...")
      reify{exp.splice("test".asInstanceOf[V])}
      
      /*inputArg.foreach {
        tree => 
          println("Subtree: "+tree+" -> "+tree.isDef)
      }*/
   }
  
}

object TryMacro extends App{
  
  def testMImpl[V](c:blackbox.Context)(expr: c.Expr[V=>Any]): c.Expr[Unit] = {
    import c.universe._ 
    
    println("Go test macro: "+expr) 
      
      // Second elemen in tree is the input arg
      var inputArg = expr.tree.children(0)
      var inputName = inputArg.productElement(1)
    
    c.abort(c.enclosingPosition, "name is: "+inputName)
  }
  
} 