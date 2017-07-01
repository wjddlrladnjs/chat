package com.never.data.won;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ChatServerThread implements Runnable{
	//객체 변수
	private ChatServer chatserver;		//Log창 등에 접근하기 위한 Chatserver 객체
	private ChatCom chatCom;			//sendMessage 등 메소드에 접근하기 위한 ChatCom 객체
	//네트워크 관련 변수
	private int port;					//넘겨받은 port 번호 저장
	private ServerSocket serverSocket = null;	//여러 메소드에서 공유하기 위한 ServerSocket 변수
	private Socket socket = null;				//여러 메소드에서 공유하기 위한 Socket 변수	
	//그 외 변수
	private boolean onAir;				//thread 연속재생 및 제어를 위한 boolean
	private ArrayList<ChatCom> chatComList = new ArrayList<>(); //메시지 브로드캐스팅을 위한 ChatCom 객체 저장용 ArrayList
	
	//생성자. Server GUI의 port번호와 Chatserver 객체를 전달 받음
	public ChatServerThread(ChatServer chatserver, int port) {
		this.chatserver = chatserver;
		this.port = port;
	}
	
	//스레드 run 메소드. 
	@Override
	public void run() {		
		try{
			//server에서 client접속 대기
			serverSocket = new ServerSocket(port);			
			chatserver.addLog("Server is ready....");			
		}catch(IOException e){
			chatserver.addLog("ChatServerThread: ServerSocket; "+e);
		}
		
		chatserver.buttonOnState();	//버튼 상태 변경

		onAir = true;
		while(onAir){
			try{
				socket = serverSocket.accept();	//client 접속 대기
				//client 접속 성공시 server에 남길 메시지
				chatserver.addLog("Accpted IP: "+socket.getInetAddress().getHostAddress());
				chatserver.addLog("Accepted time: "+getTime());
				//client와 지속 소통 위해 새로운 스레드 생성해서 chatcom에 전달. 
				chatCom = new ChatCom(this, chatserver, socket);
				chatComList.add(chatCom);		//chatcom 객체가 생성되면 ArrayList에 저장
				new Thread( chatCom ).start();
			}catch(IOException e){
				chatserver.addLog("ChatServerThread: serverSocket.accept(); "+e);
			}
		}
	}
	//run 메소드 종료
	
	//모든 구성원에에 메세지 보내는 메소드
	public void sendAllmember(char type, String name,String msg){
		for(ChatCom com : chatComList){//내가 작성하는 객체 새로운 이름:기존 리스트명
			com.sendMessage(type,name, msg);
		}		
	}
	
	//자기자신만 제외하고 메세지 보내는 메소드
	public void sendExceptSelf(char type, String name,String msg, ChatCom me){
		for(ChatCom com : chatComList){//내가 작성하는 객체 새로운 이름:기존 리스트명
			if(com != me){
			com.sendMessage(type,name, msg);
			}
		}
	}
	
	//자기자시만 제외하고 메세지 보내되 Arraylist에서 자기 자신을 제거하는 메소드
	public void exitRoom(char type, String name,String msg, ChatCom me){
		for(ChatCom com : chatComList){//내가 작성하는 객체 새로운 이름:기존 리스트명
			if(com != me){
			com.sendMessage(type,name, msg);
			}
		}
		chatComList.remove(me);
	}	
	
	//	user list method
	public void sendUserList(ChatCom chatCom){
		String userList = "";
		StringBuilder sb = new StringBuilder();
		for(ChatCom c : chatComList){
			sb.append(c.name).append(",");
		}
		userList = sb.toString();
		userList = userList.substring(0,userList.length()-1);
		//sendMessage 메소드를 활용하기 위해 userList를 두개나 보내야 됨. 하나만 사용할 것이지만 다른 메소드에서 정한 매개변수 규칙 때문에 이와 같이 전달.
		chatCom.sendMessage('L', userList,userList);
	}	
	
	//server 및 client에 시간 정보 전달
	String getTime(){
		String time = "";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat();
		time = format.format(now.getTime());
		return time;
	}
	
	//server 창을 멈추지 않고 끌 때 사용할 메소드. 아직 사용하지 않음.(as of 토요일 아침)
	void stopServerSocket(){
		try{
			if(serverSocket != null)	serverSocket.close();
			if(socket != null)			socket.close();
//			if(chatCom.dis != null)		chatCom.dis.close();
//			if(chatCom.dos != null)		chatCom.dos.close();
		}catch(IOException e){
			chatserver.addLog("ChatServerThread: close Socket(); "+e);
		}
	}
}
