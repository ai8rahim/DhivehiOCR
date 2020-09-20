package epublisher;

import java.io.*;
import java.util.Vector;

public class CVDBLoader
{
	String CPATH = "nn\\consonantsDB.txt";
	String VPATH = "nn\\vowelsDB.txt";
	
	BufferedReader br;
	Vector cdata;
	Vector vdata;
	
	public CVDBLoader()
	{
		cdata=new Vector();
		vdata=new Vector();
		String record;
		
		try
		{
			br = new BufferedReader(new FileReader(CPATH));		
			if ((record = br.readLine())!=null )
			{
				cdata.add(parse(record));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			br = new BufferedReader(new FileReader(VPATH));		
			if ((record = br.readLine())!=null )
			{
				vdata.add(parse(record));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	private Object[] parse(String record)
	{
		Object[] obj = new Object[2];	//for the element and the pattern
		
		String[] raw = record.split(":");
		obj[0] = new String(raw[0]);
		
		String[] values = raw[1].split(";");
		double[] dvalues = new double[values.length];
		for (int i = 0; i < values.length; i++)
		{
			dvalues[i] = Double.parseDouble(values[i]);
		}
		obj[1] = dvalues;
		
		return obj;
	}
	
	public Vector getCData()
	{
		return cdata;
	}
	
	public Vector getVData()
	{
		return vdata;
	}
}