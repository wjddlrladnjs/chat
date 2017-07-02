package com.never.data.lee.clent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatCilent {
	JFrame f; 
	JButton btnConnect,btnSend ,btnRename,btnLogDown;//@@@@@@@@@@@@
	JTextField tfIP, tfPort,tfMessge;
	JTextArea taChatList ;

	
	DataInputStream dis;
	DataOutputStream dos ;

	ReadThread readThread ;

	String name;


	Socket s = null;

	JList<String> list;
	DefaultListModel<String> model ;

	ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			switch(e.getActionCommand()){
			case "A" :
				connertChatServer();
				break;
			case "B" :
				sendMessage();
				break;
			case "C" :
				reName();
				break;
			case "D" : 
				serverLogDown();
				break;
			}
		}
	};

	synchronized void  serverLogDown(){
		try {
			dos.writeChar('1');
			dos.flush();
			dos.writeUTF(name);
			dos.flush();
			
		} catch (IOException e) {}
	}


	void reName(){//닉네임을바꾸는메소드

		String name = tfMessge.getText();
		setName(name);
		sendReName(name);



	}

	void sendReName(String name){
		if(name.length()==0){
			return ;
		}
		try{
			dos.writeChar('R');
			dos.flush();

			dos.writeUTF(name);
			dos.flush();


		}catch(IOException e ){

		}
		tfMessge.setText("");
	}


	void sendMessage(){
		String msg = tfMessge.getText(); //tf메서드가많다 나중에확인해봐라 
		String rName ; 
		if(msg.length()==0){
			return ;
		}
		if(!msg.endsWith("exit")){//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			 
		rName = list.getSelectedValue();    //get,selet 선택된것  . 모델에는 데이터가 선택된게없네?   그럼 리스트를보자  getSelectedValue  선택하면 그값을보여준다 
		try{

			if(rName == null){ //이름선택안됐을때
				dos.writeChar('M');
				dos.flush();

//				dos.writeUTF(msg);
//				dos.flush(); 
			}else{ //이름선택됐을때  
				dos.writeChar('P');
				dos.flush();
				dos.writeUTF(rName);
				dos.flush();
//				list.clearSelection(); //선택된게 싺~ 풀린다    클리어 셀렉션   //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//				dos.writeUTF(msg);
//				dos.flush();
			}
			dos.writeUTF(msg);
			dos.flush();
			//받아오는건 병행되도록 쓰레드를사용하자 ~

		}catch(IOException e ){

		}
		}else if(msg.equals("exit")){ //귓속말을 보내고 귓속말상태가 풀릴려면  채팅창에 exit 를 써야지만 풀리도록 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 근데귓속말이 자신한테안보이니까 수정좀할게요 
			list.clearSelection();//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		}//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		//@@@@@@@@@@@@@@@@
		tfMessge.setText("");
	}


	public synchronized void addChatList(String msg){ //동기화 서버가 쓰레드기때문에 여러사람이한번에달라고하면 안되니까 동기화시켜야함 
		taChatList.append(msg+"\n");
		int length = taChatList.getText().length();
		taChatList.setCaretPosition(length);
	}
	void connertChatServer(){
		String ip = tfIP.getText().trim();//읽어오는데 앞뒤에 공백이있으면 제거해주는 역할 
		String sPort = tfPort.getText();
		int port = 12345;
		try{
			port = Integer.parseInt(sPort);
		}catch(NumberFormatException e ){
			addChatList("port오류  ["+e + "]");
		}

		//ip,port 서버접속 


		try {
			s = new Socket(ip,port);
			addChatList("접속 성공 .");
			//접속후 io객체생성
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream()); 
			addChatList("IO객체를 얻었습니다.");
			btnConnect.setEnabled(false);
			btnSend.setEnabled(true);
			btnRename.setEnabled(true);
			btnLogDown.setEnabled(true);

			readThread = new ReadThread(this,dis); //어나니머스오브젝트 를쓰지말고  따로변수를잡아서 
			readThread.start();
			//그변수에 접근해서   메소드를 실행하면된다 그리고 그변수를 쓰레드에 

		} catch (IOException e) {
			addChatList("소켓 접속오류"+e);
		}

	}
	public ChatCilent() {
		// TODO Auto-generated constructor stub

		f= new JFrame("클라이언트창 ");
		f.setBounds(600,100,400,500);
		//-----
		JPanel nPanel = new JPanel(new BorderLayout());
		JPanel nPanelSub = new JPanel(new GridLayout(1,2)); // 
		JPanel nPanelSubSub1 = new JPanel(new BorderLayout()); // 
		JPanel nPanelSubSub2 = new JPanel(new BorderLayout()); // 

		tfIP = new JTextField("59.9.187.151") ; 
		nPanelSubSub1.add(new JLabel("서버 IP :  "),BorderLayout.WEST);
		nPanelSubSub1.add(tfIP,BorderLayout.CENTER);

		tfPort = new JTextField("12345");
		nPanelSubSub2.add(new JLabel(" Port :  "),BorderLayout.WEST);
		nPanelSubSub2.add(tfPort,BorderLayout.CENTER);

		nPanelSub.add(nPanelSubSub1);
		nPanelSub.add(nPanelSubSub2);


		btnConnect  = new JButton("접속");
		btnConnect.setActionCommand("A");
		btnConnect.addActionListener(listener);
		nPanel.add(btnConnect,BorderLayout.EAST);//버튼
		nPanel.add(nPanelSub); //패널

		//-------
		taChatList = new JTextArea();
		//		taChatList.setEnabled(false); //인에이블  
		taChatList.setEditable(false);
		//--------

		JPanel sPanel = new JPanel(new BorderLayout());

		tfMessge = new JTextField();
		tfMessge.setActionCommand("B");//텍스트필드도 엑션커맨드가있다/  j텍스트 에리어 에서는 안먹음
		tfMessge.addActionListener(listener);//엑션커맨드에연결    /j텍스트필드만가능


		btnSend  = new JButton("전송");
		btnSend.setActionCommand("B");
		btnSend.addActionListener(listener);
		btnSend.setEnabled(false);


		btnRename  = new JButton("이름바꾸기");
		btnRename.setToolTipText("채팅창에 사용할 닉네임을적으신뒤 이름바꾸기 버튼을 눌러주세요");
		btnRename.setActionCommand("C");
		btnRename.addActionListener(listener);
		btnRename.setEnabled(false);


		sPanel.add(btnRename,BorderLayout.WEST);
		sPanel.add(btnSend,BorderLayout.EAST);
		sPanel.add(tfMessge,BorderLayout.CENTER);

		//---

		model =new DefaultListModel<String>(); //디폴트 리스트모델  테이블까지가면 
		list = new JList<String>();
		list.setModel(model); //셋모델     
		list.setFixedCellWidth(100);  //사이즈 픽셀단위조절




		//---------
		btnLogDown = new JButton("서버로그저장 ");
		btnLogDown.setActionCommand("D");		
		btnLogDown.addActionListener(listener);
		btnLogDown.setEnabled(false);
		
		nPanel.add(btnLogDown,BorderLayout.WEST);
		//--------


		f.add(nPanel,BorderLayout.NORTH);
		f.add(new JScrollPane( list ) , BorderLayout.EAST);
		f.add(new JScrollPane(taChatList),BorderLayout.CENTER); //뷰포지션 
		f.add(sPanel,BorderLayout.SOUTH);

		//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //이럴경우 클라이언트를종료안하고나갈수도있으니 조심해야함 
		f.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				//나가기 프로토콜실행 


				doExit();

			}

		});
		f.setVisible(true);
	}

	public void updateUser(String oldName,String newName){ //올드를 뉴로 바꾼다 
		//-----*****
		//		model.removeElement(oldName);  
		//		model.addElement(newName);
		//-----*****
		//		model.contains() //있냐없냐 1   없네
		//		model.get  //있냐없냐 2 없네
		int idx = model.indexOf(oldName); //있냐없냐 있네          //값이 없으면  -1리턴 
		if(idx != -1){ // -1아닐때     indexof  메세지
			model.setElementAt(newName, idx);
		}
	}

	public void DeleteUser(String name){
		model.removeElement(name);//리무브 엘리먼트 
	}

	public void addUser(String name){
		model.addElement(name);
	}

	public void showUserList(String[] nameList){
		for(String name  : nameList){
			model.addElement(name);


		}
	}
	void sendExit(){
		try{
			if(dos!=null){
				dos.writeChar('X');
				dos.flush();
			}
		}catch(IOException e ){
		}finally{
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

	void doExit(){ //나가기프로토콜 
		sendExit();
		System.exit(0);
	}

	public void setName (String name){
		this.name = name ; 

		if(name.length()==0){
			return ;
		}
		f.setTitle( String.format("채팅클라이언트  [ %s ]", name));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatCilent();
	}

}
