package tkom.ast.binop.comparison

import tkom.ast.ASTNode
import tkom.ast.binop.BinOpAstNode

class LessAstNode(leftOperand: ASTNode, rightOperand: ASTNode) : BinOpAstNode(leftOperand, rightOperand)