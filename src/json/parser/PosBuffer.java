package json.parser;

public class PosBuffer {
	String name;
	int pos;
	int line;
	int col;
	public PosBuffer(String name2, int pos2, int line2, int col2) {
		name=name2;
		pos=pos2;
		line=line2;
		col=col2;
	}
	public void set(PosBuffer b) {
		name=b.name;
		pos=b.pos;
		line=b.line;
		col=b.col;
	}
	public PosBuffer(String name2) {
		name=name2;
	}
	public int get(){
		return pos;
	}
	public int getLine(){
		return line;
	}
	public int getCol(){
		return col;
	}
	public String getName(){
		return name;
	}
	public void advance(){
		pos++;
		col++;
	}
	public void newLine(){
		line++;
		col=0;
	}
	public PosBuffer clone(){
		return new PosBuffer(name, pos, line, col);
	}
	@Override
	public String toString() {
		return "'"+name+"' at line "+(line+1)+", column "+(col+1);		
	}
	public PosBuffer advance(int length) {
		pos += length;
		col += length;
		return this;
	}
	public void advance(boolean newLine) {
		advance();
		if(newLine)
			newLine();
	}
	public void advance(char c) {
		advance();
		if(c=='\n')
			newLine();
	}
}
