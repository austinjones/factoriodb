// Generated from src-antlr/Lua.g4 by ANTLR 4.7
package com.factoriodb.luaparser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LuaParser}.
 */
public interface LuaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LuaParser#lua}.
	 * @param ctx the parse tree
	 */
	void enterLua(LuaParser.LuaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#lua}.
	 * @param ctx the parse tree
	 */
	void exitLua(LuaParser.LuaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LuaParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(LuaParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(LuaParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link LuaParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(LuaParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(LuaParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link LuaParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(LuaParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(LuaParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LuaParser#map}.
	 * @param ctx the parse tree
	 */
	void enterMap(LuaParser.MapContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#map}.
	 * @param ctx the parse tree
	 */
	void exitMap(LuaParser.MapContext ctx);
	/**
	 * Enter a parse tree produced by {@link LuaParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(LuaParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(LuaParser.ArrayContext ctx);
}