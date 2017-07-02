package com.never.data.kim.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ReaderThread extends Thread{
	
	MultiChatClient client;
	DataInputStream dis;
	
	public ReaderThread(MultiChatClient client, DataInputStream dis){
		this.client = client;
		this.dis = dis;	//야! 상식적으로 생각해봐! 생성자 인자받았는데 여서만쓰겠냐 아님 딴데서도 갖다쓰겠냐? 엉?
	}
	
	public void run(){
		String msg = "";
		char protocol = '\u0000';
		
		try{
			while(true){	//문제나면 루프풀리도록
				protocol = dis.readChar();	//얜 2byte자너..보통은 byte많이보내는데(65 이런식으로)
				
				switch(protocol){	//기존프로토콜은손대지마
				case 'M':
					msg = dis.readUTF();
					client.addChatAlert(msg);
					
					break;
				case 'N':
					msg = dis.readUTF();
					//client.addChatAlert(String.format("나의이름: %s", msg));
					//클라이언트에서 프레임창 건들수있는 메솓 클라이언트가서 만들자
					client.setFrameName(msg);
					client.addChatAlert("## welcome to chat server, " + msg + "! ##"); //공지사항은 #들을 많이쓰네~	//서버 전체메세지가 입장메세지 있어서 클라단계에서는 생략하기로함
					break;
				case 'R':
					msg = dis.readUTF();
					client.addChatAlert("## your name has been changed to: " + msg + " ##");
					break;
				case 'L':
					msg = dis.readUTF();
					
					String[] nameList = msg.split(",");	//얠 리스트에 추가하면 나타나는거얌
					
					client.showUserList(nameList);	//짤라진놈 줄테니까 니가알아서 리스트로 정렬해~
					
//					StringBuilder sb = new StringBuilder("\n");
//					//nameList만큼 반보ㅓㄱ한다
//					sb.append(nameList);
//					for(String name: nameList){
//						sb.append(name).append("\n");
////						System.out.println(name);
//					}
//					//현재 접속자리스트를 뿌려준다
//					client.addListAlert(sb.toString());
					break;
				case 'A':
					msg = dis.readUTF();
					client.addUser(msg);
					
					break;
				case 'D':
					msg = dis.readUTF();
					client.deleteUser(msg);
					
					break;
				case 'U':
					msg = dis.readUTF();
					//파-싱
					String[] name = msg.split(",");
					client.updateUserName(name[0], name[1]);
					break;
				case 'P':
					msg = dis.readUTF();
					client.addChatAlert(msg);
					break;
				}
				
			}
		}catch(IOException e){
			client.addChatAlert("msg오류: " + msg);
		}
	}
	
}
