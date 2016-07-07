package json.parser;

public class ParserException extends RuntimeException{

	private static final long serialVersionUID = -1642269615301673767L;
	private PosBuffer pos;
	public final String message;

	public ParserException(String message, PosBuffer pos){
		super(message + " in "+pos);
		this.message=message;
		this.pos=pos.clone();
	}

	public PosBuffer getPosition() {
		return pos.clone();
	}


}
