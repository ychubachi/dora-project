package sample.rsnp2x.multi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * カメラの画像のキャプチャを行なうクラス<br>
 * JMFで実装
 * 
 */
public class ImgCapture {

	/**
	 * JMFプレイヤ
	 */
	private Player player = null;

	private static ImgCapture imgCapture = null;

	public static ImgCapture getInstance() {

		if (imgCapture == null) {
			try {
				imgCapture = new ImgCapture();
			} catch (NoPlayerException e) {
				e.printStackTrace();
			} catch (CannotRealizeException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return imgCapture;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dir
	 *            画像の保存ディレクトリ
	 * @param fileFormat
	 *            画像のファイルフォーマット
	 * @throws IOException
	 * @throws CannotRealizeException
	 * @throws NoPlayerException
	 */
	private ImgCapture() throws NoPlayerException, CannotRealizeException,
			IOException {

		// コンポーネント生成
		JFrame frame = new JFrame("ImgCapture");
		frame.setBounds(50, 50, 640, 480);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // エラー回避のため、画面の終了ボタンからは終了させない

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		player = Manager.createRealizedPlayer(new MediaLocator("vfw://0"));

		// コンポーネント配置
		panel.add(player.getVisualComponent(), BorderLayout.CENTER);
		frame.add(panel);
		frame.setVisible(true);

		player.start();

	}

	/**
	 * 画像をキャプチャする
	 * 
	 * @throws IOException
	 */
	public byte[] getImg() {
		// 画像をバッファードイメージに入れる
		FrameGrabbingControl frameGrabber = (FrameGrabbingControl) player
				.getControl("javax.media.control.FrameGrabbingControl");
		Buffer buf = frameGrabber.grabFrame();
		BufferToImage bti = new BufferToImage((VideoFormat) buf.getFormat());
		Image img = bti.createImage(buf);
		BufferedImage bufferdImg = new BufferedImage(320, 240,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferdImg.getGraphics();
		g.drawImage(img, 0, 0, 320, 240, Color.white, null);

		// ファイルに保存
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferdImg, "jpg", bas);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte image[] = bas.toByteArray();
		try {
			bas.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;

	}
}