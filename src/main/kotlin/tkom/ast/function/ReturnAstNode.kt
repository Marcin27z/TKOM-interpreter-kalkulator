package tkom.ast.function

import tkom.ast.ASTNode
import tkom.ast.NopAstNode

class ReturnAstNode(val value: ASTNode = NopAstNode()) : ASTNode()