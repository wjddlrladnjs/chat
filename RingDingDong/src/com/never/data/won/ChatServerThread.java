package com.never.data.won;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class ChatServerThread implements Runnable{
	private int port;
	private ChatServer chatserver;
	private ChatCom chatCom;
	private boolean onAir;
	ServerSocket serverSocket = null;
	Socket socket = null;
	
	static HashMap<String, String> ipNname = new HashMap<String, String>();
	
	ArrayList<ChatCom> chatComList = new ArrayList<>();

	public void sendAllmember(char type, String name,String msg){
		for(ChatCom com : chatComList){//내가 작성하는 객체 새로운 이름:기존 리스트명
			com.sendMessage(type,name, msg);
		}		
	}
	
	public void sendExceptSelf(char type, String name,String msg, ChatCom me){
		for(ChatCom com : chatComList){//내가 작성하는 객체 새로운 이름:기존 리스트명
			if(com != me){
			com.sendMessage(type,name, msg);
			}
		}
	}
	
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
		
		chatCom.sendMessage('L', userList,userList);
	}
	
	
	
	public ChatServerThread(ChatServer chatserver, int port) {
		this.chatserver = chatserver;
		this.port = port;
	}
	
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

	@Override
	public void run() {		
		try{
			serverSocket = new ServerSocket(port);			
			chatserver.addLog("Server is ready....");
			
		}catch(IOException e){
			chatserver.addLog("ChatServerThread: ServerSocket; "+e);
		}
		chatserver.buttonOnState();

		onAir = true;
		while(onAir){
			try{
				socket = serverSocket.accept();
				chatserver.addLog("Accpted IP: "+socket.getInetAddress().getHostAddress());
				chatserver.addLog("Accepted time: "+getTime());
				
				chatCom = new ChatCom(this, chatserver, socket);
				chatComList.add(chatCom);
				new Thread( chatCom ).start();

			}catch(IOException e){
				chatserver.addLog("ChatServerThread: serverSocket.accept(); "+e);
			}
		}
	}
	
	

	String getTime(){
		String time = "";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat();
		time = format.format(now.getTime());
		return time;
	}

}
