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
		int command = -1;
		String name = "";
		// 예외가 발행해도 종료하지 않는다.
		try {
			while( true ) {
				
				protocol = dis.readChar();
				switch( protocol ) {

				// 일반적인 메시지 처리.
				case 'M' :
					msg = dis.readUTF();
					client.appendClientLog(msg);
					break;
					// 관리자 메시지
				case 'Z' :
					command = dis.readInt();
					
					switch( command ) {
					// -4는 서버 중지 명령어.
					case -4 :
						msg = dis.readUTF();
						client.appendClientLog(msg);
						client.doExitEvent(-4);
						client.changeButton(false);
						break;
					}
					break;
				case 'U' :
					msg = dis.readUTF();
					String[] clientNames = msg.split(",");
					client.updateClient( clientNames[0], clientNames[1]);
					break;
				}
			}
		}catch (IOException e) {

		}

	}



}
