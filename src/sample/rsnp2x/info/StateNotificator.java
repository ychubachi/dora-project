/*
 * $Id: StateNotificator.java 117 2010-04-12 09:27:58Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.info;

import java.util.Timer;
import java.util.TimerTask;

import org.robotservices.v02.exception.RSiException;
import org.robotservices.v02.profile.invoker.IInformation_profile;

public class StateNotificator extends TimerTask {

	private long conv_id;
	private IInformation_profile inp;
	private Timer timer;
	private StateParam statusParams;

	/**
	 * コンストラクタ。
	 *
	 * @param conv_id
	 * @param inp
	 */
	public StateNotificator(long conv_id, IInformation_profile inp) {
		this.conv_id = conv_id;
		this.inp = inp;

		timer = new Timer();
		timer.schedule(this, 0, 10000);
	}

	/**
	 * 状態通知を停止します。
	 */
	public void stopNotify() {
		inp = null;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 状態を設定します。
	 *
	 * @param params
	 */
	public void setStatus(StateParam params) {
		synchronized (this) {
			statusParams = params;
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		if (statusParams != null && inp != null) {
			synchronized (this) {
				// XML作成
				StringBuilder buff = new StringBuilder();
				buff.append("<mimamori><status>");
				buff.append("<account_id>" + statusParams.getAccountID()
						+ "</account_id>");
				buff.append("<user_account>" + statusParams.getLoginUser()
						+ "</user_account>");
				buff.append("<camera_state>" + statusParams.getCameraStatus()
						+ "</camera_state>");
				buff.append("<sipcl_state>" + statusParams.getSipClientStatus()
						+ "</sipcl_state>");
				buff.append("<latitude>21.039311</latitude>"
						+ "<longitude>105.83415</longitude>"
						+ "<location>Hanoi</location>"
						+ "<comment>VNU-AIIT joint PBL</comment>");
				buff.append("</status></mimamori>");

				try {
					inp.notify_state(conv_id, buff.toString(), null);
				} catch (RSiException e) {
					e.printStackTrace();
					// 通知を停止する
					stopNotify();
				}
			}
		}
	}
}
