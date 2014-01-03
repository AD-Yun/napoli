package search.crawl.napoli.urlfeeder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search.crawl.milano.db.MysqlHandler;

public class OrderListJobs {

	// update KNOW_SEEDURL set ProcState = '0' and extrdate = '0000-00-00
	// 00:00:00';
	/**
	 * @param args
	 */
	// public boolean updateFeedStatus(int status) {
	// boolean doneUpdate = false;
	//
	// // update status in here
	// return doneUpdate;
	// }

	public void makeOrderListJobs() {
		// MYSQL CONFIGURE

		String DBName = "LIST_SEED";
		String tableName = "KNOW_SEEDURL";
		String UseDBsql = "use " + DBName;

		ResultSet rs = null;

		MysqlHandler mh = FeedServer.mp.getHandler();
		mh.useDB(UseDBsql);

		String sql = "select Host, Path, PagingInfo, SleepInfo, Templates, BatchInfo from "
				+ tableName
				+ " where RunningStep != '100' and (TIMEDIFF(now(), extrdate) +0) > CycleInfo";

		rs = mh.selectData(sql);

		try {
			if (!rs.isBeforeFirst()) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				synchronized (FeedServer.SerialId) {
					FeedServer.SerialId.add(returnMap);
				}
			} else {
				if (FeedServer.SerialId.size() == 0) {

					while (rs.next()) {
						List<String> urllist = new ArrayList<String>();
						Map<String, Object> returnMap = new HashMap<String, Object>();

						Date now = new Date();
						SimpleDateFormat format = new SimpleDateFormat(
								"ddHHmmssS");

						String idNumber = format.format(now);

						String url = rs.getString(1) + rs.getString(2);
						//System.out.println("URL" + url);
						String paging = rs.getString(3);
						String sleepinfo = rs.getString(4);
						String template = rs.getString(5);
						String batchinfo = rs.getString(6);

						urllist.add(url);

						returnMap.put("target", urllist);
						returnMap.put("paging", paging);
						returnMap.put("crawlid", idNumber);
						returnMap.put("sleepinfo", sleepinfo);
						returnMap.put("templates", template);
						returnMap.put("batchinfo", batchinfo);

						synchronized (FeedServer.SerialId) {
							FeedServer.SerialId.add(returnMap);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mh.close();
	}

	public void makeOrderBodyJobs() {
		// MYSQL CONFIGURE

		String DBName = "UCC_KNOW_LIST";
		String tableName = "KNOW_LIST_RECENT";
		String UseDBsql = "use " + DBName;

		ResultSet beforeRS = null;
		ResultSet rs = null;

		MysqlHandler mh = FeedServer.mp.getHandler();
		mh.useDB(UseDBsql);

		String beforeSQL = "select BodyFilter, count(BodyFilter) as Cnt from "
				+ tableName
				+ " where RunningStep = '000' group by BodyFilter order by Cnt";
		//System.out.println(beforeSQL);
		beforeRS = mh.selectData(beforeSQL);
		try {
			if (!beforeRS.isBeforeFirst()) {
			} else {
				List<String> SplitHost = new ArrayList<String>();

				while (beforeRS.next()) {
					String BodyFilter = beforeRS.getString(1);
					SplitHost.add(BodyFilter);
				}
				
				beforeRS.close();
				
				for (String BodyFilter : SplitHost) {

					String sql = "select Host, Path from "
							+ tableName
							+ " where RunningStep = '000' and BodyFilter = '"
							+ BodyFilter + "' limit 10";

					rs = mh.selectData(sql);

					try {
						if (!rs.isBeforeFirst()) {
							Map<String, Object> returnMap = new HashMap<String, Object>();
							synchronized (FeedServer.SerialId) {
								FeedServer.SerialId.add(returnMap);
							}
						} else {
							List<String> urllist = new ArrayList<String>();
							Map<String, Object> returnMap = new HashMap<String, Object>();

							while (rs.next()) {
								String url = rs.getString(1) + rs.getString(2);
								urllist.add(url);
							}

							Date now = new Date();
							SimpleDateFormat DateFormat = new SimpleDateFormat(
									"ddHHmmssS");

							String idNumber = DateFormat.format(now);
							String template = BodyFilter;

							returnMap.put("target", urllist);
							returnMap.put("crawlid", idNumber);
							returnMap.put("templates", template);
							synchronized (FeedServer.SerialId) {
								FeedServer.SerialId.add(returnMap);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
				rs.close();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mh.close();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
