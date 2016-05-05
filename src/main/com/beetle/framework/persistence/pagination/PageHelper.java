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
package com.beetle.framework.persistence.pagination;

/**
 * <p>
 * Title: Beetle Persistence Framework
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: BeetleSoft
 * </p>
 * 
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public class PageHelper {
	public static int pageCount(int pageSize, int total) {
		int a = total % pageSize;
		if (a > 0) {
			return total / pageSize + 1;
		} else {
			return total / pageSize;
		}
	}

	public static int prePage(int curPage) {
		if (curPage <= 1) {
			return 1;
		} else {
			return curPage - 1;
		}
	}

	public static int nextPage(int curPage, int pageAmount) {
		if (curPage >= pageAmount) {
			return pageAmount;
		} else {
			return curPage + 1;
		}
	}

	public static int curPos(int curPage, int pageSize, int total) {
		int i = (curPage - 1) * pageSize;
		if (i >= total) {
			return lastPos(pageSize, total);
		} else {
			return i;
		}
	}

	public static int pagePos(int curPos, int pageSize, int total) {
		if (curPos == 0) {
			return 1;
		}
		if (curPos == lastPos(pageSize, total)) {
			return pageCount(pageSize, total);
		}
		return curPos / pageSize + 1;
	}

	public static int nextPos(int curPos, int pageSize, int total) {
		int a = curPos + pageSize;
		if (a < total) {
			return a;
		} else {
			return curPos;
		}
	}

	public static int prePos(int curPos, int pageSize) {
		int a = curPos - pageSize;
		if (a >= 0) {
			return a;
		} else {
			return curPos;
		}
	}

	public static int lastPos(int pageSize, int total) {
		int a = pageCount(pageSize, total);
		// int b = a * pageSize;
		return (a - 1) * pageSize;
	}

	public static void countPageInfo(PageParameter pInfo, PageResult pr) {
		if (pr.getRecordAmount() > 0) {
			pr.setPageAmount(PageHelper.pageCount(pInfo.getPageSize(), pr
					.getRecordAmount()));
			pr.setNextPageNumber(PageHelper.nextPage(pInfo.getPageNumber(), pr
					.getPageAmount()));
			pr.setPrePageNumber(PageHelper.prePage(pInfo.getPageNumber()));
			pr.setCurPos(PageHelper.curPos(pInfo.getPageNumber(), pInfo
					.getPageSize(), pr.getRecordAmount()));
		}
	}
}
