package tkom.ast

import tkom.Token

class ASTNode(
  val token: Token,
  var nodes: List<ASTNode>
) {

}