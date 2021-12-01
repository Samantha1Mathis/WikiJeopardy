package edu.arizona.cs;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class QueryEngineLemma {
    boolean indexExists=false;
    String inputFilePath ="";
    WhitespaceAnalyzer lemmaAnalyzer;
    Directory index;
    File[] directory;

    public QueryEngineLemma(File[] directoryListing) throws IOException{
        directory = directoryListing;
        buildIndex(directory);
        questionRead();
    }

    
    private void buildIndex(File[] directoryListing) throws IOException{
        
        //ClassLoader classLoader = getClass().getClassLoader();
        //File file = new File(classLoader.getResource(inputFilePath).getFile());

        lemmaAnalyzer = new WhitespaceAnalyzer();
        
        index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(lemmaAnalyzer);
        IndexWriter w;
        try {
            w = new IndexWriter(index, config);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        
        if (directoryListing != null) {
	          for (File files : directoryListing) {
	            try{
	                Scanner myReader = new Scanner(files);
	                String title = "";
	                String content = "";
	                
	                while (myReader.hasNextLine()) {
	                    String data = myReader.nextLine();
	                    if (data.startsWith("[[") && data.endsWith("]]") && !data.contains(":")){
	                        title = data.substring(2, data.length()-2);
	                        //System.out.println(title);
	                    }else{
	                        content = data;
                        }
	                    if (!title.equals("") && !content.equals("")){
                            //System.out.println(title + " : " + content);

                            addDoc(w, title, content);
                            w.commit();
	                        title = "";
	                        content = "";
	                    }   
	                }
	                myReader.close();
	            } catch(FileNotFoundException e) {
	                System.out.println("An error occurred.");
	                e.printStackTrace();

	            }
	          }
	        } else {
	        }
    }
    
    private static void addDoc(IndexWriter w, String id, String text) throws IOException {
        Document doc = new Document();
        // This holds the contents of the document
        doc.add(new TextField("Content", text, Field.Store.YES));
        // use a string field for document id because we don't want it tokenized
        doc.add(new StringField("Title", id, Field.Store.YES));
        w.addDocument(doc);
        
    }
    

    public void questionRead() throws IOException{
        File questions = new File("src/main/resources/questions.txt");
        Scanner questionReader = new Scanner(questions);
        String cat = "";
        String quest = "";
        String ans = "";
        int totalScore = 0;
        //System.out.println(questionReader.nextLine());
        while (questionReader.hasNextLine()){
            cat = questionReader.nextLine();
            quest = questionReader.nextLine();
            ans = questionReader.nextLine();
            //System.out.println(cat + quest + ans);
            questionReader.nextLine();
            String ansResult = searchPage(quest);
            System.out.println(ansResult + " : " + ans);
            if (ansResult.equals(ans)){
                totalScore++;
            }
        }
        questionReader.close();
        //System.out.println(totalScore);

    }
    public String searchPage(String query) throws IOException{
        if(!indexExists) {
            buildIndex(directory);
        }
        //Creates the answer list of from ResultClass objects
        //List<ResultClass>  ans=new ArrayList<ResultClass>();
        Query q;
        String qAns;
        //System.out.println("hello");
        qAns = Lemma.lemma(query);
        try {
            q = new QueryParser("Content", lemmaAnalyzer).parse(query);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        String titleResult="";
        if (hits.length > 0) {
			Document d = searcher.doc(hits[0].doc);
			titleResult = d.get("Title");
		} else {
        }
        
            return titleResult;
       
    }

    public void cosineScore(String[] q){
        float Scores[];
        float Length[];
        for (String element: q){
            
        }

    }
}
