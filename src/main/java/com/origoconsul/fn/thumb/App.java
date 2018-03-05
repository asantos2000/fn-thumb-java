package com.origoconsul.fn.thumb;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import org.xmlpull.v1.XmlPullParserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.origoconsul.fn.thumb.dto.S3EventNotification;
import com.origoconsul.fn.thumb.dto.S3EventNotification.S3EventNotificationRecord;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

/**
 * Thumb image!
 *
 */
public class App {
	public String handleRequest(String input)
	//public static void main(String[] args)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException {

		S3EventNotification event = new ObjectMapper().readValue(input, S3EventNotification.class);
		
		if (input != null && event.getRecords() != null) {
			//System.out.println("----- event-----\n" + event);
			for (S3EventNotificationRecord record : event.getRecords()) {
				String bucketName = record.getS3() != null && record.getS3().getBucket() != null ? record.getS3().getBucket().getName() : null;
				String objectName = record.getS3() != null && record.getS3().getObject() != null ? record.getS3().getObject().getKey() : null;
				String objectContentType = record.getS3() != null && record.getS3().getObject() != null ? record.getS3().getObject().getContentType() : "image/*";
				
				System.out.println("bucketName: " + bucketName);
				System.out.println("objectName: " + objectName);
				System.out.println("objectContentType: " + objectContentType);
				
				System.out.println("start");
				long startTime = System.nanoTime();
				generateThumb(bucketName, objectName, objectContentType, "images-processed");
				long endTime = System.nanoTime();
				System.out.println("finish");
				long totalTime = endTime - startTime;
				System.out.println("took (ms): " + (double) totalTime / 1000000);
			}
		}
		
		return "Ok";
	}

	static void generateThumb(String bucketName, String objectName, String objectContentType, String destBucket)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException, XmlPullParserException {
		try {
			/* play.minio.io for test and development. */
			MinioClient minioClient = new MinioClient("http://172.20.10.208:9000", "4CU2YXB8M42FPXHF1C2D",
					"xb8XN9HbU92nrWRQc2loEP4XOBwd7UarNhf6LJlO");

			//minioClient.statObject(bucketName, objectName);

			// Get input stream
			InputStream originalImage = minioClient.getObject(bucketName, objectName);
			InputStream resizedImage = resizeImage(originalImage, 100, 100);

			// Put image
			minioClient.putObject(destBucket, "small-" + objectName, resizedImage, "application/octet-stream");

			// Close the input stream.
			originalImage.close();
			
			System.out.println("bucketName: " + destBucket);
			System.out.println("small-objectName: " + "small-" + objectName);

		} catch (MinioException e) {
			System.out.println("Error occurred: " + e);
		}
	}

	public static InputStream resizeImage(InputStream inputStream, int width, int height) throws IOException {
		BufferedImage sourceImage = ImageIO.read(inputStream);
		Image thumbnail = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null), thumbnail.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedThumbnail, "jpeg", baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
