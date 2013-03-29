package com.group19.hypochondriapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

//Class that handles and maintains references (and threads) to all modules of the system.
public class MainManager 
{
	private static boolean shutdown = false;
	
	//List of all modules.
	private static TwitterManager twitterManager;
	private static GoogleManager googleManager;
	private static AppNetworkManager appNetworkManager;
	private static DataManager dataManager;
	
	//Objects for storing logged messages.
	private static ConcurrentLinkedQueue<String> log;
	private static SimpleDateFormat timeFormat;
	
	public static void init()
	{
		log = new ConcurrentLinkedQueue<String>();
		timeFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS - ");
		
		twitterManager = new TwitterManager();
		googleManager = new GoogleManager();
		appNetworkManager = new AppNetworkManager();
	}
	
	public static void cleanup()
	{
		writeLog();
	}
	
	public static void logMessage(String message)
	{
		String toLog = new String(timeFormat.format(Calendar.getInstance().getTime()) + message);
		log.add(toLog);
		System.out.println(toLog);
	}
	
	public static void writeLog()
	{
		if(log.isEmpty()) return;
		
		File file = new File("./res/log.txt");
		
		if(file.exists()) file.delete();
		
		try
		{
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			while(!log.isEmpty())
			{
				writer.write(log.poll() + "\r\n");
			}
			
			writer.close();
		}
		catch(IOException e)
		{
			logMessage("#MainManager: Could not write log to file");
		}
		
	}
	
	public static void main(String[] args) throws IOException 
	{
		init();
		
		
		/*
		Thread twitter = new Thread(twitterManager);
		twitter.start();
		
		//Tests for the twitter miner.
		System.in.read();
		shutdown = true;
		twitter.interrupt();
		
		try 
		{
			twitter.join();
		} 
		catch (InterruptedException e) {}
		
		*/
		
		appNetworkManager.run();
		
		//googleManager.updateCSV();
		
		cleanup();
	}
	
	public static TwitterManager getTwitterManager() { return twitterManager; }
	public static GoogleManager getGoogleManager() { return googleManager; }
	public static AppNetworkManager getAppNetworkManager() { return appNetworkManager; }
	public static DataManager getDataManager() {return dataManager; };
	
	public static boolean isShutdown() { return shutdown; }

}
