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
package com.beetle.framework.persistence.seq;

import com.beetle.framework.persistence.seq.SeqType.SeqImpType;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: 序列号接口
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public interface ISequence {

	/**
	 * 返回数据库系统下一个序列号码
	 * 
	 * 
	 * @param seqtype
	 *            SeqType序列号输入参数对象
	 * 
	 * @return long
	 */
	long nextSequenceNum(SeqType seqtype);

	SeqImpType getImpType();

	/**
	 * 初始化序列的开始值
	 * 
	 * 
	 * @param initValue
	 *            初始值
	 */
	void initSequenceValue(int initValue, SeqType seqtype);
}
