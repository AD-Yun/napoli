package search.crawl.milano.db;

import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;

import com.mchange.v2.c3p0.ComboPooledDataSource;

//public class MysqlPool implements CommonDBPool {
public class MysqlPool {

	private Configuration config;
	private String DBIP;
	private String DBPort;
	private String UserID;
	private String PW;
	private String JDBCURL;
	
	public String hostName = "";
	public Connection connection;
	public ComboPooledDataSource cpds;
	public MysqlHandler mh;
	
	public MysqlPool(String hostName){
		try {
			
			// read the 'DB-info.xml' file
			config = new XMLConfiguration("DB-info.xml");
			
			//set DB info
			DBIP = this.config.getString( hostName + ".ip");
			DBPort = this.config.getString( hostName + ".port");
			UserID = this.config.getString( hostName + ".userid");
			PW = this.config.getString( hostName + ".password");
			
			JDBCURL = new String ("jdbc:mysql://" + DBIP + ":" +  DBPort );
			System.out.println("jdbc : " + JDBCURL);
			
			// set c3p0(connection pool)
			cpds = new ComboPooledDataSource();
			cpds.setJdbcUrl( this.JDBCURL);
			cpds.setUser(this.UserID);
			cpds.setPassword(this.PW);
			
		} catch (Exception e) { 
	    	e.printStackTrace(); 
	    }
	}
	
	public MysqlPool(String hostName, String DBName){
		try {
			
			// read the 'DB-info.xml' file
			config = new XMLConfiguration("DB-info.xml");
			
			//set DB info
			DBIP = this.config.getString( hostName + ".ip");
			DBPort = this.config.getString( hostName + ".port");
			UserID = this.config.getString( hostName + ".userid");
			PW = this.config.getString( hostName + ".password");
			
			// DB Name 넣을 경우
			JDBCURL = new String ("jdbc:mysql://" + DBIP + ":" +  DBPort + "/" + DBName + "?zeroDateTimeBehavior=convertToNull" );
			
			// set c3p0(connection pool)
			cpds = new ComboPooledDataSource();
			cpds.setJdbcUrl( this.JDBCURL);
			cpds.setUser(this.UserID);
			cpds.setPassword(this.PW);
			
		} catch (Exception e) { 
	    	e.printStackTrace(); 
	    }
	}
	
	public synchronized MysqlHandler getHandler(){
		try {
			mh =  new MysqlHandler(cpds.getConnection());
			
//			System.out.println("getNumConnections : " + cpds.getNumConnections("blog_worker","qmffhrm1234!@#$"));
//			System.out.println("getNumBusyConnections : " + cpds.getNumBusyConnections("blog_worker","qmffhrm1234!@#$"));
//			System.out.println("getNumIdleConnections : " + cpds.getNumIdleConnections("blog_worker","qmffhrm1234!@#$"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mh;
	}
	
	public boolean returnHandler( ){
		try {
			
//			returnHandler.connection.close();
		} catch (Exception e) { 
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
	
}
