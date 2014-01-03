package search.crawl.milano.db;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.milano.common.MilanoEnums.DBReturnValues;

//public class MysqlHandler implements CommonDBHandler {
public class MysqlHandler {
	
	Connection connection;
	ResultSet  rs = null;
	PreparedStatement  prestmt = null;
	SQLWarning warning = null;
	int errorCode;
	
	public MysqlHandler (Connection connection){
		this.connection = connection; 
	}
	
	public DBReturnValues createDB(String sql){
		errorCode = 0000;
		
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues createTable(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues useDB(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	
	public DBReturnValues dropDB(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	public ResultSet showDBs(){
		errorCode = 0000;
		try {
			String sql = "show databases ;";
			prestmt = connection.prepareStatement(sql);
			rs = prestmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			errorCode = e.getErrorCode();
		}
		return rs;
	}
	
	
	public ResultSet selectData(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			rs = prestmt.executeQuery(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
			errorCode = e.getErrorCode();
		}
		return rs;
	}
	
	public DBReturnValues deleteData(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	
	public DBReturnValues updateData(String sql){
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
			
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	} 

	public DBReturnValues insertData(String sql) {
		errorCode = 0000;
		try {
			prestmt = connection.prepareStatement(sql);
			prestmt.executeUpdate(sql);
	
			if ( prestmt.getWarnings() != null ){
				errorCode = prestmt.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} finally{
			if  ( prestmt != null) {
				try{
					prestmt.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues setAutoCommit(boolean isAutoCommit){
		errorCode = 0000;
		try{
			connection.setAutoCommit(isAutoCommit);
			
			if ( connection.getWarnings() != null ){
				errorCode = connection.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} 
		return DBReturnValues.getErrors(errorCode);
	} 
	
	public DBReturnValues rollback(){
		errorCode = 0000;
		try{
			connection.rollback();
			if ( connection.getWarnings() != null ){
				errorCode = connection.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} 
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues commit(){
		errorCode = 0000;
		try{
			connection.commit();
			if ( connection.getWarnings() != null ){
				errorCode = connection.getWarnings().getErrorCode();
			}
		} catch (SQLException e) {
			errorCode = e.getErrorCode();
		} 
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues close(ResultSet rs){
		errorCode = 0000;
		try {
			if ( prestmt != null) { 	prestmt.close();  }
			if ( rs != null) { 	rs.close(); }
		} catch (SQLException e) { 
	    	e.printStackTrace();
	    	errorCode = e.getErrorCode();
	    }
		return DBReturnValues.getErrors(errorCode);
	}
	
	public DBReturnValues close(){
		errorCode = 0000;
		try {
			if ( prestmt != null) { 	prestmt.close(); }
			if ( rs != null) { 	rs.close(); }
			if ( connection != null) { 	connection.close(); }
		} catch (SQLException e) { 
	    	e.printStackTrace();
	    	errorCode = e.getErrorCode();
	    }
		return DBReturnValues.getErrors(errorCode);
	}
	
}
