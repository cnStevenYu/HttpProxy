import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/*
 * http代理，使用线程池来处理浏览器发过来的请求。
 */
public class HttpAgent implements Runnable {
	public static VideoCatcher flvCatcher = new VideoCatcher("d:\\");
	private final ExecutorService pool;
	private final ServerSocket serverSocket;
	 
	public HttpAgent (int port, int poolSize) throws IOException{
		serverSocket = new ServerSocket(port);
		pool = Executors.newFixedThreadPool(poolSize);
	}
	@Override
	public void run() {	
		try{
			for(;;){
				getPool().execute(new Task(serverSocket.accept()));
			}
		} catch(IOException e){
			System.out.println("exit");
			//shutdownAndAwaitTermination(pool);
		}
	}
	/**
	 * @return
	 */
	public ExecutorService getPool() {
		return pool;
	}
	public static void main(String args[]) throws IOException {
		HttpAgent agent = new HttpAgent(55555, 10);
		/*设置计时器，等待一段时间后断开所有连接，写文件*/
		new Timer(60*2, agent.getPool()).start();
		agent.run();
		
	}
}
