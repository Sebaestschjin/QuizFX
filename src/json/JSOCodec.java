package json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class JSOCodec {
	public static JSOCodec std=new JSOCodec();
	public final JSOWithPosition encodeEncodable(JSOEncodable o){
		return o.encode(this);
	}
	public final JSOWithPosition encodeAsList(Collection<?> data){
		List<Object> ret=new ArrayList<>();
		for(Object o: data){
			ret.add(encode(o));
		}
		return new JSOWithPosition(ret);
	}
	public final JSOWithPosition encodeList(List<?> data){
		List<Object> ret=new ArrayList<>();
		for(Object o: data){
			ret.add(encode(o));
		}
		return new JSOWithPosition(ret);
	}
	@SuppressWarnings("unchecked")
	public <E> E reflectionDecode(JSOWithPosition jso, Class<E> expectedType) throws CannotReflectionDecodeException{
		if(jso==null || jso.get()==null)
			return null;
		try {
			Method m=expectedType.getMethod("decodeJSO", JSOWithPosition.class, JSOCodec.class);
			if(!m.getReturnType().equals(expectedType))
				throw new CannotReflectionDecodeException("decodeJSO method of "+expectedType+" has incorrect return type.");
			if(!Modifier.isStatic(m.getModifiers()))
				throw new CannotReflectionDecodeException("decodeJSO method of "+expectedType+" is not static.");
			return (E)m.invoke(null, jso, this);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new CannotReflectionDecodeException(expectedType+" has no decodeJSO method.");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CannotReflectionDecodeException(" calling the decodeJSO method of type "+expectedType+" failed.");
		} catch (InvocationTargetException e) {
			Throwable t=e.getCause();
			if(t instanceof RuntimeException)
				throw (RuntimeException) t;
			if(t instanceof Error)
				throw (Error) t;
			throw new CannotReflectionDecodeException("decodeJSO method of "+expectedType+" threw a checked Exception.");
		}
	}
	@SuppressWarnings("unchecked")
	public <E> E decode(JSOWithPosition jso, Class<E> expectedType){
		if(jso==null || jso.get()==null)
			return null;
		if(expectedType.equals(Boolean.class)){
			return (E) jso.getBoolean();
		}
		if(expectedType.equals(String.class)){
			return (E) jso.getString();
		}
		if(expectedType.equals(Integer.class)){
			return (E) jso.getInteger();
		}
		if(expectedType.equals(Double.class)){
			return (E) jso.getDouble();
		}
		try{
			return reflectionDecode(jso, expectedType);
		}catch(CannotReflectionDecodeException x){
			throw new UnexpectedDataTypeException("Decoder cannot handle class "+expectedType, jso.getPosition(), x);
		}
	}
	public final <E> List<E> decodeList(JSOWithPosition l, Class<E> etype){
		if(l==null || l.get()==null)
			return null;
		try{
			@SuppressWarnings("unchecked")
			List<JSOWithPosition> c= (List<JSOWithPosition>)l.get();
			List<E> ret = new ArrayList<>(c.size());
			for(JSOWithPosition e: c)
				ret.add(decode(e, etype));
			return ret;
		}catch(ClassCastException x){
			throw new UnexpectedDataTypeException("List expected", l.getPosition(), x);
		}
	}
	public final List<?> decodeNestedList(JSOWithPosition l, int levels, Class<?> etype){
		if(levels<1)
			throw new IllegalArgumentException();
		if(levels==1){
			return decodeList(l, etype);
		}
		if(l==null || l.get()==null)
			return null;
		try{
			@SuppressWarnings("unchecked")
			List<JSOWithPosition> c= (List<JSOWithPosition>)l.get();
			List<Object> ret = new ArrayList<>();
			for(JSOWithPosition e: c)
				ret.add(decodeNestedList(e, levels-1, etype));
			return ret;
		}catch(ClassCastException x){
			throw new UnexpectedDataTypeException("List expected", l.getPosition(), x);
		}
	}
	public JSOWithPosition encode(Object o) {
		if(o==null)
			return new JSOWithPosition(null);
		if(o instanceof String || o instanceof Integer || o instanceof Double || o instanceof Boolean)
			return new JSOWithPosition(o);
		if(o instanceof List<?>)
			return encodeList((List<?>)o);
		else if(o instanceof JSOEncodable)
			return encodeEncodable((JSOEncodable) o);
		
		throw new IllegalArgumentException("cannot JSO-encode type "+o.getClass());
	}
	
}
