package com.bokecc.dev;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bokecc.util.Md5Encrypt;

public class Create extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");

	}

	//上传测试入口
	public static void main(String[] args) {
		int limit = 1024*1024*2;
		Create create = new Create();
		try {
			create.create();
			create.uploadmeta();
			int left = filesize;
			while(left > 0){
				int end = filesize - 1;
				if(left > limit){
					end = received + limit -1;
				}
				int i = create.uploadchunk(received,end);
				if(i == -1){
					System.out.println("上传出错");
					return;
				}
				left = filesize - received;
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("执行完毕");
	}

	
	
	
	
	
	
	
	
	
	String key = "RBYH5E1nfO8OQ7e5Yh1IVvIGIMkHSXPK";
	String userid = "87ACA21FD20C9C52";

	// 第一步所需参数
	String notify_url = "http://192.168.1.104:8080/test/Back";
	String path = "G:\\test.mpg";
	File file = null;
	String filename = null;
	static int filesize = 0;

	// 第二步所需参数
	String servicetype = null;
	String ccvid = null;
	String metaurl = null;
	String md5 = null;

	// 第三步所需参数
	int chunkStart = 0;
	String chunkurl = null;
	static int received = 0;

	// 第一步：创建视频上传信息
	public void create() throws Exception {
		Map<String, String> treeMap = new TreeMap<String, String>();
		// 查询参数输入
		treeMap.put("userid", userid);
		file = new File(path);
		filename = file.getName();
		FileInputStream fis = new FileInputStream(file);
		filesize = fis.available();
		md5 = Md5Encrypt.getFileMD5(file);
		// 设置参数
		treeMap.put("title", filename);
		treeMap.put("description", filename);
		treeMap.put("filename", filename);
		treeMap.put("filesize", filesize + "");
		treeMap.put("notify_url", notify_url);
		String qs = createQueryString(treeMap);
		// 生成时间片
		long time = new Date().getTime() / 1000;
		// 生成HASH码值
		String hash = md5(String.format("%s&time=%s&salt=%s", qs, time, key));
		// 生成地址
		String address = "http://spark.bokecc.com/api/video/create" + "?" + qs
				+ "&time=" + time + "&hash=" + hash;

		System.out.println(address);
		// 获取返回信息
		URL url = new URL(address);
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			sb.append(str);
			sb.append("\r\n");
		}
		System.out.println(sb.toString());

		Document document = null;
		try {
			document = DocumentHelper.parseText(sb.toString());
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("解析xml错误");
		}
		Element root = document.getRootElement();
		Element eleVideoid = root.element("videoid");
		ccvid = eleVideoid.getTextTrim();

		Element eleServicetype = root.element("servicetype");
		servicetype = eleServicetype.getTextTrim();

		Element eleMetaurl = root.element("metaurl");
		metaurl = eleMetaurl.getTextTrim();
		System.out.println("metaurl:" + metaurl);

		Element eleChunkurl = root.element("chunkurl");
		chunkurl = eleChunkurl.getTextTrim();
		System.out.println("chunkurl:" + chunkurl);
	}

	// 第二步：上传视频meta信息
	public void uploadmeta() throws Exception {
		// 举例说明为视频搜索
		Map<String, String> treeMap = new TreeMap<String, String>();
		// 查询参数输入

		treeMap.put("uid", userid);
		treeMap.put("ccvid", ccvid);
		treeMap.put("first", "1");
		treeMap.put("filename", filename);
		treeMap.put("md5", md5);
		treeMap.put("filesize", filesize + "");
		treeMap.put("servicetype", servicetype);

		String qs = createQueryString(treeMap);
		// 生成时间片
		long time = new Date().getTime() / 1000;

		// 生成HASH码值
		String hash = md5(String.format("%s&time=%s&salt=%s", qs, time, key));
		// System.out.println(hash);
		// 生成地址
		String address = metaurl + "?" + qs + "&time=" + time + "&hash=" + hash;
		System.out.println(address);

		// 获取返回信息
		URL url = new URL(address);
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			sb.append(str);
			sb.append("\r\n");
		}
		System.out.println(sb.toString());

	}

	public static byte[] readChunk(File file, int chunkStart, int chunkEnd)
			throws IOException {
		if (file == null) {
			throw new IOException("The file does not exist");
		}
		long fileLength = file.length();
		if (chunkStart >= fileLength) {
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
			if (accessFile != null) {
				try {
					accessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 第三步：断点续传
	public int uploadchunk(int start, int end) {
		String result = uploadchunk(chunkurl + "?ccvid=" + ccvid
				+ "&format=xml", start, end, file);
		
		Document document = null;
		try {
			document = DocumentHelper.parseText(result);
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("解析xml错误:第三步");
		}
		Element root = document.getRootElement();
		String msg = root.element("msg").getText();
		if("success".equals(msg)){
			String r = root.element("received").getTextTrim();
			int ls = Integer.parseInt(r);
			System.out.println();
			received = ls;
			System.out.println("上传成功："+received+"字节");
			return ls;
		}
		return -1;
	}

	/* url为/servlet/uploadchunk?ccvid=&format= */
	/* chunkStart为chunk起始位置 */
	/* chunkEnd为chunk结束位置 */
	/* file为文件 */
	/* bufferOut为实际文件输出二进制内容 */

	public static String uploadchunk(String url, int chunkStart, int chunkEnd,
			File file) {
		byte[] bufferOut = null;
		try {
			bufferOut = readChunk(file, chunkStart, chunkEnd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (bufferOut == null) {
			System.out
					.println("---------------read file chunk error-----------------");
			return "read file error";
		}
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
			System.out.println("bytes " + chunkStart + "-" + chunkEnd + "/"
					+ file.length());

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
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
			System.out.println("第三步返回结果："+resultBuf.toString());
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

	/** 以下为THQS算法的相关函数 */

	/**
	 * 功能：用一个Map生成一个QueryString，参数的顺序不可预知。
	 * 
	 * @param queryString
	 * @return
	 */
	public static String createQueryString(Map<String, String> queryMap) {

		if (queryMap == null) {
			return null;
		}

		try {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : queryMap.entrySet()) {
				if (entry.getValue() == null) {
					continue;
				}
				String key = entry.getKey().trim();
				String value = URLEncoder.encode(entry.getValue().trim(),
						"utf-8");
				sb.append(String.format("%s=%s&", key, value));
			}
			return sb.substring(0, sb.length() - 1);
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 功能：计算字符串的md5值
	 * 
	 * @param src
	 * @return
	 */
	public static String md5(String src) {
		return digest(src, "MD5");
	}

	/**
	 * 功能：根据指定的散列算法名，得到字符串的散列结果。
	 * 
	 * @param src
	 * @param name
	 * @return
	 */
	private static String digest(String src, String name) {
		try {
			MessageDigest alg = MessageDigest.getInstance(name);
			byte[] result = alg.digest(src.getBytes());
			return byte2hex(result);
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	/**
	 * 功能：将byte数组转换成十六进制可读字符串。
	 * 
	 * @param b
	 *            需要转换的byte数组
	 * @return 如果输入的数组为null，则返回null；否则返回转换后的字符串。
	 */
	public static String byte2hex(byte[] b) {

		if (b == null) {
			return null;
		}
		StringBuilder hs = new StringBuilder();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0" + stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

}
