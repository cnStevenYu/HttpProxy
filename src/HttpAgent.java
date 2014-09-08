import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/*
 * http����ʹ���̳߳������������������������
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
		/*���ü�ʱ�����ȴ�һ��ʱ���Ͽ��������ӣ�д�ļ�*/
		new Timer(60*2, agent.getPool()).start();
		agent.run();
		
	}
}
