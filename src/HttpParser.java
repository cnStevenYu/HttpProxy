import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicRequestLine;

/*
 * 处理socket连接中的http请求，重新封装成RequestUriRequest对象
 * 默认只处理GET请求
 */
public class HttpParser {
	static final String CRLF = "\r\n";
	static final String DEL = ":"; 

	public HttpUriRequest parse(byte[] reqRaw) {
		// TODO Auto-generated method stub
		String reqStr = new String(reqRaw);
		StringBuffer reqStrBuf = new StringBuffer(reqStr);
		RequestBuilder builder = null;
		/*request line*/
		int indexOfCRLF = reqStrBuf.indexOf(CRLF);
		String requestLineStr = reqStrBuf.substring(0, indexOfCRLF);
		reqStrBuf.delete(0, indexOfCRLF + CRLF.length());
		BasicRequestLine requestLine = new BasicRequestLine(
						requestLineStr.substring(0, requestLineStr.indexOf(" ")), 
						requestLineStr.substring(requestLineStr.indexOf(" ")+1, requestLineStr.indexOf(" ", requestLineStr.indexOf(" ")+1)), 
						HttpVersion.HTTP_1_1);
		builder = RequestBuilder.create(requestLine.getMethod());
		builder.setUri(requestLine.getUri());
		builder.setVersion(requestLine.getProtocolVersion());
		/*headers*/
		indexOfCRLF = reqStrBuf.indexOf(CRLF);
		while(indexOfCRLF != -1){
			String header = reqStrBuf.substring(0, indexOfCRLF);
			if(header.isEmpty()){
				if(builder.getMethod().equalsIgnoreCase("GET") )
					break;
				else {
					if(builder.getMethod().equalsIgnoreCase("POST")){
						/*
						reqStrBuf.delete(0, CRLF.length());
						int length = Integer.parseInt(builder.getFirstHeader("Content-Length").getValue());
						reqStrBuf.substring(0, length - 1);
						*/
						
						System.out.println("post");
						break;
					}
				}
			}
			builder.addHeader(header.substring(0, header.indexOf(DEL)), 
					header.substring(header.indexOf(DEL) + 1).trim());
			reqStrBuf.delete(0, indexOfCRLF + CRLF.length());
			indexOfCRLF = reqStrBuf.indexOf(CRLF);
		}

		return builder.build();
	}
	/*debug
	public void test(){
		String raw = ("GET dict.youdao.com/suggest HTTP/1.1\r\nAccept: text/html\r\nAccept-Encoding: gzip\r\nUser-Agent: Yodao Desktop Dict (Windows 6.2.9200)\r\nHost: dict.youdao.com\r\nConnection: Keep-Alive\r\n\r\n");
		StringBuffer reqStrBuf = new StringBuffer(raw);
		RequestBuilder builder = null;
		int indexOfCRLF = reqStrBuf.indexOf(CRLF);
		String requestLineStr = reqStrBuf.substring(0, indexOfCRLF);
		reqStrBuf.delete(0, indexOfCRLF + CRLF.length());
		BasicRequestLine requestLine = new BasicRequestLine(
						requestLineStr.substring(0, requestLineStr.indexOf(" ")), 
						requestLineStr.substring(requestLineStr.indexOf(" ")+1, requestLineStr.indexOf(" ", requestLineStr.indexOf(" ")+1)), 
						HttpVersion.HTTP_1_1);
		builder = RequestBuilder.create(requestLine.getMethod());
		builder.setUri(requestLine.getUri());
		builder.setVersion(requestLine.getProtocolVersion());
		indexOfCRLF = reqStrBuf.indexOf(CRLF);
		while(indexOfCRLF != -1){
			String header = reqStrBuf.substring(0, indexOfCRLF);
			if(header.isEmpty()){
				if(builder.getMethod().equalsIgnoreCase("GET") )
					break;
				else {
					System.out.println("post");
				}
			}
			builder.addHeader(header.substring(0, header.indexOf(DEL)), 
					header.substring(header.indexOf(DEL) + 1).trim());
			reqStrBuf.delete(0, indexOfCRLF + CRLF.length());
			indexOfCRLF = reqStrBuf.indexOf(CRLF);
		}
		System.out.println("request: " + 
				builder.build().toString());
	}
	public static void main(String[] args){
		HttpParser parser = new HttpParser();
		parser.test();
	}
	*/
}
