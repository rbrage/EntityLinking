package utility;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MongoDB {

	MongoClient mongoClient;
	DB db;
	DBCollection coll;
	
	String databaseName = "surfaceforms";
	String collectionName = "surfaceforms";
	
	BasicDBObject query;
	DBCursor cursor;
	
	
	public MongoDB(){
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db = mongoClient.getDB(this.databaseName);
		coll = db.getCollection(this.collectionName);
		
	}
	
	public DBCursor getCandidates(String word){
		query = new BasicDBObject("_id",word);
		cursor = coll.find(query);
		return cursor;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
		db = mongoClient.getDB(this.databaseName);
		coll = db.getCollection(this.collectionName);
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
		db = mongoClient.getDB(this.databaseName);
		coll = db.getCollection(this.collectionName);
	}
	
	
}
