package edu.arizona.cs;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class Mean {
    int[] totalRanks;
    int num;

    /**
     * Initializes the totalrank array to keep track of the top 100 answers
     */
    public Mean(){
        this.totalRanks = new int[100];
        this.num = 0;
    }

    /**
     * This method finds the ranking, by taking the top 100 answers to the question
     * and figuring out how far down the correct answer was. Keeps track using an 
     * array
     *  
     * @param hits, array of top 100 documents
     * @param ans, the real answer
     * @param searcher, the indexSearcher
     * @throws IOException
     */
    public void findRanking(ScoreDoc[] hits, String ans, IndexSearcher searcher) throws IOException{
        //Loops through the 100 answers
        for (int i=0; i <hits.length;i++){
            String[] ansTotal = ans.split("\\|");
            //Loops through the multiple answers allowed
            for (String ansResult: ansTotal){
                Document d = searcher.doc(hits[i].doc);
                String titleResult = d.get("Title");
                if (titleResult.equals(ansResult)){
                    //Adds to the array where it fell in the 100 list
                    this.totalRanks[this.num] = i+1;
                    this.num++;
                    //System.out.println(i+1);
                    return;
                }
            }
            
        }
        //if the correct answer wasn't in the top 100
        this.totalRanks[this.num] = 0;
        this.num++;   
    }

    /**
     * This method does the mean reciprocal rank calculation
     * 
     * @return total, which is the MRR 
     */
    public double CalculateMRR(){
        double sums = 0.0;
        for (int num : totalRanks){
            if (num != 0){
                sums += 1.0/num;
            }
        }
        double total = sums / 100;
        return total;
    }
}
