package com.never.data.won;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatServer {
	//GUI변수
	private JFrame f;							//전체 프레임
	private JButton btnOn, btnOff;				//btnOn: 서버켜기 btn2: 서버끄기
	private JTextField tfPort;					//port번호 기입
	private JTextArea taLog;					//textArea: Log 기록창
	//객체 변수
	private ChatServerThread serverThread;		//서버스레드 객체. 전달용
	
	//GUI 버튼 리스너
	ActionListener listener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd = arg0.getActionCommand();
			switch(cmd){
			case "a":
				startServer();
				break;
			case "b":
				stopServer();
				break;
			}
		}		
	};
	
	//listener 'a' cmd에 따른 서버켜기 기능 구현 메소드
	//숫자 이외를 입력하거나  port가 공란일 경우 exception의 초기 port 번호로 server에 port 오픈.
	//숫자를 기존의 port 번호 등에 연결하거나 숫자가 0~65535 범위가 넘어 가는 등 int 내에서의 오류는 따로 잡아주지 않으므로 유의할 것.
	void startServer(){
		try{
		int port = Integer.parseInt(tfPort.getText());
		new Thread( new ChatServerThread(this, port) ).start();	//ChatServerThread는 Runnable로 구현
		}catch(NumberFormatException e){
			addLog("You typed wrong format port. It will connect with default value.");
			serverThread = new ChatServerThread(this, 12345);	
			new Thread( serverThread ).start();
		}
	}
	
	//서버가 켜짐에 따라 프레임에 있는 버튼 변화 메소드
	void buttonOnState(){
		btnOn.setEnabled(false);
		btnOff.setEnabled(true);
	}
	
	//listener 'b' cmd에 따른 서버 끄기 기능 구현 메소드.
	void stopServer(){
		serverThread.stopServerSocket();
		btnOn.setEnabled(true);
		btnOff.setEnabled(false);
	}
	
	//서버 로그 창 업데이트 메소드
	void addLog(String msg){
		taLog.append(msg+"\n");
		int length = taLog.getText().length(); 
		taLog.setCaretPosition(length);
	}	

	//생성자 - GUI 구현 메소드 호출
	public ChatServer() {
		initGUI();
	}
	
	//GUI 구현 메소드
	void initGUI(){
		f = new JFrame("Server Screen");//서버 프레임
		f.setBounds(100, 100, 500, 500);
		JPanel p1 = new JPanel(new GridLayout(1,2));	//켜고 끄는 버튼 panel
		JPanel p2 = new JPanel(new BorderLayout()); 	//켜고 끄는 버튼 panel + port입력 textfield

		btnOn = new JButton("TurnOn");
		btnOn.setActionCommand("a");
		btnOn.addActionListener(listener);
		btnOff = new JButton("TurnOff");
		btnOff.setActionCommand("b");
		btnOff.addActionListener(listener);
		btnOff.setEnabled(false);
		p1.add(btnOn);
		p1.add(btnOff);
		p2.add("West",new JLabel("Port"));


		tfPort = new JTextField(10);
		tfPort.setToolTipText("Port number can be between 0 to 65535.");
		p2.add("Center",tfPort);


		f.add("South", p1);

		f.add("North",p2);
		taLog = new JTextArea();
		taLog.setEditable(false);
		f.add("Center",new JScrollPane(taLog));
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//frame close. 메소드는 만들었지만 아직 연결하지 않음.
	}

	//실행 메소드
	public static void main(String[] args) {
		new ChatServer();
	}

}
