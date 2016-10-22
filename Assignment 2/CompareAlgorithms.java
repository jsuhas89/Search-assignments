package Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CompareAlgorithms {
    public static void main (String args[])  throws IOException, ParseException {
        CompareAlgorithms c = new CompareAlgorithms();
    	Scanner sc = new Scanner(System.in);
        System.out.println("Enter your choice of Similarity (VSM, BM, LMD, LMJ)");
        String similarity = sc.nextLine().toUpperCase();
        c.check(similarity);
    }
		
    public void check(String s1){
    	switch (s1) {
        case "VSM":
            VSM();
            break;

        case "BM":
            BM();
            break;

        case "LMD":
            LMD();
            break;

        case "LMJ":
            LMJ();
            break;

        default:
            VSM();
            break;
    	}
    }
    
    public void VSM(){
    	try {
    		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);
		int N = reader.maxDoc();
		String s = new String(Files.readAllBytes(Paths.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100")));
		Pattern pattern = Pattern.compile("<title>(.+?)<desc>", Pattern.DOTALL);
		Pattern pattern1 = Pattern.compile("<desc>(.+?)<smry>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		Matcher matcher1 = pattern1.matcher(s);
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultShortQuery", true)));
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultLongQuery", true)));
		int qID=51;
		while(matcher.find()){
			String queryString = matcher.group(1).toString().replaceAll("Topic:", "").trim();
			String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
	        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        Analyzer analyzer = new StandardAnalyzer();
	        indexSearcher.setSimilarity(new ClassicSimilarity());
	        QueryParser queryParser = new QueryParser("TEXT", analyzer);

	        Query query = queryParser.parse(QueryParser.escape(queryString));

	        TopDocs topDocs = indexSearcher.search(query, 1000);
	        int noOfHits = topDocs.totalHits;
	        System.out.println("Total number of matching documents: "+ noOfHits);
	        int count = 1;
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	            StringBuilder stringBuilder = new StringBuilder();
	            for (ScoreDoc scoreDoc : scoreDocs) {
	                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
	                Document document = indexSearcher.doc(scoreDoc.doc);
	                stringBuilder.append(qID).append(" Q0 ").append(document.get("DOCNO")).
	                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-VSMShort");
	                pw1.println(stringBuilder.toString());
	                count+=1;
	                stringBuilder.setLength(0);
	                stringBuilder.trimToSize();
	            }
	        	qID++;
	        }
		    int qID1=51;
	        while(matcher1.find()){
				String queryString = matcher1.group(1).toString().replaceAll("Description:", "").trim();
				String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
		        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		        Analyzer analyzer = new StandardAnalyzer();
		        indexSearcher.setSimilarity(new ClassicSimilarity());
		        QueryParser queryParser = new QueryParser("TEXT", analyzer);

		        Query query = queryParser.parse(QueryParser.escape(queryString));

		        TopDocs topDocs = indexSearcher.search(query, 1000);
		        int noOfHits = topDocs.totalHits;
		        System.out.println("Total number of matching documents: "+ noOfHits);
		        int count = 1;
		        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		        //PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultShortQuery", true)));
		        //PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultLongQuery", true)));
		            StringBuilder stringBuilder = new StringBuilder();
		            for (ScoreDoc scoreDoc : scoreDocs) {
		                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
		                Document document = indexSearcher.doc(scoreDoc.doc);
		                stringBuilder.append(qID1).append(" Q0 ").append(document.get("DOCNO")).
		                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-VSMLong");
		                pw2.println(stringBuilder.toString());
		                count+=1;
		                stringBuilder.setLength(0);
		                stringBuilder.trimToSize();
		            }
		        	qID1++;

	            }
	        pw1.close();
	        pw2.close();
    }catch (Exception e) {
		e.printStackTrace();
	}
    }
    
    
    public void BM(){
    	try {
    		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);
		int N = reader.maxDoc();
		String s = new String(Files.readAllBytes(Paths.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100")));
		Pattern pattern = Pattern.compile("<title>(.+?)<desc>", Pattern.DOTALL);
		Pattern pattern1 = Pattern.compile("<desc>(.+?)<smry>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		Matcher matcher1 = pattern1.matcher(s);
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/BMShortQuery", true)));
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/BMLongQuery", true)));
		int qIDb=51;
		while(matcher.find()){
			String queryString = matcher.group(1).toString().replaceAll("Topic:", "").trim();
			String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
	        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        Analyzer analyzer = new StandardAnalyzer();
	        indexSearcher.setSimilarity(new BM25Similarity());
	        QueryParser queryParser = new QueryParser("TEXT", analyzer);

	        Query query = queryParser.parse(QueryParser.escape(queryString));

	        TopDocs topDocs = indexSearcher.search(query, 1000);
	        int noOfHits = topDocs.totalHits;
	        System.out.println("Total number of matching documents: "+ noOfHits);
	        int count = 1;
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	            StringBuilder stringBuilder = new StringBuilder();
	            for (ScoreDoc scoreDoc : scoreDocs) {
	                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
	                Document document = indexSearcher.doc(scoreDoc.doc);
	                stringBuilder.append(qIDb).append(" Q0 ").append(document.get("DOCNO")).
	                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-BMShort");
	                pw1.println(stringBuilder.toString());
	                count+=1;
	                stringBuilder.setLength(0);
	                stringBuilder.trimToSize();
	            }
	        	qIDb++;
	        }
		    int qIDb1=51;
	        while(matcher1.find()){
				String queryString = matcher1.group(1).toString().replaceAll("Description:", "").trim();
				String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
		        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		        Analyzer analyzer = new StandardAnalyzer();
		        indexSearcher.setSimilarity(new BM25Similarity());
		        QueryParser queryParser = new QueryParser("TEXT", analyzer);

		        Query query = queryParser.parse(QueryParser.escape(queryString));

		        TopDocs topDocs = indexSearcher.search(query, 1000);
		        int noOfHits = topDocs.totalHits;
		        System.out.println("Total number of matching documents: "+ noOfHits);
		        int count = 1;
		        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		        //PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultShortQuery", true)));
		        //PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultLongQuery", true)));
		            StringBuilder stringBuilder = new StringBuilder();
		            for (ScoreDoc scoreDoc : scoreDocs) {
		                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
		                Document document = indexSearcher.doc(scoreDoc.doc);
		                stringBuilder.append(qIDb1).append(" Q0 ").append(document.get("DOCNO")).
		                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-BMLong");
		                pw2.println(stringBuilder.toString());
		                count+=1;
		                stringBuilder.setLength(0);
		                stringBuilder.trimToSize();
		            }
		        	qIDb1++;

	            }
	        pw1.close();
	        pw2.close();
    }catch (Exception e) {
		e.printStackTrace();
	}
    }
    
    
    public void LMD(){
    	try {
    		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);
		int N = reader.maxDoc();
		String s = new String(Files.readAllBytes(Paths.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100")));
		Pattern pattern = Pattern.compile("<title>(.+?)<desc>", Pattern.DOTALL);
		Pattern pattern1 = Pattern.compile("<desc>(.+?)<smry>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		Matcher matcher1 = pattern1.matcher(s);
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/LMDShortQuery", true)));
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/LMDLongQuery", true)));
		int qIDl=51;
		while(matcher.find()){
			String queryString = matcher.group(1).toString().replaceAll("Topic:", "").trim();
			String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
	        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        Analyzer analyzer = new StandardAnalyzer();
	        indexSearcher.setSimilarity(new LMDirichletSimilarity());
	        QueryParser queryParser = new QueryParser("TEXT", analyzer);

	        Query query = queryParser.parse(QueryParser.escape(queryString));

	        TopDocs topDocs = indexSearcher.search(query, 1000);
	        int noOfHits = topDocs.totalHits;
	        System.out.println("Total number of matching documents: "+ noOfHits);
	        int count = 1;
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	            StringBuilder stringBuilder = new StringBuilder();
	            for (ScoreDoc scoreDoc : scoreDocs) {
	                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
	                Document document = indexSearcher.doc(scoreDoc.doc);
	                stringBuilder.append(qIDl).append(" Q0 ").append(document.get("DOCNO")).
	                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-LMDShort");
	                pw1.println(stringBuilder.toString());
	                count+=1;
	                stringBuilder.setLength(0);
	                stringBuilder.trimToSize();
	            }
	        	qIDl++;
	        }
		    int qIDl1=51;
	        while(matcher1.find()){
				String queryString = matcher1.group(1).toString().replaceAll("Description:", "").trim();
				String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
		        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		        Analyzer analyzer = new StandardAnalyzer();
		        indexSearcher.setSimilarity(new LMDirichletSimilarity());
		        QueryParser queryParser = new QueryParser("TEXT", analyzer);

		        Query query = queryParser.parse(QueryParser.escape(queryString));

		        TopDocs topDocs = indexSearcher.search(query, 1000);
		        int noOfHits = topDocs.totalHits;
		        System.out.println("Total number of matching documents: "+ noOfHits);
		        int count = 1;
		        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		        //PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultShortQuery", true)));
		        //PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultLongQuery", true)));
		            StringBuilder stringBuilder = new StringBuilder();
		            for (ScoreDoc scoreDoc : scoreDocs) {
		                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
		                Document document = indexSearcher.doc(scoreDoc.doc);
		                stringBuilder.append(qIDl1).append(" Q0 ").append(document.get("DOCNO")).
		                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-VSMSLong");
		                pw2.println(stringBuilder.toString());
		                count+=1;
		                stringBuilder.setLength(0);
		                stringBuilder.trimToSize();
		            }
		        	qIDl1++;

	            }
	        pw1.close();
	        pw2.close();
    }catch (Exception e) {
		e.printStackTrace();
	}
    }
    
    
    public void LMJ(){
    	try {
    		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);
		int N = reader.maxDoc();
		String s = new String(Files.readAllBytes(Paths.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100")));
		Pattern pattern = Pattern.compile("<title>(.+?)<desc>", Pattern.DOTALL);
		Pattern pattern1 = Pattern.compile("<desc>(.+?)<smry>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		Matcher matcher1 = pattern1.matcher(s);
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/LMJShortQuery", true)));
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/LMJLongQuery", true)));
		int qIDll=51;
		while(matcher.find()){
			String queryString = matcher.group(1).toString().replaceAll("Topic:", "").trim();
			String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
	        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        Analyzer analyzer = new StandardAnalyzer();
	        indexSearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
	        QueryParser queryParser = new QueryParser("TEXT", analyzer);

	        Query query = queryParser.parse(QueryParser.escape(queryString));

	        TopDocs topDocs = indexSearcher.search(query, 1000);
	        int noOfHits = topDocs.totalHits;
	        System.out.println("Total number of matching documents: "+ noOfHits);
	        int count = 1;
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	            StringBuilder stringBuilder = new StringBuilder();
	            for (ScoreDoc scoreDoc : scoreDocs) {
	                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
	                Document document = indexSearcher.doc(scoreDoc.doc);
	                stringBuilder.append(qIDll).append(" Q0 ").append(document.get("DOCNO")).
	                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-LMJShort");
	                pw1.println(stringBuilder.toString());
	                count+=1;
	                stringBuilder.setLength(0);
	                stringBuilder.trimToSize();
	            }
	        	qIDll++;
	        }
		    int qIDll1=51;
	        while(matcher1.find()){
				String queryString = matcher1.group(1).toString().replaceAll("Description:", "").trim();
				String index = "E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index";
		        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		        Analyzer analyzer = new StandardAnalyzer();
		        indexSearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
		        QueryParser queryParser = new QueryParser("TEXT", analyzer);

		        Query query = queryParser.parse(QueryParser.escape(queryString));

		        TopDocs topDocs = indexSearcher.search(query, 1000);
		        int noOfHits = topDocs.totalHits;
		        System.out.println("Total number of matching documents: "+ noOfHits);
		        int count = 1;
		        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		        //PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultShortQuery", true)));
		        //PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 3/DefaultLongQuery", true)));
		            StringBuilder stringBuilder = new StringBuilder();
		            for (ScoreDoc scoreDoc : scoreDocs) {
		                System.out.println("Document is = " + scoreDoc.doc + " score = " + scoreDoc.score);
		                Document document = indexSearcher.doc(scoreDoc.doc);
		                stringBuilder.append(qIDll1).append(" Q0 ").append(document.get("DOCNO")).
		                        append(" ").append(count).append(" ").append(scoreDoc.score).append(" run-1-LMJLong");
		                pw2.println(stringBuilder.toString());
		                count+=1;
		                stringBuilder.setLength(0);
		                stringBuilder.trimToSize();
		            }
		        	qIDll1++;

	            }
	        pw1.close();
	        pw2.close();
    }catch (Exception e) {
		e.printStackTrace();
	}
    }
}