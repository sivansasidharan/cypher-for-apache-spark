package org.opencypher.spark.impl.frame

import org.apache.spark.sql.Dataset
import org.opencypher.spark.impl.newvalue.CypherValue
import org.opencypher.spark.impl.{StdCypherFrame, StdField, StdRuntimeContext}

import scala.reflect._

object OrderBy extends FrameCompanion {

  def apply(input: StdCypherFrame[Product])(key: Symbol): StdCypherFrame[Product] = {
    val keyFieldIndex = obtain(input.signature.slot)(key).ordinal
    OrderBy(input)(keyFieldIndex)
  }

  private final case class OrderBy(input: StdCypherFrame[Product])(keyIndex: Int) extends StdCypherFrame[Product](input.signature) {

    override protected def execute(implicit context: StdRuntimeContext): Dataset[Product] = {
      val in = input.run

      val sortedRdd = in.rdd.sortBy(OrderByColumn(keyIndex))(???, classTag[CypherValue])

      val out = context.session.createDataset(sortedRdd)(context.productEncoder(slots))
      out
    }
  }

  private final case class OrderByColumn(index: Int) extends (Product => CypherValue) {

    import org.opencypher.spark.impl.util._

    override def apply(product: Product): CypherValue = {
      product.getAs[CypherValue](index)
    }
  }

}