/*

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TFTP_Server {

	public TFTP_Server() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws IOException {
		System.out.println("Server");
		DatagramSocket ds = new DatagramSocket(3000);
		byte buf1[] = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf1, 1024);
		ds.receive(dp);
		String s=new String(dp.getData());
		System.out.println(s);
		ds.close();
		
		
		System.out.println("Client");
		DatagramSocket ds = new DatagramSocket(3000);
		InetAddress ip = InetAddress.getByName("glados.cs.rit.edu");
		byte buff[] = new byte[30];
		buff[0] = (byte) 0;
		buff[1] = (byte) 1;
		int b = 2;
		String fname = "cat.txt";
		for (int i = 0; i < fname.length(); i++)
			buff[b++] = (byte) fname.charAt(i);
		buff[b++] = 0;
		String mode = "netascii";
		for (int i = 0; i < mode.length(); i++)
			buff[b++] = (byte) mode.charAt(i);
		buff[b++] = 0;
		for (int i = 0; i < b; i++)
			System.out.println((char) buff[i]);
		DatagramPacket dp = new DatagramPacket(buff, b, ip, 69);
		ds.send(dp);
		ds.close();
		System.out.println("Server");
		DatagramSocket ds1 = new DatagramSocket(3000);
		byte buf1[] = new byte[1024];
		DatagramPacket dp1 = new DatagramPacket(buf1, 1024);
		ds1.receive(dp1);
		byte a[] = dp1.getData();

		for (int i = 4; a[i] != 0; i++)
			System.out.print((char) a[i]);

		ds1.close();

	}
}
*/