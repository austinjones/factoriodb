// Generated from src-antlr/Lua.g4 by ANTLR 4.7
package com.factoriodb.luaparser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LuaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LuaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LuaParser#lua}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLua(LuaParser.LuaContext ctx);
	/**
	 * Visit a parse tree produced by {@link LuaParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(LuaParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link LuaParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(LuaParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by {@link LuaParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(LuaParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LuaParser#map}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap(LuaParser.MapContext ctx);
	/**
	 * Visit a parse tree produced by {@link LuaParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(LuaParser.ArrayContext ctx);
}