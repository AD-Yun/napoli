package search.crawl.napoli.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import search.crawl.napoli.common.ThumbnailInfo;

public final class ImageController {
	private ImageController() { 
	}
	
	public static byte[] crawlImage(String url) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom().build();
		byte[] bytes;
		
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			
			try {
				HttpEntity entity = response.getEntity();
				bytes = EntityUtils.toByteArray(entity);
			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}
		
		return bytes;
	}
	
	public static void saveImage(byte[] imageBytes, String destFileName) throws Exception {		
		FileOutputStream out = new FileOutputStream(new java.io.File(destFileName));
		out.write(imageBytes);
		out.close();
	}
	
	public static ThumbnailInfo getThumbnailData(String url, int width, int height) throws Exception {
		ThumbnailInfo thumbInfo = new ThumbnailInfo();
		byte[] imageBytes = crawlImage(url);
		ByteArrayInputStream byArrIs = new ByteArrayInputStream(imageBytes);
		BufferedImage orgImg = ImageIO.read(byArrIs);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img.createGraphics().drawImage(orgImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.flush();
		baos.close();
		
		thumbInfo.setAfterBytes(baos.toByteArray());
		thumbInfo.setOrgSize(imageBytes.length);
		thumbInfo.setOrgHeigh(orgImg.getHeight());
		thumbInfo.setOrgWidth(orgImg.getWidth());
		thumbInfo.setAfterSize(thumbInfo.getAfterBytes().length);
		thumbInfo.setAfterHeigh(img.getHeight());
		thumbInfo.setAfterWidth(img.getWidth());
		
		return thumbInfo;
	}
}


