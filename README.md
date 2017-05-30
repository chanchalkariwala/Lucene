# Lucene
Learning Apache Lucene

I am studying the API of Apache Lucene for a project at my workplace.
I haven't been able to commit to this project completely. Will dedicate time, after finishing the project in office.
<hr/>

## About Apache Lucene
**I have worked directly with the Java API of Apache Lucene, and not Solr or ElastiSearch. But I guess the basic principles are the same.**
<br/>
**TLDR;** Read the performance chapter from the book 'Lucene in Action'. 
<br/><br/>
### About Index Writers and Reader
- You can use one instance of IndexWriter throughout your web application. 
- In most cases, the default configuration of IndexWriterConfig can be used. Before tweaking IndexWriterConfig, make sure you handle all Index Writers and Readers correcty throughout the webapp.<br/>
  Index size and no. of files created can bloat unnecessarily if you have not handled Writers and Readers properly. <br/>
  When I faced this situation, my first instinct was to modify IndexWriterConfig such as - 
  
  - Max Buffered Documents
  - Max RAM Usage
  - Merge Segments 
  - Use Compound File
  - We even tried closing and opening the IndexWriter after a fixed number of commits.<br/><br/>
  
  None of the above worked. I finally had an **Eureka** moment when we called Garbage Collection after a fixed number of commits. The actual problem was that too many IndexReaders were left open. Once I rectified this, the Index stats were better than any stats achieved by above.
- The Java API for IndexWriter has a method which checks if the Writer has any uncommitted changes. I found out the hard way that the method isn't perfect. There are scenarios listed where the method would return true even if there were no changes.
  
- When working with a near-real-time Indexing need, new Readers will need to be opened for every query, since the data keeps changing. It is more cost-effective to create IndexReaders from a IndexWriter. <br/>
   - When an Index Reader is opened from FSDirectory, it will be able to read only that data which was **persisted** by the Writer before this instance of Reader was created. <br/>
   - When an Index Reader is opened from IndexWriter, the reader will have all data changes made by the Writer **before** opening the Reader. Changes which were not committed by the Writer yet, will also be available to the Reader. <br/>
  - The Reader will be operational even if the Writer is closed at a later stage. 

- The API has a construct which opens a new Index Reader, from a live Index Reader and Writer, if the Writer has changes that Reader does not have. It is the ultimate soln! However on reading the javadocs, the API isn't perfect.. There are scenarios listed where a new reader would be opened even if there are no changes.
- An IndexSearcher is created from an IndexReader. After reading the above point, one may assume that something similar could be done for IndexReader. **DO NOT!** IndexSearcher will throw an 'AlreadyClosedException' for the IndexReader instance. If you think about it, it's only natural that this would fail, since the Reader instance is the Searcher's only source of reading the Index.

  
  **All points mentioned above hold true for Taxonomy Writers and Readers too.**
  
 <br/><br/>
### About Analysis and Indexing Data
How the data should be analysed and indexed depends largely on the application. For eg. Search by Text - Whether the application supports a *Contains* Match, *Exact* Match or a *Starts With* Match.<br/>
The data structure used to store data is Document and Field. A collection of Fields form a Document. This a similiar to a normal Text File on your computer system. The File is a Document. It has fields such as - Title, Author, Creation Date, Contents.<br/>
  ### Storing Data
  - 
  
