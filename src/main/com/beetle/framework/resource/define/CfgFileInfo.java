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
package com.beetle.framework.resource.define;

import java.io.Serializable;

public class CfgFileInfo implements Serializable {

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("filename:").append(filename);
		sb.append(';');
		sb.append("path:").append(path);
		sb.append(';');
		sb.append("modifyCount:").append(modifyCount);
		sb.append(';');
		sb.append("lastReadTime:").append(lastReadTime);
		sb.append(';');
		sb.append("lastFileModifiedTime:").append(lastFileModifiedTime);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result
				+ (int) (lastFileModifiedTime ^ (lastFileModifiedTime >>> 32));
		result = prime * result + (int) (lastReadTime ^ (lastReadTime >>> 32));
		result = prime * result + modifyCount;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		CfgFileInfo other = (CfgFileInfo) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (lastFileModifiedTime != other.lastFileModifiedTime)
			return false;
		if (lastReadTime != other.lastReadTime)
			return false;
		if (modifyCount != other.modifyCount)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1946853458952861115L;
	private String filename;
	private String path;// 含文件名
	private int modifyCount;
	private long lastReadTime;
	private long lastFileModifiedTime;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getModifyCount() {
		return modifyCount;
	}

	public void setModifyCount(int modifyCount) {
		this.modifyCount = modifyCount;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public void setLastReadTime(long lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	public long getLastFileModifiedTime() {
		return lastFileModifiedTime;
	}

	public void setLastFileModifiedTime(long lastFileModifiedTime) {
		this.lastFileModifiedTime = lastFileModifiedTime;
	}

}
