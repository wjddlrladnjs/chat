package com.never.data.kim.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Comm extends Thread{
	
	Socket s;//^0^7
	MultiChatServer server;
	ServerThread thread;
	
	//user닉넴을위해서
	String name;
	static int nameCnt = 0;	//이 객체가 여러번만들어져도 한개죠?
	
	//io는 byte밖에안되는데 난 문자열 등등 주고받고싶어!
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	//이미지를 위하여
	FileInputStream fis = null;
	FileOutputStream fos = null;
	BgrImage bgrImage;
	
	boolean isRunning = false;
	
	public Comm(MultiChatServer server, ServerThread thread, Socket s){//생성자 인자전달! 현업friendly :D:D:D:D:D:D:D:D:D:D:D:D:D:D:D:D:D
		name = "user" + ++nameCnt;	//디폴트네임	//개신박하네 이런식으로 static 변수를 ㄷㄷ(자동차 고유번호 생성 등에도 used)
		this.server = server; 
		this.thread = thread;
		this.s = s;
		try{
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			
			server.addChatAlert("IO객체를 얻었습니다.");
		}catch(IOException e){
			//입출력에대한 오류
			server.addChatAlert("입출력 오류! " + e);
				//여기서 return해도 실행이 된다..!
		}
		
		//이름 바로보낼까?
	}
	
	//이름넣으면
//	public Comm(MultiChatServer server, ServerThread thread, Socket s, String name){//생성자 인자전달! 현업friendly :D:D:D:D:D:D:D:D:D:D:D:D:D:D:D:D:D
//		name = name;	//디폴트네임	//개신박하네 이런식으로 static 변수를 ㄷㄷ(자동차 고유번호 생성 등에도 used)
//		this.server = server; 
//		this.thread = thread;
//		this.s = s;
//		try{
//			dis = new DataInputStream(s.getInputStream());
//			dos = new DataOutputStream(s.getOutputStream());
//			
//			server.addChatAlert("IO객체를 얻었습니다.");
//		}catch(IOException e){
//			//입출력에대한 오류
//			server.addChatAlert("입출력 오류! " + e);
//				//여기서 return해도 실행이 된다..!
//		}
//	}
	
	//이미지를 위한 메소드 추가
	public synchronized int readLength(){
		int tempLength = 0;
		try{
			tempLength = dis.readInt();
		}catch(IOException e){
			server.addChatAlert("reveive error: " + e);
		}
		
		return tempLength;
	}
	
	public synchronized byte[] readImageData(int dataLength){
		byte[] tempImageData = null;
		System.out.println("클라가 준 dataLength: " + dataLength);
		
		try{
			tempImageData = new byte[dataLength];
			dis.readFully(tempImageData, 0, dataLength - 1);
		}catch(IOException e){
			server.addChatAlert("reveive error: " + e);
		}
		return tempImageData;
	}
	
	public synchronized String readFileName(){
		String tempName = "";
		
		try{
			tempName = dis.readUTF();
		}catch(IOException e){
			server.addChatAlert("reveive error: " + e);
		}
		return tempName;
	}

	//이미지 byte배열 보내기
	public synchronized void sendImageData(char protocol, String fileName, int imgSize, byte[] imgData){
		try{
			dos.writeChar(protocol);	//프로토콜보내고 그담에메세지보내
			dos.flush();	//live한 data들이 좀 빨리감. 없으면 살짝 딜레이생김
			dos.writeUTF(fileName);
			dos.flush();
			dos.writeInt(imgSize);
			dos.flush();
			dos.write(imgData);
			dos.flush();
		}catch(IOException e){
			server.addChatAlert("send Error: " + e);
		}
	}
	
	
	public void sendMessage(char protocol, String msg){
		try{
			dos.writeChar(protocol);	//프로토콜보내고 그담에메세지보내
			dos.flush();	//live한 data들이 좀 빨리감. 없으면 살짝 딜레이생김
			dos.writeUTF(msg);
			dos.flush();
		}catch(IOException e){
			server.addChatAlert("send Error: " + e);
		}
	}
	
	public void setIsRunning(){
		isRunning = !isRunning;
	}
	
	public void run(){
		if(dos == null){
			server.addChatAlert("IO 오류로 인하여 그냥 꺼버립니다.");
			return;	//윗부분에서 오류났으면 픽 상해부렀어! //반복문 피하기
		}
		
		isRunning = true;
		
		char protocol = '\u0000';
		String msg = "";
		String tName = "";
		String newName = "";
		//app 동작할떄 횐갑하고 록인하고 물건확인하고 게시판가고.... ==> Scenario
		
		
		sendMessage('N', name);	//시작하자마자이름보내고싶으면 요렇게!
		//방문자 알러트(나빼고 다)
		thread.sendMsg2All('M', String.format("## [%s] 님이 입장하였습니다. -주인 백 ##", name), this);
		
		//리스트추가 (유저 리스트 목록 구해서 보내기)
		thread.sendUserList(this); //나자신에게 리스트를 하나 줄까
		
		thread.sendMsg2All('A', name, this);//나 빼고 나머지애들한테 내가들어온것을 알린다
		
		//상대방이주는문자받자
		while(isRunning){		//이리할줄 아는게 진정 switch case나 if를활용알줄아는것이다
			
			try {
				protocol = dis.readChar();
				switch(protocol){ //항상 문자하나 보내고 뭐 보내는걸루다가
				case 'M':	
					msg = dis.readUTF();	//먼저 읽고
					
					//서버도 보여줘~
//					server.addChatAlert("받은문자열: " + msg);
					
					//Comm들을갖고있는 리스트가 하면돼!
					thread.sendMsg2All(protocol, String.format("[%s] %s", name, msg));
					
					break;
				case 'N':	//new protocol	: 시작하자마자 클라한테 이름보내고 새이름 되받는다
					
					break;
				case 'R':	//rename	//나빼고^^이름^^
					tName = name;	//구name을 저장
					newName = dis.readUTF();
					name = newName;	//이름을 바꾼다
					sendMessage('R', name);	//바꾼이름을 보내준다
					thread.sendMsg2All('M', String.format("## %s has renamed to %s ##", tName, name), this);
					
					//업데이트 시켜준다
					thread.sendMsg2All('U', tName + "," + newName);	//구이름만 보낸다
					
					break;
				case 'C':
					break;
				case 'X':
					server.addChatAlert(name + " has left the server.");	//서버로그
					
					thread.exitComm(this);	//나 자신을 종.료.시.켜.버.린.다(쑻)
					
					thread.sendMsg2All('M', String.format("## %s is leaving the server :( ##", name));
					thread.sendMsg2All('D', name);//리스트에서 제거하는거
					isRunning = !isRunning;
					break;
				case 'P':	//add user (이떄 전체로 보낸다)
					String rName = dis.readUTF();
					msg = name + "의 pm: " + dis.readUTF();
					server.addChatAlert(rName + "에게" + msg + "보냄");//정통법에의거하여 전산담당자가 로그 다 저장해둬야함 안그러면 처벌받음 나라에서달라면 줘야함
					thread.sendMsg2One(protocol, msg, rName);
					
					break;
					
				// 이미지
				case 'i':
				//클라서 서버로 보내는 프로토콜
				//dos.writeChar('i');
				//dos.writeUTF(fileName);
				//dos.writeInt(length);
				//fos.write(brr);
					server.addChatAlert("프로토콜 i를 받았습니다.");
//					bgrImage = new BgrImage(server, thread); 
					bgrImage = new BgrImage(server, thread, this);
					
					bgrImage.start(); //데이터 파일로 변환한다음 각각 클라에 쏘는거
					server.addChatAlert("이미지 보내기용 쓰레드를 생성합니다.");
					
					break;
				}
				
				
				
				
				//don't rely on libraries too much!!
				//write your own whenever possible!!!!!
			} catch (IOException e) {
				
			}
			
			
			
		}////// while문 E.N.D ///////
		
		if(dis != null){
			try{
				dis.close();	//입력 객체 종료
			}catch(IOException e){
			}
		}
		if(dos != null){
			try{
				dos.close();	//출력 객체 종료
			}catch(IOException e){
			}
		}
		if(s != null){
			try{
				s.close();		//소켓 객체 종료
			}catch(IOException e){
			}
		}
		
		
	}
	
	
}
