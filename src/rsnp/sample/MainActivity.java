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
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fujitsu.rsi.helper.ContentsProfileHelper;

/**
 * ãƒ¡ã‚¤ãƒ³ã‚¢ã‚¯ãƒ†ã‚£ãƒ“ãƒ†ã‚£ã‚¯ãƒ©ã‚¹
 *
 */
public class MainActivity extends Activity {
	private static final MainActivity instance = new MainActivity();
	public static MainActivity getInstance(){
    	return instance;
	}
//	private class HandlePictureStorage implements PictureCallback
//    {
//			/*private CameraSurfaceView cameraView; 
//			public HandlePictureStorage(CameraSurfaceView cameraView) {
//				this.cameraView = cameraView;
//			}*/
//            @Override
//            public void onPictureTaken(byte[] picture, Camera camera) 
//            {                    
//                    System.out.println("Picture successfully taken: "+picture);
//                    
//                    //String fileName = "shareme.jpg";
//                    //String mime = "image/jpeg";
//                    
//                    MainActivity.this.cameraView.saveImage(picture);
//            }
//    }
	private CameraSurfaceView cameraView;
	
	public CameraSurfaceView getCameraView(){
		return cameraView;
	}
	/** è�½æ›¸ã��ãƒ“ãƒ¥ãƒ¼ */
	//private SurfaceScribeView scribeView;
	/** RSNPé€šä¿¡ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ */
	private RSNPController rsnp;

	/** TTS */
	private TextToSpeech tts;
	/** TTSåˆ�æœŸåŒ–ãƒªã‚¹ãƒŠ */
	private OnInitListener ttsInitListener = new OnInitListener() {

		/*
		 * (é�ž Javadoc)
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

	/** Contents_profileã�®ã‚³ãƒ¼ãƒ«ãƒ�ãƒƒã‚¯ */
	private IAsyncCallBack callback = new IAsyncCallBack() {

		/*
		 * (é�ž Javadoc)
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
		 * (é�ž Javadoc)
		 *
		 * @see
		 * org.robotservices.v02.IAsyncCallBack#doException(java.lang.Exception)
		 */
		@Override
		public void doException(Exception arg0) {
			// ã�ªã�«ã‚‚ã�—ã�ªã�„
		}
	};

	/**
	 * ãƒ‘ãƒ©ãƒ¡ã‚¿ã�§æŒ‡å®šã�•ã‚Œã�Ÿæ–‡å­—åˆ—ã‚’ç”»é�¢ã�«è¡¨ç¤ºã�—ã€�èª­ã�¿ä¸Šã�’ã‚‹(è‹±èªž)
	 *
	 * @param message
	 *            è¡¨ç¤ºã�™ã‚‹æ–‡å­—åˆ—
	 */
	private void displayAndSpeech(final String message) {

		// ãƒ“ãƒ¥ãƒ¼ã�«è¨­å®š
		final TextView txtView = (TextView) findViewById(R.id.textView);
		txtView.post(new Runnable() {
			@Override
			public void run() {
				txtView.setText(message);
			}
		});

		// èª­ã�¿ä¸Šã�’ã‚‹
		tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
		System.out.println(message);
	}

	/*
	 * (é�ž Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		instance = this;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		tts = new TextToSpeech(getApplicationContext(), ttsInitListener);
		rsnp = new RSNPController(callback);
		
		cameraView = new CameraSurfaceView(this);
//		Button takeAPicture = (Button) findViewById(R.id.take_picture_button);
//        if (takeAPicture==null) System.err.println("NULLLLLLL");
//        takeAPicture.setOnClickListener(new OnClickListener() 
//        {
//                public void onClick(View v) 
//                {
//                        Camera camera = cameraView.getCamera();
//                        CameraSurfaceView.HandlePictureStorage c = cameraView.new HandlePictureStorage();
//                        camera.takePicture(null, null, c);
//                }
//        });
		//scribeView = new SurfaceScribeView(this);
		// ObjectHolderã�«ç™»éŒ²
		//ObjectHolder.getInstance().add(ImageProvidor.class.getName(), scribeView);
		ObjectHolder.getInstance().add(ImageProvidor.class.getName(), cameraView);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMain);
		//layout.addView(scribeView);
		layout.addView(cameraView);
	}

	/*
	 * (é�ž Javadoc)
	 *
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		// RSNPåˆ‡æ–­
		rsnp.disconnect();
		// TTSçµ‚äº†
		tts.shutdown();

		super.onDestroy();
	}

	/*
	 * (é�ž Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// ãƒ¡ãƒ‹ãƒ¥ãƒ¼ã�®ç”Ÿæˆ�
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/*
	 * (é�ž Javadoc)
	 *
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// æŽ¥ç¶šçŠ¶æ…‹ã�«ã‚ˆã‚Šãƒ¡ãƒ‹ãƒ¥ãƒ¼ã�®æœ‰åŠ¹ç„¡åŠ¹ã‚’åˆ‡ã‚Šæ›¿ã�ˆã‚‹
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
	 * (é�ž Javadoc)
	 *
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.connect:
			rsnp.connect();
			//scribeView.setBackColor(Color.WHITE);
			break;
		case R.id.disconnect:
			rsnp.disconnect();
			//scribeView.setBackColor(Color.GRAY);
			break;
		case R.id.clear:
			//scribeView.clearStrokes();
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
}