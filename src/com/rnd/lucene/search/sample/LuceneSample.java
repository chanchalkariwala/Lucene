package com.rnd.lucene.search.sample;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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

public class LuceneSample 
{
	public static void main(String args[]) throws IOException, ParseException
	{
		//Directory to store index
		//Creating in-memory index for simplicity, as opposed to index in file system
		Directory lIndexDirectory = new RAMDirectory();	
		
		//Create IndexWriter to build index
		IndexWriterConfig lIndexWriterConfig = new IndexWriterConfig();
		IndexWriter lIndexWriter = new IndexWriter(lIndexDirectory, lIndexWriterConfig);
		
		//Add documents to index
		addDocuments(lIndexWriter);
		
		//Commit is required to 'commit' all changes to the index
		//Close releases 'lock' on indexs
		lIndexWriter.commit();
		lIndexWriter.close();
		
		IndexReader lReader = DirectoryReader.open(lIndexDirectory);
		IndexSearcher lSearcher = new IndexSearcher(lReader);
		
		//Simple query
		String lstrQuery = args.length == 0 ? "lucene" : args[0];
		
		//'title is used as default field, if not specified in query
		Query lQuery = new QueryParser("title", new StandardAnalyzer()).parse(lstrQuery);
		
		//Search for results
		TopDocs lHits = lSearcher.search(lQuery, 10);
		ScoreDoc[] lDocs = lHits.scoreDocs;
		
		//Print results
		System.out.println("Hits found for query '"+lQuery+"' : "+lHits.totalHits+"\n");
		for(int i=0; i< lDocs.length; i++)
		{
			Document lDocument = lReader.document(lDocs[i].doc);
			System.out.println("\t"+lDocument.get("title")+" by "+lDocument.get("author"));
		}
		
	}
	
	private static void addDocuments(IndexWriter pIndexWriter) throws IOException
	{
		
		addDocument(pIndexWriter, "Lucene in Action", "Erick Hatcher");
		addDocument(pIndexWriter, "Lucene 4 Cookbook", "Edwood Ng");
		
	}
	
	private static void addDocument(IndexWriter pIndexWriter, String pTitle, String pAuthor) throws IOException
	{
		Document lDocument = new Document();
		
		//Storing fields as 'Text' fields for simplicity
		lDocument.add(new TextField("title", pTitle, Field.Store.YES));
		lDocument.add(new TextField("author", pAuthor, Field.Store.YES));
		
		pIndexWriter.addDocument(lDocument);
	}
}
