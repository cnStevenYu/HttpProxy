import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/*
 * 利用HttpClient与目标服务器建立连接，并把响应结果封装成RawHttpResponse对象，
 * 针对RawHttpResponse进行解析，如果是video文件，则记录下来。
 */
public class Task implements Runnable {
	/**
	 * 用来处理Socket请求的
	 */
	

	private Socket socket;
	//private FlvCatcher catcher;

	public Task(Socket socket) {
		this.socket = socket;
		//this.catcher = catcher;
	}

	public void run() {
		try {
			handleSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void handleSocket() throws Exception {
		VideoCatcher catcher = HttpAgent.flvCatcher;
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		
		byte [] raw = new byte[1024*1024*10];
		if((-1 != in.read(raw))) {
			HttpParser parser = new HttpParser();
			HttpUriRequest uri = parser.parse(raw);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try{
				CloseableHttpResponse response = httpclient.execute(uri);
				RawHttpResponse rawHttpResp = new RawHttpResponse(response);
				int status = response.getStatusLine().getStatusCode();
				if(status == 200){
					raw = rawHttpResp.changeToRaw();
					catcher.proc(rawHttpResp);
					if(out != null) {
						out.write(raw);
					}
				}
				else{
					System.out.println("Status:" + response.getStatusLine().getStatusCode());
					/*directly forward */
					out.write(rawHttpResp.buildRawResponseHeaders(Charset.forName("UTF-8")));
				}
			} catch(SocketException e){
				System.out.println("Error:" + e.getMessage().toString());
			}
			finally{
				httpclient.close();
				socket.close();
			}
		}
		else{
			System.out.println("connection to proxy error");
		}
	}
	/*
	public FlvCatcher getFlvCatcher(){
		return this.catcher;
	}
	*/
}
