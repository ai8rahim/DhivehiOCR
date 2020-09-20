package epublisher;

import jigl.image.*;

class LinebreaksHistogram
{
	byte[][] data; //byte array to hold the binary image data
	
	int[] hIntensity;	//int array that contains the intensities of
						//the foreground pixels
						//array index corresponds to the y coordinate of the
						//image(byte array)
						//this array will help determine the line breaks
						//starts with zero (meaning) no forground pixel
						//in that particular line
	
	int[] lineBreaks; // the y coordinates of the line breaks
	
	int max=0;	//the maximum intensity (sometimes needed when scaling the output
					//when drawing the histogram
	
	public LinebreaksHistogram(GrayImage gimage)
	{
		data = new BinaryImage(gimage).getData();
		hIntensity = new int[data.length];
		
		int prevIntensity = 0;
		int lineBreakCount = 0;
		
		//raster image to get the forground pixel intensities
		//in every line
		for (int y=0; y < data.length; y++)
		{
			hIntensity[y] =0;
			for (int x=0; x < data[0].length; x++)
			{
				if (data[y][x]==1)
					hIntensity[y]++;
			}
			if (hIntensity[y]>max) max = hIntensity[y];
			
			//get the line break counts for the array size
			if (prevIntensity == 0 && hIntensity[y] != prevIntensity) lineBreakCount++;
			else if (hIntensity[y] == 0 && hIntensity[y] != prevIntensity) lineBreakCount++;
			prevIntensity = hIntensity[y];
		}
		
		//finally set the line breaks array
		lineBreaks = new int[lineBreakCount];
		prevIntensity=0;
		lineBreakCount=0;

		for (int i=0; i<hIntensity.length;i++)
		{	
			if (prevIntensity == 0 && hIntensity[i] != prevIntensity)
			{
				lineBreaks[lineBreakCount]=i;
				lineBreakCount++;
			}
			else if (hIntensity[i] == 0 && hIntensity[i] != prevIntensity)
			{
				lineBreaks[lineBreakCount]=i;
				lineBreakCount++;
			}
			prevIntensity = hIntensity[i];
		}
	}
	
	public int getMax()
	{
		return max;
	}
	
	public int[] getLinebreaks()
	{
		return lineBreaks;
	}	
	
	public int[] getData()
	{
		return hIntensity;
	}
}
	