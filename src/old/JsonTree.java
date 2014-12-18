package old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IMMUTABLE KEEP THINGS IMMUTABLE
public class JsonTree {
	
	private final Map<String, Object> elems;
	
	public boolean contains(String key) {
		return (elems.containsKey(key));
	}
	
	public Object get(String key) {
		return elems.get(key);
	}
	
	public String typeof(String key) {
		Object e = elems.get(key);
		if(e instanceof JsonTree) return "JsonTree";
		else if(e instanceof Long) return "int";
		else if(e instanceof String) return "String";
		else if(e instanceof Boolean) return "boolean";
		else if(e instanceof List) return "List";
		else return "null";
	}
	
	
	private JsonTree(HashMap<String, Object> h) {
		elems = h;
	}
	
	public static JsonTree parse(String in) {
		Parser p = new Parser(in);
		return parse(p);
	}
	public static JsonTree parse(Parser p) {
		JsonTree tree = new JsonTree(new HashMap<String, Object>());
		
		while(!p.atEnd() && p.charPeek() != '}') {
			p.charNext(); // eat '{' or ','
			
			String key = parseJsonKey(p);
			
			Object value = parseJsonObj(p);
			
			tree.elems.put(key, value);
			p.read(Content.WHITESPACE);
		}
		if(!p.atEnd()) p.charNext(); // to eat '}' char
		return tree;
	}
	private static String parseJsonKey(Parser p) {
		
		p.read(Content.WHITESPACE); // eat whitespace
		p.charNext(); // for first " in key
		String key = p.read(Content.UNTIL_ENDQUOTE); // key!
		p.read(Content.UNTIL_COLON);
		p.charNext(); // eat ':', finish up key
		
		return key;
	}
	private static Object parseJsonObj(Parser p) {
		Object value = null;
		p.read(Content.WHITESPACE);
		char t = p.charPeek();
		if(t == '{') {					// Object
			value = JsonTree.parse(p);
		} else if(t == '[') {			// Array
			List<Object> li = new ArrayList<Object>();
			while(!p.atEnd() && p.charPeek() != ']') {
				p.charNext();				// either a '[' or a ','
				p.read(Content.WHITESPACE);
				li.add(parseJsonObj(p));
				p.read(Content.WHITESPACE);
			}
			p.charNext(); // eat ']'
			value = li;
		} else {					// Easy type: int, bool, string, null
			if(p.charPeek() == '"') { // string
				p.charNext();
				value = p.read(Content.UNTIL_ENDQUOTE);
				p.charNext();
			} else {
				String w = p.read(Content.WORD);
				if(w.equals("true")) value = true;
				else if(w.equals("false")) value = false;
				else {
					try {
						Long i = Long.valueOf(w);
						value = i;
					} catch(Exception ex) {
						
						value = null;
					}
				}
			}
		}
		return value;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append("{\n");
		for(String key : elems.keySet()) {
			output.append(key + ": ");
			if(typeof(key).equals("String")) {
				output.append("\""+elems.get(key) + "\" (type String)\n");
			} else {
				output.append(elems.get(key) + " (type " + typeof(key) + ")\n");
			}
		}
		output.append("}");
		return output.toString();
	}
}

class Parser {
	protected int pos;
	protected String input;
	
	public Parser(String in) {
		input = in;
		pos = 0;
	}

	// -----------------------------------------------------------
	// the important read() function
	protected String read(Content toRead) {
		String toReturn = "";
		switch (toRead) {
			case WHITESPACE:
				while(!atEnd() && Character.isWhitespace(charPeek())) {
					toReturn+=charNext();
				}
				break;
			case WORD:
				while(!atEnd() && String.valueOf(charPeek()).matches("[a-zA-Z0-9]")) {
					toReturn+=charNext();
				}
				break;
			case UNTIL_COLON:
				while(!atEnd() && charPeek() != ':') {
					toReturn+=charNext();
				}
				break;
			case UNTIL_ENDQUOTE:
				while(!atEnd() && charPeek() != '"') {
					if(charPeek() == '\\') {
						toReturn+=charNext();
						if(charPeek() == '"') {
							toReturn+=charNext();
						}
					} else {
						toReturn+=charNext();
					}
				}
				break;
		}
		return toReturn;
	}

	// -----------------------------------------------------------
	// util functions
	
	protected char charPeek() {
		return input.charAt(pos);
	}
	protected char charNext() {
//		System.out.println(pos+" :: "+input.charAt(pos));
		pos++;
		return input.charAt(pos-1);
	}
	
	protected boolean startsWith(String s) {
		if(input.substring(pos, pos+s.length()).equals(s)) {
			return true;
		} else {
			return false;
		}
	}
	protected boolean atEnd() {
		if(pos >= input.length()) {
			return true;
		} else {
			return false;
		}
	}
}

enum Content { WHITESPACE, WORD, UNTIL_COLON, UNTIL_ENDQUOTE }
