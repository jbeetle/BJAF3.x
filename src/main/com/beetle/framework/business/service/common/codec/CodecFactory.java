package com.beetle.framework.business.service.common.codec;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;

public class CodecFactory {

	// #rpc_common_codec=java/jbossSerialization/json
	public static ChannelHandler createEncoder() {
		String f = AppProperties.get("rpc_common_codec", "java");
		if (f.equalsIgnoreCase("java")) {
			return new ObjectEncoder();
		} else if (f.equalsIgnoreCase("jbossSerialization")) {
			return new JBossSerializationEncoder();
		} else if (f.equalsIgnoreCase("json")) {
			throw new AppRuntimeException("not support " + f + " yet!");
		} else if (f.equalsIgnoreCase("hessian")) {
			return new HessianEncoder();
		} else {
			throw new AppRuntimeException("not support " + f + " yet!");
		}
	}

	public static ChannelHandler createDecoder() {
		int maxObjectSize = AppProperties.getAsInt("rpc_common_maxObjectSize",
				1024 * 1024);
		String f = AppProperties.get("rpc_common_codec", "java");
		if (f.equalsIgnoreCase("java")) {
			return new ObjectDecoder(maxObjectSize,
					ClassResolvers.softCachingConcurrentResolver(null));
		} else if (f.equalsIgnoreCase("jbossSerialization")) {
			return new JBossSerializationDecoder(maxObjectSize);
		} else if (f.equalsIgnoreCase("json")) {
			throw new AppRuntimeException("not support " + f + " yet!");
		} else if (f.equalsIgnoreCase("hessian")) {
			return new HessianDecoder(maxObjectSize);
		} else {
			throw new AppRuntimeException("not support " + f + " yet!");
		}
	}
}
