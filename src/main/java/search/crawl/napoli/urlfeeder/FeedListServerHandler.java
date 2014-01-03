/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.crawl.milano.common.MilanoEnums;
import search.crawl.milano.db.MysqlHandler;

public class FeedListServerHandler extends SimpleChannelUpstreamHandler {

	static final Logger LOGGER = LoggerFactory.getLogger(FeedListServerHandler.class);
	OrderListJobs OrderJobs = new OrderListJobs();
	private HttpRequest request;
	private boolean readingChunks;
	/** Buffer that stores the response content */
	private final StringBuilder buf = new StringBuilder();
	String DBName = "LIST_SEED";
	String tableName = "KNOW_SEEDURL";
	String UseDBsql = "use " + DBName;

	public boolean updateUrlStatusCrawl(List<String> urls, int StatusSignature) {

		MysqlHandler mh = FeedServer.mp.getHandler();
		mh.useDB(UseDBsql);

		for (String hostpath : urls) {
			try {
				URL url = new URL("http://" + hostpath);
				String host = url.getHost();
				String path = url.getPath();
				if (url.getQuery() != null) {
					path = (url.getPath() + "?" + url.getQuery());
				}

				String sql = "update " + tableName + " set RunningStep = '"
						+ StatusSignature
						+ "', ExtrDate = now() where host = '" + host
						+ "' and Path = '" + path + "'";
				//System.out.println("SQL += " + sql);
				MilanoEnums.DBReturnValues isCompleted = mh.updateData(sql);
				
				if ( isCompleted != MilanoEnums.DBReturnValues.SUCCESS ) {
					// Code Write if not success, handle 
				}
				// update KNOW_SEEDURL set ProcState = 3 and UrlState = 200
				// where host = '$HOST' and path = '$PATH';
				// doneUrlList 를 목록 테이블에 업데이트 하도록 처리
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mh.close();
				return false;
			}
		}
		mh.close();
		return true;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		
		FeedServer.TotalCountReceive = FeedServer.TotalCountReceive + 1;
		LOGGER.info(Integer.toString(FeedServer.TotalCountReceive));
		// String method = null;
		this.request = (HttpRequest) e.getMessage();

		//System.out.println("SIZE : " + FeedServer.SerialId.size());
		if (FeedServer.SerialId.size() == 0) {
			OrderJobs.makeOrderListJobs();
		}

		if (!readingChunks) {
			this.request = (HttpRequest) e.getMessage();
			HttpRequest request = this.request;
			ChannelBuffer content = request.getContent();
			if (content.readable()) {
				buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8)
						+ "\r\n");
			}
		}

		writeResponse(e, FeedServer.SerialId);

	}

	private void writeResponse(MessageEvent e, LinkedList<Object> SerialId)
			throws IOException, ClassNotFoundException {
		// Decide whether to close the connection or not.
		boolean keepAlive = isKeepAlive(request);

		//System.out.println(FeedServer.SerialId.size());

		Map<String, Object> popParam = null;

		synchronized (FeedServer.SerialId) {
			popParam = (HashMap<String, Object>) FeedServer.SerialId.poll();
			//System.out.println("Size : " + FeedServer.SerialId.size());
			//System.out.println(popParam);
		}

		try {
			if (popParam != null) {
				String crawlid = (String) popParam.get("crawlid");
				List<String> ListUrl = (List<String>) popParam.get("target");
				// String paging = (String) popParam.get("paging");
				// String sleepinfo = (String) popParam.get("sleepinfo");
				// String template = (String) popParam.get("template");
				if (ListUrl != null) {
					updateUrlStatusCrawl(ListUrl, 100);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(popParam);

		// Build the response object.

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(byteOut.toByteArray()));
		response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		//System.out.println(response + "" + "RESPONSE");

		// if (keepAlive) {
		// // Add 'Content-Length' header only for a keep-alive connection.
		// response.setHeader(CONTENT_LENGTH, response.getContent()
		// .readableBytes());
		// // Add keep alive header as per:
		// // -
		// //
		// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
		// response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		// }
		// Write the response.
		ChannelFuture future = e.getChannel().write(response);

		future.addListener(ChannelFutureListener.CLOSE);
		// Close the non-keep-alive connection after the write operation is
		// done.
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
		e.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
