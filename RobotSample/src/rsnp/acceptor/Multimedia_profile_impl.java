/*
 * $Id: Multimedia_profile_impl.java 272 2010-04-28 07:11:27Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package rsnp.acceptor;

import static com.fujitsu.rsi.util.DetailCodeManager.DC_INDEFINITE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.robotservices.v02.profile.common.AttachedFile;
import org.robotservices.v02.profile.common.Ret_value;

import rsnp.sample.RobotMain;

import com.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase;
import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * Multimedia_profileのacceptor実装クラス<br>
 * MultimediaProfileBaseを継承しているため、不要なメソッドを実装する必要がない
 */
public class Multimedia_profile_impl extends MultimediaProfileBase {

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * org.robotservices.v02.profile.acceptor.IMultimedia_profile#get_camera_image
	 * (long, java.lang.String, java.lang.String)
	 */
	@Override
	public Ret_value get_camera_image(long conv_id, String id, String options) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String time = sdf.format(new Date());
		System.out.println("画像取得時間：" + time);

		byte[] bytes = createImage("png", time);

		AttachedFile af = new AttachedFile();
		af.set_mime_type("image/png");
		af.set_file_name(time + ".png");
		af.set_capture_time(time);
		af.set_byte_array(bytes);

		Ret_value ret = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(ret);
		helper.setResult(RESULT.SUCCESS.getResult());
		// 正常終了時は詳細コードの値が不定
		helper.setDetailCode(DC_INDEFINITE);
		// 任意の文字列を設定
		helper.setDetail("実行結果=正常終了");
		helper.setAttachedFile(af);

		return ret;
	}

	/**
	 * 画像を取得します。
	 *
	 * @param type
	 *            画像フォーマット
	 * @param time
	 *            画像に埋め込む時間
	 * @return 画像データ
	 */
	private byte[] createImage(String type, String time) {
		// 画像を取得
		BufferedImage im;
		im = new BufferedImage(224, 126, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = im.createGraphics();
		int height = im.getHeight();
		int width = im.getWidth();
		int randrgb = (int) (Math.pow(2, 24) * Math.random());
		g2d.setColor(new Color(randrgb));
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(Color.RED);
		g2d.setFont(new Font("Century", Font.PLAIN, 14));
		g2d.drawString("Robot ID : " + RobotMain.getRobotId(), 5, 20);
		g2d.drawString("Capture time : ", 5, 40);
		g2d.drawString(time, 10, 54);
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
		return bytes;
	}
}
