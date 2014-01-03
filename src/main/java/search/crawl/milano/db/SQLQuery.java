package search.crawl.milano.db;

import java.util.ArrayList;

public class SQLQuery {
	
	public String makeCreateDBQuery(String DBName) {
		String query = "";
		query += "create database " + DBName + " ;";
		
		return query;
	}
	
	public String makeUseDBQuery(String DBName) {
		String query = "";
		query += "use " + DBName + " ;";
		
		return query;
	}
	
	public String makeDropDBQuery(String DBName) {
		String query = "";
		query += "drop database " + DBName + " ;";
		
		return query;
	}
	
	public String makeShowDBsQuery() {
		String query = "";
		query += "show databases;";
		
		return query;
	}
	
	public String makeSelectDataQuery(String tableName) {
		String query = "";
//		query += "select host, path from " + tableName + " where status = '1' ;";
		query += "select host, path from " + tableName + ";";
		
		return query;
	}
	
	public String makeSelectDataQuery(ArrayList columns, String tableName) {
		String query = "";
		
		query += "select " + columns.get(0);
		for (int i=1; i < columns.size(); i++){
			query += ", " + columns.get(i);
		}
//		query += " from " + tableName + " where status = '1' ;";
		query += " from " + tableName + " ;";
		
		return query;
	}
	
	public String makeUpdateDataQuery(String setColumn, String setValue, String whereColumn, String whereValue, String tableName) {
		String query = "";
		
		query += "update " + tableName + " set " + setColumn + " = '" + setValue;
		query += "' where " + whereColumn + " = '" + whereValue + "' ;";
		
		return query;
	}
	
	public String makeCreateTableQuery(String tableName) {
		String query = "";
		query += "create table if not exists `" + tableName + "` (";
		query += "`SN` int(11) NOT NULL AUTO_INCREMENT," 
				+"`Host` varchar(64) NOT NULL DEFAULT ''," 
				+"`Path` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '/'," 
//				+"`SiteID` tinyint(4) NOT NULL DEFAULT '-1'," 
//				+"`DocID` int(11) NOT NULL DEFAULT '-1'," 
//				+"`Signature` varchar(32) DEFAULT NULL," 
//				+"`ExtrDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00'," 
//				+"`ModiDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00'," 
//				+"`Title` varchar(255) NOT NULL DEFAULT ''," 
//				+"`Category` varchar(255) DEFAULT ''," 
				+"`UserID` varchar(32) NOT NULL DEFAULT ''," 
//				+"`UserNickname` varchar(32) DEFAULT ''," 
//				+"`UserSN` varchar(12) DEFAULT '0'," 
//				+"`Permit2Scrap` tinyint(4) DEFAULT '0'," 
//				+"`Hits` int(11) DEFAULT '0'," 
//				+"`RecommandCnt` int(11) DEFAULT '0'," 
//				+"`ScrappedCnt` int(11) DEFAULT '0'," 
//				+"`IsScrapped` tinyint(4) NOT NULL DEFAULT '0'," 
//				+"`Date` date NOT NULL DEFAULT '0000-00-00'," 
//				+"`Time` time NOT NULL DEFAULT '00:00:00'," 
//				+"`Body` text NOT NULL," 
//				+"`MulmeTag` text NOT NULL," 
//				+"`MulmeLink` varchar(255) DEFAULT ''," 
//				+"`MulmeSignature` varchar(32) DEFAULT NULL," 
//				+"`VisitTurn` tinyint(4) DEFAULT '1'," 
//				+"`UpdateCnt` tinyint(4) DEFAULT '0'," 
//				+"`UpdateDone` tinyint(4) DEFAULT '0'," 
//				+"`Status` char(1) DEFAULT '1'," 
//				+"`DupSig` varchar(32) DEFAULT NULL," 
//				+"`SpamMsg` varchar(255) DEFAULT NULL," 
//				+"`GroupInfo` varchar(64) DEFAULT NULL," 
//				+"`GroupFlag` tinyint(4) DEFAULT '0'," 
//				+"`Tag` text NOT NULL," 
//				+"`CookedTitle` varchar(255) NOT NULL DEFAULT ''," 
//				+"`RecommendedCat` varchar(255) NOT NULL DEFAULT ''," 
//				+"`RecommendedTag` varchar(255) NOT NULL DEFAULT ''," 
//				+"`MapInfo` varchar(255) NOT NULL DEFAULT ''," 
//				+"`SemanticCat` varchar(64) NOT NULL DEFAULT ''," 
//				+"`SemanticCatDesc` text NOT NULL," 
//				+"`SemanticAttr` varchar(255) NOT NULL DEFAULT ''," 
//				+"`SemanticAttrDesc` text NOT NULL," 
//				+"`Extra1` text NOT NULL," 
//				+"`Extra2` text NOT NULL," 
//				+"`Extra3` text NOT NULL," 
//				+"`LinkExtrDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00'," 
//				+"`ThumbPath` text NOT NULL," 
//				+"`ExpertCate` varchar(255) DEFAULT ''," 
//				+"`CmtCnt` int(11) DEFAULT '0'," 
//				+"`TrbCnt` int(11) DEFAULT '0'," 
				+"PRIMARY KEY (`Host`,`Path`)," 
				+"UNIQUE KEY `SN` (`SN`)," 
//				+"UNIQUE KEY `DocID` (`DocID`)," 
//				+"KEY `date` (`Date`)," 
//				+"KEY `UpdateDone` (`UpdateDone`)," 
//				+"KEY `Status` (`Status`)," 
				+ "KEY `UserID` (`UserID`)"
				+ ") ENGINE=MyISAM CHARSET=utf8";

		return query;
	}

}
