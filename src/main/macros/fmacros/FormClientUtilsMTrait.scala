package fmacros

import scala.reflect.macros.blackbox
import scala.language.experimental.macros

trait FormClientUtilsMTrait {

  def formInputMatch(name: String)(cl: String => Any): Unit = macro FormClientUtilsMTraitMacros.formInputMatchImpl


}

object FormClientUtilsMTraitMacros {

  
  
  def formInputMatchImpl(c: blackbox.Context)(name: c.Expr[String])(cl: c.Expr[String => Any]): c.Expr[Unit] = {
    import c.universe._

    // Find cases in closure
    val closurebase = cl.tree.children.last
    val closureInputArgs = cl.tree.children.last.children
    //c.echo(c.enclosingPosition, "CT: "+closurebase.children.head.)
    closureInputArgs.foreach {
      case arg if(arg.children.size>0) => 
       c.echo(c.enclosingPosition, "Arg: "+arg.children.head+" -> "+arg.children.head.tpe+" -> "+arg.children.last  )
      case other => 
    }
    
    val inputcases = closureInputArgs.collect {
      case arg if(arg.children.size>0) => 
       
        (arg.children.head.toString() -> (arg.children.last))
        
    }
    //c.echo(c.enclosingPosition, s"""Test: ${cl.tree.children.last.children}""")
     
    
    //c.Expr(q"""debugExpr(c)(cl)""") ${reify(name.splice)}
    c.Expr(q"""formInputMatchList("-",$inputcases)""")
  }

}