package com.never.data.won;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatCom implements Runnable{
	private Socket socket;
	private ChatServer server;
	private ChatServerThread serverThread;
	private char type='A';
	private boolean onAir;
	private static int userCount = 1;
	private int myCount = 0;
	String name = "";
	static ArrayList<String> userName = new ArrayList<>();

	DataInputStream dis = null;
	DataOutputStream dos = null;

	public ChatCom(ChatServerThread serverThread, ChatServer server, Socket socket) {
		this.socket = socket;
		this.serverThread = serverThread;
		this.server = server;
		try{
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		}catch(IOException e){

		}
	}

	public void sendMessage(char type,String name, String msg){
		try{
			String tmp = "";
			switch(type){
			case 'A':
				dos.writeChar(type);
				tmp = String.format("# [%s] %s", name, msg);
				dos.writeUTF(tmp);
				dos.writeUTF(name);
				dos.flush();				
				break;
			case 'M':
				dos.writeChar(type);
				tmp = String.format("%s[%s] %s", serverThread.getTime(), name, msg);
				dos.writeUTF(tmp);
				dos.flush();
				break;
			case 'N':
				dos.writeChar(type);
				tmp = String.format("# %s is changed to %s", name, msg);
				dos.writeUTF(tmp);
				dos.writeUTF(name);
				dos.flush();
				break;
			case 'X':
				dos.writeChar(type);
				tmp = String.format("# [%s] %s", name, msg);
				dos.writeUTF(tmp);
				dos.flush();
				dos.writeUTF(name);
				dos.flush();
				break;
			case 'L':
				dos.writeChar(type);
				dos.writeUTF(name);
				dos.flush();
				break;

			}
		}catch(IOException e){
			server.addLog("sendMessage error: "+e);
		}
	}

	//user가 1명 생성되고 나서 또 다른 1명이 생성되기 직전에 한다. 해당 메소드 사용에는 arrayList 사용을 확인하며 해야 한다.
	String nameCheck(String name){
		if(userName.size()>=1){
			int nameCount = 0;
			boolean sameCheck = false;
			for(int i=0; i<userName.size(); i++){
				for(int j=0; j<userName.size(); j++){
					if(name.equals(userName.get(i))){
						nameCount++;
						sameCheck = true;
					}
				}
			}
			if(sameCheck)	name = name+nameCount;
		}
		return name;
	}
	

	@Override
	public void run() {

		if(dos == null){
			server.addLog("IOException generated");
			return;
		}

		try {
			type = dis.readChar();
			if(type != 'A'){
				server.addLog("Invalid access tried.");
				return;
			}

			name = dis.readUTF();
			name = nameCheck(name);
			userName.add(myCount, name);
			String hello = "Valid access & Success!";
			server.addLog(name+hello);
			hello = " is entered this room.";
			serverThread.sendExceptSelf(type, name, hello,this);
//			serverThread.sendAllmember(type, name, name);
			dos.writeUTF(String.format("# [%s] %s", name, hello));
			dos.flush();
			myCount++;	
			server.addLog(userName.toString());
		} catch (IOException e) {
			server.addLog("ChatCom: run(); "+e);
		}
		
		//사용자 리스트 목록 구해서 보내기
		serverThread.sendUserList(this);

		onAir = true;
		String tmp;
		while(onAir){
			try{
				type = dis.readChar();
				switch(type){
				case 'M':					
					tmp = dis.readUTF();	
					server.addLog(String.format("%s[%s] %s", serverThread.getTime(), name, tmp));

					serverThread.sendAllmember(type, name, tmp);
					//아래는 서버와 1:1 통신할 때만 사용하는 것.
					//					dos.writeUTF(tmp);
					//					dos.flush();
					break;
				case 'N':
					tmp = dis.readUTF();
					server.addLog(String.format("%s has been changed to %s", name, tmp));								
					serverThread.sendAllmember(type, name, tmp);	
					name = nameCheck(tmp);		
					userName.set(myCount, name);
					break;
				case 'X':
					tmp = " is getting out this room. ";
					server.addLog(String.format("[%s] %s", name, tmp));
					serverThread.exitRoom(type, name, tmp, this);
					onAir = false;
					break;
				}
			}catch (IOException e) {
				server.addLog("ChatCom: run2(); "+e);
//				serverThread.chatComList.remove(this);
//				onAir = false;
			}			
		}

	}

}
