package com.never.data.lee.server.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.never.data.lee.server.Com;


public class ChatServerThread implements Runnable{
	//객체 변수
	private ChatServer chatServer;		//Log창 등에 접근하기 위한 Chatserver 객체
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
		this.chatServer = chatserver;
		this.port = port;
	}

	//run 메소드. serversocket = socket 접속
	@Override
	public void run() {
		try{
			//server에서 client접속 대기
			serverSocket = new ServerSocket(port);			
			chatServer.addLog("Server is ready....");			
		}catch(IOException e){
			chatServer.addLog("ChatServerThread: ServerSocket; "+e);
		}
		
		chatServer.buttonOnState();	//버튼 상태 변경

		onAir = true;
		while(onAir){
			try{
				socket = serverSocket.accept();	//client 접속 대기
				//client 접속 성공시 server에 남길 메시지
				chatServer.addLog("Accpted IP: "+socket.getInetAddress().getHostAddress());
				chatServer.addLog("Accepted time: "+getTime());
				//client와 지속 소통 위해 새로운 스레드 생성해서 chatcom에 전달. 
				chatCom = new ChatCom(this, chatServer, socket);
				chatComList.add(chatCom);		//chatcom 객체가 생성되면 ArrayList에 저장
				new Thread( chatCom ).start();
			}catch(IOException e){
				chatServer.addLog("ChatServerThread: serverSocket.accept(); "+e);
			}
		}
	}
	//run 메소드 종료
	
	//메시지 전송 이벤트(본인 포함)
	public void sendAllMessage(char protocol, String msg){
		for(ChatCom com : chatComList){
			com.sendMessage(protocol, msg);
		}
	}
	
	//메시지 전송 이벤트 (본인 제외)
	public void sendAllMessage(char protocol, String msg, ChatCom myself){
		for(ChatCom com : chatComList){
			if(com != myself) com.sendMessage(protocol, msg);
		}
	}
	
	//메세지 전송 이벤트(특정 유저)
	public void sendOnlyOne(char protocol, String msg, ChatCom you){
		ChatCom com = null;
		for(int i=0; i<chatComList.size(); i++){
			com = chatComList.get(i);
			if(com == you ){
				com.sendMessage(protocol, msg);
			}
		}
	}
	
	public void sendOneMessage(ChatCom comme,String name ,String pName , String msg, ArrayList<String> userName){ 
		ChatCom com = null ; //에러떨어질까봐 
		int number = 0;
		for(int  i = 0 ; i <userName.size() ; i ++){ //for: 은 무조건다돌고  ;; 은 돌다가나올수있으니 하다나올거면 ;;사용
//			com = chatComList.get(i);// 컴리스트에있는것을 . 겟 한다 (i번째껄)
			
			if(userName.get(i).equals(pName)){
				number = i;				
				break;
			}
		}
		
		
//		if(com != null){
			comme.sendMessage('M', pName+" 님 에게 : "+msg);
			chatComList.get(number).sendMessage('M',name+" 님의 귓속말 :" +msg);
//		}
		
		
	}
	

	
	String getTime(){
		String time = "";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat();
		time = format.format(now.getTime());
		return time;
	}

	void stopSocket(ChatServerThread serverThread, ChatCom self, Socket socket){		
		try {
			sendAllMessage('M', self.name+" is get out of this room. ", self);
			chatServer.addLog(self.name+" is get out of this room. ");
			self.disNdosClose();
			socket.close();
			chatComList.remove(self);
		} catch (IOException e) {
			chatServer.addLog("[class]ServerThread [method]stopSocket "+e);
		}
	}
	
	void stopServerSocket(){
		onAir = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			chatServer.addLog("[class]ServerThread [method]stopServerSocket "+e);
		}
	}

}
