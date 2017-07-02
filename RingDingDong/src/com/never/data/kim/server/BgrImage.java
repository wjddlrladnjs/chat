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
	Comm comm;
	File file;
	byte[] imgData;// imgFile;
	int fileLength;
	String fileName;
	boolean isRunning = false; 
	
	//I/O 멤버변수들
	FileInputStream fis = null;
	FileOutputStream fos = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	public BgrImage(MultiChatServer server, ServerThread thread, 
			Comm comm){
		
		this.server = server;
		this.thread = thread;
		this.comm = comm;
		
	}
	
	public void run(){
		
		varDistribute();	//length와 byte[]를 얻어와 저장한다
		
		isRunning = true;
		
		//이제 byte에서 파일로 변환해야겠지?
		while(isRunning){
			System.out.println("BgrImage들어왔어!");
			thread.sendImageData2All('i', fileName, fileLength, imgData, comm);
			server.addChatAlert(fileName + " 파일을 각 클라이언트로 보냄");
			System.out.println("파일 클라들한테 보냈어!");
			isRunning = false;	//다 했으면 쓰레드 종료
			
		}//while문 끝
		
	}
	
	public void varDistribute(){	//length와 data 얻어오는 메솓
		fileName = comm.readFileName();
		fileLength = comm.readLength();
		imgData = comm.readImageData(fileLength);
		
		server.addChatAlert(String.format
				("받은 파일명: %s, 파일 크기: %d", fileName, fileLength));
	}
}
