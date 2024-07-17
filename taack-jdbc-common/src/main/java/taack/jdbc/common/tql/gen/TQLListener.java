// Generated from TQL.g4 by ANTLR 4.13.1
package taack.jdbc.common.tql.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TQLParser}.
 */
public interface TQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TQLParser#tql}.
	 * @param ctx the parse tree
	 */
	void enterTql(TQLParser.TqlContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#tql}.
	 * @param ctx the parse tree
	 */
	void exitTql(TQLParser.TqlContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#selectStar}.
	 * @param ctx the parse tree
	 */
	void enterSelectStar(TQLParser.SelectStarContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#selectStar}.
	 * @param ctx the parse tree
	 */
	void exitSelectStar(TQLParser.SelectStarContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void enterSelectExpression(TQLParser.SelectExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#selectExpression}.
	 * @param ctx the parse tree
	 */
	void exitSelectExpression(TQLParser.SelectExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#groupByExpression}.
	 * @param ctx the parse tree
	 */
	void enterGroupByExpression(TQLParser.GroupByExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#groupByExpression}.
	 * @param ctx the parse tree
	 */
	void exitGroupByExpression(TQLParser.GroupByExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#selectFunctionExpression}.
	 * @param ctx the parse tree
	 */
	void enterSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#selectFunctionExpression}.
	 * @param ctx the parse tree
	 */
	void exitSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#fromExpression}.
	 * @param ctx the parse tree
	 */
	void enterFromExpression(TQLParser.FromExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#fromExpression}.
	 * @param ctx the parse tree
	 */
	void exitFromExpression(TQLParser.FromExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#idTableWithAlias}.
	 * @param ctx the parse tree
	 */
	void enterIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#idTableWithAlias}.
	 * @param ctx the parse tree
	 */
	void exitIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#idTable}.
	 * @param ctx the parse tree
	 */
	void enterIdTable(TQLParser.IdTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#idTable}.
	 * @param ctx the parse tree
	 */
	void exitIdTable(TQLParser.IdTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#selFunc}.
	 * @param ctx the parse tree
	 */
	void enterSelFunc(TQLParser.SelFuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#selFunc}.
	 * @param ctx the parse tree
	 */
	void exitSelFunc(TQLParser.SelFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#columnExpression}.
	 * @param ctx the parse tree
	 */
	void enterColumnExpression(TQLParser.ColumnExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#columnExpression}.
	 * @param ctx the parse tree
	 */
	void exitColumnExpression(TQLParser.ColumnExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#idColumn}.
	 * @param ctx the parse tree
	 */
	void enterIdColumn(TQLParser.IdColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#idColumn}.
	 * @param ctx the parse tree
	 */
	void exitIdColumn(TQLParser.IdColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#aliasColumn}.
	 * @param ctx the parse tree
	 */
	void enterAliasColumn(TQLParser.AliasColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#aliasColumn}.
	 * @param ctx the parse tree
	 */
	void exitAliasColumn(TQLParser.AliasColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#aliasTable}.
	 * @param ctx the parse tree
	 */
	void enterAliasTable(TQLParser.AliasTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#aliasTable}.
	 * @param ctx the parse tree
	 */
	void exitAliasTable(TQLParser.AliasTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#idTableStar}.
	 * @param ctx the parse tree
	 */
	void enterIdTableStar(TQLParser.IdTableStarContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#idTableStar}.
	 * @param ctx the parse tree
	 */
	void exitIdTableStar(TQLParser.IdTableStarContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(TQLParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(TQLParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#whereExpression}.
	 * @param ctx the parse tree
	 */
	void enterWhereExpression(TQLParser.WhereExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#whereExpression}.
	 * @param ctx the parse tree
	 */
	void exitWhereExpression(TQLParser.WhereExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#whereExpressionElement}.
	 * @param ctx the parse tree
	 */
	void enterWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#whereExpressionElement}.
	 * @param ctx the parse tree
	 */
	void exitWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#junctionOp}.
	 * @param ctx the parse tree
	 */
	void enterJunctionOp(TQLParser.JunctionOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#junctionOp}.
	 * @param ctx the parse tree
	 */
	void exitJunctionOp(TQLParser.JunctionOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#additionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditionalExpression(TQLParser.AdditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#additionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditionalExpression(TQLParser.AdditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#multiplyingExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#multiplyingExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#powExpression}.
	 * @param ctx the parse tree
	 */
	void enterPowExpression(TQLParser.PowExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#powExpression}.
	 * @param ctx the parse tree
	 */
	void exitPowExpression(TQLParser.PowExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#signedAtom}.
	 * @param ctx the parse tree
	 */
	void enterSignedAtom(TQLParser.SignedAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#signedAtom}.
	 * @param ctx the parse tree
	 */
	void exitSignedAtom(TQLParser.SignedAtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(TQLParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(TQLParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#scientific}.
	 * @param ctx the parse tree
	 */
	void enterScientific(TQLParser.ScientificContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#scientific}.
	 * @param ctx the parse tree
	 */
	void exitScientific(TQLParser.ScientificContext ctx);
	/**
	 * Enter a parse tree produced by {@link TQLParser#relOp}.
	 * @param ctx the parse tree
	 */
	void enterRelOp(TQLParser.RelOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TQLParser#relOp}.
	 * @param ctx the parse tree
	 */
	void exitRelOp(TQLParser.RelOpContext ctx);
}