package com.never.data.kim.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable{
	
	int port;
	MultiChatServer server;
	boolean isActivated;
	ServerSocket ss;
	
	//매번 만들어지는 소켓을 가진 Comm쓰레드 묶어서 관리할 객체
	ArrayList<Comm> commList = new ArrayList<Comm>(); //이거랑 httpurlConnectino이랑붙는거래~
	
	public ServerThread(MultiChatServer server, int port){//객체를 전달하면 . 찍으면 원래거에 접근된다: Call by Reference
		this.server = server;
		this.port = port;
	}
	
	
	public void sendUserList(Comm comm){
		//arraylist를 모아서 쏴주면되겟지?
		
		String userList = "";
		//반복문일땐 StringBuilder써라1!!!!!!!!!!
		StringBuilder sb = new StringBuilder();
		
		for(Comm cm : commList){
			sb.append(cm.name).append(","); //반복문일떈 이걸로...
		}
		
		userList = sb.toString();
		userList.substring(0, userList.length() - 1);	//마지막한자에서 1을 뺸 나머지 0~ 1ength - 1까지..
		
		//섭->클로 보낼때 약속을 하자(엔터로 바꾸기로)
		
		
		//한사람인 comm한테만 보내야되지? 사용자 정보 리스트!
		comm.sendMessage('L', userList);
	}
	
	
	//arrayList에서 comm 객체 제거하기(나가기 구현)
	public void exitComm(Comm comm){//commList라는 자원에 접근 == 싱크로나이즈드로 접근은 한놈씩만 하게해야한다!
		commList.remove(comm);
	}
	
	
	public synchronized void sendImageData2All(char protocol, String fileName, int fileLength, byte[] brr){
		for(Comm comm : commList){
			comm.sendImageData(protocol, fileName, fileLength, brr);
		}
	}
	
	public synchronized void sendImageData2All(char protocol, String fileName, int fileLength, byte[] brr, Comm self){
		for(Comm comm : commList){
			if(comm != self){
				comm.sendImageData(protocol, fileName, fileLength, brr);
			}
		}
	}
	
	
	public void sendMsg2All(char protocol, String msg){
		for(Comm comm : commList){ //ouya
			comm.sendMessage(protocol, msg);
		}
	}
	
	public void sendMsg2All(char protocol, String msg, Comm self){ //오버로딩 오졌죠
		
		//나만빼고 보내고싶으면 이렇게
		
		for(Comm comm : commList){ //ouya		//퍄퍄;;;
			if(comm != self){
				comm.sendMessage(protocol, msg);
			}
		}
	}

	public void sendMsg2One(char protocol, String msg, String rName){ //오버로딩 오졌죠

		//나만빼고 보내고싶으면 이렇게

		//		for(Comm comm : commList){ 
		//			if(comm.name.equals(rName)){
		//				comm.sendMessage(protocol, msg);
		//			}
		//		}

		//for Colon은 전체를 무적권 다 하기때문에
		//중간에 빠져나올가능성있거나 하는거 있으면 i 루다가 해라.퍼포먼스^^
		Comm com = null;
		for(int i = 0; i < commList.size(); i++){
			com = commList.get(i);	//어레이리스트가 불러오는방법. 많이쓰니 필히 숙지하자!
			if(com.name.equals(rName)){
				break;	//찾았으면 나가요~
			}
		}
		if(com != null){		//이런것도중요해~
			com.sendMessage(protocol, msg);
		}
	}
	
	public void stop(){
		//외부에서 멤버변수 접근 직접하지 못허게하라
		//요렇게 메소드를 구현을 하도록 하여라
		isActivated = false;
		
		//isActivated false로 한다고 해도 accept()때문에 안꺼지죠?
		//서버소켓 강제로 잡아야죠?
		
		try{
			ss.close();
		}catch(IOException e){
			server.addChatAlert("소켓을 강제로 닫는다!");
		}
		
	}
	
	public void run(){
		
		ss = null;
		
		try {
			ss = new ServerSocket(port); //상대방접속대기할라믄만들어야대
			//server.getTaOutput().append("한조 대기중.....\n");
		} catch (IOException e) {
//			server.getTaOutput().append("포트 에러: " + e + "\n");
			server.addChatAlert("포트 오류: " + e);
			return;	//이미돌고있는데 또왔다? 그럼 리턴^^
		} 
		
		server.setStartBtnInvisible();	//시작시에만 버튼 조정해준다(에러없을시에만)
		
		server.addChatAlert("서버가 시작되었다구! 포트번호: " + port);
		
		isActivated = true;

		//서버가 죽지않는다! : try catch 를 while문 안에다
		while(isActivated){
			//클라 올때까지 기달료~
			
			Socket s = null;
			String remoteIP = "";
			Comm comm = null;	//잠깐 사용할 변수
			
			try {						//랜선끊어질수잇으니까..
				server.addChatAlert("한조 대기중....");

				s = ss.accept();
				remoteIP = s.getInetAddress().getHostAddress();
				
				server.addChatAlert("접속 IP: " + remoteIP);
				
				//접속이 이루어진 부분
				//주거니~받거니~주거니~받거니~
				
				//통신할거 쓰레드 분리해야하죠?
				comm = new Comm(server, this, s);	//접근할일이생겻으니 한줄로안가지~
				commList.add(comm);	//여다가 add해야죠?
				comm.start();//comm쓰레드하나만들어소ㅓ 동작하세요~
				//그리고 난 다시 접속을 기다릴고얌
				//sendUserList(comm);
				
				
			} catch (IOException e) {
				server.addChatAlert("접속대기 실패 " + e);
			}
		}
		server.addChatAlert("서버가 멈추었다!");
	}
	
}
