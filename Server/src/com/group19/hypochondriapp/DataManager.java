package com.group19.hypochondriapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataManager
{
	//To be used with loadStationTravel to specify the info desired
	public static final String ENTER = "En11";
	public static final String EXIT = "Ex11";
	public static final String WEEK = "Week";
	public static final String SAT = "sat";
	public static final String SUN = "sun";
	private TreeMap<String, float[]> stations;
	private LinkedList<StationInfo> currentStations;
	
	//Files for stored data
	File boroughKey = new File("./res/DataManager/BoroughKey.txt");
	File boroughDensities = new File("./res/DataManager/BoroughDensities.txt");
	File boroughPlaces = new File("./res/DataManager/BoroughPlace.txt");
	File fakeNHS = new File("./res/DataManager/FakeNHS.txt");
	File fluRates = new File("./res/DataManager/flurates.csv");

	
	public DataManager() 
	{
		init();
	}
	
	public void init()
	{
		stations = new TreeMap<String, float[]>();
		currentStations = new LinkedList<StationInfo>();
		loadStations();
	}
	
	//Used to get the BroughNames for Analysis Manager.
	public String[] getBoroughNames()
	{
		
		String[] BoroughNames = new String[33];
		
		try 
		{
 
			String Temp;
			BufferedReader br = new BufferedReader(new FileReader(boroughKey));
			Temp = br.readLine();
			br.close();
			BoroughNames = Temp.split(", ");
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/DataAnalysis/BoroughKey.txt.");
		
		}
		
		return BoroughNames;
		
	}
	
	//Used to get the Borough Densities for AnalysisManager.
	public int[] getBoroughDensities()
	{
	
		int[] PopDen = new int[33];
		BufferedReader br = null;
		
		//Takes in Densities, in sets it to the array, then sets borough and pop to cells.
		
		try 
		{
	
			String BoroughPos;
			br = new BufferedReader(new FileReader(boroughDensities));
			BoroughPos = br.readLine();
			br.close();
			String[] tokens = BoroughPos.split(" ");
			
			for(int i = 0; i < tokens.length; i++)	{ PopDen[i] = (int)(259*Double.valueOf(tokens[i]));	}
	
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/DataManager/BoroughDensities.txt.");
			
		}
		
		return PopDen;
		
	}
	
	//Used to allocate the cells boroughs for AnalysisManager.
	public byte[] getBoroughPlaces()
	{
		
		byte[] BoroughPlaces = new byte[1600];
		BufferedReader br = null;
		
		try 
		{
 
			String BoroughPos;
			br = new BufferedReader(new FileReader(boroughPlaces));
			BoroughPos = br.readLine();
			br.close();
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 1600; i++)
			{
				
				BoroughPlaces[i] =  Byte.valueOf(tokens[i]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/DataManager/BoroughPlace.txt.");
		
		}
		
		return BoroughPlaces;
		
	}
	
	
	//Get's the Fake NHS data.
	public float[] getNHS()
	{
		
		float[] NHS = new float[33];
		BufferedReader br = null;
		
		try 
		{
 
			String BoroughPos;
			br = new BufferedReader(new FileReader(fakeNHS));
			BoroughPos = br.readLine();
			br.close();
			String[] tokens = BoroughPos.split(" ");
				
			for(int i = 0; i < 33; i++)
			{
				
				NHS[i] =  Float.valueOf(tokens[i]);
					
			}
 
		} 
		catch (Exception e) 
		{
			
			MainManager.logMessage("#DataManager: Could not read ./res/DataManager/FakeNHS.txt.");
		
		}
		
		return NHS;
		
	}
	
	//Gets the sample flu rates found on data.london.gov
	public float[] getFluRates(String place)
	{
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(fluRates));
		}
		catch(FileNotFoundException e)
		{
			MainManager.logMessage("#DataManager: Could not find file \"" + fluRates.getAbsolutePath() + "\"");
			e.printStackTrace();
			return null;
		}
		
		String record = new String();
		float[] values = null;
		
		try
		{
			while(record != null)
			{
				record = reader.readLine();
				if(record == null) break;
				String[] attribs = record.split(",");
				if(attribs.length < 1) continue;
				if(!attribs[1].contains(place)) continue;
				
				values = new float[attribs.length - 2];
				
				for(int i = 2; i < attribs.length; i++)
				{
					try
					{
						values[i-2] = Float.parseFloat(attribs[i]);
					}
					catch(NumberFormatException e)
					{
						values[i-2] = -1;
					}
				}
				break;
			}
		}
		catch(IOException e)
		{
			MainManager.logMessage("#DataManager: Could not read \"" + fluRates.getAbsolutePath() + "\", encountered IOException");
			e.printStackTrace();
		}
		
		try
		{
			reader.close();
		}
		catch(Exception e){}
		
		return values;
	}

	
	//Gets the next StationInfo as long as loadStationTravel has been called and there are more stations available to get
	public StationInfo getNextStation()
	{
		if(currentStations.isEmpty()) return null;
		else return currentStations.poll();
	}
	
	//Loads from the file next to be used by Analysis to enable getNextStation()
	public void loadStationTravel(String direction, String time)
	{
		currentStations.clear();
		//MainManager.logMessage("#DataManager: Current working directory " + System.getProperty("user.dir"));
		String path = "./res/TravelManager/CSVTravelData/" + direction + time + ".xls.csv";
		File csv = new File(path);
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(csv));
		}
		catch(FileNotFoundException e)
		{
			MainManager.logMessage("#DataManager: Could not find file \"" + csv.getAbsolutePath() + "\"");
			e.printStackTrace();
			return;
		}
		
		String record = new String();
		
		try
		{
			do
			{
				record = reader.readLine();
				if(record == null) break;
				if(record.length() == 0) continue;
				
				if(Character.isDigit(record.charAt(0)))
				{
					String[] attributes = record.split(",");
					String stationName = attributes[1];
					stationName = stationName.replaceAll("\"", "").trim();
					float[] coords = getStationLocation(stationName);
					
					int[] values = new int[96]; //96 sets of 15 minutes per day
					
					for(int i = 0; i < 96; i++)
					{
						values[i] = Integer.parseInt(attributes[4 + i]);
					}
					
					currentStations.add(new StationInfo(coords, values));
				}
			}
			while(record != null);
		}
		catch(IOException e)
		{
			MainManager.logMessage("#DataManager: Could not read \"" + csv.getPath() + "\", encountered IOException");
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			MainManager.logMessage("#DataManager: Unable to parse values in\"" + csv.getPath() + "\"");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e){}
		}
		
		return;
		
		
	}
	
	
	//Gets the insight for the given day
	//Probably contains a bug where record will not be found as 01-01-2011 is in flu2010.csv
	//Returns -1 if nothing found in file
	public byte getGoogleInsights(Calendar date)
	{
		Byte returnVal = -1;
		String dir = new String("./res/GoogleManager/flu" + date.get(Calendar.YEAR) + ".csv");
		File csv = new File(dir);
		
		returnVal = getInsightsFromFile(date, csv);
		
		if(returnVal == -1)
		{
			if(date.get(Calendar.MONTH) == Calendar.JANUARY)
			{
				int year = date.get(Calendar.YEAR) - 1;
				dir = new String("./res/GoogleManager/flu" + year + ".csv");
				returnVal = getInsightsFromFile(date, new File(dir));
			}
			else if(date.get(Calendar.MONTH) == Calendar.DECEMBER)
			{
				int year = date.get(Calendar.YEAR) + 1;
				dir = new String("./res/GoogleManager/flu" + year + ".csv");
				returnVal = getInsightsFromFile(date, new File(dir));
			}
		}
		
		return returnVal;
	}
	
	private byte getInsightsFromFile(Calendar date, File csv)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Byte returnVal = -1;
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader(csv));
		}
		catch(FileNotFoundException e)
		{
			MainManager.logMessage("#DataManager: Google Insights does not exist for that year, try updating GoogleManager");
			return -1;
		}

		String record = new String();

		try
		{
			while(record != null)
			{
				record = reader.readLine();

				if(record == null) continue;
				if(record.length() == 0) continue;

				if(Character.isDigit(record.charAt(0)))
				{
					String[] pair = record.split(",");
					String[] dates = pair[0].split(" - ");

					long earlyDate = format.parse(dates[0]).getTime();
					long laterDate = format.parse(dates[1]).getTime();

					if(date.getTimeInMillis() <= laterDate && date.getTimeInMillis() >= earlyDate)
					{
						returnVal = Byte.parseByte(pair[1]);
						break;
					}
				}
			}
		}
		catch(IOException e)
		{
			MainManager.logMessage("#DataManager: Could not read Google Insights, encountered IOException");
			e.printStackTrace();
			returnVal = -1;
		}
		catch(ParseException e)
		{
			MainManager.logMessage("#DataManager: Unable to parse dates in Google Insights file");
			e.printStackTrace();
			returnVal = -1;
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e){}
		}

		return returnVal;
	}
	
	public float[] getStationLocation(String name)
	{
		return stations.get(name.toUpperCase());
	}
	
	//Loads the station locations from a KML file into a TreeMap for later retrieval by AnalysisManager
	private void loadStations()
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("./res/TravelManager/stations.kml"));
			
			doc.getDocumentElement().normalize();
			
			NodeList stationList = doc.getElementsByTagName("Placemark");
			
			for(int i = 0; i < stationList.getLength(); i++)
			{
				Node stationNode = stationList.item(i);
				
				NodeList stationInfo = stationNode.getChildNodes();
				
				String stationName = null;
				float[] coordinates = new float[2];
				
				for(int j = 0; j < stationInfo.getLength(); j++)
				{
					if(stationInfo.item(j).getNodeName() == "name")
					{
						stationName = stationInfo.item(j).getTextContent();
						if(stationName.contains(" Station")) stationName = stationName.replaceFirst(" Station", "");
					}
					else if(stationInfo.item(j).getNodeName() == "Point")
					{
						String coordStr = stationInfo.item(j).getTextContent();
						String[] coords = coordStr.split(",");
						coordinates[0] = Float.parseFloat(coords[0]);
						coordinates[1] = Float.parseFloat(coords[1]);
					}
				}
				
				//System.out.println(stationName.toUpperCase().trim());
				stations.put(stationName.toUpperCase().trim(), coordinates);
			}
		}
		catch(Exception e)
		{
			MainManager.logMessage("#DataManager: Could not parse station KML file");
			e.printStackTrace();
		}
	}
	
	//Class containing coordinates and movement of people for a single station
	public class StationInfo
	{
		private StationInfo(float[] coords, int[] peeps)
		{
			coordinates = coords;
			people = peeps;
		}
		
		public float[] coordinates;
		public int[] people;
	}
}
