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

import search.crawl.napoli.util.DocIDGenerator;

public class FeedDocidServerHandler extends SimpleChannelUpstreamHandler {

	private HttpRequest request;
	private boolean readingChunks;
	/** Buffer that stores the response content */
	private final StringBuilder buf = new StringBuilder();

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		// String method = null;
		this.request = (HttpRequest) e.getMessage();
		
		if (!readingChunks) {
			this.request = (HttpRequest) e.getMessage();
			HttpRequest request = this.request;
			ChannelBuffer content = request.getContent();
		}


		String SiteIdentfication = request.getUri().split("=")[1];
		long DocumentIdentification = (long) DocIDGenerator.getId(Integer.parseInt(SiteIdentfication));
		//System.out.println(DocumentIdentification);
		//"http://127.0.0.1:8088?siteid=22"
		writeResponse(e, DocumentIdentification);

	}

	private void writeResponse(MessageEvent e, long ServerIdentification)
			throws IOException, ClassNotFoundException {
		// Decide whether to close the connection or not.
		boolean keepAlive = isKeepAlive(request);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(ServerIdentification);

		// Build the response object.

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(byteOut.toByteArray()));
		response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		//System.out.println(response + "" + "RESPONSE");


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
