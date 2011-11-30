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
 * RSNPæŽ¥ç¶šã‚’ç®¡ç�†ã�™ã‚‹ã‚¯ãƒ©ã‚¹
 *
 */
public class RSNPController {

	//private static String epn = "http://192.168.251.1:8080/ServiceSample/services";
	private static String epn = "http://192.168.251.1:8080/ServiceSample/services";
	private static String robot_id = "rx7";
	private static String password = "fd3s";

	private IAsyncCallBack callback;

	private InvokerProfileFactory factory = null;
	private IBasic_profile bp = null;
	private IContents_profile cp = null;
	private IMultimedia_profile mp = null;
	long conv_id;

	/**
	 * æŒ‡å®šã�•ã‚Œã�Ÿãƒ‘ãƒ©ãƒ¡ã‚¿ã�§RSNPControllerã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆã‚’æ§‹ç¯‰ã�™ã‚‹
	 *
	 * @param callback
	 *            Contents_profileã�§é…�ä¿¡ã�•ã‚Œã‚‹æƒ…å ±ã‚’å�—ã�‘å�–ã‚‹ã€�IAsyncCallBackã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ
	 */
	public RSNPController(IAsyncCallBack callback) {
		this.callback = callback;
	}

	/**
	 * ã‚µãƒ¼ãƒ“ã‚¹ã�«æŽ¥ç¶šã�™ã‚‹
	 */
	public void connect() {

		ConnectionInfo connectioninfo = new ConnectionInfo();
		connectioninfo.set_endpointname(epn);

		// ä¸‹ä½�é€šä¿¡æŽ¥ç¶š
		try {
			// ãƒ•ã‚¡ã‚¯ãƒˆãƒªã�®å�–å¾—
			factory = InvokerProfileFactory.newInstance(connectioninfo);
			// æŽ¥ç¶š
			factory.connect();
		} catch (RSiException e) {
			e.printStackTrace();
			factory = null;
			return;
		}

		// RSNPé€šä¿¡é–‹å§‹
		try {
			IBasic_profile bp = factory.getBasic_profile();
			Ret_value ret = bp.open(robot_id, password);
			BasicProfileHelper helper = new BasicProfileHelper(ret);
			if (helper.getResult() == RESULT.SUCCESS.getResult()) {
				conv_id = helper.getConv_id();

				System.out.println("èª�è¨¼æˆ�åŠŸã€€conv_id:" + conv_id);

				// ã‚³ãƒ³ãƒ†ãƒ³ãƒ„ãƒ—ãƒ­ãƒ•ã‚¡ã‚¤ãƒ«(invoker)
				cp = factory.getContents_profile();
				cp.distribute_contents(conv_id, "", -1, 10000, callback);

				// ãƒžãƒ«ãƒ�ãƒ¡ãƒ‡ã‚£ã‚¢ãƒ—ãƒ­ãƒ•ã‚¡ã‚¤ãƒ«ï¼ˆinvokerï¼‰
				mp = factory.getMultimedia_profile();
				mp.start_profile(conv_id);
			}
		} catch (RSiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ã‚µãƒ¼ãƒ“ã‚¹ã�‹ã‚‰åˆ‡æ–­ã�™ã‚‹
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
				// åˆ‡æ–­
				factory.disconnect();
			}
		} catch (RSiException e) {
			e.printStackTrace();
		} finally {
			factory = null;
		}

	}

	/**
	 * æŽ¥ç¶šçŠ¶æ…‹ã‚’è¿”ã�™
	 *
	 * @return true:æŽ¥ç¶š false:åˆ‡æ–­
	 */
	public boolean isConnected() {

		if (factory != null) {
			return factory.isConnected();
		}
		return false;
	}

}
