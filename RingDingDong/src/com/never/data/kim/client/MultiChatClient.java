package com.never.data.kim.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MultiChatClient {
	
	JFrame f;
	JTextField tfIP, tfPort, tfMsg;
	JTextArea taOutput, taUserList;
	JButton btnConnect, btnMsgSend;
	
	
	//테스트
	JButton btnChangeBgrImg;
	JFileChooser fc;
	Image img;
	File file;
	
	//InputStream is = null;
	//OutputStream os = null;
	FileInputStream fis = null;
	FileOutputStream fos = null;
	
	
	//userList위한것들
	JList<String> list;	//얘도 JScrollPane해줘야댐SSSsss   //모델이 따로있다(listmodel)을 상속
	DefaultListModel<String> model;
	
	String name;
	Socket s;	//닫을떄쓸라고 뺀거!
	
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	//이미지 파일 선택을 위한 파일필터
	FileFilter filter = new FileFilter() {
		
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()){	//폴더면 true반환
				return true;
			}else if(f.getName().endsWith(".jpg") ||
					f.getName().endsWith(".png") ||
					f.getName().endsWith(".bmp")){
				return true;
			}
			else return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	ActionListener al = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch(cmd){
			case "A":
				connect2Server();
				break;
			case "B":
				sendMsg();
				break;
			case "C":
				rename();
				break;
			case "D":
				changeBgrImg();
				break;
			}
		}
	};
	
	void changeBgrImg(){
		////// 연결된상태인지 검증하자 <-join버튼이랑 같이 처리해주면 되는거 아니야?
		//JOptionPane.showConfirmDialog(f, "");
		
		fc = new JFileChooser(".");
		fc.setAcceptAllFileFilterUsed(false); //모든확장자 보이는 필터 끈다
		fc.setFileFilter(filter);
		
		int returnVal = fc.showOpenDialog(f);
		
		int tlength = (int) fc.getSelectedFile().length();
		System.out.println("tlength: " + tlength);
		
		if(returnVal == JFileChooser.APPROVE_OPTION){ //if approved(yes || select)
			file = fc.getSelectedFile();	//선택된 파일을 얻는다.
			System.out.println(file.length());
		}
		
		if(file == null){	//null이면 소용없게.
			return;
		}
		
		addChatAlert("@@ " +  file.getName() + "을 선택하였습니다. @@");
		
		//서버로 보내자
		sendImageFile(file, tlength);
		
		//나의 배경화면을 바꾼다
		//setBgrImageAs(file);
		
	}
	
	void sendImageFile(File file, int tempLength){
		int data = 0;	//데이타의 크기
		int length = 0;	//파일의 크기
		String fileName = "";
		byte[] brr = null; //데이타가 들어갈 배열
		try {
			fos = new FileOutputStream(file);
			//파일이 정상적으로 들어왓으면 length를구한다
			length = (int) file.length();
			System.out.println("length:" + length);
			
			if(length == 0){	//크기 0이면 리턴
				addChatAlert("파일 크기가 너무 작거나 파일이 존재하지 않습니다.");
				return;
			}
			
			fileName = file.getName();
			brr = new byte[length];	//length만큼 byte배열 잡아줌
			
//			DataInputStream dis = new DataInputStream(is);
			
			//여기가 쓰레드가 되어야하는부분 아닌가?
			dis.readFully(brr, 0, length);	//꽉찰때까지 읽는다
			
			//프로토콜 보낸다
			dos.writeChar('i');
			dos.flush();
			dos.writeUTF(fileName);
			dos.flush();
			dos.writeInt(length);
			dos.flush();
			fos.write(brr, 0, brr.length);	//쓴다
			fos.flush();
			
			addChatAlert(String.format("서버로 파일(파일명: %s, 크기: %d)을 보냅니다..",
					fileName, length));
			
		} catch (FileNotFoundException e) {
			addChatAlert("파일 출력 에라: " + e);
		} catch (IOException e) {
			addChatAlert("I/O 에러: " + e);
		}
		
		//여기서 서버로 보내는 프로토콜
		//dos.writeChar('i');
		//dos.writeUTF(fileName);
		//dos.writeInt(length);
		//fos.write(brr);
		
	}
	
	//나의 배경화면만 바꾼다
	void setBgrImageAs(){
		
	}
	
	//서버에서 받아왔을경우 바꾼다
	void setBgrImageFromServer(){
		
	}
	
	void exitClient(){	//for later calls 메솓화한다SSSsss
		sendExit();
		System.exit(0);
	}
	
	void sendExit(){
		try{
			if(dos != null){
				dos.writeChar('X');
				dos.flush();
			}
		}catch(IOException e){
		}finally{
			if(dis != null){
				try{
					dis.close();
				}catch(IOException e){}
			}
			if(dos != null){
				try{
					dos.close();
				}catch(IOException e){}
			}
			if(s != null){
				try{
					s.close();
				}catch(IOException e){}
			}
		}
	}
	
	void addUser(String name){
		model.addElement(name);
	}
	
	void deleteUser(String name){
		model.removeElement(name);
	}
	
	void updateUserName(String oldName, String newName){
		int index = model.indexOf(oldName); //없으면 -1 알죠?zz
		if(index != -1){
			model.setElementAt(newName, index);
		}
	}
	
	public void showUserList(String[] nameList){
		String[] list = nameList;
		for(String name : list){
			model.addElement(name);
		}
	}
	
	void rename(){
		name = tfMsg.getText().trim();
		
		if(name.isEmpty()) return;
		
		setFrameName(name);
		sendRename(name);
	}
	
	void sendRename(String name){
		try{
			dos.writeChar('R');
			dos.flush();
			dos.writeUTF(name);
			dos.flush();
		}catch(IOException e){
			
		}
		
		tfMsg.setText("");
		
	}
	
	void connect2Server(){
		String port = ""; 
		int portNum = 0;
		String ip = "";

		try{
			port = tfPort.getText().trim();	//앞뒤공백 지워야지
			portNum = Integer.parseInt(port);
			ip = tfIP.getText().trim();
		}catch(NumberFormatException e){
			addChatAlert("port 오류: " + e);
		}
		

		//ip와 port로 서버에 접속해보자!
		s = null;
		try{
			s = new Socket(ip, portNum);
			
			addChatAlert("접속 성공함");
			
			//입출력 객체 얻어내자!
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			addChatAlert("IO객체를 얻었습니다.");
			
			btnConnect.setEnabled(false);
			btnMsgSend.setEnabled(true);//버튼들 처리
			
			//보내는건 이벤트처리할거니가 내비두고 받는거
			//쓰레드루다가 하자 gg
			new ReaderThread(this, dis).start();
			
		}catch(IOException e){
			addChatAlert("접속 오류: " + e);
		}
		
		
	}
	
	void sendMsg(){
//		taOutput.append("나의 입력 내용: " + str + "\n");
		String msg = tfMsg.getText();
		
		//엔터만치고 공백이면 안보내게 할거야!
		if(msg.isEmpty()) return;
		
		
		//귓속말이구요!
		
		String rName = list.getSelectedValue();
		
		try{
			if(rName == null){ 		//for alot of cases 널검사많이해야된다
				dos.writeChar('M');
				dos.flush();
				
			}else{	//선택된거ㅔ있으,면 private!	
				dos.writeChar('P');
				dos.flush();
				dos.writeUTF(rName);	//받는사람이름보내주구
				dos.flush();
				
				//야! pm 보내고나면 선택된거 없에고싶어!
				list.clearSelection();
			}
			
			//얘네는 어쨌든 보내지는거니까 빼자
			dos.writeUTF(msg);	//보내는건 이게 맞는거! 버튼누를시!
			dos.flush(); //읽어오는건 받는거랑 무관하게 병행해서 받아오느놈이 있어야되겠지? 쓰레드지? 그지? 내 말 맞지?
			
		}catch(IOException e){}
		

		
//		addChatAlert("[나] " + str);
		tfMsg.setText("");
	}
	
	void setGUI(){	//gui복잡하면 이렇게 메솓하나에 빼는사람들도있다

		f = new JFrame();
		f.setBounds(new Rectangle(700, 250, 500, 600));
		//north
		JPanel p1 = new JPanel(new BorderLayout());	//종합
		JPanel p10 = new JPanel(new GridLayout(1, 2));	//center
		JPanel p11 = new JPanel(new BorderLayout());
		JPanel p12 = new JPanel(new BorderLayout());
		
		//center
		JPanel pc = new JPanel(new BorderLayout());
		JPanel pce = new JPanel(new BorderLayout());
		
		//south
		JPanel p2 = new JPanel(new BorderLayout());
		
		//panel border
		p1.setBorder(BorderFactory.createLineBorder(new Color(123, 123, 123), 5));
		p10.setBorder(BorderFactory.createLineBorder(new Color(212, 233, 233), 4));
		p11.setBorder(BorderFactory.createLineBorder(new Color(255, 211, 211), 3));
		p12.setBorder(BorderFactory.createLineBorder(new Color(178, 187, 155), 3));
		
		pc.setBorder(BorderFactory.createLineBorder(new Color(192, 168, 205), 3));
		
		p2.setBorder(BorderFactory.createLineBorder(new Color(155, 155, 177), 5));
		
		
		//north-grid
		JLabel lb = new JLabel(" IP ");
		lb.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 15));
		tfIP = new JTextField("192.168.205.146");
		tfIP.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 15));
		tfIP.setBackground(new Color(233, 244, 255));
		tfIP.setBorder(BorderFactory.createLineBorder(new Color(183, 194, 205), 5, true));
		
		p11.add(lb, BorderLayout.WEST);
		p11.add(tfIP, BorderLayout.CENTER);
		
		lb = new JLabel(" Port ");
		lb.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 15));
		tfPort = new JTextField("12345");
		tfPort.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 15));
		tfPort.setBackground(new Color(233, 244, 255));
		tfPort.setBorder(BorderFactory.createLineBorder(new Color(183, 194, 205), 5, true));
		
		p12.add(lb, BorderLayout.WEST);
		p12.add(tfPort, BorderLayout.CENTER);
		
			//north-center
		p10.add(p11);	
		p10.add(p12);
		
		btnConnect = new JButton("Join");
		btnConnect.setActionCommand("A");
		btnConnect.addActionListener(al);
		btnConnect.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		
		btnChangeBgrImg = new JButton("Img");
		btnChangeBgrImg.setActionCommand("D");
		btnChangeBgrImg.addActionListener(al);
		btnChangeBgrImg.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		
		
		JPanel ptempRight = new JPanel(new GridLayout(1,2));
		ptempRight.add(btnConnect);
		ptempRight.add(btnChangeBgrImg);
		
		p1.add(p10, BorderLayout.CENTER);
		p1.add(ptempRight, BorderLayout.EAST);
		
		
		//center
		taOutput = new JTextArea();
		taOutput.setBackground(new Color(233, 243, 247));
		//taOutput.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 13));
		taOutput.setBorder(BorderFactory.createLineBorder(new Color(212, 189, 199), 5, true));
		taOutput.setEditable(false);
		//taOutput.setEnabled(false);
		
		list = new JList<String>();
		model = new DefaultListModel<String>();
		list.setModel(model);
		//model = (DefaultListModel)list.getModel(); //기본으로 있는놈
		list.setBackground(new Color(232, 224, 215));
		//list.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 13));
		list.setBorder(BorderFactory.createLineBorder(new Color(210, 218, 232), 5, true));
		list.setFixedCellWidth(120);
		
		lb = new JLabel(" User List ");
		lb.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 14));
		
		pce.add(lb, BorderLayout.NORTH);
		pce.add(new JScrollPane(list), BorderLayout.CENTER);
		
		pc.add(new JScrollPane(taOutput), BorderLayout.CENTER);
		pc.add(pce, BorderLayout.EAST);
		
		
		//south
		tfMsg = new JTextField();
		//tfMsg.setFont(new Font("Bitstream Vera Sans Mono", Font.PLAIN, 14));
		tfMsg.addActionListener(al);	//엔터는 특이하게 KeyListener가아녀도 이걸로해도된다(textArea는 원래 엔터키 역할이 있었으므로 거기선 엔터 리스너로 안먹혀
		tfMsg.setActionCommand("B");	//엔터치면 이값이 돼
		
		//textField에 액션리스너, 엔터키 적용가능 
		
		
		btnMsgSend = new JButton("Send");
		btnMsgSend.setActionCommand("B");
		btnMsgSend.addActionListener(al);	
		btnMsgSend.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		
		//일단은 비활성화하자
		btnMsgSend.setEnabled(false);
		
		//rename버튼
		JButton btnRename = new JButton("Rename");
		btnRename.setActionCommand("C");
		btnRename.addActionListener(al);	
		btnRename.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 17));
		
		p2.add(btnRename, BorderLayout.WEST);
		p2.add(tfMsg, BorderLayout.CENTER);
		p2.add(btnMsgSend, BorderLayout.EAST);
		
		
		
		//mergin' ya'll  in f's 
		f.add(p1, BorderLayout.NORTH);
		f.add(pc,  BorderLayout.CENTER);
		f.add(p2, BorderLayout.SOUTH);
		
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() { //소켓닫아주는거. 완전한 종료
			@Override
			public void windowClosing(WindowEvent e) {
				//new JOptionPane.showConfirmDialog(parentComponent, message);
				exitClient();
			}
		});
	}

	public MultiChatClient(){
		
		setGUI();
		
	}
	
	public void setFrameName(String name){
		this.name = name;
		f.setTitle(String.format("채팅 클라이언트 [ %s ]", name));
	}

	public void addChatAlert(String msg){
		//스크롤 자동으로 내려가게 하고싶으면?
		taOutput.append(msg + "\n");
		int length = taOutput.getText().length();// + 1;
		taOutput.setCaretPosition(length);
	}
	
//	public void addListAlert(String msg){
//		//스크롤 자동으로 내려가게 하고싶으면?
//		taUserList.append(msg + "\n");
//		int length = taOutput.getText().length();// + 1;
//		
//	}
	

	
	public static void main(String[] args){
		new MultiChatClient();
	}
}
