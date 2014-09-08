import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.Header;

/*
 * 分析http响应结果，捕捉video
 * content-type:video/*
 * etag:
 */
public class VideoCatcher {
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String ETAG = "ETag";
	private ArrayList<Video> videoList;
	private String dir;
	public VideoCatcher(String dir){
		this.dir = dir;
		this.videoList = new ArrayList<Video>();
	}
	//解析响应结果，捕捉video
	public void proc(RawHttpResponse rawHttpResp){
		int status = rawHttpResp.getStatusLine().getStatusCode();
		
		if(rawHttpResp.containsHeader(CONTENT_TYPE)){
			Header conTypeHeader = rawHttpResp.getFirstHeader(CONTENT_TYPE);
			String conType = conTypeHeader.getValue().trim();
			//System.out.println("content-type:"+conType);
			if(status == 200 && (conType.contains("video")))
			{
				String etag = rawHttpResp.getFirstHeader(ETAG).getValue();
				System.out.println("ETag:" + etag);
				if(etag.isEmpty()){
					return;
				}
				Iterator<Video> it = videoList.iterator();
				while(it.hasNext()){
					Video vd = it.next();
					if(vd.getEtag().equals(etag)){
						byte[] rawEntity = rawHttpResp.getRawEntity();
						/*The first video is part of the second video in youku.com */
						if(rawEntity[0] == vd.getData().toByteArray()[0] && 
								rawEntity[1] ==  vd.getData().toByteArray()[1]&& 
								rawEntity[2] == vd.getData().toByteArray()[2] &&
								rawEntity[3] == vd.getData().toByteArray()[3] &&
								rawEntity[4] == vd.getData().toByteArray()[4] &&
								rawEntity[5] == vd.getData().toByteArray()[5] &&
								rawEntity[6] == vd.getData().toByteArray()[6] &&
								rawEntity[7] == vd.getData().toByteArray()[7] &&
								rawEntity[8] == vd.getData().toByteArray()[8])
							vd.restart(rawEntity);
						else
							vd.append(rawHttpResp.getRawEntity());
						return;
					}
				}
				Video vd = new Video(rawHttpResp);
				videoList.add(vd);
			}
		}
	}
	public void writeAll(){
		int num=0;
		System.out.println("size of catcher:"+ videoList.size());
		Iterator<Video> it = videoList.iterator();
		while(it.hasNext()){
			Video vd = it.next();
			vd.writeTo(dir + num + "." + vd.getSuffix());
			num++;
		}
	}
}
