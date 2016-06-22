package util;


import java.io.PrintWriter;

public class PrintWriterCharSink implements CharSink{
	public static PrintWriterCharSink out=new PrintWriterCharSink(new PrintWriter(System.out, true));
	public static PrintWriterCharSink err=new PrintWriterCharSink(new PrintWriter(System.err, true));
	PrintWriter pw;
	public PrintWriterCharSink(PrintWriter sink) {
		pw=sink;
	}
	@Override
	public PrintWriterCharSink append(char c) {
		pw.write(c);
		return this;
	}

	@Override
	public PrintWriterCharSink append(String s) {
		pw.write(s);
		return this;
	}
	public PrintWriter getWriter(){
		return pw;
	}
	public void flush() {
		pw.flush();
	}
	
}
