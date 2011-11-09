/*
 * $Id: MainActivity.java 419 2010-09-16 08:33:33Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited. FUJITSU CONFIDENTIAL.
 */
package rsnp.sample;

import java.util.Locale;

import org.robotservices.v02.IAsyncCallBack;
import org.robotservices.v02.profile.common.Ret_value;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fujitsu.rsi.helper.ContentsProfileHelper;

/**
 * メインアクティビティクラス
 *
 */
public class MainActivity extends Activity {

	/** 落書きビュー */
	private SurfaceScribeView scribeView;
	/** RSNP通信オブジェクト */
	private RSNPController rsnp;

	/** TTS */
	private TextToSpeech tts;
	/** TTS初期化リスナ */
	private OnInitListener ttsInitListener = new OnInitListener() {

		/*
		 * (非 Javadoc)
		 *
		 * @see android.speech.tts.TextToSpeech.OnInitListener#onInit(int)
		 */
		@Override
		public void onInit(int status) {

			if (status == TextToSpeech.SUCCESS) {

				tts.setLanguage(Locale.ENGLISH);
				displayAndSpeech("I'm ready!");
			} else {
				System.out.println("TextToSpeech initialize error!!");
			}
		}
	};

	/** Contents_profileのコールバック */
	private IAsyncCallBack callback = new IAsyncCallBack() {

		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * org.robotservices.v02.IAsyncCallBack#doEvent(org.robotservices.v02
		 * .profile .common.Ret_value, boolean)
		 */
		@Override
		public void doEvent(Ret_value ret, boolean isLast) {

			ContentsProfileHelper helper = new ContentsProfileHelper(ret);
			String message = helper.getDetail();

			displayAndSpeech(message);
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * org.robotservices.v02.IAsyncCallBack#doException(java.lang.Exception)
		 */
		@Override
		public void doException(Exception arg0) {
			// なにもしない
		}
	};

	/**
	 * パラメタで指定された文字列を画面に表示し、読み上げる(英語)
	 *
	 * @param message
	 *            表示する文字列
	 */
	private void displayAndSpeech(final String message) {

		// ビューに設定
		final TextView txtView = (TextView) findViewById(R.id.textView);
		txtView.post(new Runnable() {
			@Override
			public void run() {
				txtView.setText(message);
			}
		});

		// 読み上げる
		tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
		System.out.println(message);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		tts = new TextToSpeech(getApplicationContext(), ttsInitListener);
		rsnp = new RSNPController(callback);

		scribeView = new SurfaceScribeView(this);
		// ObjectHolderに登録
		ObjectHolder.getInstance().add(ImageProvidor.class.getName(), scribeView);

		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMain);
		layout.addView(scribeView);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		// RSNP切断
		rsnp.disconnect();
		// TTS終了
		tts.shutdown();

		super.onDestroy();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// メニューの生成
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// 接続状態によりメニューの有効無効を切り替える
		boolean connected = rsnp.isConnected();
		if (menu != null) {
			MenuItem menuConnect = (MenuItem) menu.findItem(R.id.connect);
			menuConnect.setEnabled(!connected);

			MenuItem menuDisconnect = (MenuItem) menu.findItem(R.id.disconnect);
			menuDisconnect.setEnabled(connected);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.connect:
			rsnp.connect();
			scribeView.setBackColor(Color.WHITE);
			break;
		case R.id.disconnect:
			rsnp.disconnect();
			scribeView.setBackColor(Color.GRAY);
			break;
		case R.id.clear:
			scribeView.clearStrokes();
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
}