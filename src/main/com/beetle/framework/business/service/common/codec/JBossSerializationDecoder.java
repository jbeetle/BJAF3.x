package com.beetle.framework.business.service.common.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.serial.io.JBossObjectInputStream;

public class JBossSerializationDecoder extends LengthFieldBasedFrameDecoder {
	@Override
	protected ChannelBuffer extractFrame(ChannelBuffer buffer, int index,
			int length) {
		return buffer.slice(index, length);
	}

	public JBossSerializationDecoder(int maxObjectSize) {
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
		JBossObjectInputStream jim = new JBossObjectInputStream(in);
		try {
			Object o = jim.readObject();
			return o;
		} finally {
			jim.close();
		}
	}

}
