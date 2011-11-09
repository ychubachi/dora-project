/*
 * $Id: RSNPController.java 271 2010-04-28 07:11:03Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited. FUJITSU CONFIDENTIAL.
 */
package rsnp.sample;

import org.robotservices.v02.IAsyncCallBack;
import org.robotservices.v02.exception.RSiException;
import org.robotservices.v02.profile.common.Ret_value;
import org.robotservices.v02.profile.invoker.IBasic_profile;
import org.robotservices.v02.profile.invoker.IContents_profile;
import org.robotservices.v02.profile.invoker.IMultimedia_profile;
import org.robotservices.v02.profile.invoker.InvokerProfileFactory;
import org.robotservices.v02.util.ConnectionInfo;

import com.fujitsu.rsi.helper.BasicProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * RSNP接続を管理するクラス
 *
 */
public class RSNPController {

	private static String epn = "http://192.168.77.12:8080/ServiceSample/services";
	private static String robot_id = "rx7";
	private static String password = "fd3s";

	private IAsyncCallBack callback;

	private InvokerProfileFactory factory = null;
	private IBasic_profile bp = null;
	private IContents_profile cp = null;
	private IMultimedia_profile mp = null;
	long conv_id;

	/**
	 * 指定されたパラメタでRSNPControllerオブジェクトを構築する
	 *
	 * @param callback
	 *            Contents_profileで配信される情報を受け取る、IAsyncCallBackオブジェクト
	 */
	public RSNPController(IAsyncCallBack callback) {
		this.callback = callback;
	}

	/**
	 * サービスに接続する
	 */
	public void connect() {

		ConnectionInfo connectioninfo = new ConnectionInfo();
		connectioninfo.set_endpointname(epn);

		// 下位通信接続
		try {
			// ファクトリの取得
			factory = InvokerProfileFactory.newInstance(connectioninfo);
			// 接続
			factory.connect();
		} catch (RSiException e) {
			e.printStackTrace();
			factory = null;
			return;
		}

		// RSNP通信開始
		try {
			IBasic_profile bp = factory.getBasic_profile();
			Ret_value ret = bp.open(robot_id, password);
			BasicProfileHelper helper = new BasicProfileHelper(ret);
			if (helper.getResult() == RESULT.SUCCESS.getResult()) {
				conv_id = helper.getConv_id();

				System.out.println("認証成功　conv_id:" + conv_id);

				// コンテンツプロファイル(invoker)
				cp = factory.getContents_profile();
				cp.distribute_contents(conv_id, "", -1, 10000, callback);

				// マルチメディアプロファイル（invoker）
				mp = factory.getMultimedia_profile();
				mp.start_profile(conv_id);
			}
		} catch (RSiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * サービスから切断する
	 */
	public void disconnect() {

		try {
			if (cp != null) {
				cp.stop_distribute_contents(conv_id, 0);
				cp = null;
			}
			if (mp != null) {
				mp.end_profile(conv_id);
			}
			if (bp != null) {
				bp.close(conv_id);
				bp = null;
			}
		} catch (RSiException e) {
			e.printStackTrace();
		} finally {
			cp = null;
			bp = null;
		}

		try {
			if (factory != null) {
				// 切断
				factory.disconnect();
			}
		} catch (RSiException e) {
			e.printStackTrace();
		} finally {
			factory = null;
		}

	}

	/**
	 * 接続状態を返す
	 *
	 * @return true:接続 false:切断
	 */
	public boolean isConnected() {

		if (factory != null) {
			return factory.isConnected();
		}
		return false;
	}

}
