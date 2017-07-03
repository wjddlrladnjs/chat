package com.never.data.kim.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BgrImage extends Thread{
	
	Socket s;
	MultiChatServer server;
	ServerThread thread;
	File file;
	Comm comm;
	byte[] imgData;// imgFile;
	int fileLength;
	String fileName;
	boolean isRunning = false; 
	
	//I/O 멤버변수들
	FileInputStream fis = null;
	FileOutputStream fos = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	public BgrImage(MultiChatServer server, ServerThread thread, Comm comm, Socket s){
		
		this.server = server;
		this.thread = thread;
		this.comm = comm;
		this.s = s;
		//
		
		
	}
	
	public void run(){
		
		varDistribute();	//length와 byte[]를 얻어와 저장한다
		System.out.println("여기까진 왔따!");
		isRunning = true;
		
		//이제 byte에서 파일로 변환해야겠지?
		while(isRunning){
			System.out.println("BgrImage들어왔어!");//////////////
			thread.sendImageData2All('i', fileName, fileLength, imgData);
			server.addChatAlert(fileName + " 파일을 각 클라이언트로 보냄");
			System.out.println("파일 클라들한테 보냈어!");
			isRunning = false;	//다 했으면 쓰레드 종료
			
		}//while문 끝
	}
	
	public void varDistribute(){	//length와 data 얻어오는 메솓
		server.addChatAlert("프로토콜 다음으로 정보 읽는다");

		//DIS DOS 설정
		try {
			this.dis = new DataInputStream(s.getInputStream());
			this.dos = new DataOutputStream(s.getOutputStream());
		} catch (IOException e1) {
		} 

		
		try {
			System.out.println("A1");
			fileName = dis.readUTF();
			System.out.println("A2");
			fileLength = dis.readInt();
			System.out.println("A3");
			dis.readFully(imgData, 0, fileLength);
			System.out.println("A4");
		} catch (IOException e) {
			server.addChatAlert("삐빅! read UTF, Int, Byte[] error!: " + e);
		}
		
		server.addChatAlert(String.format
				("받은 파일명: %s, 파일 크기: %d", fileName, fileLength));
	}
	
}
