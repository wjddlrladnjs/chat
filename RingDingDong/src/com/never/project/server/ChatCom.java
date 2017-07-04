package com.never.project.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.never.data.kim.server.BgrImage;

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
	public static ArrayList<String> userName = new ArrayList<>();	//사용자 대화명 저장을 위한 ArrayList

	//이미지 변수 test
	FileInputStream fis = null;
	FileOutputStream fos = null;
	BgrImage bgrImg;

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
	
	
	
	
/*
 * 
 * 	봉준
 * 
 */
	// 서버 명령어 모음을 객체로 보낸다.
	public void sendChatCommand( char protocol ) {

		HashMap<String,String> chatCommand = server.chatCommand;
		ObjectInputStream oos = null;
		DataInputStream odos = null;
		File f = null;
		String filePath = "./src/com/never/data/jung/chat/server/file/";
		String fileName = "hash.map";
		try {
			f = new File(filePath, fileName);
			dos.writeChar(protocol);
			int size = (int)f.length();
			byte[] data = new byte[size];
			odos = new DataInputStream(new FileInputStream(f));
			odos.readFully(data, 0, size);
			dos.writeUTF(fileName);
			dos.flush();
			dos.writeInt(size);
			dos.flush();
			dos.write(data, 0, size);
			dos.flush();
			server.addLog("서버 명령어 전송 성공" );
			
		} catch (IOException e) {
			server.addLog("메시지  전송 애러 : " + e.toString());
		} finally {
			if( oos != null ) {
				try { oos.close(); oos = null; } catch (IOException e) {server.addLog("객체 생성 애러" + e.toString());}
			}
			if( odos != null ) {
				try { odos.close(); odos = null; } catch (IOException e) {server.addLog("객체 생성 애러" + e.toString());}
			}
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
			sendChatCommand('/');
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

					server.addLog( this.name+" 님이 서버의 로그를 저장하였습니다 ");
					String log = server.myLog();

//					dos.writeChar('2');//서버에서 또 클라이언트로 보냄 
//					dos.flush();
//					dos.writeUTF(log);
//					dos.flush();
					serverThread.sendOnlyOne('2', log, this);
					
					break;
				case '5':
					String pName = dis.readUTF(); //이름받아옴 
					msg = dis.readUTF();
					server.addLog(name + " 님이"+pName+" 에게 "+msg+"라고 귓속말을보냄");
					serverThread.sendOneMessage(this,name,pName,msg,userName);
					
					break;
					

					///이미지
				case 'i':
					byte[] bytes = null;
					server.addLog("프로토콜 i를 받았습니다.");

					String str = dis.readUTF();
					System.out.println("받기 utf : " + str);
					int length = dis.readInt();
					System.out.println("받기 int : " + length);


					bytes = new byte[length];

					dis.readFully(bytes, 0, length);
					System.out.println("받기 byte : " + bytes.length);
					

					System.out.println("받기 complete");
					server.addLog(String.format("%s 에게서 파일을 받았습니다."
							+ "파일명 : %s, 크기: %d", this.name, str, length));

					//이제 쓰자
					//sendImageData('i', str, length, bytes);

					serverThread.sendImageData2All('i', str, length, bytes);
					server.addLog("파일을 모든 클라에 전송합니다.");
					break;
				}
			} catch (IOException e) {
				server.addLog("[class]ChatCom [method] run2 "+e);
			}
		}
	}
	//run method end

	//이미지 파일 보내는 메소드
		public void sendImageData(char protocol, String fileName, int imgSize, byte[] imgData){
			try{
				dos.writeChar(protocol);	//프로토콜보내고 그담에메세지보내
				dos.flush();	//live한 data들이 좀 빨리감. 없으면 살짝 딜레이생김
				dos.writeUTF(fileName);
				dos.flush();
				dos.writeInt(imgSize);
				dos.flush();
				dos.write(imgData);
				dos.flush();
			}catch(IOException e){
				server.addLog("send Error: " + e);
			}
		}

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
