package test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class HttpUpload3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		File file = new File("d:\\daoshi.mp4");
		//String result =uploadchunk("http://2.15.vacombiner.bokecc.com/servlet/uploadchunk?ccvid=2CF1F262EAFDEA8E9C33DC5901307461&format=json", 0, (1024 * 1024 * 2 - 1), file);	
		String result =uploadchunk("http://2.15.vacombiner.bokecc.com/servlet/uploadchunk?ccvid=2CF1F262EAFDEA8E9C33DC5901307461&format=json", 1024 * 1024 * 2, (4155466 - 1), file);	
		System.out.println(result);	
		
	}
	
	
	
	public static byte[] readChunk(File file, int chunkStart, int chunkEnd) throws IOException{
		if(file == null) {
			throw new IOException("The file does not exist");
		}
		long fileLength = file.length();
		if(chunkStart >= fileLength) {
			throw new IOException("Start position > file length");
		}
		RandomAccessFile accessFile = null;
		try {
			int chunksLen = chunkEnd - chunkStart + 1;
			byte[] chunks = new byte[chunksLen];
			accessFile = new RandomAccessFile(file, "r");
			accessFile.seek(chunkStart);
			int readLength = accessFile.read(chunks, 0, chunksLen);
			System.out.println("read Length: " + readLength);
			accessFile.close();
			return chunks;
		} finally {
			if(accessFile != null) {
				try {
					accessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/* url为/servlet/uploadchunk?ccvid=&format= */
    /* chunkStart为chunk起始位置*/
    /* chunkEnd为chunk结束位置*/
    /* file为文件*/
    /* bufferOut为实际文件输出二进制内容*/

 	public static String uploadchunk(String url, int chunkStart, int chunkEnd, File file) {
 		byte[] bufferOut = null;
 		try {
			bufferOut = readChunk(file, chunkStart, chunkEnd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(bufferOut == null) {
			System.out.println("---------------read file chunk error-----------------");
			return "read file error";
		}
		HttpURLConnection conn = null;
		try {
			String BOUNDARY = "---------CCHTTPAPIFormBoundaryEEXX" + new Random().nextInt(65536); // 定义数据分隔线
			URL openUrl = new URL(url);
			conn = (HttpURLConnection)openUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			// content-range
			conn.setRequestProperty("Content-Range", "bytes " + chunkStart + "-" + chunkEnd + "/" + file.length());
			System.out.println("bytes " + chunkStart + "-" + chunkEnd + "/" + file.length());

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			sb.append("--").append(BOUNDARY).append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file" + file.getName() + "\";filename=\"" + file.getName()
					+ "\"\r\n");
			sb.append("Content-Type: application/octet-stream\r\n");
			sb.append("\r\n");
			byte[] data = sb.toString().getBytes();
			out.write(data);
			out.write(bufferOut);
			out.write("\r\n".getBytes());
			// 定义最后数据分隔线
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();
			out.write(end_data);
			out.flush();
			out.close();

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer resultBuf = new StringBuffer("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				resultBuf.append(line);
			}
			reader.close();
			conn.disconnect();
			return resultBuf.toString();
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

	
	
	
	
	
	
	
	
	

}
