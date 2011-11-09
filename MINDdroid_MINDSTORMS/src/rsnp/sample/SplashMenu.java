/**
 * Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 * This file is part of MINDdroid.
 *
 * MINDdroid is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * MINDdroid is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * MINDdroid. If not, see <http://www.gnu.org/licenses/>.
 **/

package rsnp.sample;

import java.util.Locale;

import org.robotservices.v02.IAsyncCallBack;
import org.robotservices.v02.profile.common.Ret_value;
import com.fujitsu.rsi.helper.ContentsProfileHelper;

//import com.lego.minddroid.Lama;
import com.lego.minddroid.DeviceListActivity;
import com.lego.minddroid.MINDdroid;
import com.lego.minddroid.Options;
import com.lego.minddroid.R;
import com.lego.minddroid.R.drawable;
import com.lego.minddroid.R.id;
import com.lego.minddroid.R.layout;
import com.lego.minddroid.R.string;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashMenu extends Activity {

	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int REQUEST_ENABLE_BT = 2000;
	private static boolean btOnByUs = false;
	private static boolean pairing;
	private static String NXTAddress;
	private Toast reusableToast;
	private final int TTS_CHECK_CODE = 9991;
	
    public static final int MENU_OPTIONS = Menu.FIRST;
    public static final int MENU_CONNECT = Menu.FIRST + 1;
    public static final int MENU_DISCONNECT = Menu.FIRST + 2;
    public static final int MENU_QUIT = Menu.FIRST + 3;
    public static final String MINDDROID_PREFS = "Mprefs";
    public static final String MINDDROID_ROBOT_TYPE = "MrobotType";
    private int mRobotType;

	/** to test /robotcamera */
	private SurfaceScribeView scribeView;
	/** RSNP controller */
	private RSNPController rsnp;
	
	/** TTS */
	private TextToSpeech tts;
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
	/**
	 * ãƒ‘ãƒ©ãƒ¡ã‚¿ã�§æŒ‡å®šã�•ã‚Œã�Ÿæ–‡å­—åˆ—ã‚’ç”»é�¢ã�«è¡¨ç¤ºã�—ã€�èª­ã�¿ä¸Šã�’ã‚‹(è‹±èªž)
	 *
	 * @param message
	 *            è¡¨ç¤ºã�™ã‚‹æ–‡å­—åˆ—
	 */
	public void displayAndSpeech(final String message) {

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
			//ret: message sent from webGUI
			System.out.println("Event!!!");
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


    public static void quitApplication() {
        if (isBtOnByUs()) { // || NXJUploader.isBtOnByUs()) {
            BluetoothAdapter.getDefaultAdapter().disable();
            setBtOnByUs(false);
            //NXJUploader.setBtOnByUs(false);
        }
        splashMenu.finish();

    }

    private View splashMenuView;

    private static Activity splashMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // show Lama and write nxj-files to SD-card
        //Lama.show(this);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mRobotType=lookupRobotType();
        
        setContentView(R.layout.splash_menu);
        
        tts = new TextToSpeech(getApplicationContext(), ttsInitListener);
		rsnp = new RSNPController(callback, this);
		scribeView = new SurfaceScribeView(this);
		// ObjectHolderã�«ç™»éŒ²
		ObjectHolder.getInstance().add(ImageProvidor.class.getName(), scribeView);

		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMain);
		layout.addView(scribeView);
       
		//button to start control NXT from this app
        Button button;
		button = (Button) findViewById(R.id.start_button);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Intent playGame = new Intent(getBaseContext(), MINDdroid.class);
				playGame.putExtra(SplashMenu.MINDDROID_ROBOT_TYPE, getRobotType());
				startActivity(playGame);

			}
		});
        /*
        splashMenuView = new SplashMenuView(getApplicationContext(), this);
        setContentView(splashMenuView);
        splashMenu = this;*/
    }
    

    @Override
    protected void onDestroy() {
    	// RSNPåˆ‡æ–­
    	rsnp.disconnect();
    	// TTSçµ‚äº†
    	tts.shutdown();
        super.onDestroy();
    }
    
    @Override
    protected void onStart() {
        super.onStart();

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            selectNXT();
            
        }
        if (!rsnp.isConnected()) {
        	System.out.println("Connecting");
        	rsnp.connect();
        }
    }
    void selectNXT() {
        //Intent serverIntent = new Intent(this, DeviceListActivity.class);
        //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    	NXTAddress="1a:4b:d6:70:dc:32"; 
    }

    /**
     * @return true, when currently pairing 
     */
    public static boolean isPairing() {
        return pairing;
    }
    public static String getNXTAddress(){
    	return NXTAddress;
    }
    /**
     * Asks if bluetooth was switched on during the runtime of the app. For saving 
     * battery we switch it off when the app is terminated.
     * @return true, when bluetooth was switched on by the app
     */
    public static boolean isBtOnByUs() {
        return btOnByUs;
    }
    /**
     * Sets a flag when bluetooth was switched on durin runtime
     * @param btOnByUs true, when bluetooth was switched on by the app
     */
    public static void setBtOnByUs(boolean btOnByUs) {
        btOnByUs = btOnByUs;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:

                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address and start a new bt communicator thread
                    NXTAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
                    //startBTCommunicator(address);
                }
                
                break;
                
            case REQUEST_ENABLE_BT:

                // When the request to enable Bluetooth returns
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        btOnByUs = true;
                        selectNXT();
                        break;
                    case Activity.RESULT_CANCELED:
                        showToast(R.string.bt_needs_to_be_enabled, Toast.LENGTH_SHORT);
                        finish();
                        break;
                    default:
                        showToast(R.string.problem_at_connecting, Toast.LENGTH_SHORT);
                        finish();
                        break;
                }
                
                break;

            // will not be called now, since the check intent is not generated                
            case TTS_CHECK_CODE:
            	/*
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // success, create the TTS instance
                    mTts = new TextToSpeech(this, this);
                } 
                else {
                    // missing data, install it
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                */
                break;                
        }
    }
    /**
     * Displays a message as a toast
     * @param textToShow the message
     * @param length the length of the toast to display
     */
    private void showToast(String textToShow, int length) {
        reusableToast.setText(textToShow);
        reusableToast.setDuration(length);
        reusableToast.show();
    }

    /**
     * Displays a message as a toast
     * @param resID the ressource ID to display
     * @param length the length of the toast to display
     */
    private void showToast(int resID, int length) {
        reusableToast.setText(resID);
        reusableToast.setDuration(length);
        reusableToast.show();
    }

    @Override
    protected void onPause() {
        if (isBtOnByUs()) {// || NXJUploader.isBtOnByUs()) {
            BluetoothAdapter.getDefaultAdapter().disable();
            setBtOnByUs(false);
            //NXJUploader.setBtOnByUs(false);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    /**
     * Creates the menu items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_OPTIONS, 1, getResources().getString(R.string.options)).setIcon(R.drawable.ic_menu_preferences);
        menu.add(0, MENU_CONNECT, 2, getResources().getString(R.string.connect)).setIcon(R.drawable.ic_menu_nxj);
        menu.add(0, MENU_DISCONNECT, 3, getResources().getString(R.string.disconnect)).setIcon(R.drawable.ic_menu_about);
        menu.add(0, MENU_QUIT, 4, getResources().getString(R.string.quit)).setIcon(R.drawable.ic_menu_exit);
        return true;
    }

    /**
     * Enables/disables the menu items
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean rsnpConnected = rsnp.isConnected();
        boolean displayMenu = false;
        System.out.println(rsnpConnected);
        if (menu != null) {
			MenuItem menuConnect = (MenuItem) menu.findItem(MENU_CONNECT);
			menuConnect.setEnabled(!rsnpConnected);
			System.out.println("menuConnect" + menuConnect.isEnabled());
			MenuItem menuDisconnect = (MenuItem) menu.findItem(MENU_DISCONNECT);
			menuDisconnect.setEnabled(rsnpConnected);
		
			displayMenu = super.onPrepareOptionsMenu(menu);
			if (displayMenu) {
				menu.getItem(1).setEnabled(getRobotType() == R.id.robot_type_4);
			}
        }
        return displayMenu;
    }

    /**
     * Handles item selections
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_OPTIONS:
                Options options = new Options(this);
                options.show();
                return true;
            case MENU_CONNECT:
                //Intent nxjUpload = new Intent(this.getBaseContext(), NXJUploader.class);
                //this.startActivity(nxjUpload);
            	//setupBTSend();
    			rsnp.connect();
    			scribeView.setBackColor(Color.WHITE);
                return true;
            case MENU_DISCONNECT:
            	rsnp.disconnect();
    			/*if (btSend != null) {
    				Log.d(TAG, "onPause() closing btSend ");
    				btSend.closeConnection();
    				btSend = null;
    			}*/
    			scribeView.setBackColor(Color.GRAY);
                return true;
            case MENU_QUIT:
                finish();
                return true;
            default:
    			return super.onContextItemSelected(item);
        }
    }

    public void setRobotType(int mRobotType) {
        SharedPreferences mUserPreferences = getSharedPreferences(MINDDROID_PREFS, Context.MODE_PRIVATE);
        Editor mPrefsEditor = mUserPreferences.edit();
        mPrefsEditor.putInt(MINDDROID_ROBOT_TYPE, mRobotType);
        mPrefsEditor.commit();
        this.mRobotType = mRobotType;
    }

    public int lookupRobotType() {
        SharedPreferences mUserPreferences;
        mUserPreferences =  getSharedPreferences(MINDDROID_PREFS, Context.MODE_PRIVATE);
        return mUserPreferences.getInt(MINDDROID_ROBOT_TYPE, R.id.robot_type_1);
    }

    public int getRobotType() {
        return mRobotType;
    }

}
