package com.youreye.tts.qq;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadPicture {

	String serverUrl = "http://118.89.25.65/upload-and-detect.php";

	HttpURLConnection connection = null;
	DataOutputStream dos = null;

	int bytesAvailable, bufferSize, bytesRead;
	int maxBufferSize = 1 * 1024 * 512;
	byte[] buffer = null;

	String boundary = "-----------------------------1954231646874";
	Map<String, String> formParams = new HashMap<String, String>();

	FileInputStream fin = null;
	
	

	// 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
	private String encode(String value) throws Exception {
		return URLEncoder.encode(value, "UTF-8");
	}

	public String uploadPicToWebServer(String filePath) {

		try {
			URL url = new URL(serverUrl);
			connection = (HttpURLConnection) url.openConnection();

			// 允许向url流中读写数据
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			// 启动post方法
			connection.setRequestMethod("POST");

			// 设置请求头内容
			connection.setRequestProperty("connection", "Keep-Alive");
			connection
					.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------1954231646874");

			dos = new DataOutputStream(connection.getOutputStream());
			fin = new FileInputStream(filePath);

			dos.writeBytes(boundary);
			dos.writeBytes("\r\n");
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"ouba.jpg\"");
			dos.writeBytes("\r\n");
			dos.writeBytes("Content-Type: image/jpeg");
			dos.writeBytes("\r\n");
			dos.writeBytes("\r\n");		

			// 取得本地图片的字节流，向url流中写入图片字节流
			bytesAvailable = fin.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fin.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fin.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fin.read(buffer, 0, bufferSize);
			}
			
			dos.writeBytes("\r\n");	
			dos.writeBytes("-----------------------------1954231646874--");
			dos.writeBytes("\r\n");	
			dos.writeBytes("\r\n");	


			// Server端返回的信息
			int code = connection.getResponseCode();

			if (code == 200) {

				InputStream inStream = connection.getInputStream();
				ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inStream.read(buffer)) != -1) {
					outSteam.write(buffer, 0, len);
				}
				
				outSteam.close();
				inStream.close();
				return new String(outSteam.toByteArray());
			}

			if (dos != null) {
				dos.flush();
				dos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	
	
	public String uploadSharedPicToWebServer(String filePath) {

		try {	
			
			URL url = new URL("http://118.89.25.65/upload-image.php");
			connection = (HttpURLConnection) url.openConnection();

			// 允许向url流中读写数据
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			// 启动post方法
			connection.setRequestMethod("POST");

			// 设置请求头内容
			connection.setRequestProperty("connection", "Keep-Alive");
			connection
					.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------1954231646874");

			dos = new DataOutputStream(connection.getOutputStream());
			fin = new FileInputStream(filePath);
			File file = new File(filePath);

			dos.writeBytes(boundary);
			dos.writeBytes("\r\n");
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename="+file.getName());
			dos.writeBytes("\r\n");
			dos.writeBytes("Content-Type: image/jpeg");
			dos.writeBytes("\r\n");
			dos.writeBytes("\r\n");		

			// 取得本地图片的字节流，向url流中写入图片字节流
			bytesAvailable = fin.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fin.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fin.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fin.read(buffer, 0, bufferSize);
			}
			
			dos.writeBytes("\r\n");	
			dos.writeBytes("-----------------------------1954231646874--");
			dos.writeBytes("\r\n");	
			dos.writeBytes("\r\n");	


			// Server端返回的信息
			int code = connection.getResponseCode();

			if (code == 200) {

				InputStream inStream = connection.getInputStream();
				ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inStream.read(buffer)) != -1) {
					outSteam.write(buffer, 0, len);
				}
				
				outSteam.close();
				inStream.close();
				return new String(outSteam.toByteArray());
			}

			if (dos != null) {
				dos.flush();
				dos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	
	
	public String requestAudioFromWebServer() {

		try {	
			
			URL url = new URL("http://118.89.25.65/get-audio.php");
			connection = (HttpURLConnection) url.openConnection();

			// 允许向url流中读写数据
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			// 启动post方法
			connection.setRequestMethod("POST");

			// Server端返回的信息
			int code = connection.getResponseCode();

			if (code == 200) {

				InputStream inStream = connection.getInputStream();
				ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inStream.read(buffer)) != -1) {
					outSteam.write(buffer, 0, len);
				}
				
				outSteam.close();
				inStream.close();
				return new String(outSteam.toByteArray());
			}

			if (dos != null) {
				dos.flush();
				dos.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

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
				if(resultJsonArray.length() > 1 && ob.getInt("tag_confidence") < 25){
					int next = 0;
					if(theLargestConfidenceIndex>0){
						next = theLargestConfidenceIndex-1;
					}else if(theLargestConfidenceIndex ==0){
						next = 1;
					}
					JSONObject ob2 = resultJsonArray
							.getJSONObject(next);

					return ob.getString("tag_name") + "或" + ob2.getString("tag_name");
				}

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
	
	
	public String parseAudioUrl(String jsonStr) {

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			int resultCode = jsonObj.getInt("code");
			if (resultCode == 0) {
				String url = jsonObj.getJSONObject("data").getString("url");
				int i ;
				return url;

			} else {
				// 返回出错
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
