/**
 * @author n2365
 */
package search.crawl.napoli.urlfeeder;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.LinkedList;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import search.crawl.milano.db.MysqlPool;

public class FeedServerPipelineFactory implements ChannelPipelineFactory {

	private final String typeFeeder;

	public FeedServerPipelineFactory(String caseFeeder) {
		this.typeFeeder = caseFeeder;
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine =
		// SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));

		pipeline.addLast("decoder", new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content
		// compression.
		pipeline.addLast("deflater", new HttpContentCompressor());
		if (this.typeFeeder.contentEquals("List")) {
			pipeline.addLast("handler",
					new FeedListServerHandler());
		} else if (this.typeFeeder.contentEquals("Body")) {
			pipeline.addLast("handler", new FeedBodyServerHandler());
		} else if (this.typeFeeder.contentEquals("Docid")) {
			pipeline.addLast("handler", new FeedDocidServerHandler());
		} else {
			//System.out.println("No Match TypeFeeder");
			System.exit(1);
		}
		return pipeline;
	}
}