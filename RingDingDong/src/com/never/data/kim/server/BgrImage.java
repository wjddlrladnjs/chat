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
			
			thread.sendImageData2All('i', fileLength, imgData, comm);
			server.addChatAlert("이미지를 클라이언트로 보냄");
			isRunning = false;	//다 했으면 쓰레드 종료
			
		}//while문 끝
		
	}
	
	public void varDistribute(){	//length와 data 얻어오는 메솓
		fileLength = comm.readLength();
		imgData = comm.readImageData(fileLength);
	}
}
