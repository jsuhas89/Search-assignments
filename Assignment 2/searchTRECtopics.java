package Lucene;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class searchTRECtopics extends easySearch {
	public static void main(String[] args) throws ParseException, IOException {
		//File f = new File("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100");
		//easySearch e = new easySearch();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);
		int N = reader.maxDoc();
		String s = new String(Files.readAllBytes(Paths.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/topics.51-100")));
		Pattern pattern = Pattern.compile("<title>(.+?)<desc>", Pattern.DOTALL);
		Pattern pattern1 = Pattern.compile("<desc>(.+?)<smry>", Pattern.DOTALL);
		Pattern cp = Pattern.compile("<num>(.+?)<dom>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(s);
		Matcher matcher1 = pattern1.matcher(s);
		Matcher mp = cp.matcher(s);
		//String s1 = e.top1000(N, 051, "New York", reader, searcher);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 2/ShortQuery", true)));
		PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/Task 2/LongQuery", true)));
		int QueryID = 51;
		while(matcher.find()){
			//e.queryscore(N, matcher1.group(1).toString().replaceAll("Description:", "").trim(), reader, searcher);
			try {
			String queryString = matcher.group(1).toString().replaceAll("Topic:", "").trim();
			PriorityQueue<Double> pq1 = new PriorityQueue<Double>(10, Collections.reverseOrder());
			int[] docid = new int[N];
			int ii=0;
			HashMap h1 = new HashMap();
			StringBuilder sb = new StringBuilder();
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("TEXT", analyzer);
			Query query;
			query = parser.parse(queryString);
			Set<Term> queryTerms = new LinkedHashSet<Term>();
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);

			// Use DefaultSimilarity.decodeNormValue(…) to decode normalized
			// document length
			ClassicSimilarity dSimi = new ClassicSimilarity();
			// Get the segments of the index
			List<LeafReaderContext> leafContexts = reader.getContext().reader()
					.leaves();
			//HashMap h = new HashMap();
			double res=0;
			// Processing each segment
			for (int i = 0; i < leafContexts.size(); i++) {
				// Get document length
				LeafReaderContext leafContext = leafContexts.get(i);
				int startDocNo = leafContext.docBase;
				int numberOfDoc = leafContext.reader().maxDoc();
				
				for (int docId = 0; docId < numberOfDoc; docId++) {
					int ctd=0;
					double res1=0;
					// Get normalized length (1/sqrt(numOfTokens)) of the document
					float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
							.getNormValues("TEXT").get(docId));
					// Get length of the document
					float docLeng = 1 / (normDocLeng * normDocLeng);
					/*System.out.println("Length of doc(" + (docId + startDocNo)
							+ ", " + searcher.doc(docId + startDocNo).get("DOCNO")
							+ ") is " + docLeng);*/
					
					for (Term t : queryTerms) {
						//System.out.println(t.text());
						
						int df=reader.docFreq(new Term("TEXT", t.text()));
						//System.out.println("Number of documents containing the term " + t.text()  + " for field \"TEXT\": "+df);
						//System.out.println();
						
						PostingsEnum p = MultiFields.getTermDocsEnum(leafContext.reader(),
								"TEXT", new BytesRef(t.text()));
						
						int doc;
						if (p != null) {
							while ((doc = p.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
								/*System.out.println(t.text() + " occurs " + de.freq()
										+ " time(s) in doc(" + (de.docID() + startDocNo)
										+ ")");*/
								if(p.docID()+startDocNo == docId + startDocNo){
									ctd = p.freq();
									break;
								}
							}
						}
					//Implement the formula
					res = (ctd/docLeng)*Math.log(1+(N/df));
					res1+=res;
					//System.out.println("Relevance score for query term " + t.text() + " and doc(" + (docId + startDocNo) + ") is " + res);
				}
				//System.out.println("Relevance score for the entire query " + queryString + " and doc(" + (docId + startDocNo) + ") is " + res1);
				//System.out.println();
				pq1.offer(res1);
				if(!h1.containsKey(docId + startDocNo)){
					h1.put(searcher.doc(docId + startDocNo).get("DOCNO"), res1);
				}
				}
			}
			HashMap m = sortByValues(h1);
			Set set2 = m.entrySet();
		      Iterator iterator2 = set2.iterator();
		      while(iterator2.hasNext() && ii<1000) {
		           Map.Entry me2 = (Map.Entry)iterator2.next();
		           System.out.println(QueryID + "\t" + "Q0" + "\t" + me2.getKey() + "\t" + (ii+1) + "\t" + me2.getValue() + "\t" + "run-l-short");
		           sb.append(QueryID).append(" Q0 ").append(me2.getKey()).append(" ").append((ii+1)).append(" ").append(me2.getValue()).append(" run-l-short");
		           pw.println(sb.toString());
	               sb.setLength(0);
	               sb.trimToSize();
		           ii++;
		      }
		      QueryID++;
			}
		catch(Exception e){
			e.printStackTrace();
		}
			
		}
		int QueryID1 = 51;
		while(matcher1.find()){
			//e.queryscore(N, matcher1.group(1).toString().replaceAll("Description:", "").trim(), reader, searcher);
			try {
			String queryString = matcher1.group(1).toString().replaceAll("Description:", "").trim();
			PriorityQueue<Double> pq1 = new PriorityQueue<Double>(10, Collections.reverseOrder());
			int[] docid = new int[N];
			int ii=0;
			HashMap h2 = new HashMap();
			StringBuilder sb = new StringBuilder();
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("TEXT", analyzer);
			Query query;
			query = parser.parse(queryString);
			Set<Term> queryTerms = new LinkedHashSet<Term>();
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);

			// Use DefaultSimilarity.decodeNormValue(…) to decode normalized
			// document length
			ClassicSimilarity dSimi = new ClassicSimilarity();
			// Get the segments of the index
			List<LeafReaderContext> leafContexts = reader.getContext().reader()
					.leaves();
			//HashMap h = new HashMap();
			double res=0;
			// Processing each segment
			for (int i = 0; i < leafContexts.size(); i++) {
				// Get document length
				LeafReaderContext leafContext = leafContexts.get(i);
				int startDocNo = leafContext.docBase;
				int numberOfDoc = leafContext.reader().maxDoc();
				
				for (int docId = 0; docId < numberOfDoc; docId++) {
					int ctd=0;
					double res1=0;
					// Get normalized length (1/sqrt(numOfTokens)) of the document
					float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
							.getNormValues("TEXT").get(docId));
					// Get length of the document
					float docLeng = 1 / (normDocLeng * normDocLeng);
					/*System.out.println("Length of doc(" + (docId + startDocNo)
							+ ", " + searcher.doc(docId + startDocNo).get("DOCNO")
							+ ") is " + docLeng);*/
					
					for (Term t : queryTerms) {
						//System.out.println(t.text());
						
						int df=reader.docFreq(new Term("TEXT", t.text()));
						//System.out.println("Number of documents containing the term " + t.text()  + " for field \"TEXT\": "+df);
						//System.out.println();
						
						PostingsEnum p = MultiFields.getTermDocsEnum(leafContext.reader(),
								"TEXT", new BytesRef(t.text()));
						
						int doc;
						if (p != null) {
							while ((doc = p.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
								/*System.out.println(t.text() + " occurs " + de.freq()
										+ " time(s) in doc(" + (de.docID() + startDocNo)
										+ ")");*/
								if(p.docID()+startDocNo == docId + startDocNo){
									ctd = p.freq();
									break;
								}
							}
						}
					//Implement the formula
					res = (ctd/docLeng)*Math.log(1+(N/df));
					res1+=res;
					//System.out.println("Relevance score for query term " + t.text() + " and doc(" + (docId + startDocNo) + ") is " + res);
				}
				//System.out.println("Relevance score for the entire query " + queryString + " and doc(" + (docId + startDocNo) + ") is " + res1);
				//System.out.println();
				pq1.offer(res1);
				if(!h2.containsKey(docId + startDocNo)){
					h2.put(searcher.doc(docId + startDocNo).get("DOCNO"), res1);
				}
				}
			}
			HashMap m = sortByValues(h2);
			Set set2 = m.entrySet();
		      Iterator iterator2 = set2.iterator();
		      while(iterator2.hasNext() && ii<1000) {
		           Map.Entry me2 = (Map.Entry)iterator2.next();
		           System.out.println(QueryID1 + "\t" + "Q0" + "\t" + me2.getKey() + "\t" + (ii+1) + "\t" + me2.getValue() + "\t" + "run-l-long");
		           sb.append(QueryID1).append(" Q0 ").append(me2.getKey()).append(" ").append((ii+1)).append(" ").append(me2.getValue()).append(" run-l-long");
		           pw1.println(sb.toString());
	               sb.setLength(0);
	               sb.trimToSize();
		           ii++;
		      }
		      QueryID1++;
			}
		catch(Exception e){
			e.printStackTrace();
		}
		}
		pw.close();
		pw1.close();
	}
	private static HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               //return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  //.compareTo(((Map.Entry) (o2)).getValue());
	               return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }
}
