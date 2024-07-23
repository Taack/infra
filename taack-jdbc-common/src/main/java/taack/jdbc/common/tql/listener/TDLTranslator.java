package taack.jdbc.common.tql.listener;

import org.antlr.v4.runtime.tree.ErrorNode;
import taack.jdbc.common.tql.gen.TDLBaseListener;
import taack.jdbc.common.tql.gen.TDLParser;

import java.util.SortedMap;
import java.util.TreeMap;

public final class TDLTranslator extends TDLBaseListener {

    final String tdl;
    String currentColId;

    public enum Kind {
        TABLE, DIAGRAM
        }
    public Kind kind;
    public final SortedMap<String, String> cols = new TreeMap<>();

    public TDLTranslator(String tdl) {
        this.tdl = tdl;
    }

    @Override
    public void enterTdl(TDLParser.TdlContext ctx) {
        super.enterTdl(ctx);
    }

    @Override
    public void exitTdl(TDLParser.TdlContext ctx) {
        super.exitTdl(ctx);
    }

    @Override
    public void enterDisplayKind(TDLParser.DisplayKindContext ctx) {
        System.out.println("enterDisplayKind " + ctx.getText());
        kind = ctx.getText().equalsIgnoreCase("table") ? Kind.TABLE : Kind.DIAGRAM;
        super.enterDisplayKind(ctx);
    }

    @Override
    public void enterIdColumn(TDLParser.IdColumnContext ctx) {
        currentColId = ctx.getText();
        super.enterIdColumn(ctx);
    }

    @Override
    public void enterAliasColumn(TDLParser.AliasColumnContext ctx) {
        cols.put(currentColId, ctx.getText().substring(1, ctx.getText().length() - 1));
        super.enterAliasColumn(ctx);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.out.println(node);
        super.visitErrorNode(node);
    }
}
