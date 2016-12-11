package com.youreye.tts.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;

public class PictureIndentify {

	private static String jsonStr = "{\"code\":0,\"message\":\"success\",\"tags\":[{\"tag_name\":\"玩偶\",\"tag_confidence\":12},{\"tag_name\":\"玩具\",\"tag_confidence\":18}]}";

	String serverUrl = "http://118.89.25.65/upload-and-detect.php";

	public String sendFileToIndentifyNormal(String filePath) {

		return jsonStr;

	}

	public byte[] sendCloudUrlFileToServer(String filePath) {

		try {

			String cloudUrlStr = "http://open-299201.image.myqcloud.com/open-299201/0/test_fileId_e8c464db-5f19-4b03-b5cc-372fbf043352/original";

			String urlDetailed = "http://118.89.25.65/image-tag-detect.php?url="
					+ cloudUrlStr;
			URL url = new URL(urlDetailed);

			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);

			urlConn.setConnectTimeout(5 * 1000);
			urlConn.setReadTimeout(3 * 1000);
			urlConn.setRequestProperty("Charset", "utf-8");
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// urlConn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			// urlConn.setRequestProperty("connection", "keep-alive");
			// urlConn.setRequestProperty("Content-Length",String.valueOf(buffer.length));

			String rsp = urlConn.getResponseMessage();
			int codeNum = urlConn.getResponseCode();
			if (urlConn.getResponseCode() == 200) {
				return readStream(urlConn.getInputStream());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}



	/**
	 * 读取流
	 * 
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {

		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}


	public String parse(String jsonStr) {

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			int resultCode = jsonObj.getInt("code");
			if (resultCode == 0) {
				JSONArray resultJsonArray = jsonObj.getJSONArray("tags");
				int theLargestConfidenceIndex = 0;
				int theLargestConfidence = 0;

				if (resultJsonArray.length() > 1) {
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject ob = resultJsonArray.getJSONObject(i);
						int confidence = ob.getInt("tag_confidence");
						if (confidence > theLargestConfidence) {
							theLargestConfidence = confidence;
							theLargestConfidenceIndex = i;
						}
					}
				}

				JSONObject ob = resultJsonArray
						.getJSONObject(theLargestConfidenceIndex);

				return ob.getString("tag_name");

			} else {
				// 返回出错
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}



	public String checkVoiceExist() {

		try {

			URL url = new URL(serverUrl);

			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			urlConn.setConnectTimeout(5 * 1000);
			urlConn.setReadTimeout(3 * 1000);
			urlConn.setRequestProperty("Charset", "utf-8");

			StringBuilder sb2 = new StringBuilder();

			int res = urlConn.getResponseCode();
			InputStream in = null;
			if (res == 200) {
				in = urlConn.getInputStream();
				int ch;
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				String str = sb2.toString();
				return str;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// 下载音频文件
	public void downLoadVoiceFile(String urlStr, String fileName) {

		String path = "youreyes";
		OutputStream output = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Charsert", "UTF-8");


			String SDCard = Environment.getExternalStorageDirectory() + "";
			String pathName = SDCard + "/" + path + "/" + fileName;// 文件存储路径

			File file = new File(pathName);
			String dir = SDCard + "/" + path;
			new File(dir).mkdir();// 新建文件夹
			if (file.exists())
				file.delete();
			file.createNewFile();// 新建文件
			output = new FileOutputStream(file);
			// 读取大文件
			InputStream input = conn.getInputStream();
			byte[] buffer = new byte[1024];
			while (input.read(buffer) != -1) {
				output.write(buffer);
			}
			output.flush();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			
			e.printStackTrace();
		}finally {
			try {
				output.close();
				System.out.println("success");
			} catch (IOException e) {
				System.out.println("fail");
				e.printStackTrace();
			}
		}

	}
}
