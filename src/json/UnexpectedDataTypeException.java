package json;

import json.parser.PosBuffer;

public class UnexpectedDataTypeException extends RuntimeException {
	private static final long serialVersionUID = 4233201338552028648L;

	PosBuffer pos;
	public UnexpectedDataTypeException(String message, PosBuffer pos, Throwable cause){
		super(message, cause);
		this.pos=pos.clone();
	}
	
	public PosBuffer getPosition(){
		return pos;
	}
	public String toString(){
		return getClass().getName()+(pos==null?"":": At "+pos)+": "+getMessage();
	}
}
