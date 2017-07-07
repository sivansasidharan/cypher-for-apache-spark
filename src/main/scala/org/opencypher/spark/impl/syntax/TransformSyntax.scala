package org.opencypher.spark.impl.syntax

import org.opencypher.spark.api.expr.{Expr, Var}
import org.opencypher.spark.api.record.{RecordHeader, RecordSlot}
import org.opencypher.spark.impl.classes.Transform

import scala.language.implicitConversions

trait TransformSyntax {
  implicit def transformSyntax[T : Transform](subject: T): TransformOps[T] = new TransformOps(subject)
}

final class TransformOps[T](subject: T)(implicit transform: Transform[T]) {
  def reorder(header: RecordHeader): T = transform.reorder(subject, header)
  def alias2(expr: Expr, v: Var, nextHeader: RecordHeader): T = transform.alias2(subject, expr, v, nextHeader)
  def join(other: T)(lhs: RecordSlot, rhs: RecordSlot): T = transform.join(subject, other)(lhs, rhs)
  def join(other: T, header: RecordHeader)(lhs: RecordSlot, rhs: RecordSlot): T = transform.join(subject, other, header)(lhs, rhs)
  def varExpand(rels: T, lower: Int, upper: Int, header: RecordHeader)
               (nodeSlot: RecordSlot, startSlot: RecordSlot, rel: Var, path: Var): T = transform.varExpand(subject, rels, lower, upper, header)(nodeSlot, startSlot, rel, path)
}
