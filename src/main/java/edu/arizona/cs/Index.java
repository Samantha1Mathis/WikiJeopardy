package edu.arizona.cs;

import java.io.File;

public class Index {


    public static void main(String[] args ) {
        try {
            File dir = new File("src/main/resources/mini-wiki/");            
            File[] directoryListing = dir.listFiles();
            
	        //System.out.println(directoryListing);
            //String fileName = "input.txt";
            //System.out.println("********Welcome to  Homework 3!");
            //String[] query13a = {"information", "retrieval"};
            QueryEngine objQueryEngine = new QueryEngine(directoryListing);
            
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
