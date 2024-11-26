// Generated from TDL.g4 by ANTLR 4.13.2
package taack.jdbc.common.tql.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TDLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, AS=2, TABLE=3, BARCHART=4, COLUMN_NAME_FRAGMANT=5, STRING=6, WS=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "AS", "TABLE", "BARCHART", "COLUMN_NAME_FRAGMANT", "STRING", 
			"STRING_CHARACTERS", "StringCharacter", "LOWER_CHARS", "UPPER_CHARS", 
			"NUMBER_CHARS", "ALL_ASCII", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "AS", "TABLE", "BARCHART", "COLUMN_NAME_FRAGMANT", "STRING", 
			"WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public TDLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TDL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0007g\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\"\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0003\u0002.\b\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003@\b\u0003\u0001\u0004\u0001"+
		"\u0004\u0005\u0004D\b\u0004\n\u0004\f\u0004G\t\u0004\u0001\u0005\u0001"+
		"\u0005\u0003\u0005K\b\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0004"+
		"\u0006P\b\u0006\u000b\u0006\f\u0006Q\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000b_\b\u000b\u0001\f\u0004\fb\b\f\u000b\f\f\fc\u0001\f\u0001"+
		"\f\u0000\u0000\r\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005"+
		"\u000b\u0006\r\u0000\u000f\u0000\u0011\u0000\u0013\u0000\u0015\u0000\u0017"+
		"\u0000\u0019\u0007\u0001\u0000\u0002\u0005\u0000\n\n\r\r\"\"\'\'\\\\\u0003"+
		"\u0000\t\n\r\r  i\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001"+
		"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001"+
		"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0001\u001b\u0001\u0000"+
		"\u0000\u0000\u0003!\u0001\u0000\u0000\u0000\u0005-\u0001\u0000\u0000\u0000"+
		"\u0007?\u0001\u0000\u0000\u0000\tA\u0001\u0000\u0000\u0000\u000bH\u0001"+
		"\u0000\u0000\u0000\rO\u0001\u0000\u0000\u0000\u000fS\u0001\u0000\u0000"+
		"\u0000\u0011U\u0001\u0000\u0000\u0000\u0013W\u0001\u0000\u0000\u0000\u0015"+
		"Y\u0001\u0000\u0000\u0000\u0017^\u0001\u0000\u0000\u0000\u0019a\u0001"+
		"\u0000\u0000\u0000\u001b\u001c\u0005,\u0000\u0000\u001c\u0002\u0001\u0000"+
		"\u0000\u0000\u001d\u001e\u0005A\u0000\u0000\u001e\"\u0005S\u0000\u0000"+
		"\u001f \u0005a\u0000\u0000 \"\u0005s\u0000\u0000!\u001d\u0001\u0000\u0000"+
		"\u0000!\u001f\u0001\u0000\u0000\u0000\"\u0004\u0001\u0000\u0000\u0000"+
		"#$\u0005T\u0000\u0000$%\u0005A\u0000\u0000%&\u0005B\u0000\u0000&\'\u0005"+
		"L\u0000\u0000\'.\u0005E\u0000\u0000()\u0005t\u0000\u0000)*\u0005a\u0000"+
		"\u0000*+\u0005b\u0000\u0000+,\u0005l\u0000\u0000,.\u0005e\u0000\u0000"+
		"-#\u0001\u0000\u0000\u0000-(\u0001\u0000\u0000\u0000.\u0006\u0001\u0000"+
		"\u0000\u0000/0\u0005B\u0000\u000001\u0005A\u0000\u000012\u0005R\u0000"+
		"\u000023\u0005C\u0000\u000034\u0005H\u0000\u000045\u0005A\u0000\u0000"+
		"56\u0005R\u0000\u00006@\u0005T\u0000\u000078\u0005b\u0000\u000089\u0005"+
		"a\u0000\u00009:\u0005r\u0000\u0000:;\u0005c\u0000\u0000;<\u0005h\u0000"+
		"\u0000<=\u0005a\u0000\u0000=>\u0005r\u0000\u0000>@\u0005t\u0000\u0000"+
		"?/\u0001\u0000\u0000\u0000?7\u0001\u0000\u0000\u0000@\b\u0001\u0000\u0000"+
		"\u0000AE\u0003\u0011\b\u0000BD\u0003\u0017\u000b\u0000CB\u0001\u0000\u0000"+
		"\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001\u0000"+
		"\u0000\u0000F\n\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000HJ\u0005"+
		"\"\u0000\u0000IK\u0003\r\u0006\u0000JI\u0001\u0000\u0000\u0000JK\u0001"+
		"\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0005\"\u0000\u0000M\f"+
		"\u0001\u0000\u0000\u0000NP\u0003\u000f\u0007\u0000ON\u0001\u0000\u0000"+
		"\u0000PQ\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000\u0000QR\u0001\u0000"+
		"\u0000\u0000R\u000e\u0001\u0000\u0000\u0000ST\b\u0000\u0000\u0000T\u0010"+
		"\u0001\u0000\u0000\u0000UV\u0002az\u0000V\u0012\u0001\u0000\u0000\u0000"+
		"WX\u0002AZ\u0000X\u0014\u0001\u0000\u0000\u0000YZ\u000209\u0000Z\u0016"+
		"\u0001\u0000\u0000\u0000[_\u0003\u0011\b\u0000\\_\u0003\u0013\t\u0000"+
		"]_\u0003\u0015\n\u0000^[\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000"+
		"\u0000^]\u0001\u0000\u0000\u0000_\u0018\u0001\u0000\u0000\u0000`b\u0007"+
		"\u0001\u0000\u0000a`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000"+
		"ca\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0001\u0000\u0000"+
		"\u0000ef\u0006\f\u0000\u0000f\u001a\u0001\u0000\u0000\u0000\t\u0000!-"+
		"?EJQ^c\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}