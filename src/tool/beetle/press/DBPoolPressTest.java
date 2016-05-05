package beetle.press;

import java.sql.Connection;

import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.util.thread.task.TaskExecutor;
import com.beetle.framework.util.thread.task.TaskImp;

public class DBPoolPressTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		docall();
		TaskExecutor.runRoutineInCommonPool(new W());// 初始化
		TaskExecutor te = new TaskExecutor();
		for (int i = 0; i < 1000; i++) {
			te.addSubRoutine(new W());
		}
		long x = System.currentTimeMillis();
		te.runRoutineParalleJoin();
		long y = System.currentTimeMillis();
		System.out.println("sum:" + (y - x) + "ms");
	}

	private static class W extends TaskImp {

		public W() {
			super();
		}

		@Override
		protected void routine() throws InterruptedException {
			docall();
		}

	}

	private static void docall() {
		for (int i = 0; i < 10; i++) {
			Connection con = ConnectionFactory
					.getConncetion("SYSDATASOURCE_DEFAULT");
			// ..
			ConnectionFactory.closeConnection(con);
		}
	}
}
