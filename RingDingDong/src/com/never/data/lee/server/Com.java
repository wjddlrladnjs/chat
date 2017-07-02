package com.never.data.lee.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Com extends Thread{
	Socket s ;
	DataInputStream dis=null;
	DataOutputStream dos=null ;
	ChatServer server ;
	ServerThread serverthread ;
	 
	String name="";
	static int num =0;
	boolean onAir ;
	Com(ChatServer server,ServerThread serverthread,Socket s){
		name = "user"+ ++num;
		this.server = server;
		this.serverthread = serverthread;
		this.s = s;
		try{
			dis = new DataInputStream(s.getInputStream());//소켓이있는것가지고 통신
			dos = new DataOutputStream(s.getOutputStream()); //소켓이있는것가지고 통신하려면 s에있는 인풋 아웃풋을 사용 
			server.addChatList("IO객체를 얻었습니다.");
			//받는부분 



			



		}catch(IOException e ){
			server.addChatList("IO 에러가 발생함"+e); //이서버는 여기나 밖이나상관없는데 아래 run에서부터 그냥써도됨
		}

	}
	public void sendMessage(char protocol,String msg){ // 서버채팅로그를긁어오자 
		try {
			dos.writeChar(protocol);
			dos.flush();

			dos.writeUTF(msg);
			dos.flush(); 
		} catch (IOException e) {
			server.addChatList("샌드메시지전송오류 "+e);
			
		}
	}
	public void run() {
		if(dos ==null){
			server.addChatList("IO에러가발생함");
			return;
		}
		onAir = true;
		char protocol;
		String msg=""; 

		sendMessage('N', name); //시작하자마자 닉네임을 클라이언트에게보내준다    근데 두개가뜨니까 이걸 클라이언트에 보내는걸 주석처리시켯다  N
		
		serverthread.sendAllMessage('M', String.format("  #  %s 님이 접속하셧습니다 .  ", name));

		
		
		//사용자 리스트 목록 구해서 보내기 
		serverthread.sendUserList(this);
		//나빼고 나머지에게 내가 들어온것을 알리기 
		serverthread.sendAllMessage('A', name,this);

		
		
		while(onAir){
			try{
				protocol  = dis.readChar();
				switch(protocol){
				case 'M' :
					msg= dis.readUTF();
					server.addChatList(name+"으로부터 받은 메시지 : "+msg);
					//					dos.writeUTF("에코"+msg);
					//					dos.flush();//버퍼 메모리에쌓여잇는것들을 보내라!!!!!!
					//주석같이하면 에코밖에안되니까 바꾸자~ 
					//전체에게 보내기
					//					serverthread.sendAllMessage(msg); //접근하려고 또생성자에 씀 
					serverthread.sendAllMessage(protocol,String.format("[%s] : %s", name,msg)); // 

					break;
				case 'R' :
					String newName= dis.readUTF(); //문자열을읽어옴 
					server.addChatList(name +"님이  "+newName+"로 이름을 변경하셧습니다");
					serverthread.sendAllMessage('M',String.format("  #   %s  님이    %s 으로 이름을 변경하셧습니다",name,newName));
				
					serverthread.sendAllMessage('U', name + ","+newName);
					
					name = newName;
					
					break;
				case 'X' :
//					server.addChatList(String.format("%s   접속종료 ", name));
					server.addChatList(name + " 님 접속종료 ");
					serverthread.exitCom(this); //자기자신을빼버렷!  이거기준으로 D는  위에라면 this까지 아래면 this빼고 
					onAir = false;
					serverthread.sendAllMessage('M',String.format("  #   %s  님이   채팅방을나갔습니다",name));
					serverthread.sendAllMessage('D',name);
					break;
				
				case 'P':
					String pName = dis.readUTF(); //이름받아옴 
					msg = dis.readUTF();
					server.addChatList(name + " 님이"+pName+" 에게 "+msg+"라고 귓속말을보냄");
					serverthread.sendOneMessage(this,name,pName,msg);
					
					break;
				
				case '1' :
					
					String namelog= dis.readUTF();
					
					server.addChatList(namelog+"님이 서버의로그를 저장하였습니다 ");
					
					String log = server.myLog();
					
					dos.writeChar('2');//서버에서 또 클라이언트로 보냄 
					dos.flush();
					dos.writeUTF(log);
					dos.flush();
					
					break;
				
				}//스위치의끝
				
				
			}catch(IOException e ){
//				server.addChatList("com run오류");
//				server.addChatList(name +"- 연결종료");
//				onAir =false ; 
//				serverthread.comList.remove(this);
			}

		}//while문 end
		
		if(dis != null){
			try {
				dis.close();
			} catch (IOException e) {
			}
		}
		if(dos != null){
			try {
				dos.close();
			} catch (IOException e) {
			}
		}
		if(s != null){
			try {
				s.close();
			} catch (IOException e) {
			}
		}
		



	}





}
