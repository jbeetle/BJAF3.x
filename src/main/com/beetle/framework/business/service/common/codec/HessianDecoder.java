package com.beetle.framework.business.service.common.codec;

import com.caucho.hessian.io.Hessian2Input;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

public class HessianDecoder extends LengthFieldBasedFrameDecoder {
	public HessianDecoder(int maxObjectSize) {
		super(maxObjectSize, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		ChannelBuffer frame = (ChannelBuffer) super
				.decode(ctx, channel, buffer);
		if (frame == null) {
			return null;
		}
		ChannelBufferInputStream in = new ChannelBufferInputStream(frame);
		Hessian2Input jim = new Hessian2Input(in);
		try {
			Object o = jim.readObject();
			return o;
		} finally {
			jim.close();
		}
	}

	@Override
	protected ChannelBuffer extractFrame(ChannelBuffer buffer, int index,
			int length) {
		return buffer.slice(index, length);
	}

}
