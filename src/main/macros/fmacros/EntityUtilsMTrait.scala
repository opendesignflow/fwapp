package fmacros

import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import scala.reflect.macros.blackbox
import scala.language.experimental.macros

trait EntityUtilsMTrait {
  
  def bindToEntity(buffer:Buffer) :   Unit = macro EntityUtilsMTraitMacros.bindToEntity
  
}

object EntityUtilsMTraitMacros {
  
  def bindToEntity(c: blackbox.Context)(buffer: c.Expr[Buffer]) : c.Expr[Unit] = {
    import c.universe._
    
    c.echo(c.enclosingPosition, s"""Test: ${buffer.tree.children}""")
    c.Expr(q"")
  }
}