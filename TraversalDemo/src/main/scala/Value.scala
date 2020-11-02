sealed trait Value

case class IntValue(n: Int) extends Value {
  override def toString: String = n.toString
}

case class RealValue(x: Double) extends Value {
  override def toString: String = x.toString
}
