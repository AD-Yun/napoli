package search.crawl.milano.db;

import java.sql.ResultSet;
import java.util.HashMap;

import search.crawl.milano.common.MilanoEnums.DBReturnValues;

public interface CommonDBHandler {
	
	// DB Operators
	public DBReturnValues createDB(String sql);
	public DBReturnValues useDB(String sql);
	public DBReturnValues dropDB(String sql);

	// Query Operators
	public DBReturnValues insertData(String sql);
	public ResultSet selectData(String sql);
	public DBReturnValues deleteData(String sql);

}
