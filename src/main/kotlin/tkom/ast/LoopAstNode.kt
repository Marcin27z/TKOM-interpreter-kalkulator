package tkom.ast

class LoopAstNode(val assignment: ASTNode, val endCondition: ASTNode, val stepAssignment: ASTNode, val instructionList: ASTNode): ASTNode() {
}