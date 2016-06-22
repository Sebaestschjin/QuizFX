package json;

import java.util.List;
import java.util.Map;

import util.CharSink;

public class JSONRenderer {
	public static void renderLinebreak(CharSink cs, String indentationPrefix){
		cs.append('\n').append(indentationPrefix);
	}
	public static void render(Object o, CharSink cs, String indentStr, String indentationPrefix){
		if(o==null){
			cs.append(" null ");
		}else if(o instanceof JSOWithPosition){
			render(((JSOWithPosition) o).get(), cs, indentStr, indentationPrefix);
		}else if(o instanceof Boolean){
			cs.append(' ').append(o).append(' ');
		}else if(o instanceof Integer){
			cs.append(' ').append(o).append(' ');			
		}else if(o instanceof Double){
			cs.append(' ').append(o).append(' ');			
		}else if(o instanceof String){
			String s = (String)o;
			cs.append('"');
			for(int i=0; i<s.length(); ++i){
				char c = s.charAt(i);
				switch(c){
				case '\\': cs.append("\\\\"); break;
				case '\n': cs.append("\\n");break;
				case '\b': cs.append("\\b");break;
				case '\f': cs.append("\\f");break;
				case '\t': cs.append("\\t");break;
				case '\r': cs.append("\\r");break;
				case '\"': cs.append("\\\"");break;
				default:
					//TODO: other control characters
					cs.append(c);
					break;
				}
			}
			cs.append('"');
		}else if(o instanceof List<?>){
			List<?> l=(List<?>)o;
			if(l.isEmpty()){
				cs.append("[ ]");
				return;
			}
			String newIndent=indentationPrefix+indentStr;
			cs.append('[');

			boolean first=true;
			for(Object e: l){
				if(first)
					first=false;
				else
					cs.append(',');
				renderLinebreak(cs, newIndent);
				render(e, cs, indentStr, newIndent);
			}
			renderLinebreak(cs, indentationPrefix);
			cs.append(']');
		}else if(o instanceof Map<?, ?>){
			Map<?, ?> m=(Map<?, ?>)o;
			if(m.isEmpty()){
				cs.append("{ }");
				return;
			}
			String newIndent=indentationPrefix+indentStr;
			cs.append('{');

			boolean first=true;
			for(Map.Entry<?, ?> e: m.entrySet()){
				if(first)
					first=false;
				else
					cs.append(',');
				renderLinebreak(cs, newIndent);
				if(!(e.getKey() instanceof String)){
					throw new IllegalArgumentException("Keys in Maps must be Strings for JSON rendering.");
				}
				
				render(e.getKey(), cs, indentStr, newIndent);
				cs.append(": ");
				render(e.getValue(), cs, indentStr, newIndent);
			}
			renderLinebreak(cs, indentationPrefix);
			cs.append('}');
		}else{
			throw new IllegalArgumentException("Cannot render type "+o.getClass()+" to JSON.");
		}


	}
	public static void render(Object o, CharSink cs, String indentStr) {
		render(o, cs, indentStr, "");
	}
}
