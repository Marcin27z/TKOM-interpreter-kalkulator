package tkom.ast.Comparison

import tkom.ast.ASTNode

class NotEqualAstNode(val leftOperand: ASTNode, val rightOperand: ASTNode) : ASTNode() {}