
/**
 * 主要思路：批量消费，就是建立一个队列，生产者put东西进来，后台的线程定时轮询，轮到时候队列当时有多少就一次批量处理多少元素，<br>
 * 同时把处理的过的元素从队列中清除掉。<br>
 * @author yuhaodong@gmail.com
 *
 */
package com.beetle.framework.util.thread.batchconsume;
