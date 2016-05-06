package net.wishwall.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveBitmap2File {
	
	private static SaveBitmap2File saveBitmap2File = null;

	public SaveBitmap2File() {		
		
	}	
	public static SaveBitmap2File getInstance(){
		if(saveBitmap2File == null){			
			synchronized (SaveBitmap2File.class) {
				if(saveBitmap2File ==null){				
					saveBitmap2File = new SaveBitmap2File();
				}
			}
		}
		return saveBitmap2File;	
	}
	public static String getSDPath(){
		  File sdDir = null;
		  boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		  if (sdCardExist) {
		   sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		  }
		  return sdDir.toString();   
		 }
	
	/**
	 * @param bm
	 * @param path
	 * @param fileName
	 * @throws IOException
	 */
  public static void saveFile(Bitmap bm, String path,String fileName) throws IOException {     
      File dirFile = new File(path);  
      if(!dirFile.exists()){  
          dirFile.mkdir();  
      }  
      File myCaptureFile = new File(dirFile.getAbsolutePath()+"/" + fileName);  
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
      bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
      bos.flush();  
      bos.close();  
  }
  /**
   * 保存到二级文件夹
   * @param bm
   * @param path
   * @param fileName
   * @throws IOException
   */
  public static void saveSecondFile(Bitmap bm, String path,String fileName)throws IOException{
	  //一级目录
	  File dirFile = new File(path);
	  if(!dirFile.exists()){
		  dirFile.mkdirs();
	  }	
	  File myCaptureFile = new File(dirFile.getAbsolutePath()+"/" + fileName); 
	  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
      bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
      bos.flush();  
      bos.close();  
  }
}
