package com.beetle.framework.web.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * Http Rest客户端，支持http的get/post等请求<br>
 * eg:
 * 
 * <pre>
 * 
 * RestClient rc = new RestClient(&quot;henry&quot;, &quot;123456&quot;);
 * rc.connect();
 * try {
 * 	RestRequest rreq = new RestRequest(
 * 			&quot;http://127.0.0.1:9090/webdemo/service/presentation/webservice/BeanDataDemoService/&quot;,
 * 			RestRequest.InvokeMethod.GET);
 * 	rreq.addParameter(&quot;$dataFormat&quot;, &quot;json&quot;);
 * 	rreq.addParameter(&quot;name&quot;, &quot;余浩东&quot;);
 * 	rreq.addParameter(&quot;home&quot;, &quot;shenzhen,china&quot;);
 * 	RestResponse rres = rc.invoke(rreq);
 * 	if (rres.getStatusCode() == 200) {
 * 		System.out.println(rres.getContent());
 * 	} else {
 * 		// ...
 * 	}
 * } catch (RestInvokeException e) {
 * 	e.printStackTrace();
 * } finally {
 * 	rc.close();
 * }
 * 
 * </pre>
 */
public class RestClient {
	public RestClient() {
		this.client = new DefaultHttpClient();
		this.invokLock = new ReentrantLock();
	}

	private final DefaultHttpClient client;
	private final ReentrantLock invokLock;
	private String username;
	private String password;

	/**
	 * 建立连接
	 */
	public void connect() {
		if (username != null && username.length() > 0) {
			UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
					username, password);
			client.getCredentialsProvider().setCredentials(AuthScope.ANY, upc);
		}
	}

	/**
	 * BASIC验证支持
	 * 
	 * @param username
	 *            --用户名
	 * @param password
	 *            --密码
	 */
	public RestClient(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	private static String encode(String content, String charset) {
		if (content == null)
			return null;
		try {
			return URLEncoder.encode(content, charset != null ? charset
					: HTTP.DEF_CONTENT_CHARSET.name());
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private static String encode(String content, Charset charset) {
		return encode(content, charset != null ? charset.name() : null);
	}

	private static String format(Map<String, String> paramMap, Charset charset) {
		StringBuilder result = new StringBuilder();
		Set<String> ks = paramMap.keySet();
		for (String key : ks) {
			String encodedName = key;
			String encodedValue = encode(paramMap.get(key), charset);
			if (result.length() > 0) {
				result.append("&");
			}
			result.append(encodedName);
			if (encodedValue != null) {
				result.append("=");
				result.append(encodedValue);
			}
		}
		return result.toString();
	}

	private static String genGetUrl(String url1, Map<String, String> paramMap,
			Charset charset) {
		if (url1.indexOf('?') > 0) {
			if (!url1.endsWith("&")) {
				url1 = url1 + "&";
			}
		} else {
			url1 = url1 + "?";
		}
		return url1 + format(paramMap, charset);
	}

	/**
	 * 执行调用
	 * 
	 * @param request
	 *            --请求参数
	 * @return 返回结果响应
	 * @throws RestInvokeException
	 *             --在执行过程中发生故障则抛出此异常
	 */
	public RestResponse invoke(RestRequest request) throws RestInvokeException {
		if (request.getInvokeMethod().equals(RestRequest.InvokeMethod.GET)) {
			return this.invokeWithGet(request);
		} else if (request.getInvokeMethod().equals(
				RestRequest.InvokeMethod.POST)) {
			return this.invokeWithPost(request);
		} else if (request.getInvokeMethod().equals(
				RestRequest.InvokeMethod.DELETE)) {
			return this.invokeWithDelete(request);
		} else if (request.getInvokeMethod().equals(
				RestRequest.InvokeMethod.PUT)) {
			return this.invokeWithPut(request);
		} else {
			throw new RestInvokeException("not support this mothod["
					+ request.getInvokeMethod() + "] yet!");
		}
	}

	private RestResponse invokeWithDelete(RestRequest request)
			throws RestInvokeException {
		invokLock.lock();
		try {
			HttpDelete httpdelete = new HttpDelete(request.getUrl());
			if (!request.getHeaderMap().isEmpty()) {
				Iterator<Entry<String, String>> it = request.getHeaderMap()
						.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> kv = (Map.Entry<String, String>) it
							.next();
					httpdelete.addHeader(kv.getKey(), kv.getValue());
				}
			}
			HttpResponse res = client.execute(httpdelete);
			return fillResponse(res, request.getCharset());
		} catch (Exception e) {
			throw new RestInvokeException(e);
		} finally {
			invokLock.unlock();
			request.clear();
		}
	}

	private RestResponse invokeWithGet(RestRequest request)
			throws RestInvokeException {
		invokLock.lock();
		try {
			String url2 = null;
			if (!request.getParamMap().isEmpty()) {
				url2 = genGetUrl(request.getUrl(), request.getParamMap(),
						request.getCharset());
			}
			final HttpGet httpget;
			if (url2 != null) {
				httpget = new HttpGet(url2);
			} else {
				httpget = new HttpGet(request.getUrl());
			}
			if (!request.getHeaderMap().isEmpty()) {
				Iterator<Entry<String, String>> it = request.getHeaderMap()
						.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> kv = (Map.Entry<String, String>) it
							.next();
					httpget.addHeader(kv.getKey(), kv.getValue());
				}
			}
			HttpResponse res = client.execute(httpget);
			return fillResponse(res, request.getCharset());
		} catch (Exception e) {
			throw new RestInvokeException(e);
		} finally {
			invokLock.unlock();
			request.clear();
		}
	}

	private RestResponse fillResponse(HttpResponse res, Charset charset)
			throws IOException {
		Map<String, String> hm = new HashMap<String, String>();
		Header hds[] = res.getAllHeaders();
		if (hds != null && hds.length > 0) {
			for (Header hd : hds) {// 整掉了重复的key的头合理不?
				hm.put(hd.getName(), hd.getValue());
			}
		}
		RestResponse rr = new RestResponse(res.getStatusLine().getStatusCode(),
				EntityUtils.toString(res.getEntity(), charset), hm);
		return rr;
	}

	private RestResponse invokeWithPut(RestRequest request)
			throws RestInvokeException {
		invokLock.lock();
		try {
			List<NameValuePair> nvps = null;
			HttpPut httpput = new HttpPut(request.getUrl());
			if (!request.getParamMap().isEmpty()) {
				nvps = new ArrayList<NameValuePair>();
				Set<String> ks = request.getParamMap().keySet();
				for (String key : ks) {
					NameValuePair np = new BasicNameValuePair(key, request
							.getParamMap().get(key));
					nvps.add(np);
				}
			}
			if (request.getContent() != null) {
				httpput.setEntity(new StringEntity(request.getContent(),
						request.getCharset()));
			}
			if (!request.getHeaderMap().isEmpty()) {
				Iterator<Entry<String, String>> it = request.getHeaderMap()
						.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> kv = (Map.Entry<String, String>) it
							.next();
					httpput.addHeader(kv.getKey(), kv.getValue());
				}
			}
			if (nvps != null && !nvps.isEmpty()) {
				httpput.setEntity(new UrlEncodedFormEntity(nvps, request
						.getCharset()));
			}
			try {
				HttpResponse res = client.execute(httpput);
				return fillResponse(res, request.getCharset());
			} catch (Exception e) {
				throw new RestInvokeException(e);
			} finally {
				if (nvps != null) {
					nvps.clear();
				}
			}
		} finally {
			invokLock.unlock();
			request.clear();
		}
	}

	private RestResponse invokeWithPost(RestRequest request)
			throws RestInvokeException {
		invokLock.lock();
		try {
			List<NameValuePair> nvps = null;
			HttpPost httpost = new HttpPost(request.getUrl());
			if (!request.getParamMap().isEmpty()) {
				nvps = new ArrayList<NameValuePair>();
				Set<String> ks = request.getParamMap().keySet();
				for (String key : ks) {
					NameValuePair np = new BasicNameValuePair(key, request
							.getParamMap().get(key));
					nvps.add(np);
				}
			}
			if (request.getContent() != null) {
				httpost.setEntity(new StringEntity(request.getContent(),
						request.getCharset()));
			}
			if (!request.getHeaderMap().isEmpty()) {
				Iterator<Entry<String, String>> it = request.getHeaderMap()
						.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> kv = (Map.Entry<String, String>) it
							.next();
					httpost.addHeader(kv.getKey(), kv.getValue());
				}
			}
			if (nvps != null && !nvps.isEmpty()) {
				httpost.setEntity(new UrlEncodedFormEntity(nvps, request
						.getCharset()));
			}
			try {
				HttpResponse res = client.execute(httpost);
				return fillResponse(res, request.getCharset());
			} catch (Exception e) {
				throw new RestInvokeException(e);
			} finally {
				if (nvps != null) {
					nvps.clear();
				}
			}
		} finally {
			invokLock.unlock();
			request.clear();
		}
	}

	/**
	 * 关闭回收资源
	 */
	public void close() {
		this.client.getConnectionManager().shutdown();
	}
}
