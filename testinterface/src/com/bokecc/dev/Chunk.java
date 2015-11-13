package com.bokecc.dev;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class Chunk {

	/* url为/servlet/uploadchunk?ccvid=&format= */
	/* chunkStart为chunk起始位置 */
	/* chunkEnd为chunk结束位置 */
	/* file为文件 */
	/* bufferOut为实际文件输出二进制内容 */

	public static String uploadchunk(String url, int chunkStart, int chunkEnd,
		File file, byte[] bufferOut) {
		HttpURLConnection conn = null;
		try {
			String BOUNDARY = "---------CCHTTPAPIFormBoundaryEEXX"
					+ new Random().nextInt(65536); // 定义数据分隔线
			URL openUrl = new URL(url);
			conn = (HttpURLConnection) openUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// content-range
			conn.setRequestProperty("Content-Range", "bytes " + chunkStart
					+ "-" + chunkEnd + "/" + file.length());

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			sb.append("--").append(BOUNDARY).append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file"
					+ file.getName() + "\";filename=\"" + file.getName()
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
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
