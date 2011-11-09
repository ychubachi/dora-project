/*
 * $Id: SurfaceScribeView.java 212 2010-04-26 07:48:33Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited. FUJITSU CONFIDENTIAL.
 */
package rsnp.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * 落書きビュークラス
 *
 */
public class SurfaceScribeView extends SurfaceView implements ImageProvidor {

	/** タッチの軌跡を格納するリスト */
	private ArrayList<ArrayList<Point>> strokes = new ArrayList<ArrayList<Point>>();
	/** 背景色 */
	private int backColor = Color.GRAY;

	/** 描画排他用ロック */
	private ReentrantLock lock = new ReentrantLock();
	/** オフスクリーン用ビットマップ */
	private Bitmap osb;
	/** オフスクリーン用キャンバス */
	private Canvas osc;
	/** SurfaceView用コールバック */
	private Callback surfcb = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			System.out.println("surfaceChanged");
			osb = null;
			osc = null;
			doDraw();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			doDraw();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};

	/**
	 * 指定したパラメタでScribeViewオブジェクトを構築する
	 *
	 * @param context
	 *            アプリケーションコンテキスト
	 */
	public SurfaceScribeView(Context context) {
		super(context);

		setFocusable(true);
		initStrokes();

		SurfaceHolder holder = getHolder();
		holder.addCallback(surfcb);
	}

	/**
	 * 描画ストロークを初期化する
	 */
	private void initStrokes() {

		strokes.clear();
		strokes.add(new ArrayList<Point>());
	}

	/**
	 * 描画ストロークをクリアする
	 */
	public void clearStrokes() {

		initStrokes();
		doDraw();
	}

	/**
	 * 背景色を設定する
	 *
	 * @param color
	 *            背景色
	 */
	public void setBackColor(int color) {

		backColor = color;
		doDraw();
	}

	/**
	 * 描画する
	 */
	private void doDraw() {

		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			if (osc == null) {
				// オフスクリーン生成
				int width = getWidth();
				int height = getHeight();
				osb = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				osc = new Canvas(osb);
			}

			// 描画するビットマップの競合制御
			lock.lock();
			try {
				// オフスクリーンに描画
				doRender(osc);
			} finally {
				lock.unlock();
			}

			// 画面へ描画
			canvas.drawBitmap(osb, 0, 0, null);
			getHolder().unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * パラメタで指定されたキャンバスに描画する。
	 *
	 * @param canvas
	 *            描画するキャンバス
	 */
	private void doRender(Canvas canvas) {

		canvas.drawColor(backColor);
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		for (ArrayList<Point> stroke : strokes) {
			Point p = null;
			for (Point c : stroke) {
				if (p != null) {
					canvas.drawLine(p.x, p.y, c.x, c.y, paint);
				}
				p = c;
			}
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();

		ArrayList<Point> stroke = strokes.get(strokes.size() - 1);
		stroke.add(new Point(x, y));
		if (event.getAction() == MotionEvent.ACTION_UP) {
			strokes.add(new ArrayList<Point>());
		}
		doDraw();
		return true;
	}

	/**
	 * 画像データを指定された形式のバイト配列で取得する （PNG/JPEGのみ）
	 *
	 * @param type
	 *            画像フォーマット
	 * @return 画像のバイト配列
	 */
	@Override
	public byte[] getImage(String type) {

		CompressFormat format = CompressFormat.JPEG;
		if (type.equals("PNG")) {
			format = CompressFormat.PNG;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		lock.lock();
		try {
			osb.compress(format, 100, out);
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}

		byte[] bytes = out.toByteArray();
		return bytes;
	}
}
