/*
 * $Id: Multimedia_profile_impl.java 213 2010-04-26 07:49:02Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited. FUJITSU CONFIDENTIAL.
 */
package rsnp.sample.acceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.robotservices.v02.profile.common.AttachedFile;
import org.robotservices.v02.profile.common.Ret_value;

import rsnp.sample.ImageProvidor;
import rsnp.sample.ObjectHolder;

import com.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase;
import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * IMultimedia_profileの実装クラス
 *
 */
public class Multimedia_profile_impl extends MultimediaProfileBase {

	/** ImageProvidorオブジェクト */
	private ImageProvidor imageProvidor;

	/**
	 * Multimedia_profile_implオブジェクトを構築する
	 */
	public Multimedia_profile_impl() {

		// ObjectHolderから取得
		imageProvidor = ObjectHolder.getInstance().get(
				ImageProvidor.class.getName());
	}

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

		// 画像を取得
		byte[] bytes = imageProvidor.getImage("PNG");

		AttachedFile af = new AttachedFile();
		af.set_mime_type("image/png");
		af.set_file_name(time + ".png");
		af.set_capture_time(time);
		af.set_byte_array(bytes);

		Ret_value ret = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(ret);
		helper.setResult(RESULT.SUCCESS.getResult());
		helper.setDetail("実行結果=正常終了");
		helper.setAttachedFile(af);

		return ret;
	}
}
