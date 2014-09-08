import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * 计时器
 * 超过指定时间后，销毁线程池，将video文件写到磁盘
 */
public class Timer extends Thread{
	private int timeout;// seconds
	private final ExecutorService pool;
	private boolean isCanceled = false;
	public Timer(int timeout, ExecutorService pool){
		this.timeout = timeout;
		this.pool = pool;
		this.setDaemon(true);
	}
	 /**
	  * 取消计时
	  */
	 public synchronized void cancel()
	 {
	  isCanceled = true;
	 }
	void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			sleep(timeout*1000);
			if(!isCanceled){
				System.out.println("************");
				HttpAgent.flvCatcher.writeAll();
				shutdownAndAwaitTermination(pool);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			cancel();
			e.printStackTrace();
		} 
		
	}
	
}
