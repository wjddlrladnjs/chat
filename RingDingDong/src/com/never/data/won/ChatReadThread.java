package com.never.data.won;

import java.io.IOException;


public class ChatReadThread extends Thread{
	private ChatClient client;
	private String input;
	private char type;

	public void run(){
		try{
			while(true){
				type = client.dis.readChar();
				switch(type){
				//대화방 진입에 따른 Server에서 보내는 메시지
				case 'A':
					input = client.dis.readUTF();
					client.addLog(input);
					input = client.dis.readUTF();
					client.addUser(input);
					break;
				//한 유저의 메시지 전송에 따른 전체에게 메시지 전송	
				case 'M':
					input = client.dis.readUTF();
					client.addLog(input);
					break;
				//한 유저의 이름 변경에 따른 전체에게 이름 변경 메시지 전송	
				case 'N':
					input = client.dis.readUTF();
					client.addLog(input);					
					input = client.dis.readUTF();
					String[] userNames = input.split(",");
					client.updataUser(userNames[0], userNames[1]);					
					break;
				//한 유저의 대화방 퇴장에 따른 메시지 전송	
				case 'X':
					input = client.dis.readUTF();
					client.addLog(input);
					input = client.dis.readUTF();
					client.deleteUser(input);
					break;	
				//유저 리스 변경에 따른 메시지 전송
				case 'L':
					input = client.dis.readUTF();
					String[] nameList = input.split(",");
					StringBuilder sb = new StringBuilder();
					client.showUserList(nameList);
					for(String name : nameList){
						sb.append(name).append("\n");
					}
					client.addLog("========Now User List======");
					client.addLog(sb.toString());
					client.addLog("===========================");
					break;
				}
			}			
		}catch(IOException e){
			client.addLog("ChatReadThread client.dis.readUTF IOException");
		}
	}

	public ChatReadThread(ChatClient chatClient) {
		this.client = chatClient;
	}

}
