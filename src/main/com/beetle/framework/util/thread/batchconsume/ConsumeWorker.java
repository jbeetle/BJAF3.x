package com.beetle.framework.util.thread.batchconsume;

import java.util.List;

import com.beetle.framework.AppRuntimeException;

/**
 * 批量处理接口
 * 
 * @author yuhaodong@gmail.com
 *
 */
public interface ConsumeWorker {
	/**
	 * 处理
	 * 
	 * @param datas
	 *            某一批的数据
	 * @throws AppRuntimeException
	 */
	void handle(List<Object> datas) throws AppRuntimeException;
}
