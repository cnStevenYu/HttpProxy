import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.util.ByteArrayBuffer;

/*
 * video
 */
public class Video {
	//д╛хо20M
	private static final int capacity = 20*1024*1024;
	private String etag;
	private String suffix;
	private ByteArrayBuffer data;
	public Video(String etag){
		this.etag = etag;
		this.setData(new ByteArrayBuffer(capacity));
	}
	public Video(RawHttpResponse rawResp){
		this.etag = rawResp.getFirstHeader("etag").getValue();
		this.setData(new ByteArrayBuffer(capacity));
		this.getData().append(rawResp.getRawEntity(), 0, rawResp.getRawEntity().length);
		String conType = rawResp.getFirstHeader("content-type").getValue();
		if(conType.contains("video/")){
			this.setSuffix(conType.substring(conType.lastIndexOf("/")+1));
		}
	}
	public void restart(byte[] b){
		getData().clear();
		getData().append(b, 0, b.length);
	}
	public void append(byte[] b) {
		getData().append(b, 0, b.length);
	}
	public boolean writeTo(String filepath){
		System.out.println(filepath);
		File file = new File(filepath);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(getData().toByteArray());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch(IOException e2) {
			e2.printStackTrace();
			return false;
		} finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}
	public String getEtag(){
		return etag;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public ByteArrayBuffer getData() {
		return data;
	}
	public void setData(ByteArrayBuffer data) {
		this.data = data;
	}
}
