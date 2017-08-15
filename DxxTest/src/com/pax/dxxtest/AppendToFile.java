package com.pax.dxxtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.http.util.EncodingUtils;

import android.text.TextUtils;

public class AppendToFile {
    /**
     * A方法追加文件：使用RandomAccessFile
     * @return 
     */
    public static String  appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return "Success!";
    }

    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public static void main(String[] args) {
        String fileName = "E:/newTemp.dat";
        String content = "new append!";
        //按方法A追加文件
        AppendToFile.appendMethodA(fileName, content);
        AppendToFile.appendMethodA(fileName, "append end.");
        //显示文件内容
        ReadFromFile.readFileByBytes(fileName);//.readFileByLines(fileName);
       
        
        /* //按方法B追加文件
        AppendToFile.appendMethodB(fileName, content);
        AppendToFile.appendMethodB(fileName, "append end. \n");
        //显示文件内容
        ReadFromFile.readFileByBytes(fileName);
       // ReadFromFile.readFileByLines(fileName);
    }
*/
    
    
    public static String readFileSdcard(String fileName){

        String res=""; 

        try{ 

         FileInputStream fin = new FileInputStream(fileName); 

         int length = fin.available(); 

         byte [] buffer = new byte[length]; 

         fin.read(buffer);     

         res = EncodingUtils.getString(buffer, "UTF-8"); 

         fin.close();     

        } 

        catch(Exception e){ 

         e.printStackTrace(); 

        } 

        return res; 

   }
    
    
    
    
    /**

    * 获取文件夹大小

    * @param file File实例

    * @return long 单位为M

    * @throws Exception

    */

    public static long getFolderSize(java.io.File file)throws Exception{

	    long size = 0;
	
	    java.io.File[] fileList = file.listFiles();
	
	    for (int i = 0; i < fileList.length; i++)
	
	    {
	
	    if (fileList[i].isDirectory())
	
	    {
	
	    size = size + getFolderSize(fileList[i]);
	
	    } else
	
	    {
	
	    size = size + fileList[i].length();
	
	    }
	
	    }
	
	    return size/1048576;

    }


    /**

    * 删除指定目录下文件及目录

    *

    * @param deleteThisPath

    * @param filepath

    * @return

    */

    public static String deleteFolderFile(String filePath, boolean deleteThisPath) {
	   
    	if (!TextUtils.isEmpty(filePath)) {
	    File file = new File(filePath);
	    if (file.isDirectory()) {// 处理目录
	    File files[] = file.listFiles();
	    for (int i = 0; i < files.length; i++) {
	    deleteFolderFile(files[i].getAbsolutePath(), true);
	    } 
	    }
	    if (deleteThisPath) {
		    if (!file.isDirectory()) {// 如果是文件，删除
		    	file.delete();
		    } else {// 目录
		    		if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
		    			file.delete();
		    		}
		    }
	    }
	
	    }
		return "Success!";
    }
}