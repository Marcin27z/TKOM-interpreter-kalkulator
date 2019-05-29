package tkom.ast.binop

import tkom.ast.ASTNode

open class BinOpAstNode(val leftOperand: ASTNode, val rightOperand: ASTNode) : ASTNode()