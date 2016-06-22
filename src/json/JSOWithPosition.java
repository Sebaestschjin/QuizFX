package json;

import java.util.List;
import java.util.Map;

import json.parser.PosBuffer;
import util.StringBuilderCharSink;

public class JSOWithPosition {
	PosBuffer pos;
	Object data;
	public JSOWithPosition(Object data, PosBuffer position){
		this.data=data;
		pos=position;
	}
	public JSOWithPosition(Object data){
		this.data=data;
	}
	public PosBuffer getPosition(){
		return pos;
	}
	public Object get(){
		return data;
	}
	public String getString(){
		if(data==null) return null;
		return String.valueOf(data);
	}
	private void throwNull(){
		throw new UnexpectedDataTypeException("Value must not be null", pos, null);
	}
	private void throwCast(String expected, ClassCastException cause){
		throw new UnexpectedDataTypeException("Value expected to be of JSON type "+expected, pos, cause);
	}
	private void throwNumberFormat(NumberFormatException cause){
		throw new UnexpectedDataTypeException("Illegal number format", pos, cause);
	}
	public String getStringStrict(){
		try{
			return (String)data;
		}catch(ClassCastException x){
			throwCast("String", x);
			return null;
		}
	}
	public String getStringNonNull(){
		if(data==null) throwNull();
		return String.valueOf(data);
	}
	public String getStringStrictNonNull(){
		if(data==null) throwNull();
		try{
			return (String)data;
		}catch(ClassCastException x){
			throwCast("String", x);
			return null;
		}
	}
	public Boolean getBoolean(){
		try{
			return (Boolean)data;
		}catch(ClassCastException x){
			throwCast("Boolean", x);
			return null;
		}		
	}
	public boolean getBooleanNonNull(){
		if(data==null) throwNull();
		try{
			return (Boolean)data;
		}catch(ClassCastException x){
			throwCast("Boolean", x);
			return false;
		}		
	}

	public Integer getInteger(){
		if(data instanceof String){
			try{
				return Integer.parseInt((String)data);
			}catch(NumberFormatException x){
				throwNumberFormat(x);
			}
		}
		try{
			return (Integer)data;
		}catch(ClassCastException x){
			throwCast("Integer", x);
			return null;
		}
	}
	public int getIntegerNonNull(){
		if(data==null) throwNull();
		return getInteger();
	}	
	public Double getDouble(){
		if(data instanceof String){
			try{
				return Double.parseDouble((String)data);
			}catch(NumberFormatException x){
				throwNumberFormat(x);
			}
		}
		if(data instanceof Integer){
			return ((Integer)data).doubleValue();
		}
		try{
			return (Double)data;
		}catch(ClassCastException x){
			throwCast("Double", x);
			return null;
		}
	}
	public double getDoubleNonNull(){
		if(data==null) throwNull();
		return getDouble();
	}
	@SuppressWarnings("unchecked")
	public List<JSOWithPosition> getList(){
		try{
			return (List<JSOWithPosition>)data;
		}catch(ClassCastException x){
			throwCast("List", x);
			return null;
		}
	}
	public List<JSOWithPosition> getListNonNull(){
		if(data==null) throwNull();
		return getList();
	}
	@SuppressWarnings("unchecked")
	public Map<String, JSOWithPosition> getObject(){
		try{
			return (Map<String, JSOWithPosition>)data;
		}catch(ClassCastException x){
			throwCast("Object", x);
			return null;
		}
	}
	public Map<String, JSOWithPosition> getObjectNonNull(){
		if(data==null) throwNull();
		return getObject();
	}
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		JSONRenderer.render(this, new StringBuilderCharSink(sb), "\t");
		return sb.toString();
	}
}
