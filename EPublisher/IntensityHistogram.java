package epublisher;

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

class IntensityHistogram extends JPanel
{
	BinaryImage bimage;
	
	byte[][] data;
	int[] hIntensity;
	
	int[] lineBreaks;
	
	int[][] charBreaks;
	
	
	JTextArea ta = new JTextArea(5,20);
	int originX = 10, originY = 10;
	
	int max=0;
	
	int histogramOp=0;
		final int LINE = 1;
		final int CHAR = 2;
	
	
	public IntensityHistogram(GrayImage gimage)
	{
		histogramOp = LINE;
		
		bimage = new BinaryImage(gimage);
		
		data = bimage.getData();
		hIntensity = new int[data.length];
		
		int prevIntensity = 0;
		int lineBreakCount = 0;
		
		//raster image
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
		
		lineBreaks = new int[lineBreakCount];
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("Histogram");
		frame.setSize(800, 400);
		JScrollPane scrollPane = new JScrollPane(this);
		this.setPreferredSize(new Dimension(max, data.length));
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(new JScrollPane(ta), BorderLayout.EAST);

		frame.setVisible(true);
	}
	
	public IntensityHistogram(GrayImage gimage, int[] lineBreaks)
	{
		histogramOp = CHAR;
		bimage = new BinaryImage(gimage);
		this.lineBreaks=lineBreaks;
		
		
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		

		
			
		if (histogramOp == CHAR)
		{
			
		}
		else if (histogramOp == LINE)
		{
			ta.setText("");
			
			int prevIntensity=0;
			int lineBreakCount=0;
	
			for (int i=0; i<hIntensity.length;i++)
			{
				g.drawLine(originX, originY+i, originX+hIntensity[i], originY+i);
				ta.append(i + " - " + hIntensity[i] + "\n");
				
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
			
			ta.append("======\n");
			
			g.setColor(Color.red);
			for (int i=0; i<lineBreaks.length; i++)
			{
				ta.append(i+" - "+lineBreaks[i]+"\n");
				g.drawLine(originX, originY+lineBreaks[i], max, originY+lineBreaks[i]);
			}
		}
	}
	
	public int[] getLineBreaks()
	{
		return lineBreaks;
	}
	
}


