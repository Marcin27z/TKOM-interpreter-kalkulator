package tkom.ast

class IfAstNode(val condition: ASTNode, val instructionList: ASTNode, val elseNode: ASTNode? = null): ASTNode() {
}