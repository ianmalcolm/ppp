package writer;

import java.io.*;

public class Writer {
	public static void main(String[] args){
		BufferedWriter writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("filename.txt"), "utf-8"));
		    writer.write("Something");
		} catch (IOException ex){
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}
