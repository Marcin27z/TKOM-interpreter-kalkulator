package tkom.ast.loop

import tkom.ast.ASTNode

class LoopAstNode(val assignment: ASTNode, val endCondition: ASTNode, val stepAssignment: ASTNode, val instructionList: ASTNode) : ASTNode()