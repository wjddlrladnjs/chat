package com.never.data.won;

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
	JFrame f;
	JTextField tfIP, tfPort, tfName, tfText;
	JTextArea taLog;
	JButton btn1, btn2, btnName, btnServerLog, btnImgBg;
	JList<String> list;
	DefaultListModel<String> model;

	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;



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
			}
		}		
	};
	//update User
	public void updataUser(String oldName, String newName){
		int idx = model.indexOf(oldName);
		if(idx != -1){
			model.setElementAt(newName, idx);
		}
	}

	//exited User remove from list
	public void deleteUser(String name){
		model.removeElement(name);
	}

	//Add user list
	public void addUser(String name){
		model.addElement(name);
	}

	//user list
	public void showUserList(String[] nameList){
		for(String name : nameList){
			model.addElement(name);
		}
	}

	void changeNickName(){
		try{
			if(!tfName.getText().equals(null)){		

				String text = tfName.getText();	
				dos.writeChar('N');
				dos.writeUTF(text);
				dos.flush();

				tfName.setText("");				
			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfName.setText("");
		}
	}


	void sendMessage(){
		try{
			if(!tfText.getText().equals(null)){		

				String text = tfText.getText();	
				dos.writeChar('M');
				dos.writeUTF(text);
				dos.flush();

				tfText.setText("");				
			}
		}catch(IOException e){
			addLog("sendMessage IOException"+e);
			tfText.setText("");
		}
	}

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

	void startClient(){
		try{
			String ipAddr = tfIP.getText();
			int port = Integer.parseInt(tfPort.getText());
			addLog("Client is ready for connect...");
			commenceClient(ipAddr, port);			
		}catch(NumberFormatException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.4",12345);			
		}catch(NullPointerException e){
			addLog("Client will connect with default into...");
			commenceClient("192.168.0.4",12345);			
		}
	}

	String nickName(){
		String nickName = "";
		nickName = tfName.getText();
		if(nickName.equals(null) || nickName.equals(""))	nickName = "User";
		setChatWindowName(String.format("Client[%s] Screen", nickName));
		return nickName;
	}

	void commenceClient(String ipAddr, int port){

		try {
			socket = new Socket(ipAddr, port);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			btn1.setEnabled(false);
			btn2.setEnabled(true);
			dos.writeChar('A');
			dos.flush();
			String name = nickName();

			dos.writeUTF(name);
			String hello = dis.readUTF();
			addLog(hello);
			new ChatReadThread(this).start();
		} catch (UnknownHostException e) {
			addLog("Socket not connect b/c "+e);
		} catch (IOException e1) {
			addLog("Socket not connect b/c "+e1);
		}

	}

	void addLog(String msg){
		taLog.append(msg+"\n");
	}

	void setChatWindowName(String msg){
		f.setTitle(String.format("Chatting Client[ %s ]", msg));
	}

	//model.setElementAt(string을 int위치에 집어 넣기);

	public ChatClient() {
		initGUI();
	}

	void initGUI(){
		f = new JFrame("Client Screen");
		f.setBounds(700,100,500,500);
		JPanel p1 = new JPanel(new BorderLayout());		// 아이피주소
		JPanel p2 = new JPanel(new BorderLayout());		// 포트번호

		JPanel p3 = new JPanel(new GridLayout(1,2));	// 아이비+포트

		JPanel p4 = new JPanel(new BorderLayout());		//위쪽 마무리		
		JPanel p5 = new JPanel(new BorderLayout());	

		JPanel p6 = new JPanel(new BorderLayout());		//대화명 변경창
		JPanel p71 = new JPanel(new GridLayout(1,2));	//버튼 2개(서버로그, 이미지 배경 변경)
		JPanel p7 = new JPanel(new GridLayout(3,1));	//대화명변경 버튼 + 대화명	변경창 + 버튼2개		
		JPanel p8 = new JPanel(new BorderLayout());



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
		///////////////////////////////////////위쪽 아래쪽
		p5.add("Center",tfText = new JTextField());
		tfText.setActionCommand("B"); // enter로 action command 값 줌
		tfText.addActionListener(listener); // enter로 action command 값 줌
		p5.add("East",btn2 = new JButton("Send"));
		btn2.setEnabled(false);
		btn2.setActionCommand("B");
		btn2.addActionListener(listener);		

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
