package taack.jdbc.common.tql.listener;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import taack.jdbc.common.tql.gen.TQLBaseListener;
import taack.jdbc.common.tql.gen.TQLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TQLTranslator extends TQLBaseListener {

    public static class Col {
        public enum Type {
            DATA, FORMULA, TABLE_STAR, STAR, GETTER
        }

        Col(Type colType, String colName) {
            this.colType = colType;
            this.colName = colName;
            this.formula = null;
        }

        Col(Type colType, String colName, String formula) {
            this.formula = formula;
            this.colType = colType;
            this.colName = colName;
        }

        public Type colType;
        public final String colName;
        public final String formula;
    }

    public final String tql;
    public final List<Col> columns = new ArrayList<>();
    public final Map<Col, String> colAliasMap = new HashMap<>();
    public final List<String> tables = new ArrayList<>();
    public final Map<String, String> tabAliasMap = new HashMap<>();
    public final List<String> errors = new ArrayList<>();
    Integer whereClauseStart = null;
    Integer whereClauseEnd;
    Integer groupByClauseStart = null;
    Integer groupByClauseEnd;
    Integer additionalExpressionStart = null;
    Integer additionalExpressionEnd;

    enum Context {NONE, COL_ID, COL_ALIAS, COL_TABLE_STAR, COL_FORMULA, TABLE_ID, TABLE_ALIAS, WHERE, GROUP_BY, SELECT_STAR, ADD_EXPR}

    Context context = Context.NONE;
    String formula = null;
    private Col lastCol;
    private String lastTab;
    private int colIndex = 0;

    public TQLTranslator(String tql) {
        this.tql = tql;
    }

    public String getSelectClause() {
        StringBuffer res = new StringBuffer();
        boolean first = true;
        for (var c : columns) {
            if (first) {
                first = false;
            } else res.append(',');
            if (c.colType == Col.Type.GETTER) {
                res.append("id as " + colAliasMap.get(c));
            } else if (c.colType == Col.Type.FORMULA) {
                if (c.formula != null) {
                    res.append(c.formula + '(' + c.colName + ')');
                } else res.append(c.colName);
            } else {
                if (c.formula != null) {
                    res.append(c.formula + '(' + c.colName + ')');
                } else res.append(c.colName);
            }
            String alias = colAliasMap.get(c);
            if (alias != null && c.colType != Col.Type.GETTER) {
                res.append(" as ");
                res.append(alias);
            }

        }
        return res.toString();
    }

    public String getFromClause() {
        StringBuffer res = new StringBuffer();
        boolean first = true;
        for (var t : tables) {
            if (first) {
                first = false;
            } else res.append(',');
            res.append(t);
            String alias = tabAliasMap.get(t);
            if (alias != null) {
                res.append(" ");
                res.append(alias);
            }
        }
        return res.toString();
    }

    public String getWhereClause() {
        if (whereClauseStart != null && whereClauseEnd != null) {
            return " where " + tql.substring(whereClauseStart, whereClauseEnd + 1);
        }
        return null;
    }

    public String getGroupByClause() {
        if (groupByClauseStart != null && groupByClauseEnd != null) {
            return " group by " + tql.substring(groupByClauseStart, groupByClauseEnd + 1);
        }
        return null;
    }

    public String getGroupByClauseColumns() {
        if (groupByClauseStart != null && groupByClauseEnd != null) {
            return tql.substring(groupByClauseStart, groupByClauseEnd + 1);
        }
        return null;
    }

    public boolean isStar() {
        if (columns != null) {
            for (var c : columns) {
                if (c.colType == Col.Type.STAR) return true;
            }
        }
        return false;
    }

    public boolean hasAliasedStar() {
        for (var a : tabAliasMap.values()) {
            if (hasAliasedStar(a)) return true;
        }
        return false;
    }

    public String getAliasFromColumnName(String col) {
        for (var a : tabAliasMap.values()) {
            if (col.startsWith(a + ".")) return a;
        }
        return null;
    }

    public String getUnaliasedColumnName(String col) {
        for (var a : tabAliasMap.values()) {
            if (col.startsWith(a + '.')) return col.substring(a.length() + 1);
        }
        return col;
    }

    public boolean hasAliasedStar(String alias) {
        if (columns != null) {
            for (var c : columns) {
                if (c.colType == Col.Type.TABLE_STAR && c.colName.startsWith(alias)) return true;
            }
        }
        return false;
    }

    public List<String> plainColumnNames() {
        var l = new ArrayList<String>();
        if (columns != null) {
            for (var c : columns) {
                if (c.colType == Col.Type.DATA)
                    l.add(c.colName);
            }
        }
        return l;
    }

    public void addColumnNames(List<String> names, String alias) {
        for (var n : names) {
            String a = alias != null ? alias + "." : "";
            columns.add(new Col(Col.Type.DATA, a + n));
        }
    }

    @Override
    public void enterSelectStar(TQLParser.SelectStarContext ctx) {
        context = Context.SELECT_STAR;
        super.enterSelectStar(ctx);
    }

    @Override
    public void enterGroupByExpression(TQLParser.GroupByExpressionContext ctx) {
        if (groupByClauseStart == null) groupByClauseStart = ctx.start.getStartIndex();
        super.enterGroupByExpression(ctx);
    }

    @Override
    public void exitGroupByExpression(TQLParser.GroupByExpressionContext ctx) {
        groupByClauseEnd = ctx.stop.getStopIndex();
        super.exitGroupByExpression(ctx);
    }

    @Override
    public void exitSelectStar(TQLParser.SelectStarContext ctx) {
        context = Context.NONE;
        super.exitSelectStar(ctx);
    }

    @Override
    public void enterTql(TQLParser.TqlContext ctx) {
        super.enterTql(ctx);
    }

    @Override
    public void exitTql(TQLParser.TqlContext ctx) {
        super.exitTql(ctx);
    }

    @Override
    public void enterSelectExpression(TQLParser.SelectExpressionContext ctx) {
        super.enterSelectExpression(ctx);
    }

    @Override
    public void exitSelectExpression(TQLParser.SelectExpressionContext ctx) {
        super.exitSelectExpression(ctx);
    }

    @Override
    public void enterSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx) {
        super.enterSelectFunctionExpression(ctx);
    }

    @Override
    public void exitSelectFunctionExpression(TQLParser.SelectFunctionExpressionContext ctx) {
        super.exitSelectFunctionExpression(ctx);
    }

    @Override
    public void enterFromExpression(TQLParser.FromExpressionContext ctx) {
        super.enterFromExpression(ctx);
    }

    @Override
    public void exitFromExpression(TQLParser.FromExpressionContext ctx) {
        super.exitFromExpression(ctx);
    }

    @Override
    public void enterIdTable(TQLParser.IdTableContext ctx) {
        context = Context.TABLE_ID;
        super.enterIdTable(ctx);
    }

    @Override
    public void exitIdTable(TQLParser.IdTableContext ctx) {
        context = Context.NONE;
        super.exitIdTable(ctx);
    }

    @Override
    public void enterIdColumn(TQLParser.IdColumnContext ctx) {
        if (whereClauseStart == null && additionalExpressionStart == null && groupByClauseStart == null) context = Context.COL_ID;
        super.enterIdColumn(ctx);
    }

    @Override
    public void exitIdColumn(TQLParser.IdColumnContext ctx) {
        if (whereClauseStart == null && additionalExpressionStart == null && groupByClauseStart == null) context = Context.NONE;
        super.exitIdColumn(ctx);
    }

    @Override
    public void enterWhereClause(TQLParser.WhereClauseContext ctx) {
        if (whereClauseStart == null) whereClauseStart = ctx.start.getStartIndex();
        // System.out.println("AUO " + whereClauseStart + " " + ctx.getText() + " " + tql.substring(whereClauseStart));
        super.enterWhereClause(ctx);
    }

    @Override
    public void exitWhereClause(TQLParser.WhereClauseContext ctx) {
        whereClauseEnd = ctx.stop.getStopIndex();
        // System.out.println("AUO " + whereClauseEnd + " " + ctx.getText() + " " + tql.substring(whereClauseEnd));
        super.exitWhereClause(ctx);
    }

    @Override
    public void enterWhereExpression(TQLParser.WhereExpressionContext ctx) {
        super.enterWhereExpression(ctx);
    }

    @Override
    public void exitWhereExpression(TQLParser.WhereExpressionContext ctx) {
        super.exitWhereExpression(ctx);
    }

    @Override
    public void enterWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx) {
        super.enterWhereExpressionElement(ctx);
    }

    @Override
    public void exitWhereExpressionElement(TQLParser.WhereExpressionElementContext ctx) {
        super.exitWhereExpressionElement(ctx);
    }

    @Override
    public void enterJunctionOp(TQLParser.JunctionOpContext ctx) {
        super.enterJunctionOp(ctx);
    }

    @Override
    public void exitJunctionOp(TQLParser.JunctionOpContext ctx) {
        super.exitJunctionOp(ctx);
    }

    @Override
    public void enterAdditionalExpression(TQLParser.AdditionalExpressionContext ctx) {
        if (additionalExpressionStart == null && whereClauseStart == null) {
            additionalExpressionStart = ctx.start.getStartIndex();
            context = Context.ADD_EXPR;
        }
        super.enterAdditionalExpression(ctx);
    }

    @Override
    public void exitAdditionalExpression(TQLParser.AdditionalExpressionContext ctx) {
        if (additionalExpressionStart != null && whereClauseStart == null) {
            additionalExpressionEnd = ctx.stop.getStopIndex();
            context = Context.NONE;
            lastCol = new Col(Col.Type.FORMULA, tql.substring(additionalExpressionStart, additionalExpressionEnd + 1), formula);
            formula = null;
            columns.add(lastCol);
            additionalExpressionStart = null;
        }
        super.exitAdditionalExpression(ctx);
    }

    @Override
    public void enterMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx) {
        super.enterMultiplyingExpression(ctx);
    }

    @Override
    public void exitMultiplyingExpression(TQLParser.MultiplyingExpressionContext ctx) {
        super.exitMultiplyingExpression(ctx);
    }

    @Override
    public void enterPowExpression(TQLParser.PowExpressionContext ctx) {
        super.enterPowExpression(ctx);
    }

    @Override
    public void exitPowExpression(TQLParser.PowExpressionContext ctx) {
        super.exitPowExpression(ctx);
    }

    @Override
    public void enterSignedAtom(TQLParser.SignedAtomContext ctx) {
        super.enterSignedAtom(ctx);
    }

    @Override
    public void exitSignedAtom(TQLParser.SignedAtomContext ctx) {
        super.exitSignedAtom(ctx);
    }

    @Override
    public void enterAtom(TQLParser.AtomContext ctx) {
        super.enterAtom(ctx);
    }

    @Override
    public void exitAtom(TQLParser.AtomContext ctx) {
        super.exitAtom(ctx);
    }

    @Override
    public void enterScientific(TQLParser.ScientificContext ctx) {
        super.enterScientific(ctx);
    }

    @Override
    public void exitScientific(TQLParser.ScientificContext ctx) {
        super.exitScientific(ctx);
    }

    @Override
    public void enterRelOp(TQLParser.RelOpContext ctx) {
        super.enterRelOp(ctx);
    }

    @Override
    public void exitRelOp(TQLParser.RelOpContext ctx) {
        super.exitRelOp(ctx);
    }

    @Override
    public void enterIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx) {
        super.enterIdTableWithAlias(ctx);
    }

    @Override
    public void exitIdTableWithAlias(TQLParser.IdTableWithAliasContext ctx) {
        super.exitIdTableWithAlias(ctx);
    }

    @Override
    public void enterSelFunc(TQLParser.SelFuncContext ctx) {
        if (whereClauseStart == null) context = Context.COL_FORMULA;
        super.enterSelFunc(ctx);
    }

    @Override
    public void exitSelFunc(TQLParser.SelFuncContext ctx) {
        if (whereClauseStart == null) context = Context.NONE;
        super.exitSelFunc(ctx);
    }

    @Override
    public void enterColumnExpression(TQLParser.ColumnExpressionContext ctx) {
        super.enterColumnExpression(ctx);
    }

    @Override
    public void exitColumnExpression(TQLParser.ColumnExpressionContext ctx) {
        super.exitColumnExpression(ctx);
    }

    @Override
    public void enterAliasColumn(TQLParser.AliasColumnContext ctx) {
        if (whereClauseStart == null) context = Context.COL_ALIAS;
        super.enterAliasColumn(ctx);
    }

    @Override
    public void exitAliasColumn(TQLParser.AliasColumnContext ctx) {
        if (whereClauseStart == null) context = Context.NONE;
        super.exitAliasColumn(ctx);
    }

    @Override
    public void enterAliasTable(TQLParser.AliasTableContext ctx) {
        context = Context.TABLE_ALIAS;
        super.enterAliasTable(ctx);
    }

    @Override
    public void exitAliasTable(TQLParser.AliasTableContext ctx) {
        context = Context.NONE;
        super.exitAliasTable(ctx);
    }

    @Override
    public void enterIdTableStar(TQLParser.IdTableStarContext ctx) {
        if (whereClauseStart == null) context = Context.COL_TABLE_STAR;
        super.enterIdTableStar(ctx);
    }

    @Override
    public void exitIdTableStar(TQLParser.IdTableStarContext ctx) {
        if (whereClauseStart == null) context = Context.NONE;
        super.exitIdTableStar(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        switch (context) {
            case WHERE:
                break;
            case NONE:
                break;
            case COL_ID:
                lastCol = new Col(Col.Type.DATA, node.getText(), formula);
                formula = null;
                columns.add(lastCol);
                break;
            case COL_ALIAS:
                colAliasMap.put(lastCol, node.getText());
                break;
            case SELECT_STAR:
                columns.add(new Col(Col.Type.STAR, node.getText()));
                break;
            case TABLE_ID:
                lastTab = node.getText();
                tables.add(lastTab);
                break;
            case TABLE_ALIAS:
                tabAliasMap.put(lastTab, node.getText());
                break;
            case COL_TABLE_STAR:
                columns.add(new Col(Col.Type.TABLE_STAR, node.getText()));
                break;
            case COL_FORMULA:
                formula = node.getText();
                break;
        }
        super.visitTerminal(node);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        errors.add(node.toString());
        super.visitErrorNode(node);
    }
}
