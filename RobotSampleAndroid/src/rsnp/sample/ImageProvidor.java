/*
 * $Id: ImageProvidor.java 185 2010-04-22 05:38:47Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package rsnp.sample;

/**
 * 画像を取得するインターフェース
 *
 */
public interface ImageProvidor {

	/**
	 * 指定された形式の画像バイト配列を取得する
	 *
	 * @param type
	 *            画像形式
	 * @return 画像のバイト配列
	 */
	public byte[] getImage(String type);
}
