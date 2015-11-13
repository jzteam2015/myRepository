package com.bokecc.dev;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.bokecc.config.Config;
import com.bokecc.util.APIServiceFunction;
import com.bokecc.util.Md5Encrypt;

public class Player {

	public static void main(String[] args) throws Exception {

		// 举例说明为视频搜索
		Map<String, String> treeMap = new TreeMap<String, String>();
		// 查询参数输入
		String key = "RBYH5E1nfO8OQ7e5Yh1IVvIGIMkHSXPK";
		treeMap.put("userid", "87ACA21FD20C9C52");
		treeMap.put("videoid", "A37D640D6132CF7A9C33DC5901307461");
		treeMap.put("player_width", "800px");
		treeMap.put("player_height", "600px");
		treeMap.put("auto_play", "true");
		treeMap.put("format", "xml");
		String qs = APIServiceFunction.createQueryString(treeMap);
		// 生成时间片
		long time = new Date().getTime() / 1000;

		// 生成HASH码值
		String hash = Md5Encrypt.md5(String.format("%s&time=%s&salt=%s", qs,
				time, key));
		// System.out.println(hash);
		// 生成地址
		/** address1 为获取账户信息 接口地址 */
		// 第一步
		String address = Config.api_playCode + "?" + qs + "&time=" + time
				+ "&hash=" + hash;

		System.out.println("请求URL:" + address);

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

}
