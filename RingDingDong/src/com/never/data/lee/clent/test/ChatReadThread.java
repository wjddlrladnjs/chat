package com.never.data.lee.clent.test;

import java.io.DataInputStream;
import java.io.IOException;


public class ChatReadThread extends Thread{
	private ChatClient chatClient;
	private DataInputStream dis;

	public ChatReadThread(ChatClient chatClient, DataInputStream dis) {
		this.chatClient = chatClient;
		this.dis = dis;
	}

	public void run(){
		char protocol;
		String msg = "";
		try{
			while(true){
				protocol = dis.readChar();
				switch(protocol){	
				case 'M':
					msg = dis.readUTF();
					chatClient.addLog(msg);
					break;
				case 'U':
					msg = dis.readUTF();
					String[] tmpList = msg.split(",");					
					chatClient.addUserList(tmpList);
					break;
				case 'R':
					msg = dis.readUTF();
					tmpList =  msg.split(",");
					chatClient.updateUser(tmpList[0], tmpList[1]);
					break;
				case 'N':
					msg = dis.readUTF();
					chatClient.setChatWindowName(msg);
					break;
				case '2' :
					String logdata = "";
					logdata= dis.readUTF();
					new LogDown(chatClient,logdata).start();;
					break;
				case 'i':
					chatClient.addLog("서버에서 프로토콜 i를 받았습니다. ");
					byte[] bytes = null;
					String str = dis.readUTF();
					System.out.println("받기 utf");
					int length = dis.readInt();
					bytes = new byte[length];
					System.out.println("받기 int");
					dis.read(bytes, 0, length);
					System.out.println("받기 byte");
					
					System.out.println("받기 complete");
					chatClient.addLog("파일을 받았습니다. 배경화면으로 설정합니다.");
					
					//setThisImageAsBackground();
					break;
				}
			}
		}catch(IOException e){

		}

	}

}
