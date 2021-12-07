package edu.arizona.cs;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class QueryEngine {
    boolean indexExists=false;
    String inputFilePath ="";
    //WhitespaceAnalyzer analyzer;
    EnglishAnalyzer analyzer;
    //StandardAnalyzer analyzer;
    Directory index;
    File[] directory;
    Mean mrr;

    public QueryEngine(File[] directoryListing) throws IOException{
        this.mrr = new Mean();
        directory = directoryListing;
        buildIndex(directory);
        questionRead();
    }

    /**
     * Goes through the files, replaces the punctuations.
     * Add the Title and the Content of each wikipedia page
     * to a Document
     *  
     * @param directoryListing, which directory the files are found
     * @throws IOException
     */
    private void buildIndex(File[] directoryListing) throws IOException{
        //This analyzer produced the best results
        //analyzer = new WhitespaceAnalyzer();
        analyzer = new EnglishAnalyzer();
        //analyzer = new StandardAnalyzer();

        index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w;
        try {
            w = new IndexWriter(index, config);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        
        if (directoryListing != null) {
              //Loops through all the files
	          for (File files : directoryListing) {
                  //Needed to lemmatized the files
                  //String filename = files.getName();
                  //String [] filename = files.split("/");
                  //FileWriter writer = new FileWriter("src/main/resources/lemma-wiki/lemma-"+filename);
                  System.out.println(files);
	            try{
	                Scanner scanFiles = new Scanner(files);
	                String title = "";
	                String content = "";
	                //Loops through each line in the file 
	                while (scanFiles.hasNextLine()) {
                        String data = scanFiles.nextLine();
                        //Finds the titles if it begins and ends with [[ ]]
	                    if (data.startsWith("[[") && data.endsWith("]]") && !data.contains(":")){
                            //If it found all the content for that one page and title
                            if (!title.equals("") && !content.equals("")){
                                //Writing the lemmatized version to a file
                                //String LemmaContent = Lemma.lemma(content);
                                //writer.write("[[" + title + "]]\n");
                                //writer.write(LemmaContent+"\n");
                                //writer.write("\n");
                                addDoc(w, title, content);
                                w.commit();
                                title = "";
                                content = "";
                            }   
                            //Gets rid of the brackets
                            title = data.substring(2, data.length()-2);
	                    }else{
                            //Collects all the info
                            content += data.replace("!", " ").replace(".", " ").replace("&", " ").replace(",", " ").replace(":", " ").replace("?", " ").replace("-", " ").replace("'", " ").replace("\"", " ").replace(";", " ").replace(")", " ").replace("(", " ").replace("=", " ").replace("|", " ").replace("$", " ").replace("/", " ");
                        }
	                }
                    scanFiles.close();
	            } catch(FileNotFoundException e) {
	                System.out.println("An error occurred.");
	                e.printStackTrace();
	            }
	          }
	        } else {
	        }
    }
    
    /**
     * Uses the Lucene package to add a Content and Title field to a Document object 
     * @param w, the writer index
     * @param id, the title of the wiki page
     * @param text, the content of the wiki page
     * @throws IOException
     */
    private static void addDoc(IndexWriter w, String id, String text) throws IOException {
        Document doc = new Document();
        // This holds the contents of the document
        doc.add(new TextField("Content", text, Field.Store.YES));
        // use a string field for document id because we don't want it tokenized
        doc.add(new StringField("Title", id, Field.Store.YES));
        w.addDocument(doc);
        
    }
    
    /**
     * This method reads through the questions and splits up each question
     * Category
     * Question
     * Answer
     * Blank line
     * Then passes on the quest to the searchPage method 
     * Then calls the Mrr ranking to get the calculations 
     * and finds the total score of how many correct answers were found
     * @throws IOException
     */
    public void questionRead() throws IOException{
        File questions = new File("src/main/resources/questions.txt");
        Scanner questionReader = new Scanner(questions);
        String cat = "";
        String quest = "";
        String ans = "";
        int totalScore = 0;        
        //Loops through question file to get each section of questions
        while (questionReader.hasNextLine()){
            cat = questionReader.nextLine();
            quest = questionReader.nextLine();
            ans = questionReader.nextLine();
            String[] ansTotal = ans.split("\\|");
            quest = quest.replace("!", " ").replace(".", " ").replace("&", " ").replace(",", " ").replace(":", " ").replace("?", " ").replace("-", " ").replace("'", " ").replace("\"", " ").replace(";", " ").replace(")", " ").replace("(", " ").replace("=", " ").replace("|", " ").replace("$", " ").replace("/", " ");
            //Adds the category to the question
            String combine = cat + " " + quest;
            questionReader.nextLine();
            String ansResult = searchPage(combine, ans);
            //Prints out the answer received vs the real answer
            System.out.println(ansResult + " : " + ans);
            // loops through the answer if multiple are allowed
            for (String eachAns : ansTotal){
                if (ansResult.equals(eachAns)){
                    //Updates totalscore counter
                    totalScore++;
                }
            }
        }
        System.out.println("total: " + totalScore);
        //Calculates the mean reciprocal ranking 
        double meanReciprocalRank = this.mrr.CalculateMRR();
        System.out.println("mrr: " + meanReciprocalRank);
        questionReader.close();

    }

    /**
     * This method does the search query going through the question and using the Lucene
     * Query Parser 
     * @param query, the question we are trying to find the answer to
     * @param ans, the real answer to compare
     * @return
     * @throws IOException
     */
    public String searchPage(String query, String ans) throws IOException{
        Query q;
        try {
            q = new QueryParser("Content", analyzer).parse(query);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        //This is to set to tf-idf 
        //searcher.setSimilarity(new ClassicSimilarity());
        //Collects top 100 hits
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        String titleResult="";
        //Uses the mrr to find the ranking 
        this.mrr.findRanking(hits, ans, searcher);
        if (hits.length > 0) {
			Document d = searcher.doc(hits[0].doc);
			titleResult = d.get("Title");
		} else {
        }
        return titleResult;
    }
}
