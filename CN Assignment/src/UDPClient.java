import java.io.*;
import java.net.*;
import java.util.Random;

class UDPClient {
	public static void main(String args[]) throws Exception {
		 UDPClient2 test = new UDPClient2();
		 test.main();
	}
}

class UDPClient2 {
	
	private static final byte BootRequest = 1;
	private short nbSecs = 0;
	
	private InetAddress IPAddress;
	private DatagramSocket clientSocket;
	
	private Message receivedMessage;
	
	private State state = State.INIT;
	
	private byte[] leaseTime = new byte[4];
	
	private int offer = 2;
	private int ack = 5;
	private int nak = 6;

	
	public Message DHCPDiscover() throws IOException {
		Message message = new Message();
		message.setOpCode(BootRequest);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		
		Random random = new Random();
		int ID = random.nextInt();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeInt(ID);
		message.setTransID(outputStream.toByteArray());
		
		outputStream.reset();
		outStream.writeShort(nbSecs);
		message.setNbSecs((outputStream.toByteArray()));
		
		byte[] flag = new byte[2];
		flag[0] = (byte)128;
		message.setFlags(flag);
		
		message.setCIPAddress(new byte[4]);
		message.setYIPAddress(new byte[4]);
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(new byte[4]);
		
		String[] macAddressString = new String[6];
		macAddressString[0] = "AA";
		macAddressString[1] = "BB";
		macAddressString[2] = "CC";
		macAddressString[3] = "DD";
		macAddressString[4] = "EE";
		macAddressString[5] = "FF";

		byte[] macAddress = new byte[16];
		for(int i=0; i<6; i++){
		    Integer j = Integer.parseInt(macAddressString[i], 16);
		    macAddress[i] = j.byteValue();
		}
		message.setCHwAddress(macAddress);
		
		byte[] sName = new byte[64];
		message.setSHName(sName);
		
		byte[] file = new byte[128];
		message.setBFName(file);
		
		message.addOption((byte)99); /*vendor magic cookies*/
		message.addOption((byte)130);
		message.addOption((byte)83);
		message.addOption((byte)99);
		
		if (leaseTime != new byte[4]) {
			message.addOption((byte)51);
			message.addOption((byte)4);
			message.addOption(leaseTime[0]);
			message.addOption(leaseTime[1]);
			message.addOption(leaseTime[2]);
			message.addOption(leaseTime[3]);
		}
		
		message.addOption((byte)53);
		message.addOption((byte)1);
		message.addOption((byte)1);
		
		message.addOption((byte)255);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(message.toByteArray(), 
				message.getLength(), IPAddress, 1234);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		receivedMessage = new Message(receiveData);
		System.out.println("FROM CLIENT: " + message.toString());
		System.out.println("FROM SERVER: " + byteArrayToString(receivePacket.getData()));
		
		state = State.SELECTING;
		
		return message;
	}
	
	public Message DHCPRequest() throws IOException {
		Message message = new Message();
		message.setOpCode(BootRequest);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		
		if(message.getTransID() ==  receivedMessage.getTransID()) {
			message.setTransID(receivedMessage.getTransID());
		}
		else {
			Random random = new Random();
			int ID = random.nextInt();
			outStream.writeInt(ID);
			message.setTransID(outputStream.toByteArray());
		}
		outputStream.reset();
		outStream.writeShort(nbSecs);
		message.setNbSecs((outputStream.toByteArray()));
		
		byte[] flag = new byte[2];
		flag[0] = (byte)128;
		message.setFlags(flag);
		
		if((state == State.BOUND) || (state == State.RENEWING) || (state == State.REBINDING)) {
			message.setCIPAddress(receivedMessage.getCIPAddress());
		}
		else {
			message.setCIPAddress(new byte[4]);
		}
		message.setYIPAddress(new byte[4]);
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(new byte[4]);
		
		byte[] chAddress = new byte[16];
		for(int i = 0; i < 4; i++){
			chAddress[i] = (byte)15;
		}
		message.setCHwAddress(chAddress);
		
		byte[] sName = new byte[64];
		message.setSHName(sName);
		
		byte[] file = new byte[128];
		message.setBFName(file);
		
		message.addOption((byte)99); /*vendor magic cookies*/
		message.addOption((byte)130);
		message.addOption((byte)83);
		message.addOption((byte)99);

		if((state == State.SELECTING) || (state == State.INITREBOOT)) {
			message.addOption((byte)50);
			message.addOption((byte)4);
			message.addOption(receivedMessage.getYIPAddress()[0]);
			message.addOption(receivedMessage.getYIPAddress()[1]);
			message.addOption(receivedMessage.getYIPAddress()[2]);
			message.addOption(receivedMessage.getYIPAddress()[3]);
		}
		
		if((state == State.SELECTING) || (state == State.REQUESTING)) {
			message.addOption((byte)54);
			message.addOption((byte)4);
			message.addOption((byte)10);
			message.addOption((byte)33);
			message.addOption((byte)14);
			message.addOption((byte)246);
		}
		
		message.addOption((byte)53);
		message.addOption((byte)1);
		message.addOption((byte)3);
		
		message.addOption((byte)255);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(message.toByteArray(), 
				message.getLength(), IPAddress, 1234);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		receivedMessage = new Message(receiveData);
		System.out.println("FROM CLIENT: " + message.toString());
		System.out.println("FROM SERVER: " + byteArrayToString(receivePacket.getData()));
		
		return message;
	}
	
	public Message DHCPDecline() throws IOException {
		Message message = new Message();
		message.setOpCode(BootRequest);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		
		Random random = new Random();
		int ID = random.nextInt();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeInt(ID);
		message.setTransID(outputStream.toByteArray());
		
		message.setNbSecs(new byte[2]);
		message.setFlags(new byte[2]);		
		message.setCIPAddress(new byte[4]);
		message.setYIPAddress(new byte[4]);
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(new byte[4]);
		
		byte[] chAddress = new byte[16];
		for(int i = 0; i < 4; i++){
			chAddress[i] = (byte)15;
		}
		message.setCHwAddress(chAddress);
		message.setSHName(new byte[64]);
		message.setBFName(new byte[128]);
		
		message.addOption((byte)99); /*vendor magic cookies*/
		message.addOption((byte)130);
		message.addOption((byte)83);
		message.addOption((byte)99);
		
		message.addOption((byte)53);
		message.addOption((byte)1);
		message.addOption((byte)4);
		
		message.addOption((byte)255);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(message.toByteArray(), 
				message.getLength(), IPAddress, 1234);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		receivedMessage = new Message(receiveData);
		System.out.println("FROM CLIENT: " + message.toString());
		System.out.println("FROM SERVER: " + byteArrayToString(receivePacket.getData()));
		
		return message;
	}
	
	public Message DHCPRelease() throws IOException {
		Message message = new Message();
		message.setOpCode(BootRequest);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		
		Random random = new Random();
		int ID = random.nextInt();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeInt(ID);
		message.setTransID(outputStream.toByteArray());
		
		message.setNbSecs(new byte[2]);
		message.setFlags(new byte[2]);
		
		message.setCIPAddress(receivedMessage.getCIPAddress());
		message.setYIPAddress(new byte[4]);
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(new byte[4]);
		
		byte[] chAddress = new byte[16];
		for(int i = 0; i < 4; i++){
			chAddress[i] = (byte)15;
		}
		message.setCHwAddress(chAddress);
		message.setSHName(new byte[64]);
		message.setBFName(new byte[128]);
		
		message.addOption((byte)99); /*vendor magic cookies*/
		message.addOption((byte)130);
		message.addOption((byte)83);
		message.addOption((byte)99);
		
		message.addOption((byte)53);
		message.addOption((byte)1);
		message.addOption((byte)7);
		
		message.addOption((byte)255);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(message.toByteArray(), 
				message.getLength(), IPAddress, 1234);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		receivedMessage = new Message(receiveData);
		System.out.println("FROM CLIENT: " + message.toString());
		System.out.println("FROM SERVER: " + byteArrayToString(receivePacket.getData()));
		
		clientSocket.close();
		
		return message;
	}
	
	public Message DHCPInform() throws IOException {
		Message message = new Message();
		message.setOpCode(BootRequest);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		
		Random random = new Random();
		int ID = random.nextInt();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeInt(ID);
		message.setTransID(outputStream.toByteArray());
		
		outputStream.reset();
		outStream.writeShort(nbSecs);
		message.setNbSecs((outputStream.toByteArray()));
		
		byte[] flag = new byte[2];
		flag[0] = (byte)128;
		message.setFlags(flag);
		
		message.setCIPAddress(receivedMessage.getCIPAddress());
		message.setYIPAddress(new byte[4]);
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(new byte[4]);
		
		byte[] chAddress = new byte[16];
		for(int i = 0; i < 4; i++){
			chAddress[i] = (byte)15;
		}
		message.setCHwAddress(chAddress);
		
		
		message.setSHName(new byte[64]);
		
		message.setBFName(new byte[128]);
		
		message.addOption((byte)99); /*vendor magic cookies*/
		message.addOption((byte)130);
		message.addOption((byte)83);
		message.addOption((byte)99);		
		
		message.addOption((byte)53);
		message.addOption((byte)1);
		message.addOption((byte)8);
		
		message.addOption((byte)255);
		
		byte[] receiveData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(message.toByteArray(), 
				message.getLength(), IPAddress, 1234);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		receivedMessage = new Message(receiveData);
		System.out.println("FROM CLIENT: " + message.toString());
		System.out.println("FROM SERVER: " + byteArrayToString(receivePacket.getData()));
		
		return message;
	}
	
	public String byteArrayToString(byte[] byteArray) {
		String string = "[";
		for(int i = 0; i < byteArray.length; i++) {
			if (i != byteArray.length - 1) {
				string += String.valueOf(byteArray[i]) + ","; 
			}
			else {
				string += String.valueOf(byteArray[i]);
			}
		}
		return string + "]";
	}
	
	public void main() throws IOException {
		clientSocket = new DatagramSocket();
		IPAddress = InetAddress.getByName("10.33.14.246");
		if ((state == State.SELECTING) && (receivedMessage.getMessageType() == offer)) {
			state = State.REQUESTING;
			DHCPRequest();
		}
		else if (state == State.REQUESTING) {
			if (receivedMessage.getMessageType() == ack) {
				state = State.BOUND;
			}
			else if (receivedMessage.getMessageType() == nak) {
				state = State.INIT;
			}
		}
		else if (state == State.BOUND) {
			if ((receivedMessage.getMessageType() == offer) || (receivedMessage.getMessageType() == ack) || (receivedMessage.getMessageType() == nak)) {
				/*record lease, set timer t1 t2*/
				state = State.BOUND;
			}
			/*if (T1 expires) {
			 * sentMessage = DHCPRequest();
			 * state = State.RENEWING;
			 * }*/
		}
		else if (state == State.RENEWING) {
			if (receivedMessage.getMessageType() == ack) {
				state = State.BOUND;
				/*record lease, set timer t1 t2*/
			}
			/*if (T2 expires) {
			 * sentMessage = DHCPRequest();
			 * state = State.REBINDING;
			 * }*/
			else if (receivedMessage.getMessageType() == nak) {
				nbSecs = 0;
				state = State.INIT;
			}
		}
		else if (state == State.INITREBOOT) {
			state = State.REBOOTING;
			DHCPRequest();
		}
		else if (state == State.REBOOTING) {
			if (receivedMessage.getMessageType() == ack) {
				state = State.BOUND;
				/*record lease, set timer t1 t2*/
			}
			else if (receivedMessage.getMessageType() == nak) {
				nbSecs = 0;
				state = State.INIT;
			}
		}
	}
}