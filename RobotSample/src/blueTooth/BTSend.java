package blueTooth;

import lejos.pc.comm.*;
import java.io.*;

public class BTSend {

	static NXTConnector conn = null;
	static DataOutputStream dos = null;
	static DataInputStream dis = null;

	public BTSend() {
		conn = new NXTConnector();

		conn.addLogListener(new NXTCommLogListener() {

			public void logEvent(String message) {
				System.out.println("BTSend Log.listener: " + message);

			}

			public void logEvent(Throwable throwable) {
				System.out.println("BTSend Log.listener - stack trace: ");
				throwable.printStackTrace();

			}

		});
		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");

		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}

		dos = conn.getDataOut();
		dis = conn.getDataIn();
	}

	public void selectFunc(int sendInt, double distance) {
		for (int i = 0; i < distance; i++) {
			try {
				dos.writeInt(sendInt);
				dos.flush();
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}

			try {
				System.out.println("Received " + dis.readInt());
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
		}
		try {
			dos.writeInt(5);
			dos.flush();
		} catch (IOException ioe) {
			System.out.println("IO Exception writing bytes:");
			System.out.println(ioe.getMessage());
		}
	}

	public void closeBT() {
		try {
			dis.close();
			dos.close();
			conn.close();
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}
}