package tkom.ast

import tkom.ast.ASTNode

class FunctionCallAstNode(val identifier: ASTNode, val argumentsList: ASTNode): ASTNode() {
}