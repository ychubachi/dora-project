/*
 * $Id: Distributor_impl.java 236 2010-04-27 01:35:50Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.multi;

import org.robotservices.v02.profile.acceptor.IDistributor;
import org.robotservices.v02.profile.common.AttachedFile;
import org.robotservices.v02.profile.common.Ret_value;

import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

public class Distributor_impl implements IDistributor {

	private CameraImage ci = new CameraImage();

	@Override
	public Ret_value doProcess() {

		ci.loadNext();

		// 返却要素を組み立てる
		Ret_value ret = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(ret);

		// ファイルを配信する
		AttachedFile attachedFile = new AttachedFile();
		attachedFile.set_mime_type("image/jpg");
		attachedFile.set_file_name(ci.getFilename());
		attachedFile.set_capture_time(ci.getCaptureDate());
		attachedFile.set_byte_array(ci.getImage());
		helper.setAttachedFile(attachedFile);

		helper.setResult(RESULT.SUCCESS.getResult());
		// 任意の文字列を設定
		helper.setDetail("distribute_camera_image : 正常終了！");

		return ret;
	}

	@Override
	public void terminate() {
		// なにもしない
	}
}
