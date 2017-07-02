package com.never.data.kim.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MultiChatServer {
	
	private JFrame f;
	private JButton btnStart;
	private JButton btnStop;
	private JTextField tfPort;
	private JTextArea taOutput;
	
	ServerThread serverThread;
	
	ActionListener al = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch(cmd){
			case "A":
				startServer();	//서버쓰레드를 start해서 동작케 하라
				break;
			case "B":
				stopServer();
				break;
			}
		}
	};
	
	void startServer(){
		
		String sPort = tfPort.getText();
		int port = 12345;
		try{
			port = Integer.parseInt(sPort);
			taOutput.append("포트 넘버를 " + port + "으로 설정합니다...\n");
		}catch(NumberFormatException e){
			taOutput.append("숫자가 올바르지 않습니다. 메세지: " + e + "\n");
			taOutput.append("12345로 임의 설정합니다! \n");
		}
		
		taOutput.append("서버를 가동중...\n");
		
		serverThread = new ServerThread(this, port);	//나중접근위해 memba로빼놨다ㅋ
		new Thread(serverThread).start();	//포트전해준다
		
		
		
		//에러나면 돌아올수 있으니 따로 처리
		
	}
	
	void stopServer(){
		serverThread.stop();
		setStartBtnVisible();
	}
	
	void setStartBtnInvisible(){
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
	}
	
	void setStartBtnVisible(){
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
	}
	
	void checkPortNum(){
		String sPort = tfPort.getText();
		int port = 12345;
		try{
			port = Integer.parseInt(sPort);
			taOutput.append("포트 넘버를 " + port + "으로 설정합니다...\n");
		}catch(NumberFormatException e){
			taOutput.append("숫자가 올바르지 않습니다. 메세지: " + e + "\n");
			taOutput.append("12345로 임의 설정합니다! \n");
		}
	}
	
	void showLocalIP(){
		InetAddress addrs = null; 
		try{
			addrs = InetAddress.getLocalHost();
			String myIP = addrs.getHostAddress();
			taOutput.append("서버의 IP: " + myIP + "\n");
			
		}catch(IOException e){}
	}
	
	void setGUI(){
		f = new JFrame("Server ^.^");
		f.setBounds(new Rectangle(0, 250, 450, 600));//안드서는 activity인데 무적권 폰 크기만큼잡혀서필요없음
		
	
		JPanel p1 = new JPanel(new BorderLayout());	//포트 윗줄 패널
		JPanel p2 = new JPanel(new GridLayout(1, 2));	//포트 밑줄 패널
		p1.setBorder(BorderFactory.createLineBorder(new Color(177, 177, 177), 5));
		p2.setBorder(BorderFactory.createLineBorder(new Color(177, 177, 178), 5));
		
		//North panel
		JLabel lb = new JLabel(" Port ");
		lb.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		
		tfPort = new JTextField();
		tfPort.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 15));
		tfPort.setBackground(new Color(233, 244, 255));
		tfPort.setBorder(BorderFactory.createLineBorder(new Color(183, 194, 205), 5, true));

		
		p1.add(lb, BorderLayout.WEST);
		p1.add(tfPort);
		
		//Center panel
		taOutput = new JTextArea();
		//taOutput.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 13));
		taOutput.setBackground(new Color(240, 254, 245));
		taOutput.setEditable(false);
		//taOutput.setEnabled(false);
		taOutput.setBorder(BorderFactory.createLineBorder(new Color(180, 170, 165), 8, true));
		
		//South panel
		btnStart = new JButton("START");
		btnStart.setActionCommand("A");
		btnStart.addActionListener(al);
		btnStart.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		p2.add(btnStart);
		
		btnStop = new JButton("STOP");
		btnStop.setActionCommand("B");
		btnStop.addActionListener(al);
		btnStop.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		p2.add(btnStop);
		
		btnStop.setEnabled(false);
		
		//merge all
		f.add(p1, BorderLayout.NORTH);
		f.add(new JScrollPane(taOutput), BorderLayout.CENTER);
		f.add(p2, BorderLayout.SOUTH);
		
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public MultiChatServer(){
		
		setGUI();
		showLocalIP();
		
	}
	
	public static void main(String[] args){
		new MultiChatServer();
	}
	
	//--------------------------------------------------------------------------
	
	public void addChatAlert(String msg){
		taOutput.append(msg + "\n");
		int length = taOutput.getText().length();
		taOutput.setCaretPosition(length);
	}
	
	//--------------------------------------------------------------------------
	
	
}
