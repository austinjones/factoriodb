package com.factoriodb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.factoriodb.luaparser.LuaBaseVisitor;
import com.factoriodb.luaparser.LuaLexer;
import com.factoriodb.luaparser.LuaParser;
import com.factoriodb.luaparser.LuaParser.ArrayContext;
import com.factoriodb.luaparser.LuaParser.LuaContext;
import com.factoriodb.luaparser.LuaParser.MapContext;
import com.factoriodb.luaparser.LuaParser.PairContext;
import com.factoriodb.luaparser.LuaParser.ValueContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;

public class DataParser {

	// modes: normal | expensive
	private static String MODE = "normal";

	// TODO: grab data from Git:
    // https://github.com/wube/factorio-data/tree/master/base/prototypes/recipe

	public static void main(String[] args) throws IOException {
	    String dataDir = args[0];
		File moduleFile = new File(dataDir + "/base/prototypes/item/module.lua");

		File recipeFolder = new File(dataDir + "/base/prototypes/recipe/");
		File fluidFolder = new File(dataDir + "/base/prototypes/fluid/");
		File itemFolder = new File(dataDir + "/base/prototypes/item/");


        String outputDir = args[1];
        new File(outputDir).mkdirs();
//		writeJsonFile(readLuaFile(testFile, "foo"), new File("json/test.json"));
		writeJsonFile(readLuaDirectory(recipeFolder, "recipe"), new File(outputDir + "recipes.json"));
		writeJsonFile(readLuaFile(moduleFile, "module"), new File(outputDir + "modules.json"));
		writeJsonFile(readLuaDirectory(itemFolder, "item"), new File(outputDir + "items.json"));
		writeJsonFile(readLuaDirectory(fluidFolder, "fluid"), new File(outputDir + "fluids.json"));
	}
	
	private static JsonElement convertData(JsonElement data, String typeFilter) {
		System.out.println(data);
		if(!data.isJsonArray()) {
			return data;
		}
		
		JsonArray array = new JsonArray();
		extractData(array, data, typeFilter);
		
		return array;
	}
	
	private static void extractData(JsonArray output, JsonElement element, String typeFilter) {
		if(element.isJsonArray()) {
			JsonArray elArray = (JsonArray)element;
			for(int i = 0; i < elArray.size(); i++) {
				extractData(output, elArray.get(i), typeFilter);
			}
		} else if(element.isJsonObject()) {
			JsonObject object = (JsonObject)element;
			JsonElement type = object.get("type");
			if (type != null && type.isJsonPrimitive()) {
				if (typeFilter.equals("name") && "ammo".equals(type.getAsString())) {
					object.remove("type");
					object.addProperty("type", "name");
					type = object.get("type");
				}
			}
			
			if (type != null && type.isJsonPrimitive()) {
				if (typeFilter.equals("name") && "capsule".equals(type.getAsString())) {
					object.remove("type");
					object.addProperty("type", "name");
					type = object.get("type");
				}
			}
			
			if (type != null && type.isJsonPrimitive()) {
				if (typeFilter.equals("name") && "module".equals(type.getAsString())) {
					object.remove("type");
					object.addProperty("type", "name");
					type = object.get("type");
				}
			}
			
			if (type != null && type.isJsonPrimitive() && typeFilter.equals(type.getAsString())) {
				if ("recipe".equals(type.getAsString())) {
					fixRecipe(object);
				}
				output.add(object);
			} else {
				for(Entry<String, JsonElement> entry : object.entrySet()) {
					extractData(output, entry.getValue(), typeFilter);
				}
			}
		} else {
			return;
		}
	}
	
	private static void fixRecipe(JsonObject element) {
		JsonObject modeObject = (JsonObject)element.get(MODE);
		if(modeObject != null) {
			for(Entry<String, JsonElement> entry : modeObject.entrySet()) {
				element.add(entry.getKey(), entry.getValue());
			}
			element.remove("normal");
			element.remove("expensive");
		}
		
		JsonArray ingredients = (JsonArray)element.get("ingredients");
		
		for(int j = 0; j < ingredients.size(); j++) {
			JsonElement effectElement = ingredients.get(j);
			if(!effectElement.isJsonArray()) {
				continue;
			}
			JsonArray effect = (JsonArray)effectElement;
			
			JsonPrimitive name = (JsonPrimitive)effect.get(0);
			JsonPrimitive amount = (JsonPrimitive)effect.get(1);
			
			JsonObject newValue = new JsonObject();
			newValue.add("name", name);
			newValue.add("amount", amount);
			
			ingredients.set(j, newValue);
		}
	}
	
	private static JsonElement readLuaFile(File input, String typeFilter) throws FileNotFoundException, IOException {
		System.out.println("Reading " + input.getAbsolutePath());
		JsonVisitor jsonVisitor = new JsonVisitor();
		
		LuaParser parser = parseFile(input);
		LuaContext root = parser.lua();
		System.out.println(root.toStringTree(parser));
		return convertData(jsonVisitor.visit(root), typeFilter);
	}
	
	private static JsonArray readLuaDirectory(File dir, String typeFilter) throws FileNotFoundException, IOException {
		File[] files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return ! name.startsWith("demo");
			}
		});
		
		JsonArray output = new JsonArray();
		if (files == null) {
		    return output;
        }

		for( File f : files ) {
			JsonElement element = readLuaFile(f, typeFilter);
			
			if(!element.isJsonArray()) {
				throw new IllegalArgumentException("Wrong type");
			}
			
			output.addAll((JsonArray)element);
		}
		
		return output;
	}
	
	private static void writeJsonFile(JsonElement element, File output) throws FileNotFoundException, IOException {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		System.out.println("Creating " + output.getPath());

		try (Writer writer = new FileWriter(output)) {
		    gson.toJson(element, writer);
		    writer.flush();
		}
	}
	
	private static LuaParser parseFile(File input) throws FileNotFoundException, IOException {
		try(FileInputStream in = new FileInputStream(input)) {
			ANTLRInputStream antlrIn = new ANTLRInputStream(in);
			LuaLexer lexer = new LuaLexer(antlrIn);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
			LuaParser parser = new LuaParser(tokens);
	        
//			LuaContext c = parser.lua();
//			System.out.println(c.toStringTree(parser));
			return parser;
		}
	}
	
	private static class JsonVisitor extends LuaBaseVisitor<JsonElement> {

		@Override
		protected JsonElement aggregateResult(JsonElement aggregate, JsonElement nextResult) {
			if (nextResult == null) {
				return aggregate;
			}
			
			if (aggregate == null) {
				return nextResult;
			}
			
			JsonArray array = new JsonArray();
			array.add(aggregate);
			array.add(nextResult);
			return array;
		}

		@Override
		public JsonElement visitArray(ArrayContext ctx) {
			JsonArray array = new JsonArray();
			for( ParseTree child : ctx.children ) {
				JsonElement el = this.visit(child);
				if (el != null) {
					array.add(el);
				}
			}
			
			return array;
		}

		@Override
		public JsonElement visitValue(ValueContext ctx) {
			if (ctx.STRING() != null) {
				String text = ctx.STRING().getText();
				String val = text.substring(1, text.length() - 1);
				return new JsonPrimitive(val);
			} else if (ctx.BOOL() != null) {
				boolean val = Boolean.parseBoolean(ctx.BOOL().getText());
				return new JsonPrimitive(val);
			} else if (ctx.NUMBER() != null) {
				float val = Float.parseFloat(ctx.NUMBER().getText());
				return new JsonPrimitive(val);
			} else {
				return this.visitChildren(ctx);
			}
		}

		private static class MapEntry {
			public String key;
			public JsonElement value;
		}
		
		public MapEntry visitMapPair(PairContext ctx) {
			MapEntry entry = new MapEntry();
			entry.key = ctx.KEY().getText();
			entry.value = this.visit(ctx.value());
			
			return entry;
		}

		@Override
		public JsonElement visitMap(MapContext ctx) {
			JsonObject object = new JsonObject();
			int i = 0;
			
			for(PairContext pairCtx = ctx.pair(i); pairCtx != null; pairCtx = ctx.pair(i)) {
				MapEntry entry = this.visitMapPair(pairCtx);
				object.add(entry.key, entry.value);
				
				i++;
			}
			
			return object;
		}

	}
}
