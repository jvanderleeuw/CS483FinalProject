package edu.arizona.cs;

// import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import java.util.*;
import java.io.FileWriter;
import java.io.BufferedWriter;

// import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
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
// import org.apache.lucene.search.DiversifiedTopDocsCollector;
import org.apache.lucene.store.Directory;
// import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.CharArraySet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/*
Jacob van der Leeuw
CS483 Assignment 3 Problem 1
*/

public class CS483FinalProject {
    boolean indexExists=false;
    String inputDirectoryPath ="./src/main/java/edu/arizona/cs";
    public static Directory index=null;
    public static WhitespaceAnalyzer analyzer=null;
    public static IndexWriter w = null;
    public static IndexWriterConfig config = null;
    public static BufferedWriter writer = null;
    // Scanner inputScanner = null;
    public static int count=0;
    public static double mrr=0;
    public static CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();

    public CS483FinalProject(){
        File f = new File(inputDirectoryPath + "/output.txt");
        if (f.exists()) {
            f.delete();
        }
        analyzer = new WhitespaceAnalyzer();
        // index = FSDirectory.open(new File(inputDirectoryPath));
        try {
            FileWriter fw = new FileWriter(inputDirectoryPath + "/output.txt", true);
            writer = new BufferedWriter(fw);
            index = FSDirectory.open((new File("./src/main/java/edu/arizona/cs/index.lucene")).toPath());
            // index = FSDirectory.open((new File("./src/main/java/edu/arizona/cs/index2.lucene")).toPath());
            // index = FSDirectory.open((new File("./src/main/java/edu/arizona/cs")).toPath());
            config = new IndexWriterConfig(analyzer);
            w = new IndexWriter(index,config);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        buildIndex();
    }

    /*
    Builds the index based off of the Lucene in 5 minutes Tutorial
    */
    private void buildIndex() {
        // File dir = new File(inputDirectoryPath + "/wikipediaArticles");
        // File[] directoryListing = dir.listFiles();
        // String docText = "";
        // String firstLine = "";
        // if (directoryListing != null) {
        //     for (File child : directoryListing) {
        //         String fileName = child.toString();
        //         int indx = fileName.lastIndexOf('.');
        //         if (fileName.substring(indx).equals(".txt")) {
        //             try (Scanner inputScanner = new Scanner(child)) {
        //                 while (inputScanner.hasNextLine()) {
        //                     String line = inputScanner.nextLine();
        //                     if (line.startsWith("[[") && line.endsWith("]]")) {
        //                         if (!docText.equals("")) {
        //                             processText(firstLine,docText);
        //                             docText = "";
        //                         }
        //                         line = line.substring(2,line.length()-2);
        //                         firstLine = line;
        //                     }
        //                     else if ((line.startsWith("==") && line.endsWith("==")) && line.length() >= 4) {
        //                         line = line.substring(2,line.length()-2);
        //                     }
        //                     docText += (line + " ");
        //                 }
        //                 processText(firstLine,docText);
        //                 inputScanner.close();
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //                 System.exit(1);
        //             }
        //         }
        //     }
        // }
        scoreQuestions();
        // System.out.println(count);
    }

    private void scoreQuestions() {
        int modLine = 0;
        String result = "";
        // LinkedList<String> result = new LinkedList<String>();
        int questionsRight = 0;
        try {
            Scanner questionScanner = new Scanner(new File(inputDirectoryPath + "/questions.txt"));
            while (questionScanner.hasNextLine()) {
                String line = questionScanner.nextLine();
                if (modLine%4 == 1) {
                    result = searchAndScore(line,1);
                    // result = searchAndScore(line,2);
                }
                else if (modLine%4 == 2) {
                    // boolean found = false;
                    // int count = 0;
                    // while (!found) {
                        // if (count >= result.size()) {
                        //     writer.write("\nnot found\n");
                        //     break;
                        // }
                        // else if (line.toLowerCase().equals(result.get(count).toLowerCase())) {
                        //     writer.write("\nfound at result# " + (count+1) + "\n");
                        //     mrr += (double)1/(count+1);
                        //     found = true;
                        // }
                        // else {
                        //     count++;
                        // }
                    // }
                    boolean matchfound = false;
                    String[] expectedResults = line.split("\\|");
                    for (String s: expectedResults) {
                        if (s.toLowerCase().equals(result.toLowerCase())) {
                            writer.write("Match: " + result + "\n");
                            questionsRight++;
                            matchfound = true;
                            break;
                        }
                    }
                    
                    if (matchfound == false) {
                        // System.out.println("not found")
                        writer.write("\nIncorrect; line was: " + line + " and result was: " + result + "\n");
                    }
                }
                modLine++;
            }
            writer.write("Number of questions right: " + questionsRight + "\n");
            // writer.write("mrr is: " + (mrr/100));
            if (writer != null) {
                writer.close();
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        System.out.println("Number of questions right: " + questionsRight);
    }

    private void processText(String firstLine,String docText) {
        count++;
        if (count%100==0) {
            System.out.println(count);
        }
        edu.stanford.nlp.simple.Document doc = new edu.stanford.nlp.simple.Document(docText);
        String newDoc = "";
        for (Sentence sent : doc.sentences()) {  // Will iterate over all sentences
            // We're only asking for words
            List<String> lemmas = sent.lemmas();
            for (int i = 0; i < lemmas.size(); i++) {
                newDoc += (lemmas.get(i) + " ");
            }
        }
        try {
            org.apache.lucene.document.Document newDocument = new org.apache.lucene.document.Document();
            newDocument.add(new TextField("DocText", newDoc, Field.Store.YES));
            newDocument.add(new StringField("docid", firstLine, Field.Store.YES));
            w.addDocument(newDocument);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String searchAndScore(String query, int option) {
    // public static LinkedList<String> searchAndScore(String query, int option) {
        float maxScore = 0;
        String newQuery = "";
        String modifiedQuery = "";
        String newModifiedQuery = "";
        String maxDoc = "";
        // LinkedList<String> docNames = new LinkedList<String>();
        // LinkedList<String> maxDocs = new LinkedList<String>();
        LinkedList<String> badTitles = new LinkedList<String>();
        try {
            edu.stanford.nlp.simple.Document querydoc = new edu.stanford.nlp.simple.Document(query);

            for (Sentence sent : querydoc.sentences()) {  // Will iterate over all sentences
                // We're only asking for words
                List<String> lemmas = sent.lemmas();
                
                for (int i = 0; i < lemmas.size(); i++) {
                    // if (!stopWords.contains(lemmas.get(i))) {
                        maxScore = 0;
                        
                        newQuery += (lemmas.get(i) + " ");
                        // maxDocs.add(maxDoc);
                    // }
                }
                modifiedQuery = QueryParser.escape(newQuery);
                newModifiedQuery = "";
                LinkedList<String> titleWords = new LinkedList<String>();
                badTitles = new LinkedList<String>();
                for (String m: modifiedQuery.split(" ")) {
                    if (Character.isUpperCase(m.charAt(0)) || Character.isDigit(m.charAt(0))) {
                        newModifiedQuery = (newModifiedQuery + "+" + m + " ");
                        titleWords.add(m);
                    }
                    else {
                        if (titleWords.size() > 1) {
                            String current = "";
                            for (String word: titleWords) {
                                current += word + " ";
                            }
                            current = current.substring(0,current.length()-1) + "";
                            badTitles.add(current);
                        }
                        titleWords = new LinkedList<String>();
                        newModifiedQuery = (newModifiedQuery + m + " ");
                    }
                }
                // for (String badTitle: badTitles) {
                //     newModifiedQuery += "+\"" + badTitle + "\" ";
                // }
                newModifiedQuery = newModifiedQuery.substring(0,newModifiedQuery.length()-1);
                Query q = new QueryParser("DocText", analyzer).parse(newModifiedQuery + " -duplicate:false");
                int hitsPerPage = 20;
                // int hitsPerPage = 20000;
                w.commit();
                IndexReader reader = DirectoryReader.open(index);
                IndexSearcher searcher = new IndexSearcher(reader);
                if (option==2) {
                    // ClassicSimilarity CS = new ClassicSimilarity();
                    BooleanSimilarity b = new BooleanSimilarity();
                    // searcher.setSimilarity(CS);
                    searcher.setSimilarity(b);
                }
                TopDocs docs = searcher.search(q, hitsPerPage);
                ScoreDoc[] hits = docs.scoreDocs;
                writer.write("\nQuery is: " + newModifiedQuery);
                // System.out.println("Query is: " + modifiedQuery);
                int cnt = 0;
                for (ScoreDoc sd: hits) {
                    Document doc = searcher.doc(sd.doc);
                    // if (cnt%2 == 0) {
                    //     docNames.add(doc.get("docid"));
                    // }
                    cnt++;
                    // if (!docNames.contains(doc.get("docid"))) {
                    // docNames.add(doc.get("docid"));
                    // }
                    writer.write("\nDoc is: " + doc.get("docid"));
                    writer.write("\nScore is: " + sd.score + "\n");
                    if (sd.score > maxScore) {
                        boolean good = true;
                        for (String title: badTitles) {
                            if (doc.get("docid").contains(title) || doc.get("docid").equals(title)) {
                                good = false;
                            }
                        }
                        if (good==true) {
                            maxScore = sd.score;
                            maxDoc = doc.get("docid");
                        }
                        
                    }
                }
                
            }
            // System.out.println("Query1: " + modifiedQuery);
            // System.out.println("Query2: " + newModifiedQuery);
            // System.out.println("bad titles: " + badTitles.toString());
            // System.out.println("maxDocs: " + maxDocs.toString());
            // System.out.println(newQuery);
            // modifiedQuery = QueryParser.escape(newQuery);
            
            // Query q = new QueryParser("DocText", analyzer).parse(modifiedQuery + " -duplicate:false");
            // int hitsPerPage = 20;
            // // int hitsPerPage = 20000;
            // w.commit();
            // IndexReader reader = DirectoryReader.open(index);
            // IndexSearcher searcher = new IndexSearcher(reader);
            // if (option==2) {
            //     ClassicSimilarity CS = new ClassicSimilarity();
			//     // BooleanSimilarity b = new BooleanSimilarity();
            //     searcher.setSimilarity(CS);
            //     // searcher.setSimilarity(b);
            // }
            // TopDocs docs = searcher.search(q, hitsPerPage);
            // ScoreDoc[] hits = docs.scoreDocs;
            // writer.write("\nQuery is: " + modifiedQuery);
            // // System.out.println("Query is: " + modifiedQuery);
            // int cnt = 0;
            // for (ScoreDoc sd: hits) {
            //     Document doc = searcher.doc(sd.doc);
            //     // if (cnt%2 == 0) {
            //     //     docNames.add(doc.get("docid"));
            //     // }
            //     cnt++;
            //     // if (!docNames.contains(doc.get("docid"))) {
            //     // docNames.add(doc.get("docid"));
            //     // }

            //     writer.write("\nDoc is: " + doc.get("docid"));
            //     // writer.write("Doc content is: \n" + doc.get("DocText") + "\n\n\n");
            //     writer.write("\nScore is: " + sd.score + "\n");


            //     // System.out.println("Doc is: " + doc.get("docid"));
            //     // System.out.println("Score is: " + sd.score);
            //     if (sd.score > maxScore) {
            //         maxScore = sd.score;
            //         maxDoc = doc.get("docid");
            //     }
            // }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        // System.out.println(maxDocs.get(maxDocs.size()-1));
        // System.out.println();
        // System.out.println();
        // System.out.println();
        // return maxDocs.get(maxDocs.size()-1);
        return maxDoc;
        // return docNames;
    }

    
    public static void main(String[] args ) {
        
    }
}