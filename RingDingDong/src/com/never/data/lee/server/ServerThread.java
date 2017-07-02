package com.never.data.lee.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements  Runnable {


	int port;
	ChatServer server ;
	boolean onAir ;
	ServerSocket ss ;

	

	ArrayList<Com> comList = new ArrayList<Com>();

	public void sendUserList(Com com){ //유저리스트 
		String userList= "";

		StringBuilder sb = new StringBuilder();
		for(Com c : comList){
			sb.append(c.name).append(",");

		}

		userList = sb.toString();// 스트링빌더를 넣는다 
		userList= userList.substring(0,userList.length()-1); //글자하나를 뺀다   서브스트링   메소드확인 해

		com.sendMessage('L', userList);
	}

	public void exitCom(Com com ){//어레이리스트에서 객체제거하기 (나가기)
		comList.remove(com); //리무브 (오브젝트 )
	}

	public void sendOneMessage(Com comme,String name ,String pName , String msg){ //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		Com com = null ; //에러떨어질까봐 
		for(int  i = 0 ; i <comList.size() ; i ++){ //for: 은 무조건다돌고  ;; 은 돌다가나올수있으니 하다나올거면 ;;사용
			com = comList.get(i);// 컴리스트에있는것을 . 겟 한다 (i번째껄)
			if(com.name.equals(pName)){
				
				
				break;
			}
		}
		
		if(com != null){
			comme.sendMessage('M', pName+" 님 에게 : "+msg);//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			com.sendMessage('M',name+" 님의 귓속말 :" +msg);
		}
		
		
	}
	
	public void sendAllMessage(char protocol ,String msg){
		//		for( int i = 0 ; i < comList.size() ; i++){
		for( Com com : comList){   //디스를보내고 디스가맞으면 안보낸다 그러면 자기채팅은안나옴 

			com.sendMessage(protocol,msg);
		}
	}

	public void sendAllMessage(char protocol ,String msg,Com me){
		//		for( int i = 0 ; i < comList.size() ; i++){
		for( Com com : comList){   //디스를보내고 디스가맞으면 안보낸다 그러면 자기채팅은안나옴 
			if(com !=me){
				com.sendMessage(protocol,msg);
			}
		}
	}



	public ServerThread(ChatServer server ,int port) {
		this.port = port;
		this.server = server;
	}	
	public void stop(){
		onAir = false ;
		try{
			ss.close();    //서버소켓을 강제로 종료 

			for(Com com : comList ){
				com.dos.writeUTF("서버끊김 ");
				com.s.close();

			}
		}catch(IOException e ){
			server.addChatList("서버가 강제로 종료되는도중오류"+e);
		}
		server.settingsStartButton();
	}

	public void run() {
		//쓰레드 
		ss =null; 

		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			server.addChatList("port오류 "+e);
			return; 
		}

		server.settingsStopButton();
		server.addChatList("서버가 시작됨  Port : "+port);
		onAir = true;

		Socket s = null;
		Com com ;

		String remoteIP= "";
		while (	onAir){ //오류가나면 서버를 죽이겠다 그러면 와일문 밖에 서버는안죽는다 하면 안쪽에 트라이캐치

			try {

				server.addChatList("접속대기중 ...");
				s = ss.accept(); //될때마다 새로운 소켓이 생성됨 
				remoteIP= s.getInetAddress().getHostAddress();
				server.addChatList("접속완료 접속IP " +remoteIP );

				//접속이이뤄진 부분 
				com = new Com(server,this,s);
				comList.add(com);

				com.start(); //접근할 변수가없으면 그냥생성해서 스타트하면되는데 모아놓을려면 변수를 따로잡고 
				// 변수를 리스트에 올려줘야한다 





			} catch (IOException e) {
				server.addChatList("서버접속 대기 실패"+e);
			}

		}
		server.addChatList("서버가 종료됨 ");

	}

}
