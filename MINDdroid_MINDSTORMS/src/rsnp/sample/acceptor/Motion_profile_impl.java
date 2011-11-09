package rsnp.sample.acceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.robotservices.v02.profile.common.Ret_value;
//import blueTooth.BTSend;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.fujitsu.rsi.client.acceptor.base.MotionProfileBase;
import com.lego.minddroid.BTCommunicator;
import com.lego.minddroid.BTConnectable;
import com.lego.minddroid.LCPMessage;
import com.lego.minddroid.R;

import rsnp.sample.SplashMenu;

public class Motion_profile_impl extends MotionProfileBase implements BTConnectable{
	//NXTã�¨ã�®BluetoothæŽ¥ç¶šã‚’è¡Œã�†
	//MotionControl mc;
	private BTCommunicator myBTCommunicator = null;
	private Handler btcHandler;
	//private boolean pairing;
	int motorLeft;
    private int directionLeft; // +/- 1
    int motorRight;
    private boolean stopAlreadySent = false;
    private int directionRight; // +/- 1
    private int motorAction;
    private int directionAction; // +/- 1
    private boolean connected = false;
    private boolean btErrorPending = false;
    int mRobotType;
    private String programToStart;
    private List<String> programList;
    private static final int MAX_PROGRAMS = 20;
	
	public Motion_profile_impl() {
		System.err.println("Motion_profile_impl constructor");
		 //mc = new MotionControl(this, BluetoothAdapter.getDefaultAdapter());
	     //btcHandler = mc.getHandler();
		setUpByType();
		startBTCommunicator(SplashMenu.getNXTAddress());
	}

    /**
     * Initialization of the motor commands for "leJOS NXJ model" robot types.
     */
    private void setUpByType() {
        switch (mRobotType) {
            case R.id.robot_type_2:
System.out.println("type2");            	
                motorLeft = BTCommunicator.MOTOR_B;
                directionLeft = 1;
                motorRight = BTCommunicator.MOTOR_C;
                directionRight = 1;
                motorAction = BTCommunicator.MOTOR_A;
                directionAction = 1;
                break;
            case R.id.robot_type_3:
            	System.out.println("type3");
                motorLeft = BTCommunicator.MOTOR_C;
                directionLeft = -1;
                motorRight = BTCommunicator.MOTOR_B;
                directionRight = -1;
                motorAction = BTCommunicator.MOTOR_A;
                directionAction = 1;
                break;
            default:
                // default
            	System.out.println("type default");
                motorLeft = BTCommunicator.MOTOR_B;
                directionLeft = 1;
                motorRight = BTCommunicator.MOTOR_C;
                directionRight = 1;
                motorAction = BTCommunicator.MOTOR_A;
                directionAction = 1;
                break;
        }
    }
    /**
     * @return true, when currently pairing 
     */
    public boolean isPairing() {
        return SplashMenu.isPairing();
    }
    /**
     * Sends the message via the BTCommuncator to the robot.
     * @param delay time to wait before sending the message.
     * @param message the message type (as defined in BTCommucator)
     * @param value1 first parameter
     * @param value2 second parameter
     */   
    void sendBTCmessage(int delay, int message, int value1, int value2) {
        Bundle myBundle = new Bundle();
        myBundle.putInt("message", message);
        myBundle.putInt("value1", value1);
        myBundle.putInt("value2", value2);
        Message myMessage = myHandler.obtainMessage();
        myMessage.setData(myBundle);

        if (delay == 0)
            btcHandler.sendMessage(myMessage);

        else
            btcHandler.sendMessageDelayed(myMessage, delay);
    }

    /**
     * Sends the message via the BTCommuncator to the robot.
     * @param delay time to wait before sending the message.
     * @param message the message type (as defined in BTCommucator)
     * @param String a String parameter
     */       
    void sendBTCmessage(int delay, int message, String name) {
        Bundle myBundle = new Bundle();
        myBundle.putInt("message", message);
        myBundle.putString("name", name);
        Message myMessage = myHandler.obtainMessage();
        myMessage.setData(myBundle);

        if (delay == 0)
            btcHandler.sendMessage(myMessage);
        else
            btcHandler.sendMessageDelayed(myMessage, delay);
    }
    
    /**
     * Creates a new object for communication to the NXT robot via bluetooth and fetches the corresponding handler.
     */
    private void createBTCommunicator() {
    	System.out.println("createBTCommunicator: ");
        // interestingly BT adapter needs to be obtained by the UI thread - so we pass it in in the constructor
        //myBTCommunicator = new BTCommunicator(this, myHandler, BluetoothAdapter.getDefaultAdapter(), getResources());
    	//Resources r = getResources();  //haven't figured out how to get resources from here
    	myBTCommunicator = new BTCommunicator(this, myHandler, BluetoothAdapter.getDefaultAdapter());//, r);
        btcHandler = myBTCommunicator.getHandler();
        
    }

    /**
     * Creates and starts the a thread for communication via bluetooth to the NXT robot.
     * @param mac_address The MAC address of the NXT robot.
     */
    private void startBTCommunicator(String mac_address) {
    	System.out.println("startBTCommunicator: NXT addr: _"+mac_address + "_");
        connected = false;        
        //connectingProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.connecting_please_wait), true);

        if (myBTCommunicator != null) {
            try {
                myBTCommunicator.destroyNXTconnection();
            }
            catch (IOException e) { }
        }
        createBTCommunicator();
        myBTCommunicator.setMACAddress(mac_address);
        myBTCommunicator.start();
        //updateButtonsAndMenu();
    }
    
    /**
     * Sends a message for disconnecting to the communcation thread.
     */
    public void destroyBTCommunicator() {

        if (myBTCommunicator != null) {
            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.DISCONNECT, 0, 0);
            myBTCommunicator = null;
        }

        connected = false;
        //updateButtonsAndMenu();
    }
    
    /**
     * Sends the motor control values to the communcation thread.
     * @param left The power of the left motor from 0 to 100.
     * @param rigth The power of the right motor from 0 to 100.
     */   
    public void updateMotorControl(int left, int right) {

        if (myBTCommunicator != null) {
            // don't send motor stop twice
            if ((left == 0) && (right == 0)) {
                if (stopAlreadySent)
                    return;
                else
                    stopAlreadySent = true;
            }
            else
                stopAlreadySent = false;         
                        
            // send messages via the handler
            sendBTCmessage(BTCommunicator.NO_DELAY, motorLeft, left * directionLeft, 0);
            sendBTCmessage(BTCommunicator.NO_DELAY, motorRight, right * directionRight, 0);
        }
    }
    /**
     * Starts a program on the NXT robot.
     * @param name The program name to start. Has to end with .rxe on the LEGO firmware and with .nxj on the 
     *             leJOS NXJ firmware.
     */   
    public void startProgram(String name) {
        // for .rxe programs: get program name, eventually stop this and start the new one delayed
        // is handled in startRXEprogram()
        if (name.endsWith(".rxe")) {
            programToStart = name;        
            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.GET_PROGRAM_NAME, 0, 0);
            return;
        }
              
        // for .nxj programs: stop bluetooth communication after starting the program
        if (name.endsWith(".nxj")) {
            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.START_PROGRAM, name);
            destroyBTCommunicator();
            return;
        }        

        // for all other programs: just start the program
        sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.START_PROGRAM, name);
    }

    /**
     * Depending on the status (whether the program runs already) we stop it, wait and restart it again.
     * @param status The current status, 0x00 means that the program is already running.
     */   
    public void startRXEprogram(byte status) {
        if (status == 0x00) {
            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.STOP_PROGRAM, 0, 0);
            sendBTCmessage(1000, BTCommunicator.START_PROGRAM, programToStart);
        }    
        else {
            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.START_PROGRAM, programToStart);
        }
    }        

    /**
     * Displays a message as a toast
     * @param textToShow the message
     * @param length the length of the toast to display
     */
    private void showToast(String message, int length) {
    	System.out.println(message);
    }
    /**
     * Displays a message as a toast
     * @param resID the ressource ID to display
     * @param length the length of the toast to display
     */
    private void showToast(int resID, int length) {
    	System.out.println(resID);
        /*reusableToast.setText(resID);
        reusableToast.setDuration(length);
        reusableToast.show();*/
    }
    
    /**
     * Receive messages from the BTCommunicator
     */
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message myMessage) {
            switch (myMessage.getData().getInt("message")) {
                case BTCommunicator.DISPLAY_TOAST:
                    showToast(myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
                    break;
                case BTCommunicator.STATE_CONNECTED:
                    connected = true;
                    programList = new ArrayList<String>();
                    //connectingProgressDialog.dismiss();
                    //updateButtonsAndMenu();
                    sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.GET_FIRMWARE_VERSION, 0, 0);
                    break;
                case BTCommunicator.MOTOR_STATE:

                    if (myBTCommunicator != null) {
                        byte[] motorMessage = myBTCommunicator.getReturnMessage();
                        int position = byteToInt(motorMessage[21]) + (byteToInt(motorMessage[22]) << 8) + (byteToInt(motorMessage[23]) << 16)
                                       + (byteToInt(motorMessage[24]) << 24);
                        //showToast(getResources().getString(R.string.current_position) + position, Toast.LENGTH_SHORT);
                        showToast("Current position: " + position, Toast.LENGTH_SHORT);
                    }

                    break;

                case BTCommunicator.STATE_CONNECTERROR_PAIRING:
                    //connectingProgressDialog.dismiss();
                    destroyBTCommunicator();
                    break;

                case BTCommunicator.STATE_CONNECTERROR:
                    //connectingProgressDialog.dismiss();
                case BTCommunicator.STATE_RECEIVEERROR:
                case BTCommunicator.STATE_SENDERROR:

                    destroyBTCommunicator();
                    System.err.println("BTCommunicator.STATE_SENDERROR. Don't know how to deal with it.");
                    /*if (btErrorPending == false) {
                        btErrorPending = true;
                        // inform the user of the error with an AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setTitle(getResources().getString(R.string.bt_error_dialog_title))
                        .setMessage(getResources().getString(R.string.bt_error_dialog_message)).setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                btErrorPending = false;
                                dialog.cancel();
                                selectNXT();
                            }
                        });
                        builder.create().show();
                    }
*/
                    break;

                case BTCommunicator.FIRMWARE_VERSION:

                    if (myBTCommunicator != null) {
                        byte[] firmwareMessage = myBTCommunicator.getReturnMessage();
                        // check if we know the firmware
                        boolean isLejosMindDroid = true;
                        for (int pos=0; pos<4; pos++) {
                            if (firmwareMessage[pos + 3] != LCPMessage.FIRMWARE_VERSION_LEJOSMINDDROID[pos]) {
                                isLejosMindDroid = false;
                                break;
                            }
                        }
                        if (isLejosMindDroid) {
                            mRobotType = R.id.robot_type_4;
                            setUpByType();
                        }
                        // afterwards we search for all files on the robot
                        sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.FIND_FILES, 0, 0);
                    }

                    break;

                case BTCommunicator.FIND_FILES:

                    if (myBTCommunicator != null) {
                        byte[] fileMessage = myBTCommunicator.getReturnMessage();
                        String fileName = new String(fileMessage, 4, 20);
                        fileName = fileName.replaceAll("\0","");

                        if (mRobotType == R.id.robot_type_4 || fileName.endsWith(".nxj") || fileName.endsWith(".rxe")) {
                            programList.add(fileName);
                        }

                        // find next entry with appropriate handle, 
                        // limit number of programs (in case of error (endless loop))
                        if (programList.size() <= MAX_PROGRAMS)
                            sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.FIND_FILES,
                                           1, byteToInt(fileMessage[3]));
                    }

                    break;
                    
                case BTCommunicator.PROGRAM_NAME:
                    if (myBTCommunicator != null) {
                        byte[] returnMessage = myBTCommunicator.getReturnMessage();
                        startRXEprogram(returnMessage[2]);
                    }
                    
                    break;
                    
                case BTCommunicator.SAY_TEXT:
                    if (myBTCommunicator != null) {
                        /*byte[] textMessage = myBTCommunicator.getReturnMessage();
                        // evaluate control byte 
                        byte controlByte = textMessage[2];
                        // BIT7: Language
                        if ((controlByte & 0x80) == 0x00) 
                            mTts.setLanguage(Locale.US);
                        else
                            mTts.setLanguage(Locale.getDefault());
                        // BIT6: Pitch
                        if ((controlByte & 0x40) == 0x00)
                            mTts.setPitch(1.0f);
                        else
                            mTts.setPitch(0.75f);
                        // BIT0-3: Speech Rate    
                        switch (controlByte & 0x0f) {
                            case 0x01: 
                                mTts.setSpeechRate(1.5f);
                                break;                                 
                            case 0x02: 
                                mTts.setSpeechRate(0.75f);
                                break;
                            
                            default: mTts.setSpeechRate(1.0f);
                                break;
                        }
                                                                                                        
                        String ttsText = new String(textMessage, 3, 19);
                        ttsText = ttsText.replaceAll("\0","");
                        showToast(ttsText, Toast.LENGTH_SHORT);
                        mTts.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null); */
                    }
                    
                    break;                    
                    
                case BTCommunicator.VIBRATE_PHONE:
                    if (myBTCommunicator != null) {
                        byte[] vibrateMessage = myBTCommunicator.getReturnMessage();
                        //Vibrator myVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        //myVibrator.vibrate(vibrateMessage[2]*10);
                    }
                    
                    break;
            }
        }
    };
    private int byteToInt(byte byteValue) {
        int intValue = (byteValue & (byte) 0x7f);

        if ((byteValue & (byte) 0x80) != 0)
            intValue |= 0x80;

        return intValue;
    }


	@Override
	public Ret_value declare_control(long conv_id) {
		//System.out.println("just in the motionProfile declare");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile declare");
		return ret;
	}

	@Override
	public Ret_value release_control(long conv_id) {
		//System.out.println("just in the motionProfile release");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile release");
		//bs.closeBT();
		//mc.disconnect();
		destroyBTCommunicator();
		return ret;
	}

	@Override
	public Ret_value forward(long conv_id, double distance, String option) {
		System.out.println("just in the motionProfile forward");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile forward");
		//bs.selectFunc(8, distance);
		updateMotorControl(20,20);
		return ret;
	}

	@Override
	public Ret_value backward(long conv_id, double distance, String option) {
		System.out.println("just in the motionProfile back");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile back");
		//bs.selectFunc(2, distance);
		return ret;
	}

	@Override
	public Ret_value right(long conv_id, double radius, double degree,
			String option) {
		System.out.println("just in the motionProfile right");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile right");
		//bs.selectFunc(6, radius);
		updateMotorControl(20,0);
		return ret;
	}

	@Override
	public Ret_value left(long conv_id, double radius, double degree,
			String option) {
		System.out.println("just in the motionProfile left");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile left");
		//bs.selectFunc(4, radius);
		updateMotorControl(0,20);
		return ret;
	}

	@Override
	public Ret_value spin_right(long conv_id, double degree, String option) {
		System.out.println("just in the motionProfile spinRight");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile spinRight");
		//bs.selectFunc(6, degree);
		updateMotorControl(20,0);
		return ret;
	}

	@Override
	public Ret_value spin_left(long conv_id, double degree, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile spinLeft");
		//bs.selectFunc(4, degree);
		updateMotorControl(0,20);
		return ret;
	}

	@Override
	public Ret_value stop(long conv_id, String option) {
		System.out.println("just in the motionProfile stop");
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile stop");
		//bs.selectFunc(5, 0);
		updateMotorControl(0,0);
		return ret;
	}
}

