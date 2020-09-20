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

class Thinning
{
	private GrayImage gimage;
	
	public Thinning(GrayImage gimage)
	{
		this.gimage=gimage;
	}
	//***A new hilditch
	public GrayImage apply()
	{
		byte[][] data=new BinaryImage(gimage).getData();
		
	/*	for (int y=0; y<data.length;y++)
		{
			System.out.print("\n");
			for (int x=0; x<data[0].length; x++)
			{
				System.out.print(data[y][x]);
			}
		}*/
		
		
		/*	THE STRUCTURING ELEMENTS
		 *a	0 0 0	b	X 0 0
		 *	X 1 X		1 1 0
		 *	1 1 1		X 1 X
		 *
		 *c	1 X 0	d	X 1 X
		 *	1 1 0		1 1 0
		 *	1 X 0		X 0 0
		 *	
		 *e	1 1 1	f	X 1 X
		 *	X 1 X		0 1 1
		 *	0 0 0		0 0 X
		 *		
		 *g	0 X 1	h	0 0 X
		 * 	0 1 1		0 1 1
		 *	0 X 1		X 1 X
		 */
		
		boolean changed=true;
		while(changed)
		{
			System.out.print(changed);
			changed=false;
			for (int y=0; y<data.length;y++)
			{
				for (int x=0; x<data[0].length; x++)
				//for (int x=data[0].length-1; x>=0; x--)
				{
					if (data[y][x]==1)
					{
						if (neighbourCount(data, x, y)>=2)
						{
							if (thinTemplate(data, x, y))
							{
								gimage.set(x,y,0);
								data[y][x]=0;
								changed=true;
							}
						}
					}
				}
			}			
		}
		
		return gimage;
	}
	
	private boolean thinTemplate(byte[][] data, int x, int y)
	{
		//a
		if(
			data[y-1][x-1]==0 &&
			data[y-1][x]==	0 &&
			data[y-1][x+1]==0 &&
			data[y+1][x-1]==1 &&
			data[y+1][x]==	1 &&
			data[y+1][x+1]==1 
			)
			return true;
		//b
		else if(
			data[y-1][x]==	0 &&
			data[y-1][x+1]==0 &&
			data[y][x-1]==	1 &&
			data[y][x+1]==	0 &&
			data[y+1][x]==	1 
			)
			return true;
		//c
		else if(
			data[y-1][x-1]==1 &&
			data[y-1][x+1]==0 &&
			data[y][x-1]==	1 &&
			data[y][x+1]==	0 &&
			data[y+1][x-1]==1 &&
			data[y+1][x+1]==0 
			)
			return true;
		//d
		else if(
			data[y-1][x]==	1 &&
			data[y][x-1]==	1 &&
			data[y][x+1]==	0 &&
			data[y+1][x]==	0 &&
			data[y+1][x+1]==0 
			)
			return true;
		//e
		else if(
			data[y-1][x-1]==1 &&
			data[y-1][x]==	1 &&
			data[y-1][x+1]==1 &&
			data[y+1][x-1]==0 &&
			data[y+1][x]==	0 &&
			data[y+1][x+1]==0 
			)
			return true;
		//f
		else if(
			data[y-1][x]==	1 &&
			data[y][x-1]==	0 &&
			data[y][x+1]==	1 &&
			data[y+1][x-1]==0 &&
			data[y+1][x]==	0 
			)
			return true;
		//g
		else if(
			data[y-1][x-1]==0 &&
			data[y-1][x+1]==1 &&
			data[y][x-1]==	0 &&
			data[y][x+1]==	1 &&
			data[y+1][x-1]==0 &&
			data[y+1][x+1]==1 
			)
			return true;
		//h
		else if(
			data[y-1][x-1]==0 &&
			data[y-1][x]==	0 &&
			data[y][x-1]==	0 &&
			data[y][x+1]==	1 &&
			data[y+1][x]==	1 
			)
			return true;
		else
			return false;
	}
	
	private int neighbourCount(byte[][] data, int x, int y)
	{
		int count=0;
		
	
		try
		{
			if (data[y-1][x-1]==1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y-1][x]==	1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y-1][x+1]==1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y][x-1]==	1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y][x+1]==	1) count++;	
		}
		catch(Exception e1){}		
		try
		{
			if (data[y+1][x-1]==1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y+1][x]==	1) count++;
		}
		catch(Exception e1){}
		try
		{
			if (data[y+1][x+1]==1) count++;
		}
		catch(Exception e1){}
		
		return count;
	}
}
	