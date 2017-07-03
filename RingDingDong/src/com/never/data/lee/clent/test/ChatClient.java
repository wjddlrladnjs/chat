package com.never.data.lee.clent.test;

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
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient {
	//GUI 변수
	private JFrame f;
	private JTextField tfIP, tfPort, tfName, tfText;
	private JTextArea taLog;
	private JButton btn1, btn2, btnName, btnServerLog, btnImgBg;
	private JList<String> list;
	private DefaultListModel<String> model;
	//네트워크 변수
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	//버튼에 따른 이벤트 리스너
	ActionListener listener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd = arg0.getActionCommand();
			switch(cmd){
			case "A":
				startClient();
				break;
			case "B":				
				sendMessage();
				break;
			case "C":				
				changeNickName();
				break;
			case "0" :  //@요한
				serverLogDown();//@요한 
				break; 
			}
		}		
	};
	synchronized void  serverLogDown(){ //@요한
		try {
			dos.writeChar('1');
			dos.flush();
//			dos.writeUTF(name); //네임 
//			dos.flush();
			
		} catch (IOException e) {}
	}
	
	
	//client 시작. 입력오류에 따른 exception은 port에 숫자가 아닌 다른 입력이 오거나 ip와 port 중 빈 값이 있을 때만 발생한다.
	//ip에 string은 넣지만 ip 주소 이외의 값을 넣거나 port에 숫자는 넣지만 port 규정에 어긋나는 값을 넣을 경우 
	//exception은 발생하지 않고 client와 server 모두(?) 죽을 수 있으니 유의하길.
	//본 메소드는 유효성을 ip와 port에 대한 유효성 검사만 함.
	void startClient(){
		try{
			String ipAddr = tfIP.getText();
			int port = Integer.parseInt(tfPort.getText());
			addLog("Client is ready for connect...");
			commenceClient(ipAddr, port);			
		}catch(NumberFormatException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.14",12345);	//초기값 설정		
		}catch(NullPointerException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.14",12345);			
		}
	}

	//Client 시작을 위한 메소드
	void commenceClient(String ipAddr, int port){
		try {
			socket = new Socket(ipAddr, port);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			btn1.setEnabled(false);
			btn2.setEnabled(true);
			String name = nickName();

			dos.writeUTF(name);
			String hello = dis.readUTF();
			addLog(hello);
			new ChatReadThread(this, dis).start();
		} catch (UnknownHostException e) {
			addLog("Socket not connect b/c "+e);
		} catch (IOException e1) {
			addLog("Socket not connect b/c "+e1);
		}
	}

	//서버로 메세지 전송 메소드
	void sendMessage(){
		String rName ; 
		String text = tfText.getText();	//@요한
		if(!text.equals("exit")){
			rName =list.getSelectedValue();
		try{
			if(!tfText.getText().equals(null)){		
				if(rName == null){
				dos.writeChar('M');
				dos.flush();
				}else{
					dos.writeChar('5');
					dos.flush();
					dos.writeUTF(rName);
					dos.flush();
				}
				dos.writeUTF(text);
				dos.flush();

			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfText.setText("");
		}
		}else if(text.equals("exit")){
			list.clearSelection();
		}
		tfText.setText("");				
	}


	//이름 변경 메소드
	void changeNickName(){
		try{
			if(!tfName.getText().equals(null)){		

				String text = tfName.getText();	
				dos.writeChar('N');
				dos.writeUTF(text);
				dos.flush();
				//				text = dis.readUTF();
				//				setChatWindowName(text);
				tfName.setText("");				
			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfName.setText("");
		}
	}

	//참여한 유저 목록
	public void addUserList(String[] name){
		for(String nameInList : name){
			int idx = model.indexOf(nameInList);
			if(idx != -1){
				model.setElementAt(nameInList, idx);
			}else{
			model.addElement(nameInList);
			}
		}
	}

	public void updateUser(String oldName,String newName){
		int idx = model.indexOf(oldName);
		if(idx != -1){
			model.setElementAt(newName, idx);
		}
	}

	//	public void changeUser(String[] names){
	//		model.size()
	//	}


	//user의 window창 종료로 인한 퇴장시 적용 메소드
	void exitClient(){
		if(dos != null){
			try{
				dos.writeChar('X');
				dos.flush();
				if(dos != null)	dos.close();	
				if(dis != null)	dis.close();
				if(socket != null)	socket.close();
			}catch(IOException e){

			}
		}
	}	

	//대화명 변경시 유효성 검사 메소드
	String nickName(){
		String nickName = "";
		nickName = tfName.getText();
		if(nickName.equals(null) || nickName.equals(""))	nickName = "User";
		//		setChatWindowName(String.format("Client[%s] Screen", nickName));
		return nickName;
	}

	//client log창 업데이트
	void addLog(String msg){
		taLog.append(msg+"\n");
	}

	//client 창 제목 업데이트
	void setChatWindowName(String msg){
		f.setTitle(String.format("Chatting Client[ %s ]", msg));
	}

	public ChatClient() {
		initGUI();
	}

	//GUI 구현 메소드
	void initGUI(){
		f = new JFrame("Client Screen");
		f.setBounds(700,100,500,500);
		JPanel p1 = new JPanel(new BorderLayout());		// 아이피주소 버튼 + textfield
		JPanel p2 = new JPanel(new BorderLayout());		// 포트번호 버튼 + textfield
		JPanel p3 = new JPanel(new GridLayout(1,2));	// 아이피 버튼 + textfield +포트 버튼 + textfield
		JPanel p4 = new JPanel(new BorderLayout());		// 위쪽 버튼 textfield 전체		
		JPanel p5 = new JPanel(new BorderLayout());		// 메시지 전송 창 + 전송 버튼
		JPanel p6 = new JPanel(new BorderLayout());		// 대화명 변경 라벨 + 변경 textfield
		JPanel p71 = new JPanel(new GridLayout(1,2));	// 버튼 2개(서버로그, 이미지 배경 변경)
		JPanel p7 = new JPanel(new GridLayout(3,1));	// 대화명변경 버튼 + 대화명	변경창 + 버튼2개		
		JPanel p8 = new JPanel(new BorderLayout());		// user list + 대화명변경 버튼 + 대화명	변경창 + 버튼2개	

		p1.add("West",new JLabel("  IP  "));
		p1.add("Center",tfIP = new JTextField(15));
		p2.add("West",new JLabel(" Port "));
		p2.add("Center",tfPort = new JTextField(5));	

		p3.add(p1);
		p3.add(p2);

		btn1 = new JButton("Connect");
		btn1.setActionCommand("A");
		btn1.addActionListener(listener);
		p4.add("Center",p3);
		p4.add("East",btn1);

		///////////////////////////////////////위쪽 마무리 아래쪽 시작
		p5.add("Center",tfText = new JTextField());
		tfText.setActionCommand("B"); // enter로 action command 값 줌
		tfText.addActionListener(listener); // enter로 action command 값 줌
		p5.add("East",btn2 = new JButton("Send"));
		btn2.setEnabled(false);
		btn2.setActionCommand("B");
		btn2.addActionListener(listener);	

		//////////////////////////////////////아래쪽 마무리 오른쪽 시작
		p6.add("West", new JLabel(" NickName "));
		p6.add("Center",tfName = new JTextField(10));

		p71.add(btnServerLog = new JButton("Get Log"));
		btnServerLog.setActionCommand("0");
		btnServerLog.addActionListener(listener);
		p71.add(btnImgBg = new JButton("Image bg"));
		btnImgBg.setActionCommand("i");
		btnImgBg.addActionListener(listener);

		p7.add(p71);

		p7.add(btnName = new JButton("Name Change"));
		btnName.setActionCommand("C");
		btnName.addActionListener(listener);

		p7.add( p6);

		p8.add("South", p7);
		model = new DefaultListModel<String>();
		list = new JList<String>();
		list.setModel(model);
		list.setFixedCellWidth(100);
		p8.add("Center", new JScrollPane(list));


		f.add("North",p4);
		taLog = new JTextArea();		
		f.add("Center", new JScrollPane(taLog));
		taLog.setEditable(false);
		f.add("East", p8);
		f.add("South", p5);

		f.setVisible(true);

		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exitClient();
				System.exit(0);
			}			
		});
	}

	public static void main(String[] args) {
		new ChatClient();
	}

}
