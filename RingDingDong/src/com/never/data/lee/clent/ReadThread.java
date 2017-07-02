package com.never.data.lee.clent;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadThread extends Thread{
	DataInputStream dis;
	ChatCilent Cilent;
	public ReadThread(ChatCilent Cilent,DataInputStream dis) {
		this.Cilent = Cilent;
		this.dis=dis;
		//클라이언트 객체하나만 받는다면 this.dis = client.dis;
	}

	
	@Override
	public void run() {


		String msg ="";
		char protocol;
		try{
			while(true){
				protocol = dis.readChar();//커스텀프로토콜 
				switch(protocol){
				case 'M':
					msg = dis.readUTF(); //읽어와서 스트링에저장 

					Cilent.addChatList(msg);
					break;

				case 'N':
					msg = dis.readUTF(); //사용자의 이름이 메시지에담긴다  
					Cilent.setName(String.format("%s 님",msg));
					//					Cilent.addChatList(" #  "+msg+"님이 접속 하셧습니다.");  //중복으로나와서 제거


					break;
				case 'L' :  //원래정보로 받는다  - 파싱 
					msg = dis.readUTF(); // 리스트를받았다 
					String[] nameList = msg.split(",");
					//					for(String s : nameList){
					//						Cilent.addChatList(s);
					//					} //이게아니네
					//-----
										Cilent.showUserList(nameList);
					//					
					//					StringBuilder sb = new StringBuilder();
					//					for(String s  : nameList){
					//						sb.append(s).append("\n");
					//					}
					//					Cilent.addChatList("=======현재접속자리스트=======");
					//					Cilent.addChatList(sb.toString());
					//					Cilent.addChatList("=========================");
					break; 
				case 'A' : 
					msg = dis.readUTF(); //이름을읽어옴 사용자이름으로추가 
					Cilent.addUser(msg);
					
					break;
					
				case 'D':
					msg = dis.readUTF(); //이름을읽어옴 사용자이름을 J리스트에서 제거 
					Cilent.DeleteUser(msg);
					break;
					
						
				case 'U' : 
					msg = dis.readUTF();
					String[] userNames = msg.split(",");
					Cilent.updateUser(userNames[0],userNames[1]);
					
					
					break;
					
				case '2' :
					String logdata = "";
					logdata= dis.readUTF();
					new LogDown(Cilent,logdata).start();;
					break;
					
					
					
				}

			}
		}catch(IOException e){
			Cilent.addChatList("메시지오류 "+e);

		}



	}




}
