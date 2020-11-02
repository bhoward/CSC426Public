sealed trait ICode

case class LetNum(id: String, num: String) extends ICode {
  override def toString: String = s"$id := $num"
}

case class LetVar(id: String, id2: String) extends ICode {
  override def toString: String = s"$id := $id2"
}

case class LetBinOp(id: String, id2: String, op: IBinOp, id3: String) extends ICode {
  override def toString: String = s"$id := $id2 ${op.toString} $id3"
}

case class Call(id: String, id2: String) extends ICode {
  override def toString: String = s"CALL $id($id2)"
}

sealed trait IBinOp
case object AddI extends IBinOp
case object AddR extends IBinOp
