package com.beetle.dwzdemo.web.user;

import com.beetle.dwzdemo.business.IUserManagerService;
import com.beetle.dwzdemo.valueobject.XxUser;
import com.beetle.dwzdemo.web.common.WsStatus;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.define.PageList;
import com.beetle.framework.util.encrypt.AesEncrypt;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.FacadeController;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

public class UserManagerController extends FacadeController {
	private static final AppLogger logger = AppLogger
			.getInstance(UserManagerController.class);

	public UserManagerController() {
		super();
		this.enableSessionCheck();// 验证用户是否登录 
	}

	@ViewCtrl
	public View pageUserAction(WebInput wi) throws ControllerException {
		// 分页显示用户数据
		int pagesize = wi.getParameterAsInteger("numPerPage", 20);
		int pagenum = wi.getParameterAsInteger("pageNum", 1);
		String orderField = wi.getParameter("orderField", "userid");
		String orderArith = wi.getParameter("orderDirection", "desc");
		logger.debug("orderField:{}", orderField);
		logger.debug("orderArith:{}", orderArith);
		IUserManagerService umSrvc = this
				.serviceLookup(IUserManagerService.class);
		PageList<XxUser> pl = umSrvc.showAllUserByPage(pagenum, pagesize,
				orderField, orderArith);
		ModelData md = new ModelData();
		md.put("userPageList", pl);
		md.put("orderField", orderField);
		md.put("orderDirection", orderArith);
		View view = new View("/userListview.jsp", md);
		return view;
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
		IUserManagerService umSrvc = this
				.serviceLookup(IUserManagerService.class);
		PageList<XxUser> pl = umSrvc.searchByName(username, pagenum, pagesize,
				orderField, orderArith);
		ModelData md = new ModelData();
		md.put("userPageList", pl);
		md.put("orderField", orderField);
		md.put("orderDirection", orderArith);
		View view = new View("/userListview.jsp", md);
		return view;
	}

	@ViewCtrl
	public View findUserAction(WebInput wi) throws ControllerException {
		long userid = wi.getParameterAsLong("uid");
		IUserManagerService umSrvc = this
				.serviceLookup(IUserManagerService.class);
		XxUser user = umSrvc.findUser(userid);
		if (user == null) {
			throw new ControllerException("用户不存在");
		}
		ModelData md = new ModelData();
		md.put("user", user);
		View view = new View("/updateUser.jsp", md);
		return view;
	}

	private String getForwardUrl(WebInput wi) {
		// $user.UserManagerController.ctrl?$action=pageUserAction&pageNum=1&numPerPage=20
		StringBuilder sb = new StringBuilder();
		sb.append(wi.getControllerName()).append("?")
				.append("$action=pageUserAction&pageNum=1&numPerPage=20");
		return sb.toString();
	}

	@WSCtrl
	public ModelData updateUserAction(WebInput wi) throws ControllerException {
		ModelData md = new ModelData();
		WsStatus wss = new WsStatus();
		try {
			IUserManagerService umSrvc = this
					.serviceLookup(IUserManagerService.class);
			XxUser user = wi.getParameterValuesAsFormBean(XxUser.class);
			XxUser userDB = umSrvc.findUser(user.getUserid());
			if (user.getPasswd() == null
					|| user.getPasswd().trim().length() == 0) {
				user.setPasswd(userDB.getPasswd());
			} else {
				user.setPasswd(AesEncrypt.encrypt(user.getPasswd()));
			}
			umSrvc.updateUser(user);
			// {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi",
			// "forwardUrl":"", "callbackType":"closeCurrent"}
			// {"statusCode":"300", "message":"操作失败"}
			wss.setStatusCode(200);
			wss.setMessage("更新用户成功！");
			wss.setNavTabId("userListview");
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
			IUserManagerService umSrvc = this
					.serviceLookup(IUserManagerService.class);
			int r = umSrvc.deleteUser(wi.getParameterAsLong("uid"));
			wss.setStatusCode(200);
			if (r > 0) {
				wss.setMessage("删除用户成功！");
			} else {
				wss.setMessage("此用户不存在，无法执行删除动作！");
			}
			wss.setNavTabId("userListview");
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
			IUserManagerService umSrvc = this
					.serviceLookup(IUserManagerService.class);
			umSrvc.createUser(user);
			// {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi",
			// "forwardUrl":"", "callbackType":"closeCurrent"}
			// {"statusCode":"300", "message":"操作失败"}
			wss.setStatusCode(200);
			wss.setMessage("添加用户成功！");
			wss.setNavTabId("userListview");
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
