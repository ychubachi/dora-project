import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.robotics.*;

/**
 * Receive data from another NXT, a PC, a phone, or another bluetooth device.
 * 
 * Waits for a connection, receives an int and returns its negative as a reply,
 * 100 times, and then closes the connection, and waits for a new one.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class BTReceive {
	private static int  motorPort;
	private static DCMotor motor = null;
//	private static int  motorPort2;
	private static DCMotor motor2 = null;
	
	public static void main(String[] args) throws Exception {
		String connected = "Connected";
		String waiting = "Waiting...";
		String closing = "Closing...";

		while (true) {
			LCD.drawString(waiting, 0, 0);
			LCD.refresh();

			BTConnection btc = Bluetooth.waitForConnection();

			LCD.clear();
			LCD.drawString(connected, 0, 0);
			LCD.refresh();

			DataInputStream dis = btc.openDataInputStream();
			DataOutputStream dos = btc.openDataOutputStream();

			while (true) {
				int n = dis.readInt();
				LCD.drawInt(n, 0, 1);
				LCD.refresh();
				motorAction(n);
				dos.writeInt(n);
				dos.flush();
				if (n == 0){
					motorAction(1);
					break;}
			}

			dis.close();
			dos.close();
			Thread.sleep(100); // wait for data to drain
			LCD.clear();
			LCD.drawString(closing, 0, 0);
			LCD.refresh();
			btc.close();
			LCD.clear();
		}
	}

	public static void motorAction(int action) {
		// select
		// motorText = "NXT";
		motorPort = 0;// 0-a,1-b,2-c
		// portText = "MotorPort.A";
		// Direct
		MotorPort port = MotorPort.getInstance(motorPort);
		motor = new NXTMotor(port);
		MotorPort port2 = MotorPort.getInstance(1);
		motor2 = new NXTMotor(port2);

		if (motor == null)
			return;

		motor.setPower(60);
		motor2.setPower(60);
		switch (action) {
		case 8:
			motor.forward();
			motor2.forward();
			break;
		case 2:
			motor.backward();
			motor2.backward();
			break;
		case 1:
			motor.stop();
			motor2.stop();
			break;
		case 5:
			motor.flt();
			motor2.flt();
			break;
		case 4:
			motor.stop();
			motor2.forward();
			break;
		case 6:
			motor.forward();
			motor2.stop();
			break;
		case 3:
			motor.setPower(100);
			motor2.setPower(100);
			motor.forward();
			motor2.forward();
			break;
		}
	}

}