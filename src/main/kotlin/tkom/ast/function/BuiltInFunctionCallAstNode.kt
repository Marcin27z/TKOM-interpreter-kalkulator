package tkom.ast.function

import tkom.ast.ASTNode
import tkom.ast.IdentifierAstNode

class BuiltInFunctionCallAstNode(val identifier: IdentifierAstNode, val argumentsList: CallArgumentsListAstNode) : ASTNode()