package tkom.ast.Arithmetic

import tkom.ast.ASTNode
import tkom.ast.BinOpAstNode

class AddAstNode(leftOperand: ASTNode, rightOperand: ASTNode): BinOpAstNode(leftOperand, rightOperand)