import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;


public class RawHttpResponse implements HttpResponse{
	private final static int capacity = 1024*1024*10;//10M
	private static final ProtocolVersion ProtocolVersion = null;
	private byte[] rawResponseHeaders;
	private byte[] rawEntity;
	private CloseableHttpResponse response;
	public RawHttpResponse(CloseableHttpResponse response) {
		this.response = response;
	}
	public byte [] buildRawResponseHeaders(Charset set){
		
		/*build raw response*/ 
		StringBuffer resStrBuf = new StringBuffer();
		resStrBuf.append(response.getStatusLine());
		resStrBuf.append("\r\n");
		HeaderIterator it = response.headerIterator();
		while(it.hasNext()){
			resStrBuf.append(((Header)it.next()).toString());
			resStrBuf.append("\r\n");
		}
		resStrBuf.append("\r\n");
		//System.out.println("response header:" + resStrBuf.toString());
		setRawResponseHeaders(resStrBuf.toString().getBytes(set));
		return getRawResponseHeaders();
	}
	public byte[] buildRawEntity(){
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			try {
				setRawEntity(EntityUtils.toByteArray(entity));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if(response != null)
					try {
						response.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		return getRawEntity();
	}
	public byte[] changeToRaw() throws UnsupportedEncodingException{
		ByteArrayBuffer raw = new ByteArrayBuffer(capacity);
		/*调用顺序先buildRawEntity后rawResponseHeaders，因为*/
		buildRawEntity();
		/*change chunked entity to that has content-length*/
		if(response.getEntity().isChunked()){
			response.removeHeaders("Transfer-Encoding");
			response.addHeader(new BasicHeader("Content-length", "" + getRawEntity().length));
		} 
		byte[] rawResponseHeaders = buildRawResponseHeaders(Charset.forName("UTF-8"));
		
		
		/*write to raw*/
		//raw.setLength(rawResponseHeaders.length + getRawEntity().length);
		raw.append(rawResponseHeaders, 0, rawResponseHeaders.length);
		raw.append(getRawEntity(), 0, getRawEntity().length);
	//	String out = new String(raw.buffer(), "UTF-8");
	//	System.out.println(out);
		//System.out.println("buffer length:" + raw.toByteArray().length);
		return raw.toByteArray();
	}
	
	
	@Override
	public void addHeader(Header arg0) {
		// TODO Auto-generated method stub
		response.addHeader(arg0);
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		response.addHeader(arg0, arg1);
	}

	@Override
	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return response.containsHeader(arg0);
	}

	@Override
	public Header[] getAllHeaders() {
		// TODO Auto-generated method stub
		return response.getAllHeaders();
	}

	@Override
	public Header getFirstHeader(String arg0) {
		// TODO Auto-generated method stub
		return response.getFirstHeader(arg0);
	}

	@Override
	public Header[] getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return response.getHeaders(arg0);
	}

	@Override
	public Header getLastHeader(String arg0) {
		// TODO Auto-generated method stub
		return response.getLastHeader(arg0);
	}

	@Override
	public HttpParams getParams() {
		// TODO Auto-generated method stub
		return response.getParams();
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		// TODO Auto-generated method stub
		return response.getProtocolVersion();
	}

	@Override
	public HeaderIterator headerIterator() {
		// TODO Auto-generated method stub
		return response.headerIterator();
	}

	@Override
	public HeaderIterator headerIterator(String arg0) {
		// TODO Auto-generated method stub
		return response.headerIterator(arg0);
	}

	@Override
	public void removeHeader(Header arg0) {
		// TODO Auto-generated method stub
		response.removeHeader(arg0);
	}

	@Override
	public void removeHeaders(String arg0) {
		// TODO Auto-generated method stub
		response.removeHeaders(arg0);
	}

	@Override
	public void setHeader(Header arg0) {
		// TODO Auto-generated method stub
		response.setHeader(arg0);
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		response.setHeader(arg0, arg1);
	}

	@Override
	public void setHeaders(Header[] arg0) {
		// TODO Auto-generated method stub
		response.setHeaders(arg0);
	}

	@Override
	public void setParams(HttpParams arg0) {
		// TODO Auto-generated method stub
		response.setParams(arg0);
	}
	@Override
	public HttpEntity getEntity() {
		// TODO Auto-generated method stub
		return response.getEntity();
	}
	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return response.getLocale();
	}
	@Override
	public StatusLine getStatusLine() {
		// TODO Auto-generated method stub
		return response.getStatusLine();
	}
	@Override
	public void setEntity(HttpEntity arg0) {
		// TODO Auto-generated method stub
		response.setEntity(arg0);
	}
	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		response.setLocale(arg0);
	}
	@Override
	public void setReasonPhrase(String arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		response.setReasonPhrase(arg0);
	}
	@Override
	public void setStatusCode(int arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		response.setStatusCode(arg0);
	}
	@Override
	public void setStatusLine(StatusLine arg0) {
		// TODO Auto-generated method stub
		response.setStatusLine(arg0);
	}
	@Override
	public void setStatusLine(ProtocolVersion arg0, int arg1) {
		// TODO Auto-generated method stub
		response.setStatusLine(arg0, arg1);
	}
	@Override
	public void setStatusLine(ProtocolVersion arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		response.setStatusLine(arg0, arg1, arg2);
		
	}
	public byte[] getRawEntity() {
		return rawEntity;
	}
	public void setRawEntity(byte[] rawEntity) {
		this.rawEntity = rawEntity;
	}
	public byte[] getRawResponseHeaders() {
		return rawResponseHeaders;
	}
	public void setRawResponseHeaders(byte[] rawResponseHeaders) {
		this.rawResponseHeaders = rawResponseHeaders;
	}



}
