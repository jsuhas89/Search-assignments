package Lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class file_merge {
	public static void main(String argv[]) {
	File folder = new File("D:/Suhas/IUB/3rd Sem/Search/Assignments/corpus/corpus");
	File[] listOfFiles = folder.listFiles();
	FileWriter fstream = null;
	BufferedWriter out = null;
	try {
		fstream = new FileWriter("D:/Suhas/IUB/3rd Sem/Search/Assignments/out.txt", false);
		out = new BufferedWriter(fstream);
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		out.write("<Test>");
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	for (File file : listOfFiles) {
	    if (file.isFile() && file.getName().endsWith(".trectext")) {
	        System.out.println(file.getName());
	        FileInputStream fis;
	        try {
				fis = new FileInputStream(file);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
				in.close();
	    }
	        catch (IOException e) {
				e.printStackTrace();
			}
	}
	}
	try {
		out.write("</Test>");
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	try {
		out.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}