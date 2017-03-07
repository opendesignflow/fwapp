package fmacros

import scala.reflect.macros.blackbox
import scala.language.experimental.macros

trait FWAppValueBindingViewTrait {
   
  def bindValue[V](expr: V => Any): Unit  = macro FWAppMacros.bindValueImpl[V]
}

object FWAppMacros {
  
  def bindValueImpl[V](c: blackbox.Context)(expr: c.Expr[V => Any]): c.Expr[Unit] = {
    import c.universe._

    // Element 0 in expression is the input arg
    var inputArg = expr.tree.children(0)

    // Input Name is the product Element 1 :=> val NAME : V
    //                                               ^ 1
    var inputName = inputArg.productElement(1).toString
    
    // Type of input is product arity 2
    var typeName = inputArg.productElement(2).toString

    //c.warning(c.enclosingPosition, s"Calling $inputName of type $typeName ")
    
    //reify {
      //c.mirror.bindValueWithName
     // bindValueWithName[V]("test", {})
    //}
    c.Expr(q"""bindValueWithName($inputName,${reify(expr.splice)})""")
   // c.Expr(q"""bindValueWithName[V]($inputName,${reify(expr.splice)})""")
  }
}