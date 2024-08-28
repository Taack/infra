// Generated from TDL.g4 by ANTLR 4.13.1
package taack.jdbc.common.tql.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TDLParser}.
 */
public interface TDLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TDLParser#tdl}.
	 * @param ctx the parse tree
	 */
	void enterTdl(TDLParser.TdlContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#tdl}.
	 * @param ctx the parse tree
	 */
	void exitTdl(TDLParser.TdlContext ctx);
	/**
	 * Enter a parse tree produced by {@link TDLParser#displayKind}.
	 * @param ctx the parse tree
	 */
	void enterDisplayKind(TDLParser.DisplayKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#displayKind}.
	 * @param ctx the parse tree
	 */
	void exitDisplayKind(TDLParser.DisplayKindContext ctx);
	/**
	 * Enter a parse tree produced by {@link TDLParser#columnExpressions}.
	 * @param ctx the parse tree
	 */
	void enterColumnExpressions(TDLParser.ColumnExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#columnExpressions}.
	 * @param ctx the parse tree
	 */
	void exitColumnExpressions(TDLParser.ColumnExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TDLParser#columnExpression}.
	 * @param ctx the parse tree
	 */
	void enterColumnExpression(TDLParser.ColumnExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#columnExpression}.
	 * @param ctx the parse tree
	 */
	void exitColumnExpression(TDLParser.ColumnExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TDLParser#idColumn}.
	 * @param ctx the parse tree
	 */
	void enterIdColumn(TDLParser.IdColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#idColumn}.
	 * @param ctx the parse tree
	 */
	void exitIdColumn(TDLParser.IdColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link TDLParser#aliasColumn}.
	 * @param ctx the parse tree
	 */
	void enterAliasColumn(TDLParser.AliasColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TDLParser#aliasColumn}.
	 * @param ctx the parse tree
	 */
	void exitAliasColumn(TDLParser.AliasColumnContext ctx);
}