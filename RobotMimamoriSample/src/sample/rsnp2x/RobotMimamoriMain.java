/*
 * $Id: RobotMimamoriMain.java 322 2010-05-18 07:35:39Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import org.robotservices.v02.exception.RSiException;
import org.robotservices.v02.profile.common.Ret_value;
import org.robotservices.v02.profile.invoker.IBasic_profile;
import org.robotservices.v02.profile.invoker.IInformation_profile;
import org.robotservices.v02.profile.invoker.IMultimedia_profile;
import org.robotservices.v02.profile.invoker.InvokerProfileFactory;
import org.robotservices.v02.util.ConnectionInfo;

import sample.rsnp2x.info.StateNotificator;
import sample.rsnp2x.info.StateParam;

import com.fujitsu.rsi.helper.BasicProfileHelper;
import com.fujitsu.rsi.helper.MultimediaProfileHelper;
import com.fujitsu.rsi.util.RESULT;

/**
 * 見守りサービス　サンプル<br>
 * ロボットアプリメイン
 */
public class RobotMimamoriMain {

	private static Logger log = Logger.getLogger(RobotMimamoriMain.class
			.getName());

//	private static String targetEPR = "http://rsnpcommon.aiit.ac.jp/glengrant-2.2/services";
	private static String targetEPR = "http://rsi.aiit.ac.jp/rsnpServer/services";

	// ベーシック認証用ID/PW
	private static String basic_id = "rsiuser0";
	private static String basic_pw = "servicerobot";

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			printUsage();
			return;
		}

		ServerSocket serversock = new ServerSocket(9000);
		Socket sock = null;

		String user_id = args[0];
		String password = args[1];

		// 接続情報の設定
		ConnectionInfo ci = new ConnectionInfo();
		ci.set_endpointname(targetEPR);
		ci.set_username(basic_id);
		ci.set_password(basic_pw);
		// ci.set_proxy("localhost:8000");

		InvokerProfileFactory factory = null;
		IBasic_profile bp = null;
		long conv_id = -1;
		StateNotificator sn = null;
		IMultimedia_profile mmp = null;
		try {
			factory = InvokerProfileFactory.newInstance(ci);
			// 通信経路を開設
			factory.connect();

			// Basic_profileによる認証
			bp = factory.getBasic_profile();
			Ret_value ret = bp.open(user_id, password);
			BasicProfileHelper bphlp = new BasicProfileHelper(ret);
			if (bphlp.getResult() != RESULT.SUCCESS.getResult()) {
				log.severe("Basic_profile#open()エラー[" + bphlp.getDetailCode()
						+ "]：" + bphlp.getDetail());
				bp = null;
				return;
			}

			conv_id = bphlp.getConv_id();
			log.info("conv_id:" + conv_id);

			// Information_profileでロボットの状態通知
			IInformation_profile inp = factory.getInformation_profile();
			sn = new StateNotificator(conv_id, inp);
			StateParam state = new StateParam();
			state.setAccountID(user_id);
			state.setLoginUser("robotuser");
			state.setCameraStatus(StateParam.CAMERA_STATE_AVAILABLE);
			sn.setStatus(state);

			// Multimedia_profileで画像送信
			mmp = factory.getMultimedia_profile();
			ret = mmp.start_profile(conv_id);
			MultimediaProfileHelper mphlp = new MultimediaProfileHelper(ret);
			if (mphlp.getResult() != RESULT.SUCCESS.getResult()) {
				log.severe("Multimedia_profile#start_profile()エラー["
						+ mphlp.getDetailCode() + "]：" + mphlp.getDetail());
				mmp = null;
				return;
			}

			// Socketに接続があるまで停止
			System.out.println("localhost の9000番ポートに接続すると終了します。");
			sock = serversock.accept();

			// マルチメディアプロファイル終了
			if (mmp != null) {
				mmp.end_profile(conv_id);
			}

			// 状態通知停止
			if (sn != null) {
				sn.stopNotify();
			}

			// 会話セッション終了
			if (bp != null) {
				bp.close(conv_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 通信経路を切断
				if (factory != null) {
					factory.disconnect();
				}
			} catch (RSiException e) {
				e.printStackTrace();
			}
			if (sock != null) {
				sock.close();
			}
			serversock.close();
		}

		log.info("exit");
		System.exit(0);
	}

	/**
	 * 使用方法を表示します。
	 */
	private static void printUsage() {
		System.out.println("使い方：");
		System.out
				.println("java sample.rsnp2x.RobotMimamoriMain <robotid> <password> ");
		System.out.println("	<robotid>:ロボットアカウント");
		System.out.println("	<password>:パスワード");
	}

}
