package tkom.ast.Comparison

import tkom.ast.ASTNode
import tkom.ast.BinOpAstNode

class LessOrEqualAstNode(leftOperand: ASTNode, rightOperand: ASTNode): BinOpAstNode(leftOperand, rightOperand) {
}