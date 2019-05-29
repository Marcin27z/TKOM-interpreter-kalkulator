package tkom.ast.function

import tkom.ast.ASTNode
import tkom.ast.IdentifierAstNode

class FunctionCallAstNode(val identifier: IdentifierAstNode, val argumentsList: CallArgumentsListAstNode) : ASTNode()