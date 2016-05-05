package com.beetle.uidemo.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.define.PageList;
import com.beetle.framework.util.ConvertUtil;
import com.beetle.framework.util.encrypt.AesEncrypt;
import com.beetle.framework.util.structure.DocumentTemplate;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.FacadeController;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;
import com.beetle.uidemo.business.IUserManagerService;
import com.beetle.uidemo.valueobject.XxUser;
import com.beetle.uidemo.web.common.DataTableResponse;
import com.beetle.uidemo.web.common.WsStatus;

public class UserManagerController extends FacadeController {

	private static final AppLogger logger = AppLogger
			.getInstance(UserManagerController.class);
	private static final String USER_FIELDS[] = { "userid", "username", "sex",
			"passwd", "email", "birthday" };

	private final IUserManagerService us;// webcontroller不能使用@InjectField形式注入

	public UserManagerController() {
		super();
		this.enableSessionCheck();// 验证用户是否登录
		this.setInstanceCacheFlag(true);
		this.us = this.serviceLookup(IUserManagerService.class);
	}

	@ViewCtrl
	public View showMainView(WebInput wi) throws ControllerException {
		return new View("views/user/userListview.jsp");
	}

	private String toSex(int x) {
		if (x == 1) {
			return "男";
		} else if (x == 0) {
			return "女";
		} else {
			return "不男不女";
		}
	}

	private String formatStr(String s) {
		if (s.length() > 6) {
			String x = s.substring(0, 5) + "...";
			return x;
		} else {
			return s;
		}
	}

	private int calcPageNum(int curPos, int pageSize) {
		if (curPos == 0) {
			return 1;
		}
		return curPos / pageSize + 1;
	}

	@WSCtrl
	public ModelData pageUserAction(WebInput wi) throws ControllerException {
		/**
		 * <pre>
		 * http://www.datatables.net/usage/server-side
		 *  int	iDisplayStart	Display start point in the current data set.
		 * int	iDisplayLength	Number of records that the table can display in the current draw. It is expected that the number of records returned will be equal to this number, unless the server has fewer records to return.
		 * int	iColumns	Number of columns being displayed (useful for getting individual column search info)
		 * string	sSearch	Global search field
		 * bool	bRegex	True if the global filter should be treated as a regular expression for advanced filtering, false if not.
		 * bool	bSearchable_(int)	Indicator for if a column is flagged as searchable or not on the client-side
		 * string	sSearch_(int)	Individual column filter
		 * bool	bRegex_(int)	True if the individual column filter should be treated as a regular expression for advanced filtering, false if not
		 * bool	bSortable_(int)	Indicator for if a column is flagged as sortable or not on the client-side
		 * int	iSortingCols	Number of columns to sort on
		 * int	iSortCol_(int)	Column being sorted on (you will need to decode this number for your database)
		 * string	sSortDir_(int)	Direction to be sorted - "desc" or "asc".
		 * string	mDataProp_(int)	The value specified by mDataProp for each column. This can be useful for ensuring that the processing of data is independent from the order of the columns.
		 * string	sEcho	Information for DataTables to use for rendering.
		 * </pre>
		 */
		int pagesize = wi.getParameterAsInteger("iDisplayLength", 20);
		int recPos = wi.getParameterAsInteger("iDisplayStart", 1);
		int pagenum = calcPageNum(recPos, pagesize);
		String sEcho = wi.getParameter("sEcho");
		String sColumns = wi.getParameter("sColumns");
		int iSortCol_0 = wi.getParameterAsInteger("iSortCol_0", 0);
		String orderField = USER_FIELDS[iSortCol_0];
		String orderArith = wi.getParameter("sSortDir_0", "desc");
		String sSearch = wi.getParameter("sSearch", "");
		logger.debug("iDisplayLength:{}", pagesize);
		logger.debug("iDisplayStart:{}", recPos);
		logger.debug("recPos:{}", recPos);
		logger.debug("sEcho:{}", sEcho);
		logger.debug("sColumns:{}", sColumns);
		logger.debug("orderField:{}", orderField);
		logger.debug("orderArith:{}", orderArith);
		final PageList<XxUser> pl;
		if (sSearch.length() >= 3) {
			pl = us.searchByName(sSearch, pagenum, pagesize, orderField,
					orderArith);
		} else {
			pl = us.showAllUserByPage(pagenum, pagesize, orderField, orderArith);
		}
		logger.debug("pageList:{}", pl);
		logger.debug("pageList size:{}", pl.size());
		DocumentTemplate dtl = new DocumentTemplate(wi.getServletContext(),
				"/views/user");
		dtl.setEncoding(wi.getCharacterEncoding());
		ModelData md = new ModelData();
		DataTableResponse dtr = new DataTableResponse();
		dtr.setiTotalRecords(pl.getRecordAmount());
		dtr.setiTotalDisplayRecords(pl.getRecordAmount());
		dtr.setsEcho(sEcho);
		dtr.setsColumns(sColumns);
		try {
			List<Object> aaData = new ArrayList<Object>();
			for (XxUser user : pl) {
				List<Object> rowData = new ArrayList<Object>();
				rowData.add(user.getUserid());
				rowData.add(user.getUsername());
				rowData.add(toSex(user.getSex()));
				//rowData.add(formatStr(user.getPasswd()));
				rowData.add(user.getEmail());
				rowData.add(ConvertUtil.dateFormat(user.getBirthday(),"yyyy-MM-dd HH:mm:ss"));
				//
				Map<String, Object> rd = new HashMap<String, Object>();
				rd.put("userId", String.valueOf(user.getUserid()));
				rd.put("userName", user.getUsername());
				String actions = dtl.process(rd, "user_dt.ftl");
				rowData.add(actions);
				rd.clear();
				//
				aaData.add(rowData);
			}
			dtr.setAaData(aaData);
			md.setData(dtr);
		} finally {
			pl.clear();
			dtl.clearCache();
		}
		return md.asJSON();
	}

	@ViewCtrl
	public View nameSearchAction(WebInput wi) throws ControllerException {
		String username = wi.getParameter("username");
		int pagesize = wi.getParameterAsInteger("numPerPage", 1000);
		int pagenum = wi.getParameterAsInteger("pageNum", 1);
		String orderField = wi.getParameter("orderField", "userid");
		String orderArith = wi.getParameter("orderDirection", "desc");
		logger.debug("orderField:{}", orderField);
		logger.debug("orderArith:{}", orderArith);
		PageList<XxUser> pl = us.searchByName(username, pagenum, pagesize,
				orderField, orderArith);
		ModelData md = new ModelData();
		md.put("userPageList", pl);
		md.put("orderField", orderField);
		md.put("orderDirection", orderArith);
		View view = new View("views/user/userListview.jsp", md);
		return view;
	}

	@ViewCtrl
	public View findUserAction(WebInput wi) throws ControllerException {
		long userid = wi.getParameterAsLong("userid");
		XxUser user = us.findUser(userid);
		if (user == null) {
			throw new ControllerException("用户不存在");
		}
		ModelData md = new ModelData();
		md.put("user", user);
		View view = new View("views/user/updateUser.jsp", md);
		return view;
	}

	private String getForwardUrl(WebInput wi) {
		// $user.UserManagerController.ctrl?$action=pageUserAction&pageNum=1&numPerPage=20
		StringBuilder sb = new StringBuilder();
		sb.append(wi.getControllerName()).append("?")
				.append("$action=showMainView");
		return sb.toString();
	}

	@WSCtrl
	public ModelData updateUserAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		WsStatus wss = new WsStatus();
		try {

			XxUser user = wi.getParameterValuesAsFormBean(XxUser.class);
			XxUser userDB = us.findUser(user.getUserid());
			if (user.getPasswd() == null
					|| user.getPasswd().trim().length() == 0) {
				user.setPasswd(userDB.getPasswd());
			} else {
				user.setPasswd(AesEncrypt.encrypt(user.getPasswd()));
			}
			us.updateUser(user);
			// {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi",
			// "forwardUrl":"", "callbackType":"closeCurrent"}
			// {"statusCode":"300", "message":"操作失败"}
			wss.setStatusCode(200);
			wss.setMessage("更新用户成功！");
			wss.setForwardUrl(getForwardUrl(wi));
			wss.setCallbackType("forward");
		} catch (Exception e) {
			logger.error(e);
			wss.setStatusCode(300);
			wss.setMessage("更新用户失败！");
		}
		md.setData(wss);
		return md.asJSON();
	}

	@WSCtrl
	public ModelData delUserAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		WsStatus wss = new WsStatus();
		try {
			us.deleteUser(wi.getParameterAsLong("userid"));
			wss.setStatusCode(200);
			wss.setMessage("删除用户成功！");
			wss.setForwardUrl(getForwardUrl(wi));
			wss.setCallbackType("forward");
		} catch (Exception e) {
			logger.error(e);
			wss.setStatusCode(300);
			wss.setMessage("删除用户失败！");
		}
		md.setData(wss);
		return md.asJSON();
	}

	@WSCtrl
	public ModelData addUserAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		WsStatus wss = new WsStatus();
		try {
			XxUser user = wi.getParameterValuesAsFormBean(XxUser.class);
			user.setPasswd(AesEncrypt.encrypt(user.getPasswd()));
			logger.debug("user:{}", user);
			us.createUser(user);
			// {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi",
			// "forwardUrl":"", "callbackType":"closeCurrent"}
			// {"statusCode":"300", "message":"操作失败"}
			wss.setStatusCode(200);
			wss.setMessage("添加用户成功！");
			wss.setForwardUrl(getForwardUrl(wi));
			wss.setCallbackType("forward");
		} catch (Exception e) {
			logger.error(e);
			wss.setStatusCode(300);
			wss.setMessage("创建用户失败！");
		}
		md.setData(wss);
		return md.asJSON();
	}

}
