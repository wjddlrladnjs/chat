package com.never.data.jung.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientReadThread implements Runnable{
	
	ChatClient client;
	DataInputStream dis;
	DataOutputStream dos;
	
	public ClientReadThread(ChatClient chatClient) {
		this.client = chatClient;
		this.dis = chatClient.getDis();
		this.dos = chatClient.getDos();
	}

	@Override
	public void run() {
		
		String msg = "";
		char protocol = ' ';
		String name = "";
		
		// 예외가 발행해도 종료하지 않는다.
		try {
			System.out.println("여긴 오니?");
			while( true ) {

				protocol = dis.readChar();
				switch( protocol ) {
				
				// 일반적인 메시지 처리.
				case 'M' :
					msg = dis.readUTF();
					client.appendClientLog(msg);
					break;
				}
				
				
			}
			
		}catch (IOException e) {
			
		}
		
	}
	
	

}
