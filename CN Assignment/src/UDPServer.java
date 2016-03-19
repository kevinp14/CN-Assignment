import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

class UDPServer {
	public static void main(String args[]) throws Exception {
		 UDPServer2 test = new UDPServer2();
		 test.main();
	}
}

class UDPServer2 {
	
	static final byte BootRequest = 1;
	static final byte BootReply = 2;
	ArrayList<byte[]> ipAddresses = new ArrayList<byte[]>();

	
	public void main() throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(19876);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			System.out.println("RECEIVED: " + sentence);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
			serverSocket.close();
		}
	}
	
	public Message DHCPRequestHandler(Message receivedMessage){
		Message message = new Message();
		message.setOpCode(BootReply);
		message.setHType((byte) 1);
		message.setHLength((byte) 6);
		message.setHopCount((byte) 0);
		message.setTransID(receivedMessage.getTransID());
		message.setNbSecs(new byte[2]);
		message.setFlags(receivedMessage.getFlags());
		message.setCIPAddress(new byte[4]);
		
		Random random = new Random();
		int yipAddress = random.nextInt();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeInt(yipAddress);
		byte[]address = outputStream.toByteArray();
		if(ipAddressTaken(address) == false){
			ipAddresses.add(address);
			message.setYIPAddress(address);
		}
				
		
		message.setSIPAddress(new byte[4]);
		message.setGIPAddress(receivedMessage.getGIPAddress());
		message.setCHwAddress(receivedMessage.getCHwAddress());
		
		byte[] sName = new byte[64];
		message.setSHName(sName);
		
		byte[] file = new byte[128];
		message.setBFName(file);

		
		int state = receivedMessage.getTransID().getClientState();
		switch(state){
		//SELECTING
		case 2:
			
		//INIT-REBOOT		
		case 8:
		
		//RENEWING
		case 5:
			
		//REBINDING
		case 6:
			
		//NOT POSSIBLE STATE TRANSITION
		default:
			
		}
	}
	
	public Message DHCPOffer(){
		return null;
	}
	
	
	public Message DHPCAck(){
		return null;
	}
	
	public boolean ipAddressTaken(byte[] ipAddress){
		if(ipAddresses.contains(ipAddress)){
			return true;
		}
		return false;
	}
	
	
	public Message DHCPNak(){
		return null;
	}
}