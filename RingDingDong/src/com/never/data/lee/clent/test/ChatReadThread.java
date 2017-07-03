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
					new LogDown();
					break;
				}
			}
		}catch(IOException e){

		}

	}

}
