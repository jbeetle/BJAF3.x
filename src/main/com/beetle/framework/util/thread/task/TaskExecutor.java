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
package com.beetle.framework.util.thread.task;

import com.beetle.framework.util.queue.BlockQueue;
import com.beetle.framework.util.queue.IQueue;
import com.beetle.framework.util.queue.NoBlockQueue;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 子程序执行器
 *
 * @author HenryYu
 */
public final class TaskExecutor {
    private TaskThreadPool workpool;

    /**
     * 默认构造函数使用框架公共线程池
     */
    public TaskExecutor() {
        this.routineQueue = new NoBlockQueue();
        this.workpool = null;
    }

    /**
     * 使用自定义线程池
     *
     * @param workpool
     */
    public TaskExecutor(TaskThreadPool workpool) {
        this.workpool = workpool;
        this.routineQueue = new NoBlockQueue();
    }

    /**
     * 停止此执行器所使用的线程池
     */
    public void shutdownPool() {
        if (this.workpool != null) {
            this.workpool.shutdownNow();
        } else {
            shutdownCommonPool();
        }
    }

    public void addSubRoutine(TaskImp subRoutine) {
        routineQueue.push(subRoutine);
    }

    private static class RC implements Callable<Object> {
        private TaskImp srValue;

        public RC(TaskImp srValue) {
            this.srValue = srValue;
        }

        public Object call() throws Exception {
            srValue.run();
            return srValue.getResult();
        }

    }

    private static class RQ extends TaskImp {
        protected void end() {
            if (cl != null) {
                cl.push(i);
            }
            super.end();
        }

        private TaskImp srValue;
        private IQueue cl;
        private int i;

        public RQ(TaskImp srValue, IQueue bq, int i) {
            super();
            this.srValue = srValue;
            this.cl = bq;
            this.i = i;
        }

        protected void routine() throws InterruptedException {
            srValue.run();
        }

    }

    private static class RR extends TaskImp {
        public RR(Runnable cmd) {
            super();
            this.cmd = cmd;
        }

        public RR(long maxBlockTime, Runnable cmd) {
            super(maxBlockTime);
            this.cmd = cmd;
        }

        private Runnable cmd;

        protected void routine() throws InterruptedException {
            cmd.run();
        }
    }

    /**
     * 在设置的时间内执行此子程序并等待结果，若超过此时间，会中断此子程序，并触发子程序的timeoutEvent()事件（方法）
     *
     * @param time --最大执行时间，单位毫秒，ms
     * @throws TaskRunException 超时、没有设置子程序或其它不明异常
     * @return--计算结果
     */
    @SuppressWarnings("unchecked")
    public Object runRoutineForTime(long time) throws TaskRunException {
        if (!routineQueue.isEmpty()) {
            TaskImp srValue = (TaskImp) routineQueue.pop();
            @SuppressWarnings("rawtypes")
            FutureTask fr = new FutureTask(new RC(srValue));
            RR rr = new RR(time, fr);
            Object o;
            try {
                if (workpool == null) {
                    boolean rf = runRoutineInCommonPool(rr);
                    if (!rf) {
                        o = null;
                    } else {
                        // o = fr.timedGet(time * 1000);
                        o = fr.get(time, TimeUnit.MILLISECONDS);
                    }
                } else {
                    boolean rf = workpool.runInPool(rr);
                    if (!rf) {
                        o = null;
                    } else {
                        o = fr.get(time, TimeUnit.MILLISECONDS);
                    }
                }
                return o;
            } catch (TimeoutException e) {
                if (srValue != null) {
                    srValue.terminated();// 触发子线程中断处理代码

                }
                throw new TaskRunException("thread timeout", e);
            } catch (Throwable e) {
                throw new TaskRunException(e.getMessage(), e);
            } finally {
                fr = null;
                rr = null;
                srValue = null;
                routineQueue.clear();
            }
        } else {
            throw new TaskRunException("no subroutine found!can't run it");
        }
    }

    /**
     * 执行子程序并等待返回其计算结果。 根据此子程序设置最大阻塞时间来防止线程超时，则超出此时间会中断此子程序
     * 并触发子程序的timeoutEvent()事件（方法）
     *
     * @return 子程序结果
     * @throws TaskRunException 超时、没有设置子程序或其它不明异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object runRoutineForTime() throws TaskRunException {
        if (!routineQueue.isEmpty()) {
            TaskImp srValue = (TaskImp) routineQueue.pop();
            if (srValue == null) {
                throw new TaskRunException(" SubRoutine can't be null");
            }
            long timeout = srValue.getMaxIdle();// s
            if (timeout <= 0) {
                throw new TaskRunException(
                        "this rountine not set maxBlockTime yet!use constructor of SubRoutine(int) to create");
            }
            FutureTask fr = new FutureTask(new RC(srValue));
            // Runnable cmd = fr.setter(new TimedCallable(new RC(srValue),
            // srValue
            // .getMaxIdle() * 1000));
            // Runnable cmd = fr.setter();
            RR rr = new RR(timeout, fr);
            Object o;
            try {
                if (this.workpool == null) {
                    boolean rf = runRoutineInCommonPool(rr);
                    if (!rf) {
                        o = null;
                    } else {
                        o = fr.get(timeout, TimeUnit.MILLISECONDS);
                    }
                } else {
                    boolean rf = workpool.runInPool(rr);
                    if (!rf) {
                        o = null;
                    } else {
                        o = fr.get(timeout, TimeUnit.MILLISECONDS);
                    }
                }
                return o;
            } catch (TimeoutException e) {
                if (srValue != null) {
                    srValue.terminated();// 触发子线程中断处理代码
                }
                throw new TaskRunException("thread timeout", e);
            } catch (Throwable e) {
                throw new TaskRunException(e.getMessage(), e);
            } finally {
                fr = null;
                rr = null;
                srValue = null;
                routineQueue.clear();
            }
        } else {
            throw new TaskRunException("no subroutine found!can't run it");
        }
    }

    /**
     * 并行执行子程序，并等待所有的子程序结束后再退出 （针对一组子程序，此方法会阻塞） （没有超时处理机制，即使子程序设置最大阻塞时间也无效）
     */
    public void runRoutineParalleJoin() throws TaskRunException {
        if (!routineQueue.isEmpty()) {
            IQueue bq = new BlockQueue();
            int i = 0;
            int qsize = routineQueue.size();
            while (!routineQueue.isEmpty()) {
                i++;
                TaskImp srV = (TaskImp) routineQueue.pop();
                RQ rs = new RQ(srV, bq, i);
                boolean flag;
                if (workpool == null) {
                    flag = runRoutineInCommonPool(rs);
                } else {
                    flag = workpool.runInPool(rs);
                }
                if (!flag) {
                    routineQueue.clear();
                    bq.clear();
                    throw new TaskRunException(
                            "can't run,maybe the thread pool is full!");
                }
            }
            for (int ind = 0; ind < qsize; ind++) {
                bq.pop();
            }
        }
    }

    /**
     * 依次执行子程序（按照顺序前一个子程序运行完毕才接着运行下一个，直到所有的子程序执行完毕） （针对一组子程序） 此方法会阻塞
     */
    public void runRoutineInTurn() throws TaskRunException {
        if (!routineQueue.isEmpty()) {
            IQueue bq = new BlockQueue();
            bq.push(0);
            int i = 0;
            int qsize = routineQueue.size();
            while (!routineQueue.isEmpty()) {
                i++;
                bq.pop();
                TaskImp srV = (TaskImp) routineQueue.pop();
                RQ rs = new RQ(srV, bq, i);
                boolean flag;
                if (workpool == null) {
                    flag = runRoutineInCommonPool(rs);
                } else {
                    flag = workpool.runInPool(rs);
                }
                if (!flag) {
                    routineQueue.clear();
                    bq.clear();
                    throw new TaskRunException(
                            "can't run,maybe the thread pool is full!");
                }
                if (i >= qsize) {
                    break;
                }
            }
            // System.out.println(bq.size());
            while (bq.isEmpty()) {
                bq.pop();
                break;
            }
        }
    }

    /**
     * 同runRoutineInTurn方法，但不会阻塞（在后台依次执行）
     */
    public void runRoutineInTurnNoBlock() throws TaskRunException {
        Runnable rab = new Runnable() {
            public void run() {
                IQueue bq = new BlockQueue();
                bq.push(0);
                int i = 0;
                int qsize = routineQueue.size();
                while (!routineQueue.isEmpty()) {
                    i++;
                    bq.pop();
                    TaskImp srV = (TaskImp) routineQueue.pop();
                    RQ rs = new RQ(srV, bq, i);
                    boolean flag;
                    if (workpool == null) {
                        flag = runRoutineInCommonPool(rs);
                    } else {
                        flag = workpool.runInPool(rs);
                    }
                    if (!flag) {
                        routineQueue.clear();
                        bq.clear();
                        throw new TaskRunException(
                                "can't run,maybe the thread pool is full!");
                    }
                    if (i >= qsize) {
                        break;
                    }
                }
                bq.clear();
                bq = null;

            }
        };
        if (!routineQueue.isEmpty()) {
            new Thread(rab).start();
        }
    }

    private FutureTask<?> theFr;

    /**
     * 提早执行子程序，后调用getResult方法获取运算结果 特别适合在主流程中提前先处理任务重部分，再处理其它任务，最后再获取重任务计算结果的场景。
     * <p/>
     * 这样处理的最大好处是优化和节约主流程的执行时间
     *
     * @throws TaskRunException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void runRoutineEarly() throws TaskRunException {
        if (!routineQueue.isEmpty()) {
            TaskImp srValue = (TaskImp) routineQueue.pop();
            theFr = new FutureTask(new RC(srValue));
            RR rr = new RR(theFr);
            if (workpool == null) {
                runRoutineInCommonPool(rr);
            } else {
                workpool.runInPool(rr);
            }
        } else {
            throw new TaskRunException("no subroutine found!can't run it");
        }
    }

    /**
     * 执行子程序
     */
    public void runRoutine() {
        while (!routineQueue.isEmpty()) {
            TaskImp srV = (TaskImp) routineQueue.pop();
            boolean wf;
            if (workpool == null) {
                wf = runRoutineInCommonPool(srV);
            } else {
                wf = workpool.runInPool(srV);
            }
            if (!wf) {
                routineQueue.clear();
                throw new TaskRunException(
                        "can't run,maybe the thread pool is full!");
            }
        }
    }

    /**
     * 获取此子程序的处理结果（此方法会阻塞）
     *
     * @return
     * @throws TaskRunException
     */
    public Object getResult() throws TaskRunException {
        if (theFr == null)
            throw new TaskRunException("invoke 'runRoutineEarly' method first!");
        try {
            return theFr.get();
        } catch (Exception e) {
            throw new TaskRunException(e);
        } finally {
            theFr = null;
            this.routineQueue.clear();
        }
    }

    public TaskThreadPool getPool() {
        if (this.workpool != null) {
            return this.workpool;
        } else {
            return TaskThreadPool.getCommonPool();
        }
    }

    private IQueue routineQueue;

    /**
     * 在线程池中运行子程序 返回true为成功入池，false为失败，有可能是池满了
     *
     * @param subRoutine
     */
    public static boolean runRoutineInCommonPool(TaskImp subRoutine) {
        return TaskThreadPool.getCommonPool().runInPool(subRoutine);
    }

    public static void shutdownCommonPool() {
        TaskThreadPool.getCommonPool().shutdown();
    }

    /**
     * 直接运行子程序 （不会使用连接池，对超时无效）
     *
     * @param subRoutine
     */
    public static void runRoutineDirect(TaskImp subRoutine) {
        subRoutine.run();
    }
}
