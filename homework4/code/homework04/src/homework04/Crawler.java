package homework04;
import org.jsoup.*;
import java.util.*;
import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.net.URL;
import java.util.Scanner;

import org.xml.sax.InputSource;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

public class Crawler {
	public static int depth;
	public static String url;
	public static ParseURL u = new ParseURL();
	
	public Crawler() {
		// TODO Auto-generated constructor stub
		depth = 0;
		url = null;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		Crawler w = new Crawler();//新建对象

		String filePath = System.getProperty("user.dir")+"/index";//创建索引的存储目录
		System.out.print("Input root URL: ");
		w.url = "http://" + s.nextLine();//创建root url
		System.out.print("Input searching depth: ");
		w.depth = s.nextInt();//输入深度
		u.processPage(w.url, w.depth, w.depth);//递归遍历所有链接
		w.createIndex(filePath);//创建索引
		w.search(filePath);//搜索
	}

	public void createIndex(String filePath) throws Exception {
		File f=new File(filePath);
		IndexWriter iwr=null;
		try {
			Directory dir=FSDirectory.open(f);
			Analyzer analyzer = new IKAnalyzer();
			
			IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_4_10_0,analyzer);
			iwr=new IndexWriter(dir,conf);//建立IndexWriter。固定套路
			//添加doc
			iwr.deleteAll();
			for(int i = 0; i < u.uList.size(); i++) {
				Document doc=getWebInfo(i);
				iwr.addDocument(doc);//添加doc，Lucene的检索是以document为基本单位	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			iwr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Document getWebInfo(int k) throws Exception {
		
		//doc中内容由field构成，在检索过程中，Lucene会按照指定的Field依次搜索每个document的该项field是否符合要求。
		try {
			final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(u.uList.get(k)));//在这里catch异常，设置超时任务
	        final TextDocument web_doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
	        //前面几行代码就是一些固定方式，修改不同的url，获取不同的值。使用时用doc.getTitle(),doc.getContent()即可。
		    //web_doc.getTitle()和web_doc.getContent()也可直接访问
	        String title = web_doc.getTitle();
	        String content = ArticleExtractor.INSTANCE.getText(web_doc);

	        if(title == null) title = "NULL";
	        if(content == null) content = "NULL";
	        Document doc=new Document();
	        Field f1=new TextField("url",u.uList.get(k),Field.Store.YES);
	        Field f2=new TextField("title",title,Field.Store.YES);
	        Field f3=new TextField("content",content,Field.Store.YES);
	      
			doc.add(f1);
			doc.add(f2);
			doc.add(f3);
		    return doc;
		}catch(Exception e) {
			//e.printStackTrace();
			return new Document();
		}
	    
	}
	
	public void search(String filePath){//对于所有的doc，检索条件有两个，指定的field和指定的field中的内容，检索的的结果是符合要求的doc
		File f=new File(filePath);
		try {
			IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(FSDirectory.open(f)));
			Scanner in = new Scanner(System.in);
			System.out.print("Input query string: ");
			String queryStr= in.nextLine();//用户输入查询条件，filed中的内容，可以使用正则表达式
			Analyzer analyzer = new IKAnalyzer();//分词器
			System.out.print("Input query field: ");
			String queryFie= in.nextLine();//用户输入查询field
			//ָ指定field为“name”，Lucene会按照关键词搜索每个doc中的name。即搜索域
			QueryParser parser = new QueryParser(Version.LUCENE_4_10_0, queryFie, analyzer);
			
			Query query=parser.parse(queryStr);
			TopDocs hits=searcher.search(query,u.uList.size());//前面几行代码也是固定套路，使用时直接改field和关键词即可
			for(ScoreDoc doc:hits.scoreDocs){
				Document d=searcher.doc(doc.doc);
				System.out.println(d.get("title"));
				System.out.println(d.get("url"));
				System.out.println(d.get("content"));
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
