package tkom.ast.binop.logical

import tkom.ast.ASTNode
import tkom.ast.binop.BinOpAstNode

class OrAstNode(leftOperand: ASTNode, rightOperand: ASTNode) : BinOpAstNode(leftOperand, rightOperand)