package epublisher;

import jigl.image.*;
import java.util.*;

class CharacterbreaksHistogram
{
	LinebreaksHistogram linebreaksHistogram;
	
	byte[][] data; //byte array to hold the binary image data
	
	int[][] vIntensity;	//int array that contains the intensities of
						//the foreground pixels
						//array index corresponds to the y coordinate of the
						//image(byte array)
						//this array will help determine the line breaks
						//starts with zero (meaning) no forground pixel
						//in that particular line
	
	int[] linebreaks; // the y coordinates of the line breaks

	int[][] characterBreaks; // 

	int max[];	//the maximum intensity (sometimes needed when scaling the output
					//when drawing the histogram
	
	public CharacterbreaksHistogram(GrayImage gimage, LinebreaksHistogram linebreaksHistogram)
	{
		this.linebreaksHistogram = linebreaksHistogram;
		data = new BinaryImage(gimage).getData();
		linebreaks = linebreaksHistogram.getLinebreaks();
		vIntensity = new int[linebreaks.length/2][data[0].length];
		characterBreaks = new int[linebreaks.length/2][];
		max = new int[linebreaks.length/2];
		
		

		//raster image to get the forground pixel intensities
		//in every colum in every line linebreaks[i] and linebreaks[i+1] are the
		//boundaries of the line.. top and bottom
		for (int i=0; i < linebreaks.length; i+=2)
		{
			
			int prevIntensity = 0;
			int characterBreakCount = 0;
			
			//processing all the intensities
			for (int x=0; x < data[0].length; x++)
			{
			
				vIntensity[i/2][x] =0;
				for (int y=linebreaks[i]; y < linebreaks[i+1]; y++)
				{
					if (data[y][x]==1)
						vIntensity[i/2][x]++;
				}
				if (vIntensity[i/2][x]>max[i/2]) max[i/2] = vIntensity[i/2][x];
				
				//get the character break counts for each column in each line
				if (prevIntensity == 0 && vIntensity[i/2][x] != prevIntensity) characterBreakCount++;
				else if (vIntensity[i/2][x] == 0 && vIntensity[i/2][x] != prevIntensity) characterBreakCount++;
				prevIntensity = vIntensity[i/2][x];
			}
			
			
			//finally set the character breaks array
			characterBreaks[i/2] = new int[characterBreakCount];
			prevIntensity=0;
			characterBreakCount=0;			
	
			for (int j=0; j<vIntensity[i/2].length;j++)
			{
				if (prevIntensity == 0 && vIntensity[i/2][j] != prevIntensity)
				{
					characterBreaks[i/2][characterBreakCount]=j;
					//System.out.print(characterBreaks[i/2][characterBreakCount]);
					characterBreakCount++;
				}
				else if (vIntensity[i/2][j] == 0 && vIntensity[i/2][j] != prevIntensity)
				{
					characterBreaks[i/2][characterBreakCount]=j;
				//	System.out.print(characterBreaks[i/2][characterBreakCount]);
					characterBreakCount++;
				}
				prevIntensity = vIntensity[i/2][j];
			}
		//	System.out.print("\n");
		}
	
			/*System.out.println("displaying");
			for (int y = 0; y<characterBreaks.length; y++)
			{
				for (int x=0; x<characterBreaks[y].length;x++)
				{
					System.out.print(characterBreaks[y][x]);
				}
				System.out.print("\n");
			}*/
	
	
	}
	
	public CharacterbreaksHistogram(GrayImage gimage, LinebreaksHistogram linebreaksHistogram, Vector wrongCharacters)
	{
		this.linebreaksHistogram = linebreaksHistogram;
		linebreaks = linebreaksHistogram.getLinebreaks();
		data = new BinaryImage(gimage).getData();
		vIntensity = new int[linebreaks.length/2][data[0].length];
		characterBreaks = new int[linebreaks.length/2][];
		max = new int[linebreaks.length/2];
		
	}
	
	public int[] getMax()
	{
		return max;
	}
	
	public int[][] getCharacterbreaks()
	{
		return characterBreaks;
	}	
	
	public int[][] getData()
	{
		return vIntensity;
	}
	
	public LinebreaksHistogram getLinebreaksHistogram()
	{
		return linebreaksHistogram;
	}
}
