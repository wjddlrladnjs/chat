package com.never.data.kim.client;

public class ImageProcess extends Thread{
	
	MultiChatClient client;
	ReaderThread thread;
	
	byte[] imageData;
	int fileLength;
	
	public ImageProcess(MultiChatClient client, ReaderThread thread){
		this.client = client;
		this.thread = thread;
	}
	
	public void run(){
		
		gainImageInfo();
		
		//얻었으면 뿌려줘야지!
		client.addChatAlert("@@ 파일을 읽고있습니다. @@");
	}
	
	public void gainImageInfo(){
		fileLength = thread.getFileLength();
		imageData = new byte[fileLength];
		imageData = thread.getImageData(fileLength);
	}
}
