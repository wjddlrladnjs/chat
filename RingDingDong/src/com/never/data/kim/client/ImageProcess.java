package com.never.data.kim.client;

public class ImageProcess extends Thread{
	
	MultiChatClient client;
	ReaderThread thread;
	
	byte[] ImageData;
	int fileLength;
	
	public ImageProcess(MultiChatClient client, ReaderThread thread){
		this.client = client;
		this.thread = thread;
	}
	
	public void run(){
		
		gainImageInfo();
		
		//얻었으면 뿌려줘야지!
		
	}
	
	public void gainImageInfo(){
		fileLength = thread.getFileLength();
		ImageData = thread.getImageData(fileLength);
	}
}
