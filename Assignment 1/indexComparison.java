package Lucene;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
//import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
//import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.Document;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.Reader;
import java.util.Iterator;

public class indexComparison {
	
		
	public static void main(String argv[]) {
		
		Comparison c1 = new Comparison();	
		Analyzer a = new KeywordAnalyzer();
		Analyzer b = new SimpleAnalyzer();
		Analyzer c = new StopAnalyzer();
		Analyzer d = new StandardAnalyzer();
		System.out.println();
		System.out.println("Statistics for Keyword Analyzer");
		c1.Compute(a);
		System.out.println();
		System.out.println("Statistics for Simple Analyzer");
		c1.Compute(b);
		System.out.println();
		System.out.println("Statistics for Stop Analyzer");
		c1.Compute(c);
		System.out.println();
		System.out.println("Statistics for Standard Analyzer");
		c1.Compute(d);
		}
}
	class Comparison{
	public void Compute(Analyzer x){	
    try {

	File fXmlFile = new File("D:/Suhas/IUB/3rd Sem/Search/Assignments/out_actual.txt");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);

	doc.getDocumentElement().normalize();

	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	NodeList nList = doc.getElementsByTagName("DOC");

	System.out.println("----------------------------");
	
	String[] tags = {"DOCNO","HEAD","BYLINE","DATELINE","TEXT"};
	String str= "";
	
	//String s="D:/Suhas/IUB/3rd Sem/Search/Assignments/Index";
	Directory dir = FSDirectory.open(Paths.get("D:/Suhas/IUB/3rd Sem/Search/Assignments/Index"));
	//Analyzer analyzer = new KeywordAnalyzer();
	IndexWriterConfig iwc = new IndexWriterConfig(x); //analyzer
	iwc.setOpenMode(OpenMode.CREATE);
	IndexWriter writer = new IndexWriter(dir, iwc);

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		//System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
			//System.out.println(eElement.getElementsByTagName("HEAD").getLength());
			for(int i=0;i<tags.length;i++){
				//System.out.println(tags[i]+": ");
				for(int j=0;j<eElement.getElementsByTagName(tags[i]).getLength();j++){
					str+=eElement.getElementsByTagName(tags[i]).item(j).getTextContent() + " ";
				}
				//System.out.println(str + "\n");
				if(tags[i]=="DOCNO"){
					luceneDoc.add(new StringField(tags[i], str, Field.Store.YES));
				}
				else{
				luceneDoc.add(new TextField(tags[i], str, Field.Store.YES));
				}
				str="";
			}
			writer.addDocument(luceneDoc);
		}
	}
	writer.forceMerge(1);
	writer.close();
	
	IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(("D:/Suhas/IUB/3rd Sem/Search/Assignments/Index"))));
	//Print the total number of documents in the corpus
	//System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
	//System.out.println("Number of documents containing the term \"in\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "the")));
	Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
	System.out.println("Number of tokens for this field:"+vocabulary.getSumTotalTermFreq());
	if(vocabulary.getSumTotalTermFreq() == reader.maxDoc()){
		System.out.println("Since the number of tokens equals the total number of documents in the corpus, we can say that Tokenization is not applied for this analyzer");
	}
	System.out.println("Size of the vocabulary for this field: "+vocabulary.size());
	if(reader.totalTermFreq(new Term("TEXT","the")) == 0 && vocabulary.getSumTotalTermFreq() != reader.maxDoc()){
		System.out.println("Number of occurrences of \"the\" in the field\"TEXT\": "+reader.totalTermFreq(new Term("TEXT","the")));
		System.out.println("Since occurances of the keyword \"the\" is 0, Stop words are removed from this analyzer");
	}
	reader.close();
    } catch (Exception e) {
	e.printStackTrace();
    }
	}
}