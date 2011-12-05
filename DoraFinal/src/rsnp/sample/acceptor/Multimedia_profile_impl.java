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

import android.hardware.Camera;

import com.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase;
import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * IMultimedia_profileã�®å®Ÿè£…ã‚¯ãƒ©ã‚¹
 *
 */
public class Multimedia_profile_impl extends MultimediaProfileBase {

	/** ImageProvidorã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ */
	private ImageProvidor imageProvidor;

	/**
	 * Multimedia_profile_implã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆã‚’æ§‹ç¯‰ã�™ã‚‹
	 */
	public Multimedia_profile_impl() {

		// ObjectHolderã�‹ã‚‰å�–å¾—
		imageProvidor = ObjectHolder.getInstance().get(
				ImageProvidor.class.getName());
		System.out.println(imageProvidor.getClass()+" imageProvider");
	}

	/*
	 * (é�ž Javadoc)
	 *
	 * @see
	 * org.robotservices.v02.profile.acceptor.IMultimedia_profile#get_camera_image
	 * (long, java.lang.String, java.lang.String)
	 */
	@Override
	public Ret_value get_camera_image(long conv_id, String id, String options) {
		System.err.println("Multimedia_profile_impl:get_camera_image enter");
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String time = sdf.format(new Date());
		System.out.println("ç”»åƒ�å�–å¾—æ™‚é–“ï¼š" + time);
		
		// ç”»åƒ�ã‚’å�–å¾—
		imageProvidor.takeImage();
		while(imageProvidor.getSemaphore() == 0){
			
		}
		byte[] bytes = imageProvidor.getImage("PNG");
		imageProvidor.setSemaphore(0);
		
		AttachedFile af = new AttachedFile();
		af.set_mime_type("image/png");
		af.set_file_name(time + ".png");
		af.set_capture_time(time);
		af.set_byte_array(bytes);

		Ret_value ret = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(ret);
		helper.setResult(RESULT.SUCCESS.getResult());
		helper.setDetail("å®Ÿè¡Œçµ�æžœ=æ­£å¸¸çµ‚äº†");
		helper.setAttachedFile(af);
		System.err.println("Multimedia_profile_impl:get_camera_image exit");
		return ret;
	}
}
