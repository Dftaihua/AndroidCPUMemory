
package com.example.androidserver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;


public class CPUMemoryofApp extends Thread {
	  int ovetallCPUSystem , OverallCPUUser ,ProcStaCPU , oldProcStatotal;
	    int overallTotalMemory, overallFreeMemory, totalTime,ProcStatotal;
	    int utime,stime,cutime,cstime,starttime,memory,UID=0,PID=0;
		int OldProcStatUtime,OldProcStatNtime,OldProcStatStime;
	    int ProcStatUtime,ProcStatNtime,ProcStatStime,ProcStatItime,ProcStatIOWtime,ProcStatIRQtime,ProcStatSIRQtime;
		double CurReceived,CurSend,Received,PreReceived,PreSend,Send;
	    String sendCPUlMemoryString,cDateTime;
		Process process;
		PackageManager pm;
		SimpleDateFormat dateFormat;
		ApplicationInfo apliInfo;
		Boolean Stop=false,sendBoolean=false;
	    float oldtotalTime, cpu_usage;		    
	    String[]  s = new String[100];
	    RandomAccessFile reader;
	    public CPUMemoryofApp(int uID, int pID) {
			super();
			UID = uID;
			PID = pID;
		}
	    @Override
		public void run() {
	    	try
	    	{
	    	//===============================Process CPU data=================================
	    	//Initialize variables
	    	totalTime = 0;
	    	cpu_usage = 0;
	    	utime = stime = cutime = cstime = starttime = memory = 0;
	    	//Get overall cpu and memory variable data
			 OldProcStatUtime=0;	
     		 OldProcStatNtime=0;
     		 OldProcStatStime=0;	
     		 oldProcStatotal =0; 
     		sendCPUlMemoryString="";
     		 while ( !Stop){
	    	 //Open proc/stat file
     			 
     			Thread.sleep(1100);
     			//===================== Extract current system date and time ===========================
     			  dateFormat= new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss",Locale.ENGLISH);
    			  cDateTime=dateFormat.format(new Date(System.currentTimeMillis()));
    			  
    			  
     			 //===================== Extract CPU Data===========================
		    	 reader = new RandomAccessFile("/proc/stat", "r");
    			 String ProcStatload = reader.readLine();
    			 String[] ProcStattoks = ProcStatload.split(" ");
    			
    			 //Take old data
    			 OldProcStatUtime=ProcStatUtime;	
	       		 OldProcStatNtime=ProcStatNtime;
	       		 OldProcStatStime=ProcStatStime;
	       		 //Extract data from the file
    			 ProcStatUtime=Integer.parseInt(ProcStattoks[2]);      			
    			 ProcStatNtime=Integer.parseInt(ProcStattoks[3]);
    		     ProcStatStime=Integer.parseInt(ProcStattoks[4]);
    			 ProcStatItime=Integer.parseInt(ProcStattoks[5]);
    			 ProcStatIOWtime=Integer.parseInt(ProcStattoks[6]);
    			 ProcStatIRQtime=Integer.parseInt(ProcStattoks[7]);
    			 ProcStatSIRQtime=Integer.parseInt(ProcStattoks[8]);
    			 oldProcStatotal=ProcStatotal;
    			ProcStatotal=ProcStatUtime+ProcStatNtime+ProcStatStime+ProcStatItime+ProcStatIOWtime+ProcStatIRQtime+ProcStatSIRQtime;
    			 ProcStaCPU=ProcStatotal-oldProcStatotal;
    			 //Open /proc/pid/stat file to get CPU data
    			 reader = new RandomAccessFile("/proc/"+PID+"/stat", "r");
    			 String load = reader.readLine();
    			 String[] toks = load.split(" ");
    			 utime    = Integer.parseInt(toks[13]);
    			 stime    = Integer.parseInt(toks[14]);
    			 cutime   = Integer.parseInt(toks[15]);
    			 cstime   = Integer.parseInt(toks[16]);
    			 starttime= Integer.parseInt(toks[21]);
    			 oldtotalTime = totalTime;
    			 totalTime    = utime + stime + cutime + cstime;
    			 
    			 
    			 //Process CPU Data
    			 cpu_usage = ( (totalTime -oldtotalTime)*100 /ProcStaCPU);
    			 
    			 
    			 //==============================Process Memory data====================================
    			 reader = new RandomAccessFile("/proc/"+PID+"/status", "r");
    			 //String procPidStatusString = reader.readLine();
    			 String[]  s = new String[100];
    			 int i=0;
    			 while ((s[i]=reader.readLine())!=null)
    			 {
				 
    				 if(s[i].contains("VmRSS"))
    				 {
    					 memory= Integer.parseInt((s[i].substring(s[i].indexOf(":")+1,s[i].length()-3)).replaceAll("\\s",""));
    					 sendCPUlMemoryString=sendCPUlMemoryString+"\n"+cDateTime+","+cpu_usage+","+memory;
    	    			 Log.e("CPU of Application = "+cpu_usage, "Memory of Application = "+memory);
    	    			 
    					 break;
    				 }
    				 i++;
     		 }
     		 }
	    	}
	    	catch (IOException e)
	    	{
	    		
	    	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    
	    //Stopping thread
	    public String requestStop() {
		      Stop = true;
		      String send=new String();
		      send=sendCPUlMemoryString;
//		      String[] toppings = new String[20];
//		      toppings=sendCPUlMemoryString.split(" ");
//		      for(String s : toppings)
//		      {
//		    	  send=send+"\n"+s;
//		      }
//		      sendCPUlMemoryString="";
		      return send;
		      
		   }
	    
	    
	    // Send data to server
	    public String senddata() {
	    	 String send=new String();
		      send=sendCPUlMemoryString;
//		      String[] toppings = new String[20];
//		      toppings=sendCPUlMemoryString.split(" ");
//		      for(String s : toppings)
//		      {
//		    	  send=send+"\n"+s;
//		      }
	    	sendCPUlMemoryString="";
	    	return send;
		   }
	    
	
	    
//	    public void processData(int PID) throws IOException{
//	    	
//	    	//===============================Process CPU data=================================
//	    	//Initialize variables
//	    	totalTime = 0;
//	    	cpu_usage = 0;
//	    	utime = stime = cutime = cstime = starttime = memory = 0;
//	    	
//	    	//Get overall cpu and memory variable data
//	    	 
//			 OldProcStatUtime=0;	
//     		 OldProcStatNtime=0;
//     		 OldProcStatStime=0;	
//     		 oldProcStatotal =0; 
//	    	 
//     		 while (true){
//	    	 //Open proc/stat file
//     			 //===================== Extract CPU Data===========================
//		    	 reader = new RandomAccessFile("/proc/stat", "r");
//    			 String ProcStatload = reader.readLine();
//    			 String[] ProcStattoks = ProcStatload.split(" ");
//    			 
//    			 
//    			 //Take old data
//    			 OldProcStatUtime=ProcStatUtime;	
//	       		 OldProcStatNtime=ProcStatNtime;
//	       		 OldProcStatStime=ProcStatStime;
//    			 
//	       		 
//	       		 //Extract data from the file
//    			 ProcStatUtime=Integer.parseInt(ProcStattoks[2]);      			
//    			 ProcStatNtime=Integer.parseInt(ProcStattoks[3]);
//    		     ProcStatStime=Integer.parseInt(ProcStattoks[4]);
//    			 ProcStatItime=Integer.parseInt(ProcStattoks[5]);
//    			 ProcStatIOWtime=Integer.parseInt(ProcStattoks[6]);
//    			 ProcStatIRQtime=Integer.parseInt(ProcStattoks[7]);
//    			 ProcStatSIRQtime=Integer.parseInt(ProcStattoks[8]);		    	 
//	       		 
//    			 
//    			 oldProcStatotal=ProcStatotal;
//    			ProcStatotal=ProcStatUtime+ProcStatNtime+ProcStatStime+ProcStatItime+ProcStatIOWtime+ProcStatIRQtime+ProcStatSIRQtime;
//    			 ProcStaCPU=ProcStatotal-oldProcStatotal;
//	    	
//    			 //Open /proc/pid/stat file to get CPU data
//    			 reader = new RandomAccessFile("/proc/"+PID+"/stat", "r");
//    			 String load = reader.readLine();
//    			 String[] toks = load.split(" ");
//			
//    			 utime    = Integer.parseInt(toks[13]);
//    			 stime    = Integer.parseInt(toks[14]);
//    			 cutime   = Integer.parseInt(toks[15]);
//    			 cstime   = Integer.parseInt(toks[16]);
//    			 starttime= Integer.parseInt(toks[21]);
// 		
//    			 oldtotalTime = totalTime;
//    			 totalTime    = utime + stime + cutime + cstime;
// 			
//    			 //Process CPU Data
//    			 cpu_usage = ( (totalTime -oldtotalTime)*100 /ProcStaCPU);
//    			 
//    		
// 			
//    			 //==============================Process Memory data====================================
// 			
//    			 reader = new RandomAccessFile("/proc/"+PID+"/status", "r");
//    			 //String procPidStatusString = reader.readLine();
//    			 String[]  s = new String[100];
//    			 int i=0;
//    			 while ((s[i]=reader.readLine())!=null)
//    			 {
//				 
//    				 if(s[i].contains("VmRSS"))
//    				 {
//    					 memory= Integer.parseInt((s[i].substring(s[i].indexOf(":"),s[i].length()-3)).replaceAll("\\s",""));
//    					 break;
//    				 }
//    				 i++;
//    			 }
//     		 
//     		 }
// 			 	 
//	    }

}
