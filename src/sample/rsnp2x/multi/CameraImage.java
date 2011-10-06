/*
 * $Id: CameraImage.java 117 2010-04-12 09:27:58Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.multi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class CameraImage {

	/** カウンター */
	private static int counter = -1;

	/** 現在のファイル名 */
	private String filename;
	/** 現在の画像 */
	private byte[] image;
	/** 現在の画像取得日付 */
	private String captureDate;

	/**
	 * 現在のカメラ画像を返す。
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * 現在の画像取得日付を返す。
	 *
	 * @return
	 */
	public String getCaptureDate() {
		return captureDate;
	}

	/**
	 * 現在の画像ファイル名を返す。
	 *
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * ファイル一覧の次のファイルをロードする。
	 */
	public void loadNext() {

		counter++;

		// 取得日付
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ");
		captureDate = formatter.format(date);

		String type = "png";
		// ファイル名
		filename = String.format("img_%08d." + type, counter);
		// イメージ取得
		image = createImage(type);
	}

	/**
	 * 画像を取得します。
	 *
	 * @param type
	 *            画像フォーマット
	 * @return 画像データ
	 */
	private byte[] createImage(String type) {
		// 画像を取得
		BufferedImage im;
		im = new BufferedImage(240, 180, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = im.createGraphics();
		int height = im.getHeight();
		int width = im.getWidth();
		int randrgb = (int) (Math.pow(2, 24) * Math.random());
		g2d.setColor(new Color(randrgb));
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(Color.RED);
		g2d.setFont(new Font("Century", Font.PLAIN, 14));
		g2d.drawString("File Name : " + filename, 5, 20);
		g2d.drawString("Capture time : ", 5, 40);
		g2d.drawString(captureDate, 10, 54);
		g2d.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(im, type, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] bytes = out.toByteArray();
		
		// カメラ画像を取得する場合のロジック
		// byte[] bytes = ImgCapture.getInstance().getImg();
		
		return bytes;
	}

}
