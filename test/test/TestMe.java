package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.persistence.composite.CompositeQueryOperator;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;
import com.beetle.framework.persistence.pagination.PageParameter;
import com.beetle.framework.persistence.pagination.PageParameter.QueryMode;
import com.beetle.framework.persistence.pagination.PageResult;
import com.beetle.framework.persistence.pagination.PaginationException;
import com.beetle.framework.persistence.pagination.imp.MysqlPaginationImp;
import com.beetle.framework.util.ObjectUtil;
import com.beetle.framework.util.encrypt.AesEncrypt;
import com.beetle.framework.util.encrypt.Coder;
import com.beetle.framework.util.encrypt.RSAEncrypt;
import com.beetle.framework.util.http.RestClient;
import com.beetle.framework.util.http.RestRequest;
import com.beetle.framework.util.http.RestResponse;
import com.beetle.framework.web.jwt.Claims;

public class TestMe {
	// to test
	public static void main(String[] args) throws Throwable {
		RSAEncrypt rsa = RSAEncrypt.getInstance();
		rsa.setPrivateKeyFromFile("/Users/henryyu/Documents/workspace/BJAF/rsa512private.key");
		rsa.setPublicKeyFromFile("/Users/henryyu/Documents/workspace/BJAF/rsa512public.key");
		String x = rsa.encryptByPublicKey("888888");
		System.out.println(x);
		System.out.println(rsa.decryptByPrivateKey(x));
	}

	public static void mainRedis(String[] args) throws Throwable {
		RedisOperator ro = new RedisOperator();
		// System.out.println(ro.exists("foo"));
		// ro.shutdownDataSourcesPool();
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "xinxin");
		map.put("age", "22");
		map.put("qq", "123456");
		System.out.println(ro.put("user", map, 10));
		List<String> xlist = new ArrayList<String>();
		xlist.add("x");
		xlist.add("xx");
		xlist.add("xxx");
		System.out.println(ro.put("alist", xlist, 10));
		User user = new User();
		user.setAge(100);
		user.setUsername("Henry");
		System.out.println(ro.put("henry", user, 100));
		while (true) {
			// System.out.println(ro.getAsList("alist"));
			System.out.println(ro.getWithCache("henry", User.class, 10));
			Thread.sleep(1000);
		}
	}

	static String[] parseStr(String token) {
		String[] xx = new String[2];
		int i = token.lastIndexOf('.');
		String y = token.substring(0, i);
		String z = token.substring(i + 1);
		xx[0] = y;
		xx[1] = z;
		return xx;
	}

	public static void mainClient(String[] args) throws Throwable {
		// mainRSA(null);
		String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJlUvcuT/EgKEsPxo/BcVWm7p68Orx2zwA9+VMipXBXctDTik7uWOSYev5sriWBDYNzy5kOew7tlz0N/EbthOmkCAwEAAQ==";
		RestClient client = new RestClient();
		client.connect();
		RestRequest req = new RestRequest("http://localhost:8080/eaat/openapi/login/", RestRequest.InvokeMethod.POST);
		req.addParameter("$username", "Tom");
		req.addParameter("$clientType", "BROWSER");
		req.addParameter("$password", RSAEncrypt.encryptByPublicKey(pk, "888888"));
		RestResponse res = client.invoke(req);
		System.out.println(res.getContent());
	}

	public static void mainAES(String[] args) throws Throwable {
		System.out.println(AesEncrypt.genKey());
		String x = "666";
		String mw = AesEncrypt.encrypt(x, "c418fe483ac2fa7d0837cfce18fb9019");
		System.out.println(mw);
		String jm = AesEncrypt.decrypt(mw, "c418fe483ac2fa7d0837cfce18fb9019");
		System.out.println(jm);
	}

	public static void main5(String[] args) throws Throwable {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		// System.out.println(keyGen.generateKey())
		System.out.println(Coder.bytesToHexString(keyGen.generateKey().getEncoded()));
	}

	public static void main244(String[] args) throws Throwable {
		Long x = 10002l;
		System.out.println(x.toString());
		Claims cc = new Claims();
		cc.setClientId("ccc");
		cc.setClientType(Claims.ClientType.BROWSER.toString());
		// cc.setExp(1000l);
		cc.setIss("myid");
		cc.setUserAgent("mmmm");
		String xx = ObjectUtil.objectToJsonWithJackson(cc);
		System.out.println(xx);
		mainRSA(null);
	}

	public static void mainMD5(String[] args) throws Throwable {
		String x = Coder.md5("iamHenry");
		System.out.println(x);
		System.out.println(x.equals("a52dd22b3d74416ef96f71da166fdc53"));
		mainRSA(null);
	}

	public static void mainRSA(String[] args) throws Throwable {
		RSAEncrypt rsa = RSAEncrypt.getInstance();
		// RSAEncrypt.generateKeys(512);
		// System.out.println(rsa.initKey());
		rsa.setPrivateKeyFromFile("/Users/henryyu/Documents/workspace/BJAF/rsa512private.key");
		rsa.setPublicKeyFromFile("/Users/henryyu/Documents/workspace/BJAF/rsa512public.key");
		// System.out.println(RSAEncrypt.getPrivateKey());
		System.out.println(rsa.decryptByPrivateKey(
				"GEv4UWXFml5ehUsrQAyPEujl1GehdNlmp0CgCQzzUPHfJ3sqyI4C9uAnbyhWaFMSd5yEykBgG6ZYVtvdlEGhTg=="));
		String jmStr = rsa.encryptByPrivateKey("我是小可乐的老爸！");
		System.out.println(jmStr);
		String jkStr = rsa.decryptByPublicKey(jmStr);
		System.out.println(jkStr);
		String xx = rsa.encryptByPublicKey("我是小可乐的老爸666！");
		System.out.println(rsa.decryptByPrivateKey(xx));
		//
		String sign = RSAEncrypt.sign(jmStr.getBytes(), rsa.getPrivateKey());
		System.out.println(sign);
		System.out.println(RSAEncrypt.verify(jmStr.getBytes(), rsa.getPublicKey(), sign));
		// String
		// info="DUMOotw6ARvGyXZmDecZtSi6FsykeY7pkpe93FquKamr6L1wJe4I92kbH+9GC/Ku9AO/WiVc4wBjKyMfGSTM5wtwI+fgkJ3BQqZwXBRLqz/gwgijmA8gzv9PBfwPwXnfxDwMSIXOP0fTAqS9R3hQJXlKVfwsIHqwmX2OfvcwOsA=";
		// String info2=rsa.decryptByPrivateKey(info,
		// "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ24d14738rTV/zXhsyYStmkif6PvMu6SPVAnNfgs5LZyYfv1t9+18FH5DKoy/T/25UX5WT32NH8SOvG7D6sQqcn6y75Gg9f95AsHxB+IDdVoFZ/iFx8bWY0azfjOT5JpHXXl8Dute5ws1hpT79LrMVeiu2ujDxigcJFrzSGV3X9AgMBAAECgYB240sPfICYSjFEBU62QIIDhxUCD2VYCIbgYCEaVWXnZ0WTs4W8GMgYCNIKIdUETC1AOLARKQaGWu6408zW/VCLstM98uoptdhVz+mDwSWxJUNOHmcOSMFcN7XrHfix6s02QFscu6c7G9Bm6kOhlCuGu7U55us45vssimO4l5bMQQJBANC1jKl05foaDp0lbCmpV6BlTNzZGb64pq7asxLgqSTI0btHXE6rziAxkte8p+NJrX3AjWtJz1fmFYit4C0dgzECQQDBdUFvvLoA/EYdEkIu3bSf1Nr6QCHx0vcJeUSH3Cv4l4sFbZak0U+lupGavnixJ0BIFHoEVTOY1coCSx9W5HSNAkBj+JKIGRaP1itp2qMU0ajHi75lixhp4sj0uI1OXY6nsAnGS0hL5r+1bAmKjTNeC8yuj60t1w0abXujHKZk9d4BAkBpp3AlZhzvjNd96QrcLZkH8WfmZEAloeHo+qHC0SvyiFEUldVADlBBYrNCn+OqXJVuyEAbPa0AO0IoHn7vJbXBAkEAuEB5e0pcvjadfwievH1cTGTLYWgc3XMeI8DPSwgfzFMQ1s0n+SC9Qom7GF9dEbyuDFU/3Qud4+3XwUUd3J9Onw==");
		// System.out.println(info2);
	}

	public static void mainSQLInject2(String[] args) {
		// mainPagination(null);
		// DBHelper.sqlInjectValidate("xxxx");

		String sql = "SELECT * FROM sys_users WHERE username=? and password=?";
		QueryOperator q = new QueryOperator();
		q.setDataSourceName("myqlTestDS");
		q.setSql(sql);
		q.addParameter("Henry");
		q.addParameter("f5dc18c37d9192997f289693a26f7728");
		q.access();
		System.out.println(q.getResultList());
	}

	public static void mainSQLInject(String[] args) {
		// mainPagination(null);
		// DBHelper.sqlInjectValidate("xxxx");
		String username = "Henry ' OR 1=1--'";
		String password = "x";
		String sql = "SELECT * FROM sys_users WHERE username='" + username + "' and password='" + password + "'";
		QueryOperator q = new QueryOperator();
		q.setDataSourceName("myqlTestDS");
		q.setSql(sql);
		// q.addParameter("Henry ' OR 1=1--'");
		// q.addParameter("f5dc18c37d9192997f289693a26f7720");
		q.access();
		System.out.println(q.getResultList());
	}

	public static void mainPagination(String[] args) throws PaginationException {
		PageParameter pp = new PageParameter(QueryMode.CompositeSQL);
		pp.setCacheRecordAmountFlag(true);
		pp.setDataSourceName("myqlTestDS");
		pp.setPageNumber(1);
		pp.setPageSize(5);
		// pp.setUserSql("select * from rcvmsg");
		pp.setUserSql("select * from rcvmsg where deviceid is not null"); // 支付可以加无输入参数的条件语句
		pp.addParameter("readFlag", "=", 0);
		pp.addParameter("msgid", ">=", 20);
		// pp.addParameter(new SqlParameter(SqlType.BIGINT, new
		// Long(500000000000002l)));
		// pp.addParameter(new SqlParameter(SqlType.INTEGER, new Integer(10)));
		// pp.addParameter(new SqlParameter(SqlType.VARCHAR, "%2%"));
		MysqlPaginationImp sp = new MysqlPaginationImp();
		PageResult pr = sp.page(pp);
		System.out.println(pr.getRecordAmount());
		System.out.println(pr.getCurPageNumber());
		System.out.println(pr.getCurPageSize());
		System.out.println("--->");
		RsDataSet rs = new RsDataSet(pr.getSqlResultSet());
		for (int i = 0; i < rs.rowCount; i++) {
			for (int j = 0; j < rs.colCount; j++) {
				System.out.println(rs.getFieldValue(j));
			}
			rs.next();
			System.out.println("---");
		}
		// PageDataList
	}

	public static void mainCompositeQueryOperator(String[] args) throws Throwable {
		Logger logger = AppLogger.getLogger(TestMe.class);
		CompositeQueryOperator cqo = new CompositeQueryOperator();
		cqo.setDataSourceName("SYSDATASOURCE_DEFAULT");
		cqo.setSql("select * from rcvmsg");
		cqo.addParameter("readFlag", "=", 0);
		cqo.addParameter("msgid", ">=", 20);
		cqo.access();
		logger.debug("{}", cqo.getSqlResultSet());

	}

	public static void main222(String[] args) throws Throwable {
		// AppLogger.getInstance(TestMe.class).debug("i am Henry");
		Logger logger = AppLogger.getLogger(TestMe.class);
		logger.debug("i am Henry");

	}

	public static void main3(String[] args) throws Throwable {
		RestClient client = new RestClient();
		client.connect();
		RestRequest req = new RestRequest("https://www.baidu.com/", RestRequest.InvokeMethod.GET);
		RestResponse res = client.invoke(req);
		System.out.println(res.getContent());
	}
}
