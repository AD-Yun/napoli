package search.crawl.napoli.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.napoli.common.GetMulmeLinkResultSet;
import search.crawl.napoli.common.OpenKnowBodyConfig;
import search.crawl.napoli.common.ThumbnailInfo;

public class OpenKnowCommons {
	static final Logger LOGGER = LoggerFactory.getLogger(OpenKnowCommons.class);
	public static final Map<Integer, String> mapSiteIDName = new HashMap<Integer, String>();
	static {
		mapSiteIDName.put(2, "nate"); // recv
		mapSiteIDName.put(3, "nate");
		mapSiteIDName.put(6, "daum");
		mapSiteIDName.put(13, "simfile");
		mapSiteIDName.put(15, "kita");
		mapSiteIDName.put(17, "jobkorea");
		mapSiteIDName.put(15, "jisiklog");
	}
	
	public static String getMakeTableQuery(String tableName) {
		String query = "create table if not exists " + tableName + "(" +
				"`SN` int(11) not null auto_increment," +
				"`HostName` varchar(64) NOT NULL default ''," +
				"`PathName` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL default '/'," +
				"`DownloadFlag` char(1) default '0'," +
				"`ParsingFlag` char(1) default '0'," +
				"`ErrMsg` varchar(255) default NULL," +
				"`SiteID` tinyint(4) NOT NULL default '-1'," +
				"`DocID` bigint(19) NOT NULL default '-1'," +
				"`Signature` varchar(32) default NULL," +
				"`VisitTurn` tinyint(4) NOT NULL default '0'," +
				"`ExtrDate` datetime NOT NULL default '0000-00-00 00:00:00'," +
				"`LastModified` datetime NOT NULL default '0000-00-00 00:00:00'," +
				"`TopCategory` varchar(64) NOT NULL default ''," +
				"`SubCategory` varchar(255) NOT NULL default ''," +
				"`QuesTitle` varchar(255) NOT NULL default ''," +
				"`QuesTitleSig` varchar(255) default NULL," +
				"`QuesBody` mediumtext," +
				"`QuesWriter` varchar(32) NOT NULL default ''," +
				"`QuesDate` date NOT NULL default '0000-00-00'," +
				"`QuesTime` time NOT NULL default '00:00:00'," +
				"`QuesHits` int(11) NOT NULL default '0'," +
				"`QuesReplyCnt` int(11) NOT NULL default '0'," +
				"`QuesPoint` int(11) NOT NULL default '0'," +
				"`NetizenVoteCnt` int(11) NOT NULL default '0'," +
				"`AnsCount` int(11) NOT NULL default '0'," +
				"`AnsSelected` mediumtext," +
				"`AnsOthers` mediumtext," +
				"`AnsDate` date NOT NULL default '0000-00-00'," +
				"`AnsTime` time NOT NULL default '00:00:00'," +
				"`MulmeLink` text," +
				"`Adult` char(1) default 'N'," +
				"`DataType` int(11) NOT NULL default '0'," +
				"`State` char(1) NOT NULL default '0'," +
				"`executed` char(1) NOT NULL default ''," +
				"`section` varchar(64) default NULL," +
				"`QuesRating` int(11) NOT NULL default '-1'," +
				"`QuesRatingCnt` int(11) NOT NULL default '-1'," +
				"`QuesMultiTag` text," +
				"PRIMARY KEY  (`HostName`,`PathName`)," +
				"UNIQUE KEY `SN` (`SN`)," + 
				"KEY `QuesDate` (`QuesDate`)" + ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
		return query;
	}
	
	public static GetMulmeLinkResultSet GetMulmeLink(String mulmeText, String docID) {
		GetMulmeLinkResultSet returnSet = new GetMulmeLinkResultSet("", "2011");
		
		Character c1 = new Character((char)0x11);
		Character c2 = new Character((char)0x09);
		Character c3 = new Character((char)0x03);
		Character c4 = new Character((char)0x02);
		
		String largeAnswerDelim = c1.toString();
		String smallAnswerDelim = c2.toString(); 
		String largeMultiDelim = c3.toString();
		String smallMultiDelim = c4.toString();
		
		int result = 0;
		String thumbID = "";
		String selectedLink = "";
		String selectedThumb = "";
		String signature = "";
		
		String[] arrMulmeText = mulmeText.split(largeMultiDelim);
		Map<String, String> mapMulmeType = new HashMap<String, String>();
		boolean takeImg = false;
		
		for(int i = 0; i < arrMulmeText.length; i++) {
			String mulmeType = "";
			String mulmeLink = "";
			String altText  = "";
			String mulmeTitle  = "";
			
			String[] elements = arrMulmeText[i].split(smallMultiDelim);
			mulmeType = elements[0].trim();
			
			String[] mulmeLinkInfo = null;
			try {
				mulmeLinkInfo = elements[1].split("\"");
			} catch (Exception e) {
				
			}
			try {
				mulmeLink = mulmeLinkInfo[0];
			} catch (Exception e) {
				mulmeLink = "";
			}
			try {
				altText = mulmeLinkInfo[1];
			} catch (Exception e) {
				
			}
			try {
				mulmeTitle = elements[2];
			} catch (Exception e) {
				
			}
			
			if (mulmeType != "") {
				mapMulmeType.put(mulmeType, "1");
			}
			
			String regexp = "([^/]*)(.*)";
			Matcher matcher = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL).matcher(mulmeLink);
			String linkHost = "";
			String linkPath = "";
			if(matcher.find()) {
				linkHost = matcher.group(1);
				linkPath = matcher.group(2);
			} else {
				continue;
			}
			
			if (mulmeType != "" && mulmeLink != "" && takeImg == false) {
				thumbID = docID;
				String referer = "";
				
				String imageUrl = "http://" + linkHost.trim() + linkPath.trim();
				
				ThumbnailInfo temp = null;
				try {
					temp = ImageController.getThumbnailData(imageUrl, 100, 100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("GetThumbNailData Failed : imgUrl = " + imageUrl);
					returnSet.setReturnState("2111");
					result = -1;
					continue;
				}
				
				try {
					String atpDaemonName = OpenKnowBodyConfig.getInstance().getThumbBDBDaemonName();
					String ip = OpenKnowBodyConfig.getInstance().getThumbBDBIP();
					int port = OpenKnowBodyConfig.getInstance().getThumbBDBPort();
					int thumbHashingNum = OpenKnowBodyConfig.getInstance().getThumbHashingNum();
					Long hashResult = HashFunction.hash32(docID, "MS949", thumbHashingNum);
					
					ATPCommons.writeBDB(atpDaemonName, docID, hashResult, temp.getAfterBytes(), ip, port);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("writeThumbBDB Failed : imgUrl = " + imageUrl);
					LOGGER.error(e.getMessage());
					returnSet.setReturnState("2141");
					result = -1;
					continue;
				}
				
				if (temp != null) {
					if ((temp.getOrgSize() > 3000) && (temp.getAfterWidth() > 50) && (temp.getAfterHeigh() > 50)) {
						
						result = 1;
					} else {
						returnSet.setReturnState("2112");
						result = -1;
					}
				}
				
				try {
					signature = Commons.getMD5Hash(temp.getAfterBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("GetMD5 Hash Failed : imgUrl = " + imageUrl);
					continue;
				}
				
				if (result == 1) {
					selectedLink = mulmeLink;
					selectedThumb = thumbID;
					returnSet.setReturnState("2011");
					takeImg = true;
				}
				
			}
		}
		
		String selected = "";
		String tagType = "";
		
		Set<String> keySet = mapMulmeType.keySet();
		Iterator<String> iter = keySet.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(key == "I") {
				tagType = tagType + key;
			}
		}
		
		String compareString = "\"";
		char compare = compareString.charAt(0);
		
		if (takeImg == true) {
			selected = tagType + "I" + "\n" + selectedThumb + "\"" + signature + "\"" + selectedLink;
		} else {
			selected = Commons.rtrim(tagType, compare);
		}
		
		returnSet.setSeleted(selected);
		
		return returnSet;
	}
}
