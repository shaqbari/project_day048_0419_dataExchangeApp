package com.ss.util.file;

public class FileUtil {
	//확장자를 제외한 파일명만 추출하기
	// c:/data/test.jpg, mario.png
	public static String getOnlyName(String path){				
		int last=path.lastIndexOf(".");			
		return path.substring(0, last);
	}	
	
	/*public static void main(String[] args) {
		System.out.println(getOnlyName("m.amri.png"));
	}*/
}
