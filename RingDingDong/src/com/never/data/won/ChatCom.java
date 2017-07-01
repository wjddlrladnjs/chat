package com.never.data.won;

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
	private char type='A';					//client에서 넘어온 첫 char를 해석하기 위한 프로토콜 변수
	private boolean onAir;					//스레드의 연속/정지를 위한 boolean 변수
	private int myCount = 0;				//ArrayList에 저장된 이름의 위치를 확인 하기 위한 이름 위치값 int 변수
	String name = "";				//사용자 대화명 저장을 위한 string name 변수
	static ArrayList<String> userName = new ArrayList<>();	//사용자 대화명 저장을 위한 ArrayList	

	//생성자. Server GUI와 Server Thread 객체를 전달 받아 일관성 유지
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
	
	//run 메소드. client에게 받은 요청이 접수되는 곳.
	@Override
	public void run() {
		if(dos == null){
			server.addLog("IOException generated");//dos가 제대로 생성되지 않았을 경우 exception 발생
			return;
		}

		try {
			type = dis.readChar();
			//client가 이 서버에 접근하는 기초 프로토콜을 알고 있는지 확인. 
			//client에서 writeChar 혹은 'A'값 이외의 다른 방식으로 접근하면 서버 접근 불가.
			//이 protocol을 활용해서 user list 추가함.
			if(type != 'A'){
				server.addLog("Invalid access tried.");
				return;
			}

			name = dis.readUTF();
			name = nameCheck(name); 							//초기 설정 대화명의 중복 확인.
			userName.add(myCount, name); 						//user 대화명을 저장해 둠.
			String hello = "Valid access & Success!";
			server.addLog(name+hello);							//server에 log 띄움.
			hello = " is entered this room.";
			serverThread.sendExceptSelf(type, name, hello,this);//다른 user에게 사용자 입장을 알리기 위한 전달
			dos.writeUTF(String.format("# [%s] %s", name, hello));//입장한 사용자에게 입장 내용 전달
			dos.flush();
			myCount++;											//arrayList에 이름 저장 시 같은 자리 중복 회피 위한 값 변경.(유효한지 모르겠음...;;)
			server.addLog(userName.toString());					//서버에 현재 접속해 있는 유저 목록 띄움
		} catch (IOException e) {
			server.addLog("ChatCom: run(); "+e);
		}
		
		//사용자 리스트 목록 구해서 보내기
		serverThread.sendUserList(this);

		onAir = true;
		String tmp;
		while(onAir){
			try{
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//client에서 전달되는 요구 사항이 종류 확인 client에서 서버로 전달되는 모든 요구의  시작은 client에서  writeChar()로 약속된 protocol로 진행되어야 함!!!
				type = dis.readChar();							
//				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				switch(type){
				//client에서  단순 메세지 보낼 때 protocol
				case 'M':					
					tmp = dis.readUTF();	
					server.addLog(String.format("%s[%s] %s", serverThread.getTime(), name, tmp));

					serverThread.sendAllmember(type, name, tmp);
					//아래는 서버와 1:1 통신할 때만 사용하는 것.
//					dos.writeUTF(tmp);
//					dos.flush();
					break;
					
				//client에서  이름 변경 시 protocol	
				case 'N':
					tmp = dis.readUTF();
					server.addLog(String.format("%s has been changed to %s", name, tmp));								
					serverThread.sendAllmember(type, name, tmp);	
					name = nameCheck(tmp);		
					userName.set(myCount, name);
					break;
				
				//서버에서 접속이 끊길 떄 protocol	
				case 'X':
					tmp = " is getting out this room. ";
					server.addLog(String.format("[%s] %s", name, tmp));
					serverThread.exitRoom(type, name, tmp, this);
					onAir = false;
					break;
				}
			}catch (IOException e) {
				server.addLog("ChatCom: run2(); "+e);
			}			
		}

	}
	//run 메소드 종료

	//server의 요구를 정리해서 client에게 전달하는 메소드
	//수업 코드에서는 아래를 세분화하지 않고 하나의 형식으로 전달하면 client에서만 나눠서 분석하는 것으로 되어 있다.
	//본 server 코드는 server에서도 다르게 나눠서 접근하고 있는데, 대개의 경우는 문장이 되는 String만 변경되지만 
	//case에 따라 name과 String을 나눠서 전달하는 경우도 한 번에 전달하는 것도 있다. 이는 추후 수정 예정.
	public void sendMessage(char type,String name, String msg){
		try{
			String tmp = "";
			switch(type){
			//user의 채팅방 입장 이벤트
			case 'A':
				dos.writeChar(type);
				tmp = String.format("# [%s] %s", name, msg);
				dos.writeUTF(tmp);
				dos.writeUTF(name);
				dos.flush();				
				break;
			//user의 메세지 전송에 따른 모든 유저에게 메세지 전송하는 이벤트	
			case 'M':
				dos.writeChar(type);
				tmp = String.format("%s[%s] %s", serverThread.getTime(), name, msg);
				dos.writeUTF(tmp);
				dos.flush();
				break;
			//user의 대화명 변경 사실을 다른 유저에게 알리는 이벤트	
			case 'N':
				dos.writeChar(type);
				tmp = String.format("# %s is changed to %s", name, msg);
				dos.writeUTF(tmp);
				dos.writeUTF(name);
				dos.flush();
				break;
			//user의 퇴장을 다른 user에게 알리는 이벤트	
			case 'X':
				dos.writeChar(type);
				tmp = String.format("# [%s] %s", name, msg);
				dos.writeUTF(tmp);
				dos.flush();
				dos.writeUTF(name);
				dos.flush();
				break;
			//user List 갱신하는 이벤트	
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
	//sendMessage 메소드 종료

	//대화명 중복 체크 메소드
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
	

	

}
