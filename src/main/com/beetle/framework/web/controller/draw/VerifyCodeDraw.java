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
package com.beetle.framework.web.controller.draw;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;

import javax.servlet.http.Cookie;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * <p>Title: BeetleWeb</p>
 *
 * <p>Description: MVC Web Framework</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: 甲壳虫软件</p>
 *
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class VerifyCodeDraw
    implements IDraw {
  private static final char Str[] = {
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
      'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  private static final String COOKIE_NAME = "VerifyCodeDraw";
  public VerifyCodeDraw() {
  }

  /**
   * 执行，画图
   *
   * @param webInput 页面参数输入对象(width--宽；height--高；digit--位数；type--类型（1-纯数字；2-纯英文字符；3-数字与英文字符混合）;font--字体名称
   * 默认为6位，纯数字，宽90；高20；系统默认字体
   * @return DrawInfo－－返回画图属性信息对象
   * @todo Implement this com.beetle.framework.web.controller.draw.IDraw method
   */
  public DrawInfo draw(WebInput wi)throws ControllerException {
    int width = wi.getParameterAsInteger("width");
    int height = wi.getParameterAsInteger("height");
    int type = wi.getParameterAsInteger("type");
    int digit = wi.getParameterAsInteger("digit");
    if (digit == 0) {
      digit = 6;
    }
    if (width == 0) {
      width = 90;
    }
    if (height == 0) {
      height = 20;
    }
    if (type == 0) {
      type = 1;
    }
    BufferedImage image = new BufferedImage(width, height,
                                            BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.setColor(randomColor(180, 250));
    g.fillRect(0, 0, width, height);
    g.setFont(new Font(wi.getParameter("font"), Font.BOLD, 18));
    g.setColor(randomColor(160, 200));
    Random random = new Random();
    for (int i = 0; i < 80; i++) {
      int x = random.nextInt(width);
      int y = random.nextInt(height);
      int xl = random.nextInt(12);
      int yl = random.nextInt(12);
      g.drawLine(x, y, x + xl, y + yl);
    }
    StringBuffer sb = new StringBuffer();
    boolean abl = false;
    for (int i = 0; i < digit; i++) {
      String key = "";
      if (type == 1) {
        key = String.valueOf(random.nextInt(10));
      }
      else if (type == 2) {
        key = String.valueOf(Str[random.nextInt(Str.length)]);
      }
      else if (type == 3) {
        if (abl) {
          key = String.valueOf(random.nextInt(10));
          abl = false;
        }
        else {
          key = String.valueOf(Str[random.nextInt(Str.length)]);
          abl = true;
        }
      }
      g.setColor(new Color(20 + random.nextInt(110), 20 + random
                           .nextInt(110), 20 + random.nextInt(110)));
      g.drawString(key, 13 * i + 6, 16);
      sb.append(key);
    }
    g.dispose();
    DrawInfo di = new DrawInfo( -1, null, 0, 0, null); //为了兼容idaw接口，还是利用drawinfo对象返回
    di.setPlusObj(image);
    //利用cookie来保存状态（为了提高性能不用session）
    Cookie cookie = wi.getCookie(COOKIE_NAME);
    if (cookie == null) {
      cookie = new Cookie(COOKIE_NAME, sb.toString());
    }
    cookie.setMaxAge( -1);
    cookie.setValue(sb.toString());
    wi.addCookie(cookie);
    sb = null;
    return di;
  }

  public Color randomColor(int fc, int bc) {
    Random random = new Random();
    int r = fc + random.nextInt(bc - fc);
    int g = fc + random.nextInt(bc - fc);
    int b = fc + random.nextInt(bc - fc);
    return new Color(r, g, b);
  }
}
