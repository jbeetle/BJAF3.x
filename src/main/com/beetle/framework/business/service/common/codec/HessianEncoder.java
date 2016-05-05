package com.beetle.framework.business.service.common.codec;

import com.caucho.hessian.io.Hessian2Output;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

public class HessianEncoder extends OneToOneEncoder {
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		ChannelBufferOutputStream bout = new ChannelBufferOutputStream(
				dynamicBuffer(1024, ctx.getChannel().getConfig()
						.getBufferFactory()));
		bout.write(LENGTH_PLACEHOLDER);
		Hessian2Output h2o = new Hessian2Output(bout);
		try {
			h2o.writeObject(msg);
			h2o.flush();
		} finally {
			h2o.close();
		}
		ChannelBuffer encoded = bout.buffer();
		encoded.setInt(0, encoded.writerIndex() - 4);
		return encoded;
	}

}
