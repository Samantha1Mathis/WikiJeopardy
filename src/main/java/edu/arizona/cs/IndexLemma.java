package edu.arizona.cs;

import java.io.File;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

public class IndexLemma {
    public void Index(){

    }

    public static void main(String[] args ) {
        try {
            File dir = new File("src/main/resources/mini-wiki/");            
            File[] directoryListing = dir.listFiles();
            
	        //System.out.println(directoryListing);
            //String fileName = "input.txt";
            //System.out.println("********Welcome to  Homework 3!");
            //String[] query13a = {"information", "retrieval"};
            QueryEngineLemma objQueryEngine = new QueryEngineLemma(directoryListing);
            
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
