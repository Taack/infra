package taack.jdbc.common.tql.listener;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import taack.jdbc.common.tql.gen.TQLBaseListener;
import taack.jdbc.common.tql.gen.TQLParser;

import java.io.PrintStream;

public class TQLDump extends TQLBaseListener {
    final static String INDENT = "    ";
    short indentCounter = 0;

    final PrintStream outputStream;

    public TQLDump(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TQLDump() {
        this.outputStream = null;
    }

    final void buildIndent(final short counter, final String logValue) {
        if (outputStream == null) return;
        StringBuilder indentation = new StringBuilder();
        for (short i = 0; i < counter; i ++) {
            indentation.append(INDENT);
        }
        outputStream.println((indentation.toString() + logValue));
    }

    final void indentedPrint(final String log) {
        buildIndent(indentCounter, log);
    }

    final void indentPrint(final String log) {
        buildIndent(indentCounter++, log);
    }

    final void deIndentedPrint(final String log) {
        buildIndent(--indentCounter, log);
    }

    @Override
    public void enterTql(TQLParser.TqlContext ctx) {
        indentPrint("enterTql " + ctx);
        super.enterTql(ctx);
    }

    @Override
    public void exitTql(TQLParser.TqlContext ctx) {
        deIndentedPrint("exitTql " + ctx);
        super.exitTql(ctx);
    }

    @Override
    public void enterGroupByExpression(TQLParser.GroupByExpressionContext ctx) {
        indentPrint("enterGroupByExpression " + ctx);
        super.enterGroupByExpression(ctx);
    }

    @Override
    public void exitGroupByExpression(TQLParser.GroupByExpressionContext ctx) {
        deIndentedPrint("exitGroupByExpression " + ctx);
        super.exitGroupByExpression(ctx);
    }

    @Override
    public void enterColumnExpression(TQLParser.ColumnExpressionContext ctx) {
        indentPrint("enterColumnExpression " + ctx);
        super.enterColumnExpression(ctx);
    }

    @Override
    public void exitColumnExpression(TQLParser.ColumnExpressionContext ctx) {
        deIndentedPrint("exitColumnExpression " + ctx);
        super.exitColumnExpression(ctx);
    }

    @Override
    public void enterSelectExpression(TQLParser.SelectExpressionContext ctx) {
        indentPrint("enterSelectExpression " + ctx);
        super.enterSelectExpression(ctx);
    }

    @Override
    public void exitSelectExpression(TQLParser.SelectExpressionContext ctx) {
        deIndentedPrint("exitSelectExpression " + ctx);
        super.exitSelectExpression(ctx);
    }

    @Override
    public void enterSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx) {
        indentPrint("enterSelectFunctionExpression " + ctx);
        super.enterSelectFunctionExpression(ctx);
    }

    @Override
    public void exitSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx) {
        deIndentedPrint("exitSelectFunctionExpression " + ctx);
        super.exitSelectFunctionExpression(ctx);
    }

    @Override
    public void enterAliasColumn(TQLParser.AliasColumnContext ctx) {
        indentPrint("enterAliasColumn " + ctx);
        super.enterAliasColumn(ctx);
    }

    @Override
    public void exitAliasColumn(TQLParser.AliasColumnContext ctx) {
        deIndentedPrint("exitAliasColumn " + ctx);
        super.exitAliasColumn(ctx);
    }

    @Override
    public void enterSelectStar(TQLParser.SelectStarContext ctx) {
        indentPrint("enterSelectStar " + ctx);
        super.enterSelectStar(ctx);
    }

    @Override
    public void exitSelectStar(TQLParser.SelectStarContext ctx) {
        deIndentedPrint("exitSelectStar " + ctx);
        super.exitSelectStar(ctx);
    }

    @Override
    public void enterAliasTable(TQLParser.AliasTableContext ctx) {
        indentPrint("enterAliasTable " + ctx);
        super.enterAliasTable(ctx);
    }

    @Override
    public void exitAliasTable(TQLParser.AliasTableContext ctx) {
        deIndentedPrint("exitAliasTable " + ctx);
        super.exitAliasTable(ctx);
    }

    @Override
    public void enterFromExpression(TQLParser.FromExpressionContext ctx) {
        indentPrint("enterFromExpression " + ctx);
        super.enterFromExpression(ctx);
    }

    @Override
    public void exitFromExpression(TQLParser.FromExpressionContext ctx) {
        deIndentedPrint("exitFromExpression " + ctx);
        super.exitFromExpression(ctx);
    }

    @Override
    public void enterWhereExpression(TQLParser.WhereExpressionContext ctx) {
        indentPrint("enterWhereExpression " + ctx);
        super.enterWhereExpression(ctx);
    }

    @Override
    public void exitWhereExpression(TQLParser.WhereExpressionContext ctx) {
        deIndentedPrint("exitWhereExpression " + ctx);
        super.exitWhereExpression(ctx);
    }

    @Override
    public void enterWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx) {
        indentPrint("enterWhereExpressionElement " + ctx);
        super.enterWhereExpressionElement(ctx);
    }

    @Override
    public void enterIdTable(TQLParser.IdTableContext ctx) {
        indentPrint("enterIdTable " + ctx);
        super.enterIdTable(ctx);
    }

    @Override
    public void exitIdTable(TQLParser.IdTableContext ctx) {
        deIndentedPrint("exitIdTable " + ctx);
        super.exitIdTable(ctx);
    }

    @Override
    public void enterSelFunc(TQLParser.SelFuncContext ctx) {
        indentPrint("enterSelFunc " + ctx);
        super.enterSelFunc(ctx);
    }

    @Override
    public void exitSelFunc(TQLParser.SelFuncContext ctx) {
        deIndentedPrint("exitSelFunc " + ctx);
        super.exitSelFunc(ctx);
    }

    @Override
    public void enterIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx) {
        indentPrint("enterIdTableWithAlias " + ctx);
        super.enterIdTableWithAlias(ctx);
    }

    @Override
    public void exitIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx) {
        deIndentedPrint("exitIdTableWithAlias " + ctx);
        super.exitIdTableWithAlias(ctx);
    }

    @Override
    public void enterIdTableStar(TQLParser.IdTableStarContext ctx) {
        indentPrint("enterIdTableStar " + ctx);
        super.enterIdTableStar(ctx);
    }

    @Override
    public void exitIdTableStar(TQLParser.IdTableStarContext ctx) {
        deIndentedPrint("exitIdTableStar " + ctx);
        super.exitIdTableStar(ctx);
    }

    @Override
    public void enterIdColumn(TQLParser.IdColumnContext ctx) {
        indentPrint("enterIdColumn " + ctx);
        super.enterIdColumn(ctx);
    }

    @Override
    public void exitIdColumn(TQLParser.IdColumnContext ctx) {
        deIndentedPrint("exitIdColumn " + ctx);
        super.exitIdColumn(ctx);
    }

    @Override
    public void exitWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx) {
        deIndentedPrint("exitWhereExpressionElement " + ctx);
        super.exitWhereExpressionElement(ctx);
    }

    @Override
    public void enterAdditionalExpression(TQLParser.AdditionalExpressionContext ctx) {
        indentPrint("enterAdditionalExpression " + ctx);
        super.enterAdditionalExpression(ctx);
    }

    @Override
    public void exitAdditionalExpression(TQLParser.AdditionalExpressionContext ctx) {
        deIndentedPrint("exitAdditionalExpression " + ctx);
        super.exitAdditionalExpression(ctx);
    }

    @Override
    public void enterMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx) {
        indentPrint("enterMultiplyingExpression " + ctx);
        super.enterMultiplyingExpression(ctx);
    }

    @Override
    public void exitMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx) {
        deIndentedPrint("exitMultiplyingExpression " + ctx);
        super.exitMultiplyingExpression(ctx);
    }

    @Override
    public void enterPowExpression(TQLParser.PowExpressionContext ctx) {
        indentPrint("enterPowExpression " + ctx);
        super.enterPowExpression(ctx);
    }

    @Override
    public void exitPowExpression(TQLParser.PowExpressionContext ctx) {
        deIndentedPrint("exitPowExpression " + ctx);
        super.exitPowExpression(ctx);
    }

    @Override
    public void enterSignedAtom(TQLParser.SignedAtomContext ctx) {
        indentPrint("enterSignedAtom " + ctx);
        super.enterSignedAtom(ctx);
    }

    @Override
    public void exitSignedAtom(TQLParser.SignedAtomContext ctx) {
        deIndentedPrint("exitSignedAtom " + ctx);
        super.exitSignedAtom(ctx);
    }

    @Override
    public void enterAtom(TQLParser.AtomContext ctx) {
        indentPrint("enterAtom " + ctx);
        super.enterAtom(ctx);
    }

    @Override
    public void exitAtom(TQLParser.AtomContext ctx) {
        deIndentedPrint("exitAtom " + ctx);
        super.exitAtom(ctx);
    }

    @Override
    public void enterScientific(TQLParser.ScientificContext ctx) {
        indentPrint("enterScientific " + ctx);
        super.enterScientific(ctx);
    }

    @Override
    public void exitScientific(TQLParser.ScientificContext ctx) {
        deIndentedPrint("exitScientific " + ctx);
        super.exitScientific(ctx);
    }

    @Override
    public void enterJunctionOp(TQLParser.JunctionOpContext ctx) {
        indentPrint("enterJunctionOp " + ctx);
        super.enterJunctionOp(ctx);
    }

    @Override
    public void exitJunctionOp(TQLParser.JunctionOpContext ctx) {
        deIndentedPrint("exitJunctionOp " + ctx);
        super.exitJunctionOp(ctx);
    }

    @Override
    public void enterRelOp(TQLParser.RelOpContext ctx) {
        indentPrint("enterRelOp " + ctx);
        super.enterRelOp(ctx);
    }

    @Override
    public void exitRelOp(TQLParser.RelOpContext ctx) {
        deIndentedPrint("exitRelOp " + ctx);
        super.exitRelOp(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
//        indentPrint("enterEveryRule " + ctx);
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
//        deIndentedPrint("exitEveryRule " + ctx);
        super.exitEveryRule(ctx);
    }

    @Override
    public void enterWhereClause(TQLParser.WhereClauseContext ctx) {
        indentPrint("enterWhereClause " + ctx + " " + ctx.invokingState + " " + ctx.start.getStartIndex() + " " + ctx.stop.getStopIndex());
        super.enterWhereClause(ctx);
    }

    @Override
    public void exitWhereClause(TQLParser.WhereClauseContext ctx) {
        deIndentedPrint("exitWhereClause " + ctx + " " + ctx.invokingState + " " + ctx.start.getStartIndex() + " " + ctx.stop.getStopIndex());
        super.exitWhereClause(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        indentedPrint("visitTerminal " + node);
        super.visitTerminal(node);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        indentedPrint("visitErrorNode " + node);
        super.visitErrorNode(node);
    }
}
