package epublisher;


import org.joone.io.*;
import org.joone.net.*;
import org.joone.util.*;
import org.joone.log.*;
import org.joone.script.*;
import org.joone.engine.*;

import java.util.*;


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

public class OCRNeuralNet
{
	private NeuralNet nn;
	
	private double[][] inputArray;
	
	private Vector characterSet;
	private GrayImage gimage;
	
	public OCRNeuralNet(Vector characterSet, GrayImage gimage)
	{
		this.gimage = gimage;
		this.characterSet=characterSet;
		String netPath = "nn\\OCRNET2.snet";
		NeuralNetLoader loader = new NeuralNetLoader(netPath);
		nn = loader.getNeuralNet();
		
		try
		{
		createArray(characterSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	private void createArray(Vector characterSet)
	{
		//trasfer character data to the array
		inputArray = new double[CharacterObject.getCCount()][2500];
		
		int cindex=-1;

		try
		{
           ROI roi;
           
			for(int i=0; i<characterSet.size(); i++)
			{
				CharacterObject co = (CharacterObject)characterSet.elementAt(i);
								
				if (co.getCVCount() <=2)
				{
					//System.out.println(co.getCVCount());
					 roi = new ROI(
									co.getCX1(), 
									co.getCY1(), 
									co.getCX2(), 
									co.getCY2());
					imageToText(gimage.copy(roi), ++cindex);
					
					//System.out.println(cindex);
					
					/*if (co.getVowelBoundary()!=null)
					{
						roi = new ROI(
										co.getVX1(), 
										co.getVY1(), 
										co.getVX2(), 
										co.getVY2());
						imageToText(gimage.copy(roi), pwv);	
						pwv.print(++tffc+"\n");					 
					}*/
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		//print out the outpt
		/*for (int y=0; y < inputArray.length; y++)
		{
			System.out.print("\n  ");
			for (int x=0; x<inputArray[0].length; x++)
			{
				System.out.print(inputArray[y][x]+"|");
			}
		}*/
		
		System.out.println(characterSet.size());
		System.out.println(CharacterObject.getCCount());
	}
	
	
	private void imageToText(jigl.image.Image gsubimage, int rowIndex)
	{
		byte[][] outdata = new BinaryImage((GrayImage)gsubimage).getData();
		
		int index=0;
	//	System.out.println(rowIndex);
		//image will be normalized to 50x50
		for (int y=0; y<50; y++)
		{
			for (int x=0; x<50; x++)
			{
				if (y>=outdata.length || x>=outdata[0].length)
				{
		//System.out.println(x+","+y+" "+index+" " + inputArray.length + " " + inputArray[0].length);
					inputArray[rowIndex][index++]=0;
				}
				else
				{
	//	System.out.println("-"+x+","+y+" "+index+" " + inputArray.length + " " + inputArray[0].length);
					inputArray[rowIndex][index++]=outdata[y][x];
				}
			}
		}
	}
	
	
	
	public void go()
	{
		//Layer input = nn.getInputLayer();
		Layer input = nn.getLayer("Layer 2");
		//input.removeAllInputs();
		MemoryInputSynapse memInp = new MemoryInputSynapse();
		memInp.setFirstRow(1);
		memInp.setAdvancedColumnSelector("1,2500");
		input.addInputSynapse(memInp);
		memInp.setInputArray(inputArray);
		
		
		//Layer output = nn.getOutputLayer();
		Layer output = nn.getLayer("Layer 3");
		//output.removeAllOutputs();
		MemoryOutputSynapse memOut = new MemoryOutputSynapse();
		output.addOutputSynapse(memOut);
		
		nn.getMonitor().setTotCicles(1);
		nn.getMonitor().setTrainingPatterns(CharacterObject.getCCount());
		nn.getMonitor().setLearning(false);
		nn.start();
		nn.getMonitor().Go();
		
		//System.out.println(memOut.getAllPatterns().size());
		
	for (int i=0; i < CharacterObject.getCCount(); ++i) 
	{
		// Read the next pattern and print out it
		double[] pattern = memOut.getNextPattern();
		System.out.print("\nOutput Pattern #"+(i+1)+" = ");
		for (int j=0; j<pattern.length; j++)
		{
			System.out.print(pattern[j] + " ");
		}
	}
	
	nn.stop();
	System.out.println("Finished"); 
			

	}
}