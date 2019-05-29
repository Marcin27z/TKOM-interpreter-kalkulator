package tkom.ast

class IfAstNode(val condition: ASTNode, val instructionList: InstructionListAstNode, val elseNode: ASTNode? = null) : ASTNode()