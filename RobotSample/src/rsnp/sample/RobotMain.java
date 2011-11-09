/*
 * $Id: RobotMain.java 325 2010-05-19 01:53:12Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package rsnp.sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.robotservices.v02.IAsyncCallBack;
import org.robotservices.v02.exception.RSiException;
import org.robotservices.v02.profile.common.Ret_value;
import org.robotservices.v02.profile.invoker.IBasic_profile;
import org.robotservices.v02.profile.invoker.IContents_profile;
import org.robotservices.v02.profile.invoker.IMotion_profile;
import org.robotservices.v02.profile.invoker.IMultimedia_profile;
import org.robotservices.v02.profile.invoker.InvokerProfileFactory;
import org.robotservices.v02.util.ConnectionInfo;

import com.fujitsu.rsi.helper.BasicProfileHelper;
import com.fujitsu.rsi.helper.ContentsProfileHelper;
import com.fujitsu.rsi.util.RESULT;

public class RobotMain {

	private static String epn = "http://localhost:8080/ServiceSample/services";
	private static String robot_id = "levin";
	private static String password = "ae86";
	private static int port = 9001;

	public static String getRobotId() {
		return robot_id;
	}

	public static void main(String[] args) throws IOException {

		if (args.length >= 3) {
			robot_id = args[0];
			password = args[1];
			port = Integer.parseInt(args[2]);
		}

		ServerSocket serversock = new ServerSocket(port);
		Socket sock = null;

		ConnectionInfo connectioninfo = new ConnectionInfo();
		connectioninfo.set_endpointname(epn);
//		connectioninfo.set_proxy("localhost:8000");
		InvokerProfileFactory factory = null;
		try {
			// ファクトリの取得
			factory = InvokerProfileFactory.newInstance(connectioninfo);
			// 接続
			factory.connect();

			// ここでRSNP通信処理をする
			IBasic_profile bp = factory.getBasic_profile();
			Ret_value ret = bp.open(robot_id, password);
			BasicProfileHelper helper = new BasicProfileHelper(ret);
			if (helper.getResult() == RESULT.SUCCESS.getResult()) {
				long conv_id = helper.getConv_id();

				System.out.println("認証成功　conv_id:" + conv_id);

				// コンテンツプロファイル(invoker)
				IContents_profile cp = factory.getContents_profile();
				IAsyncCallBack callback = new IAsyncCallBack() {
					@Override
					public void doEvent(Ret_value ret, boolean isLast) {
						ContentsProfileHelper helper = new ContentsProfileHelper(
								ret);
						String message = helper.getDetail();
						System.out.println(message);
					}

					@Override
					public void doException(Exception e) {
					}
				};
				cp.distribute_contents(conv_id, "", -1, 0, callback);

				// マルチメディアプロファイル（invoker）
				IMultimedia_profile mp = factory.getMultimedia_profile();
				mp.start_profile(conv_id);
				
				// モーションプロファイル（invoker）
				IMotion_profile mop = factory.getMotion_profile();
				mop.start_profile(conv_id);

				// Socketに接続があるまで停止
				System.out.println("localhost の" + port + "番ポートに接続すると終了します。");
				sock = serversock.accept();

				// 後始末
				mp.end_profile(conv_id);
				mop.end_profile(conv_id);
				cp.stop_distribute_contents(conv_id, 0);
				bp.close(conv_id);
			} else {
				System.out.println("認証失敗　" + helper.getDetail());
			}

		} catch (RSiException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (factory != null) {
				try {
					// 切断
					factory.disconnect();
				} catch (RSiException e) {
					e.printStackTrace();
				}
			}
			if (sock != null) {
				sock.close();
			}
			serversock.close();

		}
	}
}
