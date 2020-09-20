package epublisher;

import java.awt.*;
import java.util.*;

import jigl.image.*;
import jigl.image.ops.levelOps.*;
import jigl.image.ops.morph.*;
import jigl.image.ops.*;

public class CharacterObject implements java.io.Serializable
{
	private int x1,y1,x2,y2;	//four coordinates of the character boundary
	
	private Vector pairConsonantVowel;	//used for creation of connected components
	
	private Vector cvSet;	//used for the classfiication of character and vowel
	
	private int cvCount;	//the count of the vowels and consonants combined
	
	private Rectangle consonantBoundary;	//boundary of consonant and the vowel.
	private Rectangle vowelBoundary;		//fixed coz the characterobject must have 2 ideally
											//if not considered invalid at the time
	
	private static int CCOUNT; //consonant count
	private static int VCOUNT; //vowel count
	
	//the OCRed character and Vowel
	String consonant;
	String vowel;
	
	public  static double AVG_WIDTH; //needed to decide whitespace width when formating after ocr
	private  static double TOT_WIDTH; //needed to decide whitespace width when formating after ocr
	
	public CharacterObject(int x1, int y1, int x2, int y2)
	{
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
		
		pairConsonantVowel=new Vector();
		
			cvSet=new Vector();
			cvCount=0;
		
	}
	
	
	//get the consonant letter
	public String getConsonant()
	{
		return consonant;
	}
	
	public String getVowel()
	{
		return vowel;
	}
	
	
	
	//width of character
	public int getWidth()
	{
		return x2-x1;
	}
	
	//height of character
	public int getHeight()
	{
		return y2-y1;
	}
	
	//get the boundary of the full character (consonant + vowel)
	//boundary height is same as line height
	public Rectangle getBoundaryRectangle()
	{
		return new Rectangle(x1, y1, x2-x1, y2-y1);
	}
	
	
	//call this method to start the seperation of consonant/vowel
	public void seperateCharacterPair(GrayImage gimage)
	{	
			
		byte[][] data = new BinaryImage(gimage).getData();
		
		
		//=========================================================================
		Vector neighbourValueSet = new Vector();
		int nextNeighbourValue=2;
		
		//raster image (the binary data)
		for (int y=y1; y<=y2; y++)
		{
				//System.out.print("\n\t");
			//create connected components
			for (int x=x1; x<=x2; x++)
			{
				//create connected components
				if (data[y][x]==1) 
				{
					int neighbourValue = getNeighbourValue(data, x, y);
					if (neighbourValue !=-1)
					{
						data[y][x]=new Integer(neighbourValue).byteValue();
						setNeighbourValue(data,x,y, new Integer(neighbourValue).byteValue());
					}
					else
					{
						data[y][x]=new Integer(nextNeighbourValue).byteValue();;
						setNeighbourValue(data,x,y, new Integer(nextNeighbourValue).byteValue());
						neighbourValueSet.add(nextNeighbourValue);
						nextNeighbourValue++;						
					}
				}
				//System.out.print(data[y][x]);
			}
		}
			
			
		//=========================================================================
		//classify and store connnected components
		for (int y=y1; y<=y2; y++)
		{
		//System.out.print("\n\t");
			for (int x=x1; x<=x2; x++)
			{
		//		System.out.print(data[y][x]);
				if (cvSet.size()==0)
				{
					cvSet.add(new CVBoundary(data[y][x], x, y));
		//					System.out.print("-");
				}
				else
				{
					boolean cvBFound=false;				
					for (int i=0; i<cvSet.size(); i++)
					{
						CVBoundary cvB = (CVBoundary)cvSet.elementAt(i);
						if(cvB.getKey()==data[y][x])
						{
							
							cvB.add(x,y);
							cvBFound=true;
							break;
						}						
					}
					
					if (!cvBFound)
					{
						
						cvSet.add(new CVBoundary(data[y][x], x, y));
		//					System.out.print("+");
					}
				}
			}
		}
		
		//set count for vowel and consonent
		cvCount=cvSet.size();
	//	System.out.println("\t"+cvCount);




//==================================================================
//remove the outer most border which is a bug actually
		//now handle the withins		
		Rectangle curSizer = ((CVBoundary)cvSet.elementAt(0)).getBoundary();
		double biggestArea = curSizer.getWidth()*curSizer.getHeight();
		int biggestIndex=0;
		
		//first determining the biggest rect
		//which must be the outer most boundary
		for (int i=0; i < cvSet.size(); i++)
		{
			curSizer = ((CVBoundary)cvSet.elementAt(i)).getBoundary();
			double tempArea = curSizer.getWidth()*curSizer.getHeight();
			if  (tempArea > biggestArea)
			{
				biggestArea=tempArea;
				biggestIndex=i;
			}
		}	
		
		//now filter the biggest one
		Vector tempSize = new Vector();
		for (int i=0; i < cvSet.size(); i++)
		{
			if (i!=biggestIndex)tempSize.add(cvSet.elementAt(i));
		}
		cvSet = new Vector(tempSize);


		//set count for vowel and consonent
		cvCount=cvSet.size();
		//System.out.println("\t"+cvCount);		
	
	
	
	
	
		//=========================================================================
		//fix too many boundaries
		//to cater for AABAAFILI type occurances
		
		Vector theWithins=new Vector();	//holds the within indexes
		
		if (cvCount>2)
		{
			//check case of overlapping
			//boolean doLoop=true, anyFound=false;
			Rectangle curRect;
			int curIndex=0;
			while(curIndex<cvSet.size())    
			{
				curRect=((CVBoundary)cvSet.elementAt(curIndex)).getBoundary();
				for(int i=curIndex+1; i<cvSet.size(); i++)
				{
					Rectangle victim = ((CVBoundary)cvSet.elementAt(i)).getBoundary();
					
					
					Rectangle intersection = curRect.intersection(victim);
					
					if (curRect.intersects(victim))
						{
							double iArea = intersection.getWidth()*intersection.getHeight();
							double cArea = curRect.getWidth()*curRect.getHeight();
							double vArea = victim.getWidth()*victim.getHeight();
							
							double goodRatio = 0.20;
							double cRatio = (iArea/cArea) * 100;
							double vRatio = (iArea/vArea) * 100;
							if (cRatio >= goodRatio || vRatio >= goodRatio)
							{
								curRect.add(victim);	
								theWithins.add(new Integer(i));						
							}
									
							//anyFound=true;
							
						}
				}
				
			//	doLoop=anyFound;
			//	anyFound = false;
				curIndex++;
				
			}
		}
		
		//now handle the withins
		Vector temp = new Vector();
		for (int i=0; i < cvSet.size(); i++)
		{
			boolean found=false;
			for (int j=0; j<theWithins.size(); j++)
			{				
				int index = ((Integer)theWithins.elementAt(j)).intValue();
				if (index==i) found=true;
			}
			
			if(!found) temp.add(cvSet.elementAt(i));
			
		}
		//now reset and add all in the temp
		cvSet= new Vector(temp);
		
		cvCount=cvSet.size();
		//System.out.println("\t"+cvCount);	
		
		//==========================================================
		//decide the consonant and vowel
		
		
	/*	
		//BASED ON LOCATION-------------------------------
		//the element that is closest to the top and furthest from the bottom is vowel
		//OR the elemtnt that is closest to the bottom and furthest from the top is vowel
	//	if (cvCount<=2)
		{
			Rectangle element1 = ((CVBoundary)cvSet.elementAt(0)).getBoundary();
			Rectangle element2 = ((CVBoundary)cvSet.elementAt(1)).getBoundary();
			
			double diffTop1 = getBoundaryRectangle().getX() - element1.getX();
			double diffTop2 = getBoundaryRectangle().getX() - element2.getX();
			double diffBot1 = (getBoundaryRectangle().getX()+getBoundaryRectangle().getHeight()) - element1.getX();
			double diffBot2 = (getBoundaryRectangle().getX()+getBoundaryRectangle().getHeight()) - element2.getX();
			
			if (diffTop1<diffTop2 && diffBot1>diffBot2)
			{
				vowelBoundary = element1;
				consonantBoundary = element2;
			}
			else if (diffTop1>diffTop2 && diffBot1<diffBot2)
			{
				vowelBoundary = element1;
				consonantBoundary = element2;
			}
			else
			{
				vowelBoundary = element2;
				consonantBoundary = element1;
			}
			
			
		}*/
		
		
		//BASED ON SIZE-------------
		//smallest is most of the time vowel
		if (cvCount==1)
		{
			consonantBoundary = ((CVBoundary)cvSet.elementAt(0)).getBoundary();
			
			CCOUNT++;
			
			//get the avg width
			TOT_WIDTH += consonantBoundary.getWidth();
			AVG_WIDTH = TOT_WIDTH / CCOUNT;
		}
		else if (cvCount==2)
		{
			Rectangle element1 = ((CVBoundary)cvSet.elementAt(0)).getBoundary();
			Rectangle element2 = ((CVBoundary)cvSet.elementAt(1)).getBoundary();
			
			double size1 = element1.getWidth() * element1.getHeight();
			double size2 = element2.getWidth() * element2.getHeight();
			
			if (size1<size2)
			{
				vowelBoundary=element1;
				consonantBoundary = element2;
			}
			else
			{
				vowelBoundary = element2;
				consonantBoundary = element1;
			}
			
			CCOUNT++;
			VCOUNT++;
			
			//get the avg width
			TOT_WIDTH += consonantBoundary.getWidth();
			AVG_WIDTH = TOT_WIDTH / CCOUNT;
		}		
		
	}
	
	
	public Rectangle getConsonantBoundary()
	{
		return consonantBoundary;
	}
	
	public Rectangle getVowelBoundary()
	{
		return vowelBoundary;
	}
	
	public Vector getCVSet()
	{
		return cvSet;
	}
	
	public int getCVCount()
	{
		return cvCount;
	}
	
		
	//sets the neighbourign connected comps recursively
	private void setNeighbourValue(byte[][] data, int x, int y,  byte neighbourValue)
	{
		if(data[y][x+1]==1 && data[y][x+1]!=0)	//e
		{
			 data[y][x+1]=neighbourValue;
			 setNeighbourValue(data, x+1,y,neighbourValue);
		}
		if(data[y+1][x+1]==1 && data[y+1][x+1]!=0)	//se
		{
			 data[y+1][x+1]=neighbourValue;
			 setNeighbourValue(data, x+1,y+1,neighbourValue);
		}
		if(data[y+1][x]==1 && data[y+1][x]!=0)	//s
		{
			 data[y+1][x]=neighbourValue;
			 setNeighbourValue(data, x,y+1,neighbourValue);
		}
		if(data[y+1][x-1]==1 && data[y+1][x-1]!=0)	//ws
		{
			 data[y+1][x-1]=neighbourValue;
			 setNeighbourValue(data, x-1,y+1,neighbourValue);
		}
		if(data[y][x-1]==1 && data[y][x-1]!=0)	//w
		{
			 data[y][x-1]=neighbourValue;
			 setNeighbourValue(data, x-1,y,neighbourValue);
		}
		if(data[y-1][x-1]==1 && data[y-1][x-1]!=0)	//nw
		{
			 data[y-1][x-1]=neighbourValue;
			 setNeighbourValue(data, x-1,y-1,neighbourValue);
		}
		if(data[y-1][x]==1 && data[y-1][x]!=0)	//n
		{
			 data[y-1][x]=neighbourValue;
			 setNeighbourValue(data, x,y-1,neighbourValue);
		}
		if(data[y-1][x+1]==1 && data[y-1][x+1]!=0)	//ne
		{
			 data[y-1][x+1]=neighbourValue;
			 setNeighbourValue(data, x+1,y-1,neighbourValue);
		}
	}
	
	//get the a value of neighbouring connected comp
	//whcih has been set if available
	private int getNeighbourValue(byte[][] data, int x, int y)
	{
		if(data[y][x+1]!=1 && data[y][x+1]!=0)	//e
		{
			return data[y][x+1];
		}
		else if(data[y+1][x+1]!=1 && data[y+1][x+1]!=0)	//se
		{
			return data[y+1][x+1];
		}
		else if(data[y+1][x]!=1 && data[y+1][x]!=0)	//s
		{
			return data[y+1][x];
		}
		else if(data[y+1][x-1]!=1 && data[y+1][x-1]!=0)	//ws
		{
			return data[y+1][x-1];
		}
		else if(data[y][x-1]!=1 && data[y][x-1]!=0)	//w
		{
			return data[y][x-1];
		}
		else if(data[y-1][x-1]!=1 && data[y-1][x-1]!=0)	//nw
		{
			return data[y-1][x-1];
		}
		else if(data[y-1][x]!=1 && data[y-1][x]!=0)	//n
		{
			return data[y-1][x];
		}
		else if(data[y-1][x+1]!=1 && data[y-1][x+1]!=0)	//ne
		{
			return data[y-1][x+1];
		}
		else
			return -1;
	}
	
	//check 8 neighbout pixels to get their count
	private int getPixelCount(byte[][] data, int x, int y)
	{
		int count=0;
		
		if (data[y][x+1]!=0)	//east
			count++;
			
		if (data[y+1][x+1]!=0)
			count++;
			
		if (data[y+1][x]!=0)	//south
			count++;
			
		if (data[y+1][x-1]!=0)
			count++;
			
		if (data[y][x-1]!=0)	//west
			count++;
			
		if (data[y-1][x-1]!=0)
			count++;
			
		if (data[y-1][x]!=0)	//north
			count++;
			
		if (data[y-1][x+1]!=0)
			count++;
			
		return count;
			
	}
	
	public Vector getConsonantVowel()
	{
		return pairConsonantVowel;
	}
	
//consonants stuff
	public int getCX1()
	{
		return new Double(consonantBoundary.getX()).intValue();
	}
	public int getCY1()
	{
		return new Double(consonantBoundary.getY()).intValue();
	}
	
	public int getCX2()
	{
		return new Double(consonantBoundary.getX()+consonantBoundary.getWidth()).intValue();
	}
	
	public int getCY2()
	{
		return new Double(consonantBoundary.getY()+consonantBoundary.getHeight()).intValue();
	}

//vowel stuff
	public int getVX1()
	{
		return new Double(vowelBoundary.getX()).intValue();
	}
	public int getVY1()
	{
		return new Double(vowelBoundary.getY()).intValue();
	}
	
	public int getVX2()
	{
		return new Double(vowelBoundary.getX()+vowelBoundary.getWidth()).intValue();
	}
	
	public int getVY2()
	{
		return new Double(vowelBoundary.getY()+vowelBoundary.getHeight()).intValue();
	}


//full character stuff
	public int getX1()
	{
		return x1;
	}
	
	public int getY1()
	{
		return y1;
	}
	
	public int getX2()
	{
		return x2;
	}
	
	public int getY2()
	{
		return y2;
	}




	//consonant vowel pair boundary class
	class CVBoundary  implements java.io.Serializable
	{
		private int key;
		private Rectangle boundary;
		
		public CVBoundary(int key, int x, int y)
		{
			this.key=key;
			boundary = new Rectangle(x,y,0,0);
		}
		
		public void add(int x, int y)
		{
			boundary.add(x,y);
		}
		
		public int getKey()
		{
			return key;
		}
		
		public Rectangle getBoundary()
		{
			return boundary;
		}
		
		
	}
	
	
	

	public static int getCCount()
	{
		return CCOUNT;
	}
	
	public static int getVCount()
	{
		return VCOUNT;
	}
	
	
	
	
	//resolve the consonants and the vowels
	//from NN
	public void resolve(GrayImage gimage, OCRNeuralNetwork ocrNN)
	{
		if (consonantBoundary != null)
			resolveConsonants(gimage, ocrNN);
		
		if (vowelBoundary!=null)
		{
			resolveVowels(gimage, ocrNN);
		}
	}
	
	private void resolveConsonants(GrayImage gimage, OCRNeuralNetwork ocrNN)
	{
		ROI roi = new ROI(
					getCX1(), 
					getCY1(), 
					getCX2(), 
					getCY2());
					
		GrayImage subGImage = (GrayImage)gimage.copy(roi);
		Normalizer norm = new Normalizer(subGImage, Normalizer.CON);
		double[] inputdata = norm.getCMatrix();
		
		consonant=ocrNN.getConsonant(inputdata);
		
		
		//just out put for the sake of displaying
		/*boolean[][] grid = norm.getData();
		System.out.println("\n\n consonant:" + consonant);
		for (int y=0;y<grid.length; y++)
		{
			System.out.print("\n");
			for(int x=0; x<grid[0].length; x++)
			{
				if(grid[y][x]==true)
					System.out.print("X");
				else
					System.out.print(".");
			}
		}*/
	}
	
	private void resolveVowels(GrayImage gimage, OCRNeuralNetwork ocrNN)
	{
		ROI roi = new ROI(
					getVX1(), 
					getVY1(), 
					getVX2(), 
					getVY2());
					
		GrayImage subGImage = (GrayImage)gimage.copy(roi);
		Normalizer norm = new Normalizer(subGImage, Normalizer.VOW);
		double[] inputdata = norm.getVMatrix();
		
		vowel=ocrNN.getVowel(inputdata);
		
		
		//just out put for the sake of displaying
		/*boolean[][] grid = norm.getData();
		System.out.println("\n" + vowel);
		for (int y=0;y<grid.length; y++)
		{
			System.out.print("\n");
			for(int x=0; x<grid[0].length; x++)
			{
				if(grid[y][x]==true)
					System.out.print("X");
				else
					System.out.print(".");
			}
		}*/
		
		//evaluate 
		//aabba -> iibii
		//aba -> ibi
		if (getVY1()>getCY1())
		{
			if (vowel.equals("w"))
				vowel="i";		
			else if (vowel.equals("W"))
				vowel="I";
		}
		
	}

}
 