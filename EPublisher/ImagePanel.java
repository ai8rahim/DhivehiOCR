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
import jigl.image.warp.*;

class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener
{
	//for gui
		//private JButton btReset = new JButton("Reset");
		//private JButton btCharBreaks = new JButton("CharBr");
		private JCustomImageCanvas canvas;
		private JCustomImageCanvas o_canvas;
		private JTabbedPane tabbedPane;
	
		private String filename;

	//from jigl
		private jigl.image.Image iimage;
		private GrayImage gimage;
		private GrayImage original_gimage;

	//for ip		
		private CharacterbreaksHistogram cbrHistogram;
		private LinebreaksHistogram lbrHistogram;
		private Vector characterSet;	//contains all the character objects
		
		public static int GLOBALTHRESHOLD = 128;
	
	//for saving the images
		private static	int fc=0;
		private static	int ffc=0;
		private static	int tfc=0;
		private static	int tffc=0;
		
		
		private static	int ofc=0;
		private static	int offc=0;
	
	
	//for the sake of generatig the test images REMOVE LATER
		private static int IMG_PREFIX=0;
		private static int OIMG_PREFIX=0;
		private int my_prefix;
		
	//for the purpose of thread synch
		private boolean isImageProcessing=true;
		private boolean ocrDone = false;
		
		private ImageProcess imageProcess;
		private OCRNeuralNetwork ocrNN;
	
	//from file
	public ImagePanel(String filename, Progresser progressBar)
	{
		//REMOVE LATER---
		IMG_PREFIX++;
		my_prefix=IMG_PREFIX;
		//----
		
		this.filename=filename;
        setLayout(new BorderLayout());
        
		try{	
           ImageInputStreamJAI iistream = new ImageInputStreamJAI(filename);
           iimage = iistream.read();
           
           int prog=0;
           progressBar.setProgress(++prog,5);
           
				gimage=new ImageConverter().toGray(iimage);
		progressBar.setProgress(++prog);
				Negative negative = new Negative();
				negative.apply(gimage);
				//make a backup of the original image for restoring purpose				
				original_gimage= (GrayImage)gimage.copy();
		progressBar.setProgress(++prog);
				JScrollPane canvasScroller = new JScrollPane();
				canvas=new JCustomImageCanvas(gimage, canvasScroller);
				canvasScroller.setViewportView(canvas);
				
				
				JScrollPane o_canvasScroller = new JScrollPane();
				o_canvas=new JCustomImageCanvas(original_gimage, o_canvasScroller);
				o_canvasScroller.setViewportView(o_canvas);
				 
		progressBar.setProgress(++prog);
		
				//set the tabbed pane
				tabbedPane = new JTabbedPane();
				tabbedPane.addTab("Preview", canvasScroller);
				tabbedPane.addTab("Original", o_canvasScroller);
		
				add(tabbedPane, BorderLayout.CENTER);
		progressBar.setProgress(++prog);
				
				
          } catch (Exception e) {
               System.out.println("Exception caught" + e);
               }

  }


//from saved project file
	public ImagePanel(PageObject pgo)
	{
		//REMOVE LATER---
		IMG_PREFIX++;
		my_prefix=IMG_PREFIX;
		//----
		
        setLayout(new BorderLayout());
        
		try{	
           		gimage=new GrayImage(pgo.getGImageData());				
				//make a backup of the original image for restoring purpose				
				original_gimage= (GrayImage)gimage.copy();
				JScrollPane canvasScroller = new JScrollPane();
				canvas=new JCustomImageCanvas(gimage, canvasScroller);
				canvasScroller.setViewportView(canvas);
				add(canvasScroller, BorderLayout.CENTER);
				
				
          } catch (Exception e) {
               System.out.println("Exception caught" + e);
               }

  }
  


  
  
	
	
	public void enableROISelection(boolean b)
	{
		canvas.showActiveBox(b);
		canvas.showSelectionBox(b);
		
		if (b == true)
		{
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);	
		}
		else
		{
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);	
		}
		
	}
	
	
	public void setGImage(GrayImage gimage)
	{
		this.gimage= gimage;
		try
		{
			canvas.setImage (gimage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public GrayImage getGImage()
	{
		return gimage;
	}
	
	public void rotateImage(float angle)
	{
		try
		{
			RotateTransform rt = new RotateTransform(angle);
			Mapper mapper = new Mapper(rt, Mapper.NEIGHBOR);
			gimage = (GrayImage)mapper.apply(gimage);
			canvas.setImage (gimage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public JCustomImageCanvas getCanvas()
	{
		return canvas;
	}
	
	public void setSelection(ROI roi) 
	{
		try
		{
			gimage = (GrayImage)gimage.copy(roi);
			canvas.setImage (gimage);
			
			original_gimage = (GrayImage)original_gimage.copy(roi);
			o_canvas.setImage (original_gimage);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void finaliseWizard()
	{
		original_gimage = (GrayImage)gimage.copy();
	}
	
	
	
	
	
	public void ocr(IPProgresser ipProgresser, DocumentPanel documentPanel)
	{		
		//processImage(ipProgresser.getIPBar());
		//resolve(characterSet, ipProgresser.getNNBar());
		
		imageProcess = new ImageProcess(ipProgresser, documentPanel);
	//	imageProcess.execute();
		//saveImagesAsText(characterSet);
		//saveImages(characterSet);
	}
	
	public boolean isProcessing()
	{
		return isImageProcessing;
	}
	
	public boolean isOCRDone()
	{
		return ocrDone;
	}
	
	public Vector getCharacterSet()
	{
		
		return characterSet;
	}
	
	private void showOriginalImage()
	{
		
	}

	
	/*
	 *convert image to text (for joone)
	 */
	private void imageToText(jigl.image.Image gsubimage, PrintWriter pw)
	{
		byte[][] outdata = new BinaryImage((GrayImage)gsubimage).getData();
		
		//image will be normalized to 50x50
		for (int y=0; y<50; y++)
		{
			//pw.print("\n");
			for (int x=0; x<50; x++)
			{
				if (y>=outdata.length || x>=outdata[0].length)
				{
					//System.out.print("0");
					pw.print("0;");
				}
				else
				{
					//System.out.print(outdata[y][x]);
					pw.print(outdata[y][x]+";");
				}
			}
		}
		//System.out.println("");
		//pw.print("\n");
	}
	
	/*
	 *save the image as text (for joone)
	 */
	private void saveImagesAsText(Vector characterSet)
	{
		String CPATH = "trainingset\\consonant.txt";
		String VPATH = "trainingset\\vowel.txt";


		try
		{
		PrintWriter pwc = new PrintWriter(new BufferedWriter(new FileWriter(CPATH)));
		PrintWriter pwv = new PrintWriter(new BufferedWriter(new FileWriter(VPATH)));
           ROI roi;
           
			for(int i=0; i<characterSet.size(); i++)
			{
				CharacterObject co = (CharacterObject)characterSet.elementAt(i);
				roi = new ROI(
								co.getX1(), 
								co.getY1(), 
								co.getX2(), 
								co.getY2());
								
				if (co.getCVCount() <=2)
				{
					//System.out.println(co.getCVCount());
					 roi = new ROI(
									co.getCX1(), 
									co.getCY1(), 
									co.getCX2(), 
									co.getCY2());
					imageToText(gimage.copy(roi), pwc);
					pwc.print(++tfc+"\n");
					
					if (co.getVowelBoundary()!=null)
					{
						roi = new ROI(
										co.getVX1(), 
										co.getVY1(), 
										co.getVX2(), 
										co.getVY2());
						imageToText(gimage.copy(roi), pwv);	
						pwv.print(++tffc+"\n");					 
					}
				}
			}
			pwc.flush();
			pwc.close();
			pwv.flush();
			pwv.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 *save characters seperately as images the originals)
	 */
	private void saveImages(Vector characterSet)
	{
		String CPATH = "trainingset\\consonants\\" + IMG_PREFIX + "_";
		String VPATH = "trainingset\\vowels\\" + IMG_PREFIX + "_";
		String EXT = ".jpg";
		try
		{
           ImageOutputStreamJAI iostream;
           ROI roi;
           
			for(int i=0; i<characterSet.size(); i++)
			{
				CharacterObject co = (CharacterObject)characterSet.elementAt(i);
				roi = new ROI(
								co.getX1(), 
								co.getY1(), 
								co.getX2(), 
								co.getY2());
								
				if (co.getCVCount() <=2)
				{
					 roi = new ROI(
									co.getCX1(), 
									co.getCY1(), 
									co.getCX2(), 
									co.getCY2());
					iostream = new ImageOutputStreamJAI(CPATH+(++fc)+EXT);
					iostream.writeJPEG(gimage.copy(roi));
					
					if (co.getVowelBoundary()!=null)
					{
						roi = new ROI(
										co.getVX1(), 
										co.getVY1(), 
										co.getVX2(), 
										co.getVY2());
						iostream = new ImageOutputStreamJAI(VPATH+(++ffc)+EXT);
						iostream.writeJPEG(gimage.copy(roi));						 
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private void saveImagesO(Vector characterSet)
	{
		String CPATH = "trainingset\\consonantsO\\" + IMG_PREFIX + "_";
		String VPATH = "trainingset\\vowelsO\\" + IMG_PREFIX + "_";
		String EXT = ".jpg";
		try
		{
           ImageOutputStreamJAI iostream;
           ROI roi;
           
			for(int i=0; i<characterSet.size(); i++)
			{
				CharacterObject co = (CharacterObject)characterSet.elementAt(i);
				roi = new ROI(
								co.getX1(), 
								co.getY1(), 
								co.getX2(), 
								co.getY2());
								
				if (co.getCVCount() <=2)
				{
					 roi = new ROI(
									co.getCX1(), 
									co.getCY1(), 
									co.getCX2(), 
									co.getCY2());
					iostream = new ImageOutputStreamJAI(CPATH+(++ofc)+EXT);
					iostream.writeJPEG(gimage.copy(roi));
					
					if (co.getVowelBoundary()!=null)
					{
						roi = new ROI(
										co.getVX1(), 
										co.getVY1(), 
										co.getVX2(), 
										co.getVY2());
						iostream = new ImageOutputStreamJAI(VPATH+(++offc)+EXT);
						iostream.writeJPEG(gimage.copy(roi));						 
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	public short[][] getGImageData()
	{
		return gimage.getData();
	}

	//UI centric
	public void actionPerformed(ActionEvent e) 
	{/*
		if (e.getSource()==btCharBreaks)
		{
			//ocr();
		}
		else if (e.getSource()==btReset)
		{
			threshold(ImagePanel.GLOBALTHRESHOLD);
			gimage = new Thinning(gimage).apply();
			try
			{
				canvas.setImage(gimage);
			}
			catch(Exception ex){}
			
		//reset_image();
		//System.out.println("running ocr nn");
		//OCRNeuralNet ocrNet = new OCRNeuralNet(characterSet, gimage);
		//ocrNet.go();
		}*/
	}

	public void mousePressed(MouseEvent e)
	{
	      if (e.getComponent() instanceof jigl.gui.JImageCanvas)
	      {
	              jigl.gui.JImageCanvas canvas = (jigl.gui.JImageCanvas)e.getComponent();
	              canvas.clearSelectionBox();
	              canvas.setSelectionBoxAnchor(e.getX(),e.getY());
	      }
	}
	
	public void mouseDragged(MouseEvent e)
	{
	      if (e.getComponent() instanceof jigl.gui.JImageCanvas)
	      {
	              jigl.gui.JImageCanvas canvas = (jigl.gui.JImageCanvas)e.getComponent();
	              canvas.setSelectionBoxExtent(e.getX(),e.getY());
	      }
	}


	public void mouseReleased(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
class ImageProcess implements Runnable
{
	IPProgresser ipProgresser;
	DocumentPanel documentPanel;
	Thread thread;
	
	public ImageProcess(IPProgresser ipProgresser, DocumentPanel documentPanel)
	{
		this.ipProgresser=ipProgresser;
		this.documentPanel = documentPanel;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{


		processImage();
		
		
		//saveImagesO(characterSet);
		
		gimage=new Thinning(gimage).apply();
		threshold(ImagePanel.GLOBALTHRESHOLD);
		
		resolve(characterSet);
		isImageProcessing=false;
		documentPanel.setCharacterSet(characterSet, ipProgresser);
		
		ocrDone = true;
		showOriginalImage();
		
		//saveImages(characterSet);
		
	}
	
	/*
	 *fix borders
	 */
	public void fixBorders()
	{
		//fix top
		for (int i =0; i<gimage.X(); i++)
		{
			gimage.set(i, 0, 0);
			gimage.set(i, 1, 0);
			gimage.set(i, 2, 0);
		}
		
		for (int i =0; i<gimage.X(); i++)
		{
			gimage.set(i, gimage.Y()-1, 0);
			gimage.set(i, gimage.Y()-2, 0);
			gimage.set(i, gimage.Y()-3, 0);
		}
		
		for (int i=0; i<gimage.Y(); i++)
		{
			gimage.set(0, i, 0);
			gimage.set(1, i, 0);
			gimage.set(2, i, 0);
		}
		
		for (int i=0; i<gimage.Y(); i++)
		{
			gimage.set(gimage.X()-1, i, 0);
			gimage.set(gimage.X()-2, i, 0);
			gimage.set(gimage.X()-3, i, 0);
		}
		
		/*threshold(ImagePanel.GLOBALTHRESHOLD);
		byte[][] data = new BinaryImage(gimage).getData();
		for (int y=0; y<data.length; y++)
		{
			System.out.print("\n");
			for (int x=0; x<data[0].length; x++)
			{
				System.out.print(data[y][x]);
			}
		}*/
		//fix top
		for (int i =0; i<original_gimage.X(); i++)
		{
			original_gimage.set(i, 0, 0);
			original_gimage.set(i, 1, 0);
			original_gimage.set(i, 2, 0);
		}
		
		for (int i =0; i<original_gimage.X(); i++)
		{
			original_gimage.set(i, original_gimage.Y()-1, 0);
			original_gimage.set(i, original_gimage.Y()-2, 0);
			original_gimage.set(i, original_gimage.Y()-3, 0);
		}
		
		for (int i=0; i<original_gimage.Y(); i++)
		{
			original_gimage.set(0, i, 0);
			original_gimage.set(1, i, 0);
			original_gimage.set(2, i, 0);
		}
		
		for (int i=0; i<original_gimage.Y(); i++)
		{
			original_gimage.set(original_gimage.X()-1, i, 0);
			original_gimage.set(original_gimage.X()-2, i, 0);
			original_gimage.set(original_gimage.X()-3, i, 0);
		}
		
	}
	
	/*
	 *the actual image processing
	 */
	private void processImage()
	{
		JProgressBar progress  = ipProgresser.getIPBar();
		
		progress.setIndeterminate(false);	
		int progVal=0;
		progress.setMaximum(12);
		ipProgresser.appendMessage("->Image Processing Started...");
		
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n\tfixing borders");
		//System.out.println("**fix borders");		
			fixBorders();
			
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n\tfinding line-breaks");
		//System.out.println("**line breaks");		
				m_dilate();
				m_dilate();
				m_dilate();
				threshold(ImagePanel.GLOBALTHRESHOLD);
				lbrHistogram = new LinebreaksHistogram(gimage);
	 
		
		
		progress.setValue(++progVal);
		//System.out.println("**resetting image");
				reset_image();
		
		
		progress.setValue(++progVal);
		//System.out.println("**thresholding");
				threshold(ImagePanel.GLOBALTHRESHOLD);
		
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n\tfinding character-breaks");
		//System.out.println("**characterbreaks");		
				//CharacterbreaksHistogram characterbreaksHistogram = new CharacterbreaksHistogram(gimage, lbrHistogram);	
				cbrHistogram = new CharacterbreaksHistogram(gimage, lbrHistogram);	
			
				int[] lbr  = cbrHistogram.getLinebreaksHistogram().getLinebreaks();
				int[][] cbr = cbrHistogram.getCharacterbreaks();
				
				characterSet = new Vector();
				
				for (int l=0; l<cbr.length; l++)
				{
					for (int c=0; c<cbr[l].length; c+=2)
					{
						characterSet.add(new CharacterObject(cbr[l][c], lbr[l*2], cbr[l][c+1], lbr[l*2+1]));	
					}
				}
															

		
		progress.setValue(++progVal);
		//System.out.println("**resetting");
				reset_image();
		
		
		progress.setValue(++progVal);
		//System.out.println("**thresholding");
				threshold(ImagePanel.GLOBALTHRESHOLD);
			
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n\tfilling gaps");
		//System.out.println("**fill gaps");
			//fillGaps();
			m_close();
				threshold(ImagePanel.GLOBALTHRESHOLD);
			
			
		//seperate the vowel and the consonent
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n\tseperating vowels and consonants");
		//System.out.println("**sperate vowel and consonant");		
				double goodOnes=0;
				double totOnes=0;
				
				for (int i =0 ; i<characterSet.size(); i++)
				{
					//System.out.println(i);
					CharacterObject character = (CharacterObject)characterSet.elementAt(i);
					character.seperateCharacterPair(gimage);
					
					//---------for reporting purpose tp display accuracy
						totOnes+=character.getCVCount();
						if (character.getCVCount() <=2)	//criteria for judging good characters
						{
							if (character.getVowelBoundary()!=null)
							{
								goodOnes++;
							}
							goodOnes++;
						}
					//--------------
				}

		//display on screen
		
		progress.setValue(++progVal);
		canvas.drawCharacterSet(characterSet);
		o_canvas.drawCharacterSet(characterSet);
		
		
		//display report of imageprocessing-----------
		
		progress.setValue(++progVal);
		ipProgresser.appendMessage("\n--Image Processing Complete");		
			ipProgresser.appendMessage("\n\tReport...");
			ipProgresser.appendMessage("\n\t"+goodOnes+" of "+totOnes+ " good");
			ipProgresser.appendMessage("\n\t"+(totOnes-goodOnes)+" of "+totOnes+ " bad");
			ipProgresser.appendMessage("\n\taccuracy: "+new Double((goodOnes/totOnes)*100)+" %");
			//System.out.println("**imageprocessing complete");
			//System.out.println("  "+goodOnes+" of "+totOnes+ " good");
			//System.out.println("  "+(totOnes-goodOnes)+" of "+totOnes+ " bad");
			//System.out.println("  accuracy: "+new Double((goodOnes/totOnes)*100)+" %");
						
		
		progress.setValue(++progVal);
		threshold(ImagePanel.GLOBALTHRESHOLD);
		//gimage=new Thinning(gimage).apply();

	}
	
	//resolve and display
	private void resolve(Vector characterSet)
	{
		JProgressBar progress = ipProgresser.getNNBar();
		
		progress.setIndeterminate(false);
		progress.setMaximum(characterSet.size()-1);
		int progVal=0;
		
		
		ocrNN = new OCRNeuralNetwork();
		
		for(int i=0; i<characterSet.size(); i++)
		{
			progress.setValue(i);
			CharacterObject co = (CharacterObject)characterSet.elementAt(i);
			co.resolve(gimage, ocrNN);
		}

	}
	

	//REMEMBER: this method can be avoided
	public void reset_image()
	{
		
			/*try{
	           ImageInputStreamJAI iistream = new ImageInputStreamJAI(filename);
	           iimage = iistream.read();


				gimage=new ImageConverter().toGray(iimage);
				Negative negative = new Negative();
				negative.apply(gimage);
				canvas.setImage(gimage);
				
				
				
			}
			catch(Exception exp)
			{
				System.out.println(exp);
			}*/
			gimage= (GrayImage)original_gimage.copy();
	}
	
	//aborted due to performance issues
	private boolean fixFalseSegmentation(Vector characterSet)
	{
	/*
	 //put these code for performing this operation
	 System.out.println("**fix false segmentations");		
	 //fix the false segmentations
	 PERFORMANCE ISSUE IS HERE...BUT WORKS
	 if (fixFalseSegmentation(characterSet))
		{
			//again try to identify the character objects
			threshold(ImagePanel.GLOBALTHRESHOLD);
					
			characterbreaksHistogram = new CharacterbreaksHistogram(gimage, lnb);
			
			characterSet = createCharacters(characterbreaksHistogram);
		}*/

		
		boolean isFalseSegmented=false;
		
		Vector wrongCharacters= new Vector();
		
		//identify the wrong segmentations
		for (int i=0; i<characterSet.size(); i++)
		{
			CharacterObject character = (CharacterObject)characterSet.elementAt(i);
			double ratio = new Integer(character.getHeight()).doubleValue()/new Integer(character.getWidth()).doubleValue();
			if (ratio<1.5)
			{
				//System.out.println(i + "\t***" + ratio);
				wrongCharacters.add(character);
				characterSet.remove(character);
				isFalseSegmented = true;
				
			}
		}
		
		
		try
		{
			//fix the wrong segmentations
			for (int i=0; i<wrongCharacters.size(); i++)
			{
				CharacterObject character = (CharacterObject)wrongCharacters.elementAt(i);
				
				ROI roi = new ROI(
								character.getX1(), 
								character.getY1(), 
								character.getX2(), 
								character.getY2());
				
				GErode gErode= new GErode(new ImageKernel(ImageKernel.UNIFORM),1,1);
				gimage = (GrayImage)gErode.apply(gimage, roi);
				
				GClose gClose= new GClose(new ImageKernel(ImageKernel.UNIFORM),1,1);
				gimage = (GrayImage)gClose.apply(gimage, roi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		
		return isFalseSegmented;	
			
	}

	

	
	//morphological operators and thresholding
	//========================================
	
	//fill gaps..like one pixel gaps and two pixels
	private void fillGaps()
	{
		byte[][] data = new BinaryImage(gimage).getData();
		
		for (int y=0; y<data.length; y++)
		{
			for (int x=0; x<data[0].length; x++)
			{
				if (data[y][x]==0 && getNeighbourCounts(data, x, y) >= 7)
				{
					data[y][x]=1;
					gimage.set(x,y,255);
				}
			}
		}
		
	}
	
	private int getNeighbourCounts(byte[][] data, int x, int y)
	{
		int count=0;

			if (x-1>=0 && x-1<data[0].length &&
				y>=0 && y<data.length)
				if (data[y][x-1]==1) count++;
				
				
			if (x+1>=0 && x+1<data[0].length &&
				y>=0 && y<data.length)
				if (data[y][x+1]==1) count++;
				
				
			if (x>=0 && x<data[0].length &&
				y-1>=0 && y-1<data.length)
				if (data[y-1][x]==1) count++;
				
				
			if (x>=0 && x<data[0].length &&
				y+1>=0 && y+1<data.length)
				if (data[y+1][x]==1) count++;
				
				
			if (x-1>=0 && x-1<data[0].length &&
				y-1>=0 && y-1<data.length)
				if (data[y-1][x-1]==1) count++;
				
				
			if (x+1>=0 && x+1<data[0].length &&
				y+1>=0 && y+1<data.length)
				if (data[y+1][x+1]==1) count++;
				
				
			if (x-1>=0 && x-1<data[0].length &&
				y+1>=0 && y+1<data.length)
				if (data[y+1][x-1]==1) count++;
				
				
			if (x+1>=0 && x+1<data[0].length &&
				y-1>=0 && y-1<data.length)
				if (data[y-1][x+1]==1) count++;
				
		return count;
	}
	
	
	private void threshold(int thresholdValue)
	{
	//	Rectangle boundary = canvas.getSelectionBox();
			try
			{
				Threshold threshold = new Threshold(thresholdValue);
			//	if (boundary!=null)
			//	{
			//		ROI roi = new ROI(new Double(boundary.getX()).intValue(),
			//								new Double(boundary.getY()).intValue(),
			//								new Double(boundary.getX()+boundary.getWidth()).intValue(),
			//								new Double(boundary.getY()+boundary.getHeight()).intValue());
			//		canvas.setImage(threshold.apply(gimage, roi));
				//}
				//else
				///{
					gimage = (GrayImage)threshold.apply(gimage);
					canvas.setImage(gimage);
				//}
         	}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
	}

	private void m_connectedComponents()
	{
		try
		{
			BinaryImage bimage = new BinaryImage(gimage);

			//ConnectedComponents conCom = new ConnectedComponents(ConnectedComponents.EIGHT_CONNECTED, 0);
			jigl.image.utils.ConnectedComponents conCom = new jigl.image.utils.ConnectedComponents(ConnectedComponents.EIGHT_CONNECTED, 0);
			//gimage = (GrayImage)conCom.apply(bimage);
			gimage = conCom.apply(bimage);
			canvas.setImage(gimage);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void m_erode()
	{
		//Rectangle boundary = canvas.getSelectionBox();
			try
			{
				GErode gErode= new GErode(new ImageKernel(ImageKernel.UNIFORM),1,1);



				//if (boundary!=null)
				//{
				//	ROI roi = new ROI(new Double(boundary.getX()).intValue(),
				//							new Double(boundary.getY()).intValue(),
				//							new Double(boundary.getX()+boundary.getWidth()).intValue(),
				//							new Double(boundary.getY()+boundary.getHeight()).intValue());
				//	gimage = (GrayImage)gErode.apply(gimage, roi);
				//}
				//else
					gimage = (GrayImage)gErode.apply(gimage);
			}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
	}

	private void m_dilate()
	{
		//Rectangle boundary = canvas.getSelectionBox();

			try
			{
				GDilate gDilate= new GDilate(new ImageKernel(1), 1,1);
				
				//if (boundary!=null)
				//{
				//	ROI roi = new ROI(new Double(boundary.getX()).intValue(),
				//							new Double(boundary.getY()).intValue(),
				//							new Double(boundary.getX()+boundary.getWidth()).intValue(),
				//							new Double(boundary.getY()+boundary.getHeight()).intValue());
				//	gimage = (GrayImage)gDilate.apply(gimage, roi);
				//}
				//else
					gimage = (GrayImage)gDilate.apply(gimage);
			}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
	}

	private void m_open()
	{
		Rectangle boundary = canvas.getSelectionBox();
			try
			{

				//using a 3x3 kernel 1,1 is the origin pixel
				GOpen gOpen= new GOpen(new ImageKernel(ImageKernel.UNIFORM),1,1);
				/*
				 *[1	1	1]
				 *[1	X	1]
				 *[1	1	1]
				 */


				if (boundary!=null)
				{
					ROI roi = new ROI(new Double(boundary.getX()).intValue(),
											new Double(boundary.getY()).intValue(),
											new Double(boundary.getX()+boundary.getWidth()).intValue(),
											new Double(boundary.getY()+boundary.getHeight()).intValue());
					gimage = (GrayImage)gOpen.apply(gimage, roi);
				}
				else
					gimage = (GrayImage)gOpen.apply(gimage);
			}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
	}

	private void m_close()
	{
		//Rectangle boundary = canvas.getSelectionBox();
			try
			{

				GClose gClose= new GClose(new ImageKernel(ImageKernel.UNIFORM),1,1);

				//if (boundary!=null)
				//{
				//	ROI roi = new ROI(new Double(boundary.getX()).intValue(),
				//							new Double(boundary.getY()).intValue(),
				//							new Double(boundary.getX()+boundary.getWidth()).intValue(),
				//							new Double(boundary.getY()+boundary.getHeight()).intValue());
				//	gimage = (GrayImage)gClose.apply(gimage, roi);
				//}
				//else
					gimage = (GrayImage)gClose.apply(gimage);
			}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
	}

	
}
	
}
	