package edu.arizona.cs;

import java.io.File;

public class Index {
    public static void main(String[] args ) {
        try {
            File dir = new File("src/main/resources/wiki-subset-20140602");            
            File[] directoryListing = dir.listFiles();
            QueryEngine objQueryEngine = new QueryEngine(directoryListing);
            
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
