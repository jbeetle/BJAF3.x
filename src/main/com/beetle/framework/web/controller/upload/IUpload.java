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
package com.beetle.framework.web.controller.upload;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.view.View;

import javax.servlet.ServletException;

/**
 * <p>Title: BeetleWeb</p>
 *
 * <p>Description: 处理文件上传的控制器接口</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: 甲壳虫软件</p>
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public interface IUpload {
  /**
   * 是否需要缓存标记，如果保证控制器为线程安全的，可以设置为true，

   * 提高效率，默认为true；如果线程不安全，请设置为false
   */
  //boolean cacheFlag = true;
  /**
   * 上传文件的大小，单位为byte，默认为10M
   */
  long sizeMax = 10485760; //10M
  int sizeThreshold = 4096; //4k
  /**
   * 执行上传
   *
   * @param uploadForm 上传的form参数对象
   * @return 返回视图对象
   * @throws ServletException
   */
  View processUpload(UploadForm uploadForm) throws ControllerException;
}
