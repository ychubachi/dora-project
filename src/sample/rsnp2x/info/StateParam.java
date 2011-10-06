/*
 * $Id: StateParam.java 117 2010-04-12 09:27:58Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.info;

/**
 * 状態パラメータクラス<br>
 * 状態パラメータを管理するクラス
 */
public class StateParam {

	/** 利用可 */
	public static int CAMERA_STATE_AVAILABLE = 0;
	/** 許可されていない */
	public static int CAMERA_STATE_DENY = 1;
	/** 利用できない状態（電源OFFなど） */
	public static int CAMERA_STATE_NOTAVAILABLE = 9;

	/** 利用可 */
	public static int SIP_STATE_AVAILABLE = 0;
	/** 許可されていない */
	public static int SIP_STATE_DENY = 1;
	/** 使用中 */
	public static int SIP_STATE_OCCUPIED = 2;
	/** 利用できない状態（電源OFFなど） */
	public static int SIP_STATE_NOTAVAILABLE = 9;

	/** アカウントID */
	private String accountID = null;

	/** ログインユーザ */
	private String loginUser = "";

	/** カメラ状態 */
	private int cameraStatus = CAMERA_STATE_NOTAVAILABLE;

	/** SIPクライアント状態 */
	private int sipClientStatus = SIP_STATE_NOTAVAILABLE;

	/**
	 * アカウントID取得<br>
	 * アカウントIDを取得
	 *
	 * @return アカウントID
	 */
	public String getAccountID() {
		return accountID;
	}

	/**
	 * アカウントID設定<br>
	 * アカウントIDを設定
	 *
	 * @param accountID
	 *            　アカウントID
	 */
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	/**
	 * カメラ状態取得<br>
	 * カメラ状態を取得
	 *
	 * @return カメラ状態
	 */
	public int getCameraStatus() {
		return cameraStatus;
	}

	/**
	 * カメラ状態設定<br>
	 * カメラ状態を設定
	 *
	 * @param cameraStatus
	 *            　カメラ状態
	 */
	public void setCameraStatus(int cameraStatus) {
		this.cameraStatus = cameraStatus;
	}

	/**
	 * ログインユーザ取得<br>
	 * ログインユーザを取得
	 *
	 * @return ログインユーザ
	 */
	public String getLoginUser() {
		return loginUser;
	}

	/**
	 * ログインユーザ設定<br>
	 * ログインユーザを設定
	 *
	 * @param loginUser
	 *            　ログインユーザ
	 */
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	/**
	 * SIPクライアント状態取得<br>
	 * SIPクライアント状態を取得
	 *
	 * @return SIPクライアント状態
	 */
	public int getSipClientStatus() {
		return sipClientStatus;
	}

	/**
	 * SIPクライアント状態設定<br>
	 * SIPクライアント状態を設定
	 *
	 * @param sipClientStatus
	 *            　SIPクライアント状態
	 */
	public void setSipClientStatus(int sipClientStatus) {
		this.sipClientStatus = sipClientStatus;
	}

	/**
	 * 文字列変換<br>
	 * 文字列へ変換
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(64);
		buf.append(accountID);
		buf.append(" ");
		buf.append(loginUser);
		buf.append(" ");
		buf.append(cameraStatus);
		buf.append(" ");
		buf.append(sipClientStatus);
		return buf.toString();
	}

}
