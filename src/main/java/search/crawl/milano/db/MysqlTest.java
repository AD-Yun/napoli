package search.crawl.milano.db;

import java.sql.*;
import java.util.ArrayList;

import search.crawl.milano.common.MilanoEnums;


public class MysqlTest {
	
	static String hostName = "wdocmdb4";
	static MysqlPool mp = new MysqlPool(hostName);
	static MysqlHandler mh = mp.getHandler();
	
	public static void main(String[] args) {
		SQLQuery sqlQuery = new SQLQuery();
		String DBName = "Blog_test";
		String tableName = "test";
		
		ResultSet rs = null;
		String sql ;
		String useDBSql ;
		
		MilanoEnums.DBReturnValues ReturnValue;
		
		
		//===============================================
		// DB engine이 innodb일 경우 auto commit 설정 가능
		// isAutoCommit이 true 이면 auto commit, false 이면 mutual commit 
//		isAutoCommit = true;
//		mh.setAutoCommit(isAutoCommit);
		//===============================================
		
		
		//==============================
		// show DBs
//		rs = mh.showDBs();
//		try {
//			
//			while (rs.next())
//			    System.out.println( rs.getString(1) );
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		//======================================
		
		
		//======================================
		// create Database
//		sql = sqlQuery.makeCreateDBQuery(DBName);
//		isCompleted = mh.createDB(sql);
//		System.out.println ("complete? " + isCompleted);
		//=========================================
		
		//======================================
		// create table
//		useDBSql = sqlQuery.makeUseDBQuery(DBName);
//		ReturnValue = mh.useDB( useDBSql );
//		System.out.println("Use DB return values : " + ReturnValue);
//		
//		sql = sqlQuery.makeCreateTableQuery(tableName);
//		
//		ReturnValue = mh.createTable( sql );
//		System.out.println ("complete? " + ReturnValue);
		//=========================================
		

		//======================================
		// insert Data 
//		useDBSql = sqlQuery.makeUseDBQuery(DBName);
//		mh.useDB( useDBSql );
//		sql = "insert into " +  tableName + " (Host, Path, UserID) values ('nate.net','/cc12','nate'); ";
//		ReturnValue = mh.insertData(sql);
//		System.out.println ("complete? " + ReturnValue);
		//=========================================
		
		
		//======================================
		// update Data 
		useDBSql = sqlQuery.makeUseDBQuery(DBName);
		mh.useDB( useDBSql );
		sql = "update " + tableName + " set Path = '/cccccccc' where SN = 1 ; ";
		ReturnValue = mh.updateData(sql);
		System.out.println ("complete? " + ReturnValue);
		//=========================================
		
		//======================================
		// select Data 
//		useDBSql = sqlQuery.makeUseDBQuery(DBName);
//		mh.useDB( useDBSql );
//		sql = "select  * from " + tableName + " where Path = '/bbbbb' ;";
//		
//		rs = mh.selectData(sql);
//		try {
//			while (rs.next())
//			    System.out.println( rs.getString(1) + "\t" +  rs.getString(2) + "\t" +  rs.getString(3) );
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		//=========================================

		//========================================
		// commit
//		mh.commit();
		//=========================================
		
		
		//=========================================
		// rollback
//		mh.rollback();
		//===========================================

	}
	
	
}
