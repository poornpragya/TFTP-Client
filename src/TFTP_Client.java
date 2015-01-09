/*
 * TFTP_Client.java
 * Author: Poorn Pragya
 * 
 * This program is a trivial implementation of TFTP protocol
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TFTP_Client {

	DatagramSocket ds;
	InetAddress ip;
	int port;
	String mode;

	/*
	 * Default Constructor
	 */
	public TFTP_Client() {
		ds = null;
		ip = null;
		port = 0;
		mode = "OCTET";
	}

	/*
	 * Parameterized constructor
	 */
	public TFTP_Client(String servername) {
		this();
		connect(servername);
	}

	/*
	 * Function connect takes servername as input and initializes the sockets
	 * and port to send
	 */
	void connect(String servername) {
		try {
			ds = new DatagramSocket(0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.print("No availabale ports to send");
		}
		port = ds.getLocalPort();
		try {
			ip = InetAddress.getByName(servername);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("tftp: nodename nor servname provided, or not known");
		}
	}

	/*
	 * Function mergeBytesToInt takes two bytes and converts them into 2 byte
	 * integer
	 */
	public int mergeBytesToInt(byte b1, byte b2) {
		return (int) ((b1 << 8) | b2);
	}

	/*
	 * This function is used to check the connectivity status of the client
	 */
	boolean getConnectionStatus() {
		if (ds != null || ip != null)
			return true;
		else
			return false;
	}

	/*
	 * Function get takes filename as argument and downloads the file from the
	 * server
	 */
	void get(String filename) throws IOException {

		int size = filename.length() + this.mode.length() + 4;
		byte RRQ[] = new byte[size];
		int RRQ_ptr = 0;

		// Creating Read packet with filename and mode=NETASCII
		RRQ[RRQ_ptr++] = 0;
		RRQ[RRQ_ptr++] = 1;
		for (int i = 0; i < filename.length(); i++)
			RRQ[RRQ_ptr++] = (byte) filename.charAt(i);
		RRQ[RRQ_ptr++] = 0;
		for (int i = 0; i < this.mode.length(); i++)
			RRQ[RRQ_ptr++] = (byte) this.mode.charAt(i);
		RRQ[RRQ_ptr++] = 0;

		// Send the RRQ request to server at port TFTP port 69
		DatagramPacket dp = new DatagramPacket(RRQ, RRQ_ptr, ip, 69);
		ds.send(dp);

		// Waiting to receive the data packet from server
		byte Data[] = null;
		int pktlen = 0;
		int opcode_received = 0;
		File f = null;
		FileOutputStream fout = null;

		long startTime = System.currentTimeMillis();
		while (true) {
			// flushing the buffer
			Data = new byte[516];
			dp = new DatagramPacket(Data, Data.length);
			ds.setSoTimeout(3000);
			ds.receive(dp);

			pktlen = dp.getLength();

			opcode_received = this.mergeBytesToInt(Data[0], Data[1]);
			if (opcode_received != 3)
				break;

			if (fout == null && f == null) {
				f = new File(filename);
				fout = new FileOutputStream(f);

			}

			int new_dest_port = dp.getPort(); // Now this port will used for
												// further
												// communication
			// writing data to file
			fout.write(Data, 4, pktlen - 4);

			// if length of packet received is less than 512 bytes then break
			if (pktlen < 516)
				break;

			// Creating ACK packet
			byte Ack[] = new byte[4];
			Ack[0] = 0;
			Ack[1] = 4;
			Ack[2] = Data[2];
			Ack[3] = Data[3];

			// Creating Datagram for ACK packet
			DatagramPacket dp_ack = new DatagramPacket(Ack, Ack.length, ip,
					new_dest_port);
			ds.send(dp_ack);

		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		// closing file object since data copy is done
		if (fout != null) {
			System.out.println("Received " + f.length() + " bytes in "
					+ ((double) totalTime / 1000) + " seconds");
			fout.close();
		}

		if (opcode_received == 5) {
			int err_code = this.mergeBytesToInt(Data[2], Data[3]);
			if (err_code == 1)
				System.out.println("File not found");
			else if (err_code == 2)
				System.out.println("Access violation");
			else if (err_code == 3)
				System.out.println("Disk full or allocation exceeded.");
			else if (err_code == 4)
				System.out.println("Illegal TFTP operation.");
			else if (err_code == 5)
				System.out.println("Unknown transfer ID.");
			else if (err_code == 6)
				System.out.println("File already exists.");
			else if (err_code == 7)
				System.out.println("No such user");
		}
	}

	/*
	 * This function is used to change the mode of transfer
	 */
	void changeMode(String mode) {
		this.mode = mode;
		System.out.println("Mode changed to " + this.mode);
	}

	/*
	 * This function gives the functionality of status command
	 */
	void status() {
		if (getConnectionStatus())
			System.out.println("Connected to " + ip.getHostName());
		else
			System.out.println("Not Connected");
		System.out.println("Mode: " + this.mode);
	}

	/*
	 * Start of main function
	 */
	public static void main(String args[]) throws IOException {

		TFTP_Client obj = null;
		if (args.length != 0)
			obj = new TFTP_Client(args[0]);
		else
			obj = new TFTP_Client();
		Scanner sc = new Scanner(System.in);
		String choice = null;
		System.out.print("tftp>");
		while (sc.hasNext()) {
			choice = sc.next();
			if (choice.equals("connect")) {
				choice = sc.next();
				obj.connect(choice);
			} else if (choice.equals("get")) {
				choice = sc.next();
				try {
					if (obj.getConnectionStatus())
						obj.get(choice);
					else
						System.out.println("TFTP not connected to server");
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			} else if (choice.equals("mode")) {
				choice = sc.next();
				if (obj.getConnectionStatus())
					obj.changeMode(choice);
				else
					System.out.println("TFTP not connected to server");
			} else if (choice.equals("quit")) {
				if (obj.ds != null)
					obj.ds.close();
				System.exit(0);
			} else if ((choice.equals("ascii"))) {
				if (obj.getConnectionStatus())
					obj.changeMode("netascii");
				else
					System.out.println("TFTP not connected to server");
			} else if ((choice.equals("binary"))) {
				if (obj.getConnectionStatus())
					obj.changeMode("octet");
				else
					System.out.println("TFTP not connected to server");
			} else if (choice.equals("status")) {
				obj.status();
			} else if (choice.equals("?")) {
				System.out
						.println("Commands may be abbreviated.  Commands are:");
				System.out.println("connect 	connect to remote tftp");
				System.out.println("mode    	set file transfer mode");
				System.out.println("get     	receive file");
				System.out.println("quit    	exit tftp");
				System.out.println("status  	show current status");
				System.out.println("binary  	set mode to octet");
				System.out.println("ascii   	set mode to netascii");
				System.out.println("?       	print help information");
			} else
				System.out.println("Invalid Command");
			System.out.print("tftp>");
		}
		if (sc != null)
			sc.close();
	}
}
