package util;

public class StringBuilderCharSink implements CharSink{
	StringBuilder sb;
	public StringBuilderCharSink() {
		sb=new StringBuilder();
	}
	public StringBuilderCharSink(StringBuilder sink) {
		sb=sink;
	}
	@Override
	public CharSink append(char c) {
		sb.append(c);
		return this;
	}
	@Override
	public CharSink append(String s) {
		sb.append(s);
		return this;
	}
	public String toString(){
		return sb.toString();
	}
	public StringBuilder getStringBuilder(){
		return sb;
	}
}
