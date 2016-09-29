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



public class generateIndex {

  public static void main(String argv[]) {

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
	Analyzer analyzer = new KeywordAnalyzer();
	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
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
	System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
	reader.close();
    } catch (Exception e) {
	e.printStackTrace();
    }
  }

}