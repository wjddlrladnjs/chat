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
	private JFrame f;
	private JButton btn1, btn2;
	private JTextField tfPort;
	private JTextArea taLog;
	private ChatServerThread serverThread;
	
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
	
	void addLog(String msg){
		taLog.append(msg+"\n");
		int length = taLog.getText().length(); //글자수만큼??녹듣..
		taLog.setCaretPosition(length);
	}
	void stopServer(){
		serverThread.stopServerSocket();
		btn1.setEnabled(true);
		btn2.setEnabled(false);
	}
	
	void startServer(){
		try{
		int port = Integer.parseInt(tfPort.getText());
		new Thread( new ChatServerThread(this, port) ).start();
		}catch(NumberFormatException e){
			addLog("You typed wrong format port. It will connect with default value.");
			serverThread = new ChatServerThread(this, 12345);
			new Thread( serverThread ).start();
		}
	}
	
	
	
	void buttonOnState(){
		btn1.setEnabled(false);
		btn2.setEnabled(true);
	}

	public ChatServer() {
		initGUI();
	}
	
	void initGUI(){
		f = new JFrame("Server Screen");
		f.setBounds(100, 100, 500, 500);
		JPanel p1 = new JPanel(new GridLayout(1,2));
		JPanel p2 = new JPanel(new BorderLayout());

		btn1 = new JButton("TurnOn");
		btn1.setActionCommand("a");
		btn1.addActionListener(listener);
		btn2 = new JButton("TurnOff");
		btn2.setActionCommand("b");
		btn2.addActionListener(listener);
		btn2.setEnabled(false);
		p1.add(btn1);
		p1.add(btn2);
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
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new ChatServer();
	}

}
