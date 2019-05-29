package tkom.ast.binop.arithmetic

import tkom.ast.ASTNode
import tkom.ast.binop.BinOpAstNode

class SubtractAstNode(leftOperand: ASTNode, rightOperand: ASTNode) : BinOpAstNode(leftOperand, rightOperand)