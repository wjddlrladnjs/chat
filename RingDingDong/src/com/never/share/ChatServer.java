package com.never.share;

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
	private JFrame f;
	private JTextField tfPort;
	private JButton btnStart,btnStop;
	private JTextArea taChatList ; 
	public String serverChat;
	
	ServerThread serverThread;
	
	
	

	ActionListener listener  = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			switch(e.getActionCommand()){
			case "A" :
				doStartServer();
				break;
			case "B" :
				doStopServer();
				break;
			}
		}
	};
	
	
	
	
	public  synchronized void addChatList(String msg){ //동기화 서버가 쓰레드기때문에 여러사람이한번에달라고하면 안되니까 동기화시켜야함 
		taChatList.append(msg+"\n");
		serverChat = String.format("%s \n", msg);
		int length = taChatList.getText().length();//스트링에 랭쓰라는메소드가있다 
		taChatList.setCaretPosition(length);//j스크롤 아니면  뷰포지션
		//j텍스트에리어 셋 케얼 포지션 
	}

	void doStopServer(){
		serverThread.stop();
	}
	
	void doStartServer(){//메소드는 동사형태로 
		String sPort = tfPort.getText();
		int port = 12345;
		try{
			port = Integer.parseInt(sPort);
		}catch(NumberFormatException e ){
			addChatList("port오류  ["+e + "]");
		}//오류가나도상관없지만  오류를 보여주자 

		
		serverThread = new ServerThread(this,port);
		
		
		new Thread( serverThread ).start();//자기자신과 포트를넘김

	}


	public void settingsStopButton(){
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
	}
	public void settingsStartButton(){
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
	}


	public ChatServer() {
		//채팅 서버  
		f = new JFrame("서버창");
		f.setBounds(1000,100,300,500);
		JPanel npanel = new JPanel(new BorderLayout());
		JPanel spanel = new JPanel(new GridLayout(1,2));


		npanel.add(new JLabel("port"),BorderLayout.WEST);
		tfPort= new JTextField("12345");
		tfPort.setToolTipText("포트는 10000부터 65535 사이만입력하세요");
		npanel.add(tfPort,BorderLayout.CENTER);

		btnStart = new JButton("시작");
		btnStart.setActionCommand("A");
		btnStart.addActionListener(listener);

		btnStop = new JButton("종료");
		btnStop.setActionCommand("B");
		btnStop.addActionListener(listener);
		btnStop.setEnabled(false); //시작누르기전에는 종료를 누를수없다 

		spanel.add(btnStart);
		spanel.add(btnStop);

		taChatList = new JTextArea() ;
		taChatList.setEditable(false); //에디터블이랑 인에이블이랑차이가뭘까
		//		taChatList.setEnabled(false);







		f.add(npanel,BorderLayout.NORTH);
		f.add(new JScrollPane(taChatList), BorderLayout.CENTER); //스크롤막대 생성 
		f.add(spanel,BorderLayout.SOUTH);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		f.setVisible(true);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatServer();
	}

}
