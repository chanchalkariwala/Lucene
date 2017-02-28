# Lucene
Learning Apache Lucene

I am studying the API of Apache Lucene for a project at my workplace.
I haven't been able to commit to this project completely. Will dedicate time, after finishing the project in office.
<hr/>

## Interesting finds about Apache Lucene
**I have worked directly with the Java API of Apache Lucene, and not Solr or ElastiSearch. But I guess the basic principles are the same.**
<br/>
**TLDR;** Read the performance chapter from the book 'Lucene in Action'. 
<br/><br/>
### About Index Writers and Reader
- You can use one instance of IndexWriter throughout your web application. If the application is near-real-time i.e. data can queried for while insert/update is happening, make sure to commit regularly. 
- In most cases, the default configuration of IndexWriterConfig can be used. Before tweaking IndexWriterConfig, make sure you handle all Index Writers and Readers correcty throughout the webapp.<br/>
  Index size and no. of files created can bloat unnecessarily if you have not handled Writers and Readers properly. <br/>
  When I faced this situation, my first instinct was to modify IndexWriterConfig such as - 
  
  - Max Buffered Documents
  - Max RAM Usage
  - Merge Segments 
  - Use Compound File
  - We even tried closing and opening the IndexWriter after a fixed number of commits.<br/>
  
  None of the above worked. I finally had an **Eureka** moment when we called Garbage Collection after a fixed number of commits. The actual problem was that many IndexReaders were left open. Once I rectified this, the Index stats were better than any stats achieved by above.
- When working with a near-real-time Indexing need, it is more cost-effective to create IndexReaders from a IndexWriter. <br/>
  A major disadvantage of opening Reader from FSDirectory is that it will be able to read only that data which was persisted by the Writer **before** this instance of Reader was created. <br/>
  When you create a Reader from Writer - 
  
  - All data addedd/updated/deleted by the Writer is available to the Reader directly without needing to **reopen** the reader.
  - All data addedd/updated/deleted by the Writer is available to the Reader even if the Writer hasn't **committed**.
  - The Reader will be operational even if the Writer is closed at a later stage. The last state of Index as changed by that particular Writer will be available to the Reader.
  
- An IndexSearcher is created from an IndexReader. After reading the above point, one may assume that something similar could be done for IndexReader. **DO NOT!** IndexSearcher will throw an 'AlreadyClosedException' for the IndexReader instance. If you think about it, it's only natural that this would fail, since the Reader instance is the Searcher's only source of reading the Index.
  
  
  
  
