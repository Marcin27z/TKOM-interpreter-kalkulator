package tkom.ast.function

import tkom.ast.ASTNode
import tkom.ast.IdentifierAstNode

class FunctionDefinitionAstNode(val identifier: IdentifierAstNode, val arguments: ArgumentsListAstNode, val instructionList: ASTNode) : ASTNode()