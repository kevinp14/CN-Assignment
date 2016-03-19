import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Message {
	private byte opCode;
	private byte hType;
	private byte hLength;
	private byte hopCount;
	private byte[] transID = new byte[4];
	private byte[] nbSecs = new byte[2];
	private byte[] flags = new byte[2];
	private byte[] cIPAddress = new byte[4];
	private byte[] yIPAddress = new byte[4];
	private byte[] sIPAddress = new byte[4];
	private byte[] gIPAddress = new byte[4];
	private byte[] cHwAddress = new byte[16];
	private byte[] sHName = new byte[64];
	private byte[] bFName = new byte[128];
	private ArrayList<Byte> optionsList = new ArrayList<Byte>();
	
	public Message(){
	}
	
	public Message(byte[] data) throws IOException{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		DataInputStream inStream = new DataInputStream(inputStream);
		opCode = inStream.readByte();
		hType = inStream.readByte();
		hLength = inStream.readByte();
		hopCount = inStream.readByte();
		inStream.read(transID, 0, 4);
		inStream.read(nbSecs, 0, 2);
		inStream.read(flags, 0, 2);
		inStream.read(cIPAddress, 0, 4);
		inStream.read(yIPAddress, 0, 4);
		inStream.read(sIPAddress, 0, 4);
		inStream.read(gIPAddress, 0, 4);
		inStream.read(cHwAddress, 0, 16);
		inStream.read(sHName, 0, 64);
		inStream.read(bFName, 0, 128);
		inStream.read(getOptions(), 0, optionsList.size());
	}

	public byte getOpCode() {
		return opCode;
	}

	public void setOpCode(byte opcode) {
		opCode = opcode;
	}

	public byte getHType() {
		return hType;
	}

	public void setHType(byte htype) {
		hType = htype;
	}

	public byte getHLength() {
		return hLength;
	}

	public void setHLength(byte hlength) {
		hLength = hlength;
	}

	public byte getHopCount() {
		return hopCount;
	}

	public void setHopCount(byte hopcount) {
		hopCount = hopcount;
	}

	public byte[] getTransID() {
		return transID;
	}

	public void setTransID(byte[] transiD) {
		transID = transiD;
	}

	public byte[] getNbSecs() {
		return nbSecs;
	}

	public void setNbSecs(byte[] nbsecs) {
		nbSecs = nbsecs;
	}

	public byte[] getFlags() {
		return flags;
	}

	public void setFlags(byte[] flag) {
		flags = flag;
	}

	public byte[] getCIPAddress() {
		return cIPAddress;
	}

	public void setCIPAddress(byte[] ciPAddress) {
		cIPAddress = ciPAddress;
	}

	public byte[] getYIPAddress() {
		return yIPAddress;
	}

	public void setYIPAddress(byte[] yiPAddress) {
		yIPAddress = yiPAddress;
	}

	public byte[] getSIPAddress() {
		return sIPAddress;
	}

	public void setSIPAddress(byte[] siPAddress) {
		sIPAddress = siPAddress;
	}

	public byte[] getGIPAddress() {
		return gIPAddress;
	}

	public void setGIPAddress(byte[] giPAddress) {
		gIPAddress = giPAddress;
	}

	public byte[] getCHwAddress() {
		return cHwAddress;
	}

	public void setCHwAddress(byte[] chwAddress) {
		cHwAddress = chwAddress;
	}

	public byte[] getSHName() {
		return sHName;
	}

	public void setSHName(byte[] shName) {
		sHName = shName;
	}

	public byte[] getBFName() {
		return bFName;
	}

	public void setBFName(byte[] bfName) {
		bFName = bfName;
	}

	public byte[] getOptions() {
		int size = optionsList.size();
		byte options[] = new byte[size];
		for(int i = 0; i < size; i++) {
			options[i] = optionsList.get(i);
		}
		return options;
	}

	public void addOption(byte option) {
		optionsList.add(option);
	}
	
	public int getMessageType() {
		int i = 4;
		while(i < optionsList.size()){
			if(optionsList.get(i) == 53) {
				return optionsList.get(i+2);
			}
			else {
				i += optionsList.get(i+1) + 1;
			}
		}
		return -1;
	}
	
	public int getLength() {
		return (236 + optionsList.size()); 
	}
	
	public byte[] toByteArray() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(outputStream);
		outStream.writeByte(opCode);
		outStream.writeByte(hType);
		outStream.writeByte(hLength);
		outStream.writeByte(hopCount);
		outStream.write(transID, 0, 4);
		outStream.write(nbSecs, 0, 2);
		outStream.write(flags, 0, 2);
		outStream.write(cIPAddress, 0, 4);
		outStream.write(yIPAddress, 0, 4);
		outStream.write(sIPAddress, 0, 4);
		outStream.write(gIPAddress, 0, 4);
		outStream.write(cHwAddress, 0, 16);
		outStream.write(sHName, 0, 64);
		outStream.write(bFName, 0, 128);
		outStream.write(getOptions(), 0, optionsList.size());
		return outputStream.toByteArray();
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
	
	@Override
	public String toString() {
		String string = "";
		string += opCode + "\r\n";
		string += hType + "\r\n";
		string += hLength + "\r\n";
		string += hopCount + "\r\n";
		string += "[" + byteArrayToString(transID) + "]" + "\r\n";
		string += "[" + byteArrayToString(nbSecs) + "]" + "\r\n";
		string += "[" + byteArrayToString(flags) + "]" + "\r\n";
		string += "[" + byteArrayToString(cIPAddress) + "]" + "\r\n";
		string += "[" + byteArrayToString(yIPAddress) + "]" + "\r\n";
		string += "[" + byteArrayToString(sIPAddress) + "]" + "\r\n";
		string += "[" + byteArrayToString(gIPAddress) + "]" + "\r\n";
		string += "[" + byteArrayToString(cHwAddress) + "]" + "\r\n";
		string += "[" + byteArrayToString(sHName) + "]" + "\r\n";
		string += "[" + byteArrayToString(bFName) + "]" + "\r\n";
		string += "[" + byteArrayToString(getOptions()) + "]";
		return string;
	}
}
