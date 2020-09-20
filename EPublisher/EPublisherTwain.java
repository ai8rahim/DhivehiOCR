

package epublisher.twain;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import SK.gnome.twain.TwainException;
import SK.gnome.twain.TwainManager;
import SK.gnome.twain.TwainSource;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import epublisher.EPublisherUI;

public class EPublisherTwain
{ 
	private EPublisherUI parent;
	
	private Image image;
	
	private String savePath;
  
	public EPublisherTwain(epublisher.EPublisherUI parent, String savePath)
	{
		this.parent=parent;
		this.savePath = savePath;
		
		scan();
		save();
		parent.scanComplete();
	}
	
	
	private void scan() 
	{ 
		try
		{
			TwainSource source=TwainManager.getDefaultSource();
			source.setPixelType(TwainSource.TWPT_BW);
			System.err.println("getPixelType="+source.getPixelType());
			System.err.println("getBitDepth="+source.getBitDepth());
			image=Toolkit.getDefaultToolkit().createImage(source);
			MediaTracker tracker=new MediaTracker(new Frame());
			tracker.addImage(image, 0);
			try
			{ tracker.waitForAll();
			}
			catch (InterruptedException e)
			{ e.printStackTrace();
			}
			tracker.removeImage(image);
			System.err.println("getPixelType2="+source.getPixelType());
			System.err.println("getBitDepth2="+source.getBitDepth());
			TwainManager.close();
		}
		catch (TwainException e)
		{ e.printStackTrace();
		}
	
	}
	
	/** 
	This method saves an image data to the jpeg file format. It uses 
	com.sun.image.codec.jpeg package.
	*/
	private void save() 
	{ 
		try
		{ File f=new File(savePath);
			BufferedImage bimg=new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			bimg.createGraphics().drawImage(image, 0, 0, null);
			FileOutputStream out=new FileOutputStream(f);
			JPEGImageEncoder encoder=JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param=encoder.getDefaultJPEGEncodeParam(bimg);
			param.setQuality(1.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bimg);
			out.close();
		}
		catch (IOException e)
		{ e.printStackTrace();
		}
	}	
			
	
	
	
} 