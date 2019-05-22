package tkom.ast.Logical

import tkom.ast.ASTNode
import tkom.ast.BinOpAstNode

class OrAstNode(leftOperand: ASTNode, rightOperand: ASTNode): BinOpAstNode(leftOperand, rightOperand) {
}