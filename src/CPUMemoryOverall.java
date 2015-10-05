

package com.example.androidserver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.format.DateFormat;
import android.util.Log;

public class CPUMemoryOverall extends Thread{
	

    
    int ovetallCPUSystem , OverallCPUUser ,ProcStaCPU , oldProcStatotal;
    int overallTotalMemory, overallFreeMemory, totalTime,ProcStatotal,overallUsedMemory;
    int utime,stime,cutime,cstime,starttime,memory,UID=0,PID=0;
	int OldProcStatUtime,OldProcStatNtime,OldProcStatStime;
    int ProcStatUtime,ProcStatNtime,ProcStatStime,ProcStatItime,ProcStatIOWtime,ProcStatIRQtime,ProcStatSIRQtime;
    double CurReceived,CurSend,Received,PreReceived,PreSend,Send;
    String sendovetallCPUSystemString,sendOverallMemoryString,sendOverallCPUUserString,sendOverallCPUMemoryString,cDateTime;
    Boolean Stop=false;
    SimpleDateFormat dateFormat;
    
    Thread server;
    
	Process process;
	PackageManager pm;
	ApplicationInfo apliInfo;
    
    float oldtotalTime, cpu_usage;		    
    String[]  s = new String[100];
    RandomAccessFile reader;
    
    
    
    
	@Override
	public void run()  {
		// TODO Auto-generated method stub
		 //Initialize with 0 at the start
   	 try
   	 {
   	     OldProcStatUtime=0;	
  		 OldProcStatNtime=0;
  		 OldProcStatStime=0;	
  		 oldProcStatotal =0; 
  		 Log.e("sendOverallCPUMemoryString from send data","hello in run of over all cpu memory");
  		 while (!Stop){
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
 			 
 			 //Overall CPU Data
 			 ovetallCPUSystem=(ProcStatUtime-OldProcStatUtime)*100/ProcStaCPU;
      		 OverallCPUUser=((ProcStatNtime-OldProcStatNtime)+(ProcStatUtime-OldProcStatUtime))*100/ProcStaCPU;	
      		 
      		Log.e("OverallCPUUser",""+OverallCPUUser);
      		 
      		 //============================Extract Memory Data=============================
      		 reader = new RandomAccessFile("/proc/meminfo", "r");
  			 for (int i=0;(s[i]=reader.readLine())!=null;i++)
  			 {
//  				Log.e("procstat==", "ProcMeminfoString == over all="+ s[i]);
  				 if(i==0)
  				 {
  					overallTotalMemory = Integer.parseInt((s[i].substring(s[i].indexOf(":")+1,s[i].length()-3)).replaceAll("\\s",""));
  				
  				 }
  				 else if(i==1){
  					overallFreeMemory = Integer.parseInt((s[i].substring(s[i].indexOf(":")+1,s[i].length()-3)).replaceAll("\\s",""));
  				 }
  				 else{ break; }
  				
  			 }	
  			 	overallUsedMemory = overallTotalMemory - overallFreeMemory;
				sendOverallCPUMemoryString=sendOverallCPUMemoryString+ovetallCPUSystem+"\n"+cDateTime+","+OverallCPUUser+","+overallUsedMemory;
				//Log.e("sendOverallCPUMemoryString",""+sendOverallCPUMemoryString);
				
				  		 }
				   	 }
				   	 catch(IOException e)
				   	 {
				   		 	
				   	 } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 
		
	}
	
	public String StopOverallMemoryCPU() throws InterruptedException {
		  Thread.sleep(1000);
		  
	      Stop = true;
	     
	      Log.e("sendOverallCPUMemoryString from send data",""+sendOverallCPUMemoryString);
	      
	      return sendOverallCPUMemoryString;
	   }
    



   
	
	
}