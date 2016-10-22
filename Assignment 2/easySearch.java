package Lucene;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Scanner;
import java.util.Set;

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

public class easySearch {
	public static void main(String[] args) throws ParseException, IOException {
		easySearch a = new easySearch();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get("E:/Suhas(D drive)/Suhas/IUB/3rd Sem/Search/Assignments/Assignment 2/index/index")));
		IndexSearcher searcher = new IndexSearcher(reader);

		/**
		 * Get query terms from the query string
		 */
		Scanner s = new Scanner(System.in);
		System.out.println("Enter the query:");
		String queryString = s.nextLine();
		s.close();
		int N = reader.maxDoc();
		System.out.println("Total number of documents in the corpus:"+N);
		//String queryString = "New York";
		a.termscore(N, queryString, reader, searcher);
		a.queryscore( N, queryString, reader, searcher);
	}
	
		public void termscore(int N, String queryString, IndexReader reader, IndexSearcher searcher) { 
		// Get the preprocessed query terms
		try {
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		Query query = parser.parse(queryString);
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		System.out.println("Terms in the query: ");
		for (Term t : queryTerms) {
			System.out.println(t.text());
		
		/**
		 * Get document frequency
		 */
		int df=reader.docFreq(new Term("TEXT", t.text()));
		//System.out.println("Number of documents containing the term " + t.text()  + " for field \"TEXT\": "+df);
		//System.out.println();

		/**
		 * Get document length and term frequency
		 */
		// Use DefaultSimilarity.decodeNormValue(…) to decode normalized
		// document length
		ClassicSimilarity dSimi = new ClassicSimilarity();
		// Get the segments of the index
		List<LeafReaderContext> leafContexts = reader.getContext().reader()
				.leaves();
		HashMap h = new HashMap();
		double res=0;
		// Processing each segment
		for (int i = 0; i < leafContexts.size(); i++) {
			// Get document length
			LeafReaderContext leafContext = leafContexts.get(i);
			int startDocNo = leafContext.docBase;
			int numberOfDoc = leafContext.reader().maxDoc();
			//c(t,doc)
			PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
					"TEXT", new BytesRef(t.text()));
			int doc;
			if (de != null) {
				while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
					/*System.out.println(t.text() + " occurs " + de.freq()
							+ " time(s) in doc(" + (de.docID() + startDocNo)
							+ ")");*/
					if(!h.containsKey(de.docID()+startDocNo)){
						h.put(de.docID()+startDocNo, de.freq());
					}
				}
			}
			
			for (int docId = 0; docId < numberOfDoc; docId++) {
				int ctd=0;
				// Get normalized length (1/sqrt(numOfTokens)) of the document
				float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
						.getNormValues("TEXT").get(docId));
				// Get length of the document
				float docLeng = 1 / (normDocLeng * normDocLeng);
				/*System.out.println("Length of doc(" + (docId + startDocNo)
						+ ", " + searcher.doc(docId + startDocNo).get("DOCNO")
						+ ") is " + docLeng);*/
				if(h.containsKey(docId + startDocNo)){
					ctd = (int) h.get(docId + startDocNo);
				}
				//Implement the formula
				res = (ctd/docLeng)*Math.log(1+(N/df));
				System.out.println("Relevance score for query term " + t.text() + " and doc(" + searcher.doc(docId + startDocNo).get("DOCNO") + ") is " + res);
				
			}
			//System.out.println();
		}
		//System.out.println();
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		
		public void queryscore(int N, String queryString, IndexReader reader, IndexSearcher searcher) { 
			// Get the preprocessed query terms
			try {
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("TEXT", analyzer);
			Query query;
			query = parser.parse(queryString);
			Set<Term> queryTerms = new LinkedHashSet<Term>();
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
			/**
			 * Get document frequency
			 */
			//int df=reader.docFreq(new Term("TEXT", t.text()));
			//System.out.println("Number of documents containing the term " + t.text()  + " for field \"TEXT\": "+df);
			//System.out.println();

			/**
			 * Get document length and term frequency
			 */
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
				//c(t,doc)
				/*PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
						"TEXT", new BytesRef(t.text()));
				int doc;
				if (de != null) {
					while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						/*System.out.println(t.text() + " occurs " + de.freq()
								+ " time(s) in doc(" + (de.docID() + startDocNo)
								+ ")");
						if(!h.containsKey(de.docID()+startDocNo)){
							h.put(de.docID()+startDocNo, de.freq());
						}
					}
				}*/
				
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
				System.out.println("Relevance score for the entire query " + queryString + " and doc(" + (searcher.doc(docId + startDocNo).get("DOCNO")) + ") is " + res1);
				//System.out.println();
				}
			}
			//System.out.println();
			}
		catch(Exception e){
			e.printStackTrace();
		}
}
}
