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
package com.beetle.framework.web.controller;

import com.beetle.framework.web.view.View;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * <p>
 * 虚拟控制器,没有具体实现类，扩展属性:<br>
 * (1)可通过“frontActionFlag”和“backActionFlag”
 * 两个页面参数控制是否处理全局回调，两个参数值为“1”时有效,为"0"时无效 <br>
 * (2)默认进行Session检查，可通过"sessionCheck=false"页面参数来关闭Session检查功能
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * <p/>
 * </p>
 *
 * @author 余浩东
 * @version 1.0
 */

public class VirtualController extends ControllerImp {

    public VirtualController(String simpleViewName, HttpServletRequest request) {
        String scf = request.getParameter("sessionCheck");
        if (scf == null || scf.equals("true")) {
            this.enableSessionCheck();
        }
        this.simpleViewName = simpleViewName;
        this.setCacheSeconds(0);
        this.setInstanceCacheFlag(false);
        String gfc = request.getParameter("frontActionFlag");
        if (gfc != null && gfc.equals("0")) {
            this.disableFrontAction();
        }
        String gbc = request.getParameter("backActionFlag");
        if (gbc != null && gbc.equals("0")) {
            this.disableBackAction();
        }
    }

    private String simpleViewName;

    public View perform(WebInput request) throws ControllerException {
        View v = new View(this.simpleViewName);
        return v;
    }

}
