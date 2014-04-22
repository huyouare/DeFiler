package tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import common.DFileID;
import dfs.*;

public class FileMaker implements Runnable{

	MyDFS myFileSystem;
	public FileMaker(MyDFS fileSystem){
		myFileSystem = fileSystem;
	}
	
	@Override
	public void run() {
		DFileID file = myFileSystem.createDFile();
		
		byte[] imageInByte;
		
		BufferedImage originalImage = null;
		
		try {
			originalImage = ImageIO.read(new File("/Users/michaelogez/documents/workspace/WillDFiler/src/tests/Argentina.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(originalImage, "png", baos);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		try {
//			baos.flush();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		imageInByte = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		myFileSystem.write(file, imageInByte, 0, imageInByte.length);
		byte[] b = new byte[imageInByte.length];
		myFileSystem.read(file, b, 0, imageInByte.length);
		
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		
		BufferedImage bImageFromConvert = null;
		try {
			bImageFromConvert = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ImageIO.write(bImageFromConvert,  "png", new File("poop.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
