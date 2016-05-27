package jena_test;

import java.io.IOException;
import java.io.OutputStream;

public class MyOutputStream extends OutputStream{
	StringBuffer buf;
	
	public MyOutputStream(){
		buf = new StringBuffer();
	}
	
	public void write(int character) throws IOException{
		buf.append((char) character);
		
	}
	public String getString(){
		return buf.toString();
	}
}