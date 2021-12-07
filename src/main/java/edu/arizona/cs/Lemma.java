package edu.arizona.cs;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Lemma {
    public static String lemma(String content) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		CoreDocument document = pipeline.processToCoreDocument(content);
		String lemmaContent = "";
		for (CoreLabel tok : document.tokens()) {
			lemmaContent += tok.lemma() + " ";
		}
		
		return lemmaContent;
	}
}
