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
package com.beetle.framework.resource.watch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Title: Beetle业务逻辑框架
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东（yuhaodong@gmail.com）
 * 
 * @version 1.0
 */
public final class WatchInfo {
	private int status; // 业务类（command/delegate）的状态;10--启动状态；11--运行中；12--结束状态

	private Map<String, VO> resoucrces; // key==资源名称;value=某种资源的共享对象（例如：数据库连接）;
	// private int resourceType; //资源名称类型;1--数据连接（Connection）;2--...
	private long clearTime; // 单位毫秒
	private long beginTime; // 单位毫秒

	static class VO {
		public int getObjType() {
			return objType;
		}

		public Object getResObj() {
			return resObj;
		}

		public VO(int objType, Object resObj) {
			super();
			this.objType = objType;
			this.resObj = resObj;
		}

		private int objType;
		private Object resObj;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((resObj == null) ? 0 : resObj.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VO other = (VO) obj;
			if (resObj == null) {
				if (other.resObj != null)
					return false;
			} else if (!resObj.equals(other.resObj))
				return false;
			return true;
		}
	}

	public WatchInfo() {
		// this.clearTime = Integer.parseInt(ResourceReader.getResStr(
		// "BUSINESS_WATCH_WAIT_TIME"));
		this.clearTime = 0;
		this.status = 10;
		this.beginTime = 0; // System.currentTimeMillis();
		this.resoucrces = new HashMap<String, VO>();
	}

	public long getClearTime() {
		return clearTime;
	}

	public int getStatus() {
		return status;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public int getResourceSizeByType(int objType) {
		int i = 0;
		Iterator<VO> it = resoucrces.values().iterator();
		while (it.hasNext()) {
			VO vo = it.next();
			if (vo.getObjType() == objType) {
				i = i + 1;
			}
		}
		return i;
	}

	public boolean checkResourceExist(String resourceName) {
		return resoucrces.containsKey(resourceName);
	}

	public Object getResourceByName(String resourceName) {
		VO vo = resoucrces.get(resourceName);
		if (vo == null) {
			return null;
		}
		return vo.getResObj();
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setClearTime(long clearTime) {
		this.clearTime = clearTime;
	}

	public void clearResoucrces() {
		resoucrces.clear();
	}

	public Map<String, VO> getResoucrces() {
		return resoucrces;
	}

	public void addResource(String resourceName, Object resourceObject,
			int objType) {
		//System.out.println(resourceObject);
		this.resoucrces.put(resourceName, new VO(objType, resourceObject));
	}

}
