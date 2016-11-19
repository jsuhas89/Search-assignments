package Lucene;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Object;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.PajekNetReader;

public class AuthorRank {
           public static void main(String args[]) throws IOException, ParseException{
        	   		  int ii=0,mm=0;	
        	   		  String Line;
                      Path currentRelativePath = Paths.get("");
                      String s = currentRelativePath.toAbsolutePath().toString();
                      String fp=new File(s+"/author.net").getAbsolutePath();
                      
        PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        Graph graph=new UndirectedSparseGraph<Integer,Integer>();
        pnr.load(fp, graph);
        
        SettableTransformer<Integer, String> vertex_label= pnr.getVertexLabeller();
        
        PageRank<Integer,String> rank=new PageRank<Integer,String>(graph, 0.85);
        rank.setMaxIterations(30);
        rank.evaluate();
        Collection<Integer> c=new ArrayList<Integer>(graph.getVertices());
        HashMap<Integer, Double> h = new HashMap<Integer, Double>();
        for(Integer i:c){
        	if(!h.containsKey(i)){
        		//System.out.println(i + "  " + rank.getVertexScore(i));
        		h.put(i, rank.getVertexScore(i));
        	}
         }
        HashMap m = sortByValues(h);
		Set set2 = m.entrySet();
	      Iterator iterator2 = set2.iterator();
	      while(iterator2.hasNext() && ii<10) {
	           Map.Entry me2 = (Map.Entry)iterator2.next();
	           mm = (int) me2.getKey();
	           System.out.println((mm+1) + " -- " + "\"" + vertex_label.transform(mm) + "\"" + " -- " + me2.getValue());
	           ii++;
	      }
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