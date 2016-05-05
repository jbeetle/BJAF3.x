/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.business.service.client;

import org.jboss.netty.channel.*;

import java.net.SocketAddress;

public class MockChannel implements Channel {

	@Override
	public int compareTo(Channel o) {
		throw new UnsupportedOperationException("Unsupported yet");
		// return -1;
	}

	@Override
	public ChannelFuture bind(SocketAddress arg0) {
		return null;
	}

	@Override
	public ChannelFuture close() {
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress arg0) {
		return null;
	}

	@Override
	public ChannelFuture disconnect() {
		return null;
	}

	@Override
	public ChannelFuture getCloseFuture() {
		return null;
	}

	@Override
	public ChannelConfig getConfig() {
		return null;
	}

	@Override
	public ChannelFactory getFactory() {
		return null;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public int getInterestOps() {
		return -1;
	}

	@Override
	public SocketAddress getLocalAddress() {
		return null;
	}

	@Override
	public Channel getParent() {
		return null;
	}

	@Override
	public ChannelPipeline getPipeline() {
		return null;
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return null;
	}

	@Override
	public boolean isBound() {
		return false;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public ChannelFuture setInterestOps(int arg0) {
		return null;
	}

	@Override
	public ChannelFuture setReadable(boolean arg0) {
		return null;
	}

	@Override
	public ChannelFuture unbind() {
		return null;
	}

	@Override
	public ChannelFuture write(Object arg0) {
		return null;
	}

	@Override
	public ChannelFuture write(Object arg0, SocketAddress arg1) {
		return null;
	}

	@Override
	public Object getAttachment() {
		return null;
	}

	@Override
	public void setAttachment(Object arg0) {

	}

}
