package com.beetle.framework.util.http;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.beetle.framework.AppProperties;

class ConnectPool {
	private final PoolingHttpClientConnectionManager cm;

	public ConnectPool(int poolMaxSize, int poolRoute) {
		super();
		LayeredConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(poolMaxSize);
		cm.setDefaultMaxPerRoute(poolRoute);
	}

	public ConnectPool() {
		super(); 
		LayeredConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(AppProperties.getAsInt("util.http.poolMaxSize", 200));
		cm.setDefaultMaxPerRoute(AppProperties.getAsInt("util.http.poolRoute", 20));
	}

	public static CloseableHttpClient createHttpClient(String username, String password) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		return httpclient;
	}

	public CloseableHttpClient getHttpClient(String username, String password) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultCredentialsProvider(credsProvider).build();
		return httpclient;
	}

	public final static CloseableHttpClient createPoolAndReturnClient(int poolMaxSize, int poolRoute) {
		ConnectPool pool = new ConnectPool(poolMaxSize, poolRoute);
		return pool.getHttpClient();
	}

	public final static CloseableHttpClient createPoolAndReturnClientWithSystemDefault() {
		ConnectPool pool = new ConnectPool();
		return pool.getHttpClient();
	}

	public final static CloseableHttpClient createHttpClient() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient;
	}

	public CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		/*
		 * CloseableHttpClient httpClient =
		 * HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接
		 */
		return httpClient;
	}

}
