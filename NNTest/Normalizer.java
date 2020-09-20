import jigl.gui.*;
import jigl.image.*;
import jigl.image.utils.ImageConverter;
import jigl.image.io.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import jigl.image.ops.levelOps.*;
import jigl.image.ops.morph.*;
import jigl.image.ops.*;

public class Normalizer
{
	private GrayImage gimage;
	
	//downsampled matrix data
	private boolean[][] data;
	
	//downsampling width and heigh for vowel
	static final int vnwidth=10;
	static final int vnheight=10;
	
	//downsampling width and height for consonant
	//static  int cnwidth=20;
	//static  int cnheight=20;
	static  int cnwidth;
	static  int cnheight;
	
	//width and height ratios
	int wratio;
	int hratio;
	
	//constants to determine whether consonant or vowel is normalised
	public final static int CON=1;
	public final static int VOW=2;
	
	//for making same size
	private int SAME_SIZE = 30;
	
	
	private final boolean VERBROSE = false;
	
	
	
	
	//for the test cases
	private boolean bRaw=false;
	private boolean bInt=false;
	private boolean bSq=false;
	private boolean bSs=false;
	
	
	
	
	
	
	
	/*public Normalizer(GrayImage gimage, int constant)
	{
		this.gimage=gimage;		
		

		
		if (constant==CON)
		{
			initForConsonant();
		}
		else if (constant==VOW)
		{
			initForVowel();
		}

	}*/
	
	public Normalizer(GrayImage gimage, int constant, int dx, int dy,
						boolean bRaw, boolean bInt, boolean bSq, boolean bSs )
	{
		this.gimage=gimage;		
		
		this.cnwidth=dx;
		this.cnheight=dy;
		
		this.bRaw = bRaw;
		this.bInt = bInt;
		this.bSq = bSq;
		this.bSs = bSs;
		
		if (constant==CON)
		{
			initForConsonant();
		}
		else if (constant==VOW)
		{
			initForVowel();
		}

	}


	private byte[][] interpolate(byte[][] data)
	{
		System.out.println("interpolate");
		//byte[][] data = new BinaryImage(igimage).getData();
		byte[][] ndata;
		
		if (VERBROSE)
		{
			System.out.println("h-" + data.length);
			System.out.println("w-" + data[0].length);
			for (int y = 0; y < data.length; y++)
			{
				System.out.print("\n");
				for (int x=0; x<data[0].length; x++)
				{
					System.out.print(data[y][x]);
				}
			}
				System.out.print("\n");
				System.out.print("\n");
		}
		
		/*double ratio = (double)igimage.X()/(double)gimage.Y();
		
		if (ratio>=0.7 && ratio<=1.3)
		{
			
		}
		else if (ratio < 0.7)
		{
			
		}*/
		
		//interpolate
		int icounter=1; //no. of iterations
		int ilimit=2;
		
		int iicounter=icounter;
		int iilimit=ilimit;
		
		ndata = new byte[data.length*ilimit][data[0].length*ilimit];
		//init ndata
		for (int y=0;y<ndata.length;y++)
			for (int x=0;x<ndata[0].length;x++)
				ndata[y][x]=0;
		
		int ny=0;
		for (int y = 0; y < data.length; y++)
		{
			if (y!=0 && iicounter!= iilimit) 
			{
				y--;
				iicounter++;
				ny++;
			}
			else if (iicounter==iilimit)
			{
				iicounter=1;
				ny++;
			}
					
			int x;
			int nx=0;
			for (x=0; x<data[0].length; x++)
			{
				if (x!=0 && icounter!= ilimit) 
				{
					x--;
					icounter++;
					nx++;
				}
				else if (icounter==ilimit)
				{
					icounter=1;
					nx++;
				}
				
				ndata[ny][nx]=data[y][x];
			}
			
			ndata[ny][nx+1]=data[y][x-1];
		}
		
		int y=data.length-1;
		icounter=1;
		int x;
		int nx=0;
		ny++;
		for (x=0; x<data[0].length; x++)
		{
			if (x!=0 && icounter!= ilimit) 
			{
				x--;
				icounter++;
				nx++;
			}
			else if (icounter==ilimit)
			{
				icounter=1;
				nx++;
			}
			
			ndata[ny][nx]=data[y][x];			
		}
		ndata[ny][nx]=data[y][x-1];
		//System.out.print(data[y][x-1]);
		
		if (VERBROSE)
		{
			System.out.println("\ninterporlated");
			for (int yy = 0; yy < ndata.length; yy++)
			{
				System.out.print("\n");
				for (int xx=0; xx<ndata[0].length; xx++)
				{
					System.out.print(ndata[yy][xx]);
				}
			}
		}
		
		return ndata;	
	}

	private byte[][] makeSameSize(byte[][] data)
	{
		System.out.println("same size");
		 byte[][] ndata = new byte[SAME_SIZE][SAME_SIZE];
		 
		 //init ndata
		 for (int y=0; y<SAME_SIZE; y++)
		 	for(int x=0; x<SAME_SIZE; x++)
		 		ndata[y][x]=0;
		 		
		int ylimit=data.length;
		int xlimit=data[0].length;
		
		if (data.length>=SAME_SIZE) ylimit=SAME_SIZE;
		if (data[0].length>=SAME_SIZE) xlimit=SAME_SIZE;
		
		for (int y=0;y<ylimit;y++)
			for (int x=0;x<xlimit;x++)
				ndata[y][x]=data[y][x];
		
		 
		 
		 return ndata;
		 		
	 
	}

	private byte[][] makeSquare(byte[][] data)
	{
		System.out.println("square");
		int height = data.length;
		int width = data[0].length;
		int sizer = (height>width?height:width);
		
		byte[][] ndata = new byte[sizer][sizer];
		//reset ndata
		for (int y=0;y<ndata.length;y++)
			for (int x=0;x<ndata[0].length;x++)
				ndata[y][x]=0;
		
		for (int y=0;y<data.length;y++)
			for (int x=0;x<data[0].length;x++)
				ndata[y][x]=data[y][x];
		
		
		return ndata;
	}

	private void initForVowel()
	{
		byte[][] bidata = new BinaryImage(gimage).getData();
		
		data = new boolean[vnheight][vnwidth];
				
		wratio = (int)Math.round((double)bidata[0].length/(double)vnwidth);
		hratio =(int)Math.round((double)bidata.length/(double)vnheight);
		
		if (wratio == 0) wratio = 1;
		if (hratio == 0) hratio = 1;
 				
		for (int y=1; y<=vnheight; y++)
		{
			for (int x=1; x<=vnwidth; x++)
			{
				data[y-1][x-1]=checkStatus(bidata,x,y);	
			}
		}
		
		int line=0;
		System.out.println("\n01234567890123456789");
		for (int y=1; y<=vnheight; y++)
		{
			System.out.print("\n"+line);
			if (++line==10) line = 0;
			for (int x=1; x<=vnwidth; x++)
			{
				if(data[y-1][x-1]==true)
					System.out.print("X");
				else
					System.out.print(".");
			}
		}
		System.out.print("\n==========\n");
	}
	
	private void initForConsonant()
	{
		//raw
		byte[][] bidata = new BinaryImage(gimage).getData();
		
		if (bSq) bidata = makeSquare(bidata);
		
		if (bSs) bidata = makeSameSize(bidata);
		
		if (bInt) bidata = interpolate(bidata);
		
		//int+sq
		//byte[][] bidata = interpolate((makeSquare(new BinaryImage(gimage).getData())));
		
		//int+sq+ss
		//byte[][] bidata = interpolate(makeSameSize(makeSquare(new BinaryImage(gimage).getData())));
		
		//-raw
		//byte[][] bidata = new BinaryImage(gimage).getData();
		
		
		
		//-int
		//byte[][] bidata = interpolate(new BinaryImage(gimage).getData());
		
		//-sq
		//byte[][] bidata = makeSquare(new BinaryImage(gimage).getData());
		
		//-ss
		//byte[][] bidata = makeSameSize(new BinaryImage(gimage).getData());
		
		//-int+ss
		//byte[][] bidata = interpolate(makeSameSize(new BinaryImage(gimage).getData()));
		
		//-sq+ss
		//byte[][] bidata = makeSameSize(makeSquare(new BinaryImage(gimage).getData()));
		
		data = new boolean[cnheight][cnwidth];
				
		wratio = (int)Math.round((double)bidata[0].length/(double)cnwidth);
		hratio =(int)Math.round((double)bidata.length/(double)cnheight);
		
		if (wratio == 0) wratio = 1;
		if (hratio == 0) hratio = 1;
 				
		for (int y=1; y<=cnheight; y++)
		{
			for (int x=1; x<=cnwidth; x++)
			{
				data[y-1][x-1]=checkStatus(bidata,x,y);	
			}
		}
		
		int line=0;
		
		if (VERBROSE)
		{
			//System.out.println("\n01234567890123456789");
			for (int y=1; y<=cnheight; y++)
			{
				System.out.print("\n"+line);
				if (++line==10) line = 0;
				for (int x=1; x<=cnwidth; x++)
				{
					if(data[y-1][x-1]==true)
						System.out.print("X");
					else
						System.out.print(" ");
				}
			}
			System.out.print("\n==========\n");
		}
	}
	
	private boolean checkStatus(byte[][] bidata, int xc, int yc)
	{
		try
		{
			for (int y=yc*hratio; y<(yc*hratio)+hratio; y++)
			{
				for (int x=xc*wratio; x<(xc*wratio)+wratio; x++)
				{
					if(bidata[y][x]==1) return true;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		}
		
		return false;
	}
		
	//get the normalised data for nn
	public double[] getVMatrix()
	{
		double [] ddata = new double[vnheight*vnwidth];
		
		for (int y=0; y<data.length; y++)
		{
			for (int x=0; x<data[0].length; x++)
			{
				if (data[y][x]==true) ddata[y*x]=1.0;
				else ddata[y*x]=0.0;
			}
		}
		
		return ddata;
	}
	
	//get the normalised data for nn
	public double[] getCMatrix()
	{
		double [] ddata = new double[cnheight*cnwidth];
		
		for (int y=0; y<data.length; y++)
		{
			for (int x=0; x<data[0].length; x++)
			{
				if (data[y][x]==true) ddata[y*x]=1.0;
				else ddata[y*x]=0.0;
			}
		}
		
		return ddata;
	}
		
	public boolean[][] getData()
	{
		return data;
	}
	
	public static int getVNWidth()
	{
		return vnwidth;
	}
	
	public static int getVNHeight()
	{
		return vnheight;
	}
	
	public static int getCNWidth()
	{
		return cnwidth;
	}
	
	public static int getCNHeight()
	{
		return cnheight;
	}
}
	