/*
 * $Id: Multimedia_profile_impl.java 319 2010-05-17 10:14:38Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.multi;

import java.util.logging.Logger;

import org.robotservices.v02.exception.RSiException;
import org.robotservices.v02.profile.acceptor.IDistributionTool;
import org.robotservices.v02.profile.common.Ret_value;

import com.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase;
import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * Multimedia_profileのacceptor実装クラス<br>
 * MultimediaProfileBaseを継承しているため、不要なメソッドを実装する必要がない
 */
public class Multimedia_profile_impl extends MultimediaProfileBase {

	private static Logger log = Logger.getLogger(Multimedia_profile_impl.class
			.getName());

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * com.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase#camera_control
	 * (long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Ret_value camera_control(long conv_id, String id, String command,
			String options) {

		log.info("camera_control");

		Ret_value retVal = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(retVal);

		// commandに従ってカメラのパン・チルトを制御する
		System.out.println("カメラ操作指示：" + command);

		helper.setResult(RESULT.SUCCESS.getResult());
		// 任意の文字列を設定
		helper.setDetail("camera_control : 正常終了！");

		return helper.get_Ret_value();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @seecom.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase#
	 * distribute_camera_image(long, java.lang.String, int, java.lang.String,
	 * org.robotservices.v02.profile.acceptor.IDistributionTool)
	 */
	@Override
	public Ret_value distribute_camera_image(long conv_id, final String id,
			final int span, final String options, final IDistributionTool tools) {

		log.info("distribute_camara_image");

		// 配信ツールから配信IDを取り出して表示
		long dist_id = tools.getDistributionId();
		log.info("distribution id: " + dist_id);

		// toolを使って配信を開始する
		tools.setDistributor(new Distributor_impl());
		try {
			tools.startDistribution();
		} catch (RSiException e) {
			e.printStackTrace();
		}

		// 配信依頼結果を返す
		Ret_value retVal = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(retVal);

		helper.setResult(RESULT.SUCCESS.getResult());
		// 任意の文字列を設定
		helper.setDetail("distribute_camera_image : 依頼正常終了！");

		return retVal;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @seecom.fujitsu.rsi.client.acceptor.base.MultimediaProfileBase#
	 * stop_distribute_camera_image(long, long,
	 * org.robotservices.v02.profile.acceptor.IDistributionTool)
	 */
	@Override
	public Ret_value stop_distribute_camera_image(long conv_id,
			long distribution_id, IDistributionTool tools) {

		log.info("stop_distribute_camara_image");

		// 配信を停止する。
		tools.stopDistribute();

		Ret_value retVal = new Ret_value();
		MultimediaProfileHelper helper = new MultimediaProfileHelper(retVal);

		helper.setResult(RESULT.SUCCESS.getResult());
		// 任意の文字列を設定
		helper.setDetail("stop_distribute_camera_image : 正常終了！");

		return helper.get_Ret_value();
	}

}
