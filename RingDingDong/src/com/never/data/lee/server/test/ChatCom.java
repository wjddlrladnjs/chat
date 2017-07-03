package com.never.data.lee.server.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatCom implements Runnable{
	//객체변수
	private ChatServer server;				//Log 남기는 등의 역할을 위한 ChatServer GUI 접근 객체 변수
	private ChatServerThread serverThread;	//sernAllMember 등의 메소드 접근을 위한 ChatServerThread 객체 변수
	//네트워크 관련 변수
	private Socket socket; 					//전달받은 socket 저장용 객체 변수
	private DataInputStream dis = null;		//본 클래스 내 여러 메소드에서 활용하기 위한 전역 변수
	private DataOutputStream dos = null;	//본 클래스 내 여러 메소드에서 활용하기 위한 전역 변수
	//그 외 변수
	private boolean onAir;					//스레드의 연속/정지를 위한 boolean 변수
	private static int myCount = 0;				//ArrayList에 저장된 이름의 위치를 확인 하기 위한 이름 위치값 int 변수
	String name = "";				//사용자 대화명 저장을 위한 string name 변수
	static ArrayList<String> userName = new ArrayList<>();	//사용자 대화명 저장을 위한 ArrayList	

	public ChatCom(ChatServerThread serverThread, ChatServer server, Socket socket) {
		this.serverThread = serverThread;
		this.server = server;
		this.socket = socket;
		try {
			dis = new DataInputStream ( socket.getInputStream() );
			dos = new DataOutputStream ( socket.getOutputStream() );
		} catch (IOException e) {
			this.server.addLog("[class]ChatCom [method] 생성자 "+e);
		} 
	}

	@Override
	public void run() {

		try {
			name = dis.readUTF();
			name = nameCheck(name);
			userName.add(name);
			dos.writeUTF(String.format("<%s>Welcome to fantastic chatting room.", name));
			dos.flush();
			server.addLog(String.format("[%s] is entered this room.", name));
			serverThread.sendAllMessage('M', String.format("# [%s] is entered this room.", name));
			serverThread.sendAllMessage('U',userList());
			serverThread.sendOnlyOne('N',name, this);
		} catch (IOException e1) {
			server.addLog("[class]ChatCom [method] run1 "+e1);
		}

		onAir = true;
		char type='A';
		String msg ="";
		while(onAir){
			try {
				type = dis.readChar();
				switch(type){
				case 'M':
					msg = dis.readUTF();
					server.addLog(serverThread.getTime()+"_"+name+":"+msg);
					serverThread.sendAllMessage(type, serverThread.getTime()+"_"+name+":"+msg, this);
					serverThread.sendOnlyOne(type, serverThread.getTime()+"_"+"What I said: "+msg, this);
					break;
				case 'N':
					msg = dis.readUTF();
					server.addLog(serverThread.getTime()+"_"+name+" is changed name to "+msg);
					serverThread.sendAllMessage('M', name+" is changed name to "+msg,this);
					serverThread.sendOnlyOne('N', msg, this);
					serverThread.sendAllMessage('R',name+","+msg);
					userName.set(getMyCount(name), msg);
					name = renameCheck(msg);					
					serverThread.sendOnlyOne('M', "Your name is changed to "+msg,this);					
					break;
				case 'X':
					userName.remove(getMyCount(name));
					serverThread.stopSocket(serverThread, this, socket);

					onAir = false;
					break;

				case '1' : //@요한	

//					String namelog= dis.readUTF(); //이름 

					server.addLog(/*namelog+*/"네임 님이 서버의로그를 저장하였습니다 ");

					String log = server.myLog();

//					dos.writeChar('2');//서버에서 또 클라이언트로 보냄 
//					dos.flush();
//					dos.writeUTF(log);
//					dos.flush();
					serverThread.sendOnlyOne('2', log, this);
					
					break;
				}
			} catch (IOException e) {
				server.addLog("[class]ChatCom [method] run2 "+e);
			}
		}
	}
	//run method end

	public void sendMessage(char protocol, String msg){
		try {
			dos.writeChar(protocol);
			dos.flush();
		} catch (IOException e) {
			server.addLog("[class]ChatCom [method] sendMessage1 "+e);
		}

		try {
			dos.writeUTF(msg);
		} catch (IOException e1) {
			server.addLog("[class]ChatCom [method] sendMessage2 "+e1);
		}

	}


	//대화명 중복 체크 메소드
	//user가 1명 생성되고 나서 또 다른 1명이 생성되기 직전에 한다. 해당 메소드 사용에는 arrayList 사용을 확인하며 해야 한다.
	String nameCheck(String name){
		String tmpName = "";
		tmpName = name;
		if(userName.size()>=1){
			int nameCount = 0;
			for(int i=0; i<userName.size(); i++){
				for(int j=0; j<userName.size(); j++){
					if(tmpName.equals(userName.get(i))){
						nameCount++;
						tmpName = name + nameCount;
					}
				}
			}
		}
		return tmpName;
	}

	String renameCheck(String name){
		String tmpName = "";
		tmpName = name;
		if(userName.size()>=2){
			int nameCount = 0;
			for(int i=0; i<userName.size(); i++){
				for(int j=0; j<userName.size(); j++){
					if(tmpName.equals(userName.get(i))){
						nameCount++;
						tmpName = name + nameCount;
					}
				}
			}
		}
		return tmpName;
	}

	int getMyCount(String name){
		int tmp = 0;
		for(int i=0; i<userName.size(); i++){
			if(name.equals(userName.get(i))) tmp = i;
		}
		return tmp;
	}

	String userList(){
		String tmp = "";
		for(String nameTmp : userName){
			tmp += nameTmp+",";
		}
		tmp.substring(0, tmp.length()-2);
		return tmp;
	}

	void disNdosClose(){
		try{
			if(dis != null)	dis.close();
			if(dos != null)	dos.close();
		}catch(IOException e){
			server.addLog("[class]ChatCom [method] disNdosClose "+e);
		}
	}


}
