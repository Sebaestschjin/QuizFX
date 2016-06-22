package util;

public interface CharSink {
	public CharSink append(char c);
	public CharSink append(String s);
	public default CharSink append(Object o){
		return append(String.valueOf(o));
	}
}
