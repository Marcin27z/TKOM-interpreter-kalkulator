package tkom.ast.Logical

import tkom.ast.ASTNode
import tkom.ast.BinOpAstNode

class AndAstNode(leftOperand: ASTNode, rightOperand: ASTNode): BinOpAstNode(leftOperand, rightOperand) {
}