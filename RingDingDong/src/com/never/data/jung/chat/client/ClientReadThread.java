package com.never.data.jung.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ClientReadThread implements Runnable{

	private ChatClient client;
	private DataInputStream dis;
	private DataOutputStream dos;

	public ClientReadThread(ChatClient chatClient) {
		this.client = chatClient;
		this.dis = chatClient.getDis();
		this.dos = chatClient.getDos();
	}

	@Override
	public void run() {

		char protocol = ' ';
		String msg = "";
		int command = -1;
		String clientName = "";
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
					
				case 'N' :
					clientName = dis.readUTF();
					client.setNickName(clientName);
					break;
				// 서버 접속시 클라 이름 목록을 받아서 처리.
				case 'L' :
					msg = dis.readUTF();
					String[] clientNameList = msg.split(",");
					StringBuffer sb = new StringBuffer();
					
					client.showUserList( clientNameList );
					// 여기까지 진행하면 접속할 때 값을 받아서 리스트에 추가는 되지만,
					// 갱신이 되지 않는 상태이다. 갱신을 진행해보자.
					for( String cName : clientNameList ) {
						sb.append(cName).append("\n");
					}
					client.appendClientLog("**** 현재 접속자 ****");
					client.appendClientLog(sb.toString().substring(0, sb.length() - 1));
					client.appendClientLog("*****************");
					break;
				// 신규 접속자 알림.
				case 'A' :
					msg = dis.readUTF();
					client.addClientName( msg );
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
				case 'D' :
					msg = dis.readUTF();
					client.deleteClient( msg );
					break;
				case '/' :
					client.getChatCommand( this );
					break;
				}
			}
		}catch (IOException e) {

		}

	}



}
