// Generated from src-antlr/Lua.g4 by ANTLR 4.7
package com.factoriodb.luaparser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LuaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, NUMBER=8, STRING=9, 
		NUMERIC=10, COMMENT=11, WHITESPACE=12, BOOL=13, KEY=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "NUMBER", "ESCAPED_QUOTE", 
		"STRING", "UPPERCASE", "LOWERCASE", "NUMERIC", "COMMENT", "WHITESPACE", 
		"BOOL", "KEY"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'nil'", "'='", "'('", "')'", "'{'", "','", "'}'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "NUMBER", "STRING", "NUMERIC", 
		"COMMENT", "WHITESPACE", "BOOL", "KEY"
	};
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


	public LuaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Lua.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20\u0085\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b"+
		"\3\t\5\t\67\n\t\3\t\6\t:\n\t\r\t\16\t;\3\t\3\t\6\t@\n\t\r\t\16\tA\3\t"+
		"\5\tE\n\t\3\t\6\tH\n\t\r\t\16\tI\5\tL\n\t\3\n\3\n\3\n\3\13\3\13\3\13\7"+
		"\13T\n\13\f\13\16\13W\13\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\17\3\17\7\17e\n\17\f\17\16\17h\13\17\3\17\3\17\3\20\6\20m\n\20\r"+
		"\20\16\20n\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21"+
		"|\n\21\3\22\3\22\3\22\3\22\6\22\u0082\n\22\r\22\16\22\u0083\3U\2\23\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\2\25\13\27\2\31\2\33\f\35\r\37\16"+
		"!\17#\20\3\2\b\4\2\f\f\17\17\3\2C\\\3\2c|\3\2\62;\5\2\13\f\17\17\"\"\4"+
		"\2<<aa\2\u0090\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\25\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\3%\3\2\2\2\5)\3\2\2\2"+
		"\7+\3\2\2\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3\2\2\2\21K\3\2"+
		"\2\2\23M\3\2\2\2\25P\3\2\2\2\27Z\3\2\2\2\31\\\3\2\2\2\33^\3\2\2\2\35`"+
		"\3\2\2\2\37l\3\2\2\2!{\3\2\2\2#\u0081\3\2\2\2%&\7p\2\2&\'\7k\2\2\'(\7"+
		"n\2\2(\4\3\2\2\2)*\7?\2\2*\6\3\2\2\2+,\7*\2\2,\b\3\2\2\2-.\7+\2\2.\n\3"+
		"\2\2\2/\60\7}\2\2\60\f\3\2\2\2\61\62\7.\2\2\62\16\3\2\2\2\63\64\7\177"+
		"\2\2\64\20\3\2\2\2\65\67\7/\2\2\66\65\3\2\2\2\66\67\3\2\2\2\679\3\2\2"+
		"\28:\5\33\16\298\3\2\2\2:;\3\2\2\2;9\3\2\2\2;<\3\2\2\2<=\3\2\2\2=?\7\60"+
		"\2\2>@\5\33\16\2?>\3\2\2\2@A\3\2\2\2A?\3\2\2\2AB\3\2\2\2BL\3\2\2\2CE\7"+
		"/\2\2DC\3\2\2\2DE\3\2\2\2EG\3\2\2\2FH\5\33\16\2GF\3\2\2\2HI\3\2\2\2IG"+
		"\3\2\2\2IJ\3\2\2\2JL\3\2\2\2K\66\3\2\2\2KD\3\2\2\2L\22\3\2\2\2MN\7^\2"+
		"\2NO\7$\2\2O\24\3\2\2\2PU\7$\2\2QT\5\23\n\2RT\n\2\2\2SQ\3\2\2\2SR\3\2"+
		"\2\2TW\3\2\2\2UV\3\2\2\2US\3\2\2\2VX\3\2\2\2WU\3\2\2\2XY\7$\2\2Y\26\3"+
		"\2\2\2Z[\t\3\2\2[\30\3\2\2\2\\]\t\4\2\2]\32\3\2\2\2^_\t\5\2\2_\34\3\2"+
		"\2\2`a\7/\2\2ab\7/\2\2bf\3\2\2\2ce\n\2\2\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2"+
		"\2fg\3\2\2\2gi\3\2\2\2hf\3\2\2\2ij\b\17\2\2j\36\3\2\2\2km\t\6\2\2lk\3"+
		"\2\2\2mn\3\2\2\2nl\3\2\2\2no\3\2\2\2op\3\2\2\2pq\b\20\2\2q \3\2\2\2rs"+
		"\7v\2\2st\7t\2\2tu\7w\2\2u|\7g\2\2vw\7h\2\2wx\7c\2\2xy\7n\2\2yz\7u\2\2"+
		"z|\7g\2\2{r\3\2\2\2{v\3\2\2\2|\"\3\2\2\2}\u0082\5\27\f\2~\u0082\5\31\r"+
		"\2\177\u0082\5\33\16\2\u0080\u0082\t\7\2\2\u0081}\3\2\2\2\u0081~\3\2\2"+
		"\2\u0081\177\3\2\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081"+
		"\3\2\2\2\u0083\u0084\3\2\2\2\u0084$\3\2\2\2\20\2\66;ADIKSUfn{\u0081\u0083"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}