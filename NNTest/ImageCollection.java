
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

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


import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.net.*;

class ImageCollection extends JPanel implements ActionListener, NeuralNetListener, ItemListener
{
	JTextField tfLoc;
	JButton btLoad;
	JButton btTrain;
	
	JTextField tfPatterns;
	JButton btCTrain;
	JButton btCTest;
	
	JButton btDown;
	JButton btCDown;
	JButton btNN;
	
	JPanel displayPanel;
	JScrollPane scrollPane;
	
	JTextField tfDX;
	JTextField tfDY;
	
	JCheckBox chRaw, chInt, chSq, chSs;
	//for the test cases
	private boolean bRaw=false;
	private boolean bInt=false;
	private boolean bSq=false;
	private boolean bSs=false;
	
	Vector images=new Vector();
	
	
	final int VRANGE = 27;
	final int HRANGE = 27;
	
	int txtIndex=0;
	
	public ImageCollection()
	{		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));

		setLayout(new BorderLayout());
		tfLoc = new JTextField("C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\input.txt",10);
		tfPatterns= new JTextField(5);
		tfPatterns.setText("27");
		p1.add(new JLabel("#pat"));
		p1.add(tfPatterns);
		p1.add(tfLoc);	
			
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		p1.add(btLoad);
		
		btTrain = new JButton("Train");
		btTrain.addActionListener(this);
		p1.add(btTrain);
			
		btDown = new JButton("Down");
		btDown.addActionListener(this);
		//p1.add(btDown);
		
			
		btCDown = new JButton("CDown");
		btCDown.addActionListener(this);
		p1.add(btCDown);
		
		
		btNN = new JButton("NN");
		btNN.addActionListener(this);
		p1.add(btNN);
		
		
		btCTrain = new JButton("btCTrain");
		btCTrain.addActionListener(this);
		p1.add(btCTrain);
		
		btCTest = new JButton("btCTest");
		btCTest.addActionListener(this);
		p1.add(btCTest);
		
		JPanel chPanel = new JPanel();
			chPanel.setLayout(new BoxLayout(chPanel, BoxLayout.Y_AXIS));
			chRaw = new JCheckBox("raw");
			chRaw.addItemListener(this);
			chInt = new JCheckBox("int");
			chInt.addItemListener(this);
			chSq = new JCheckBox("sq");
			chSq.addItemListener(this);
			chSs = new JCheckBox("ss");
			chSs.addItemListener(this);
			chPanel.add(chRaw);
			chPanel.add(chInt);
			chPanel.add(chSq);
			chPanel.add(chSs);
		
		JPanel pd = new JPanel();
		p1.add(pd, BorderLayout.SOUTH);
		tfDX = new JTextField(5);
		tfDY = new JTextField(5);
		pd.add(new JLabel("DX"));
		pd.add(tfDX);
		pd.add(new JLabel("DX"));
		pd.add(tfDY);
		p1.add(Box.createVerticalGlue());
		add(p1, BorderLayout.EAST);
		add(chPanel, BorderLayout.WEST);
		
		displayPanel=new JPanel();
		scrollPane = new JScrollPane(displayPanel);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void itemStateChanged(ItemEvent e) {
		
		Object source = e.getItemSelectable();
		if (e.getStateChange() == ItemEvent.DESELECTED)
		{
			if (source == chRaw)
			{
				bRaw = false;
			}
			else if (source == chInt)
			{
				bInt = false;
			}
			else if (source == chSq)
			{
				bSq = false;
			}
			else if (source == chSs)
			{
				bSs = false;
			}
		}
		else
		{
			if (source == chRaw)
			{
				bRaw = true;
			}
			else if (source == chInt)
			{
				bInt = true;
			}
			else if (source == chSq)
			{
				bSq = true;
			}
			else if (source == chSs)
			{
				bSs = true;
			}
		}
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==btNN)
		{
			testNN();
		}

		if (e.getSource()==btTrain)
		{
			trainNN();		
		}
		
		if (e.getSource()==btCTest)
		{
			try
			{
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);	
					GrayImage gi = dp.getImage();
					Threshold threshold = new Threshold(128);

					gi = (GrayImage)threshold.apply(gi);
					byte[][] data  = new BinaryImage(gi).getData();	
					for (int y = 0; y<data.length; y++)
					{
						System.out.print("\n\t");
						for (int x = 0; x<data[0].length;x++)
						{
							System.out.print(data[y][x]);
						}
					}
						System.out.print("\n\t");
							System.out.print("\n");

				}
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}
			
			testNNCC();
		}

		if (e.getSource()==btCTrain)
		{
			 trainNNCC();
			//System.out.println(txtIndex);		
		}
		
		
		
		
		
		if (e.getSource()==btLoad)
		{
			txtIndex++;
	          
	          		//open the file
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			
			try
			{
				int ij = chooser.showOpenDialog(null);
				File[] files = chooser.getSelectedFiles();
				
				
				for (int i=0; i<files.length; i++)
				{
					//String filename=files[i].getPath();
					
					DataPanel dp = new DataPanel(files[i]);
					displayPanel.add(dp);
					images.add(dp);
				
				}	
				
			} 
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}	
	          
	          
		}
		
		if (e.getSource()==btDown)
		{		
			try
			{
				//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tfLoc.getText())));
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);					
					//Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON);
					Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);
					//norms(norm, pw, dp.getId());
				}
				//pw.flush();
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}
		}
		
		if (e.getSource()==btCDown)
		{	
			try
			{
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);
					Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);

				}
			}
			catch (Exception ee)
			{
			   ee.printStackTrace();
			}
		}
	}
	
	
	
	//NEURAL NETWORK STUFF
	//========================
	private void testNN()
	{
		double [][] inputdata = new double[images.size()][Normalizer.getVNWidth()*Normalizer.getVNHeight()];
		
		for (int i=0; i<images.size(); i++)
		{
			DataPanel dp = (DataPanel)images.elementAt(i);
			//Normalizer norm = new Normalizer(dp.getImage(), Normalizer.VOW);
			Normalizer norm = new Normalizer(dp.getImage(), Normalizer.VOW, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);
			inputdata[i] = norm.getVMatrix();
		}
		
	//	String path="C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\thinNN\\ocrnn.snet";
		String path="C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\myNN.snet";
		NeuralNetLoader loader = new NeuralNetLoader(path);
		NeuralNet myNN = loader.getNeuralNet();
		
		if (myNN != null) 
		{			
			Layer input = myNN.getInputLayer();
			input.removeAllInputs();
			
			
			MemoryInputSynapse memInp = new MemoryInputSynapse();
			memInp.setFirstRow(1);
			memInp.setAdvancedColumnSelector("1-" + Normalizer.getVNWidth()*Normalizer.getVNHeight());
			
			
			input.addInputSynapse(memInp);
			memInp.setInputArray(inputdata);
			Layer output = myNN.getOutputLayer();
			output.removeAllOutputs();
			
			MemoryOutputSynapse memOut = new MemoryOutputSynapse();
			output.addOutputSynapse(memOut);
				
		//	System.out.println("pattern - " + input.getRows());
		//	System.out.println("input - " + input.getRows());
		//	System.out.println("linear - " + myNN.getLayer("linearLayer").getRows());
		
			myNN.getMonitor().setTotCicles(1);
			myNN.getMonitor().setTrainingPatterns(images.size());
			myNN.getMonitor().setLearning(false);
			myNN.start();
			myNN.getMonitor().Go();
			
		/*	for (int y=0; y<inputdata.length; y++)
			{
				System.out.print("\n");
				for(int x=0; x<50; x++)
				{
					System.out.print(inputdata[y][x]);
				}
				
			}*/
			
		//	System.out.println("size "+memOut.getAllPatterns().size());
			
			for (int i=0; i < inputdata.length; ++i) {
				double[] pattern = memOut.getNextPattern();
				System.out.print("\nOutput Pattern #"+(i+1)+" = ");
				for (int j=0; j<pattern.length; j++)
					System.out.print(pattern[j] + ";");
			}
			
			myNN.stop();
			System.out.println("Finished"); 
		}		
		
	}
	
	//create, train and save the NN
	private void trainNN()
	{
		double [][] inputdata = new double[images.size()][Normalizer.getVNWidth()*Normalizer.getVNHeight()];
		
		for (int i=0; i<images.size(); i++)
		{
			DataPanel dp = (DataPanel)images.elementAt(i);
			//Normalizer norm = new Normalizer(dp.getImage(), Normalizer.VOW);
			Normalizer norm = new Normalizer(dp.getImage(), Normalizer.VOW, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);
			inputdata[i] = norm.getVMatrix();
		}
		
		
		
		int nCharacters=5;
		int nPatterns=images.size();
		int nNeurons=Normalizer.getVNWidth()*Normalizer.getVNHeight();
		int nTrainingCicles=10000;
		
		System.out.println(nCharacters);
		System.out.println(nPatterns);
		System.out.println(nNeurons);
		System.out.println(nTrainingCicles);
		
		// The input layer 
		LinearLayer input = new LinearLayer();  
		input.setRows(nNeurons); // # of input nodes 
			
			
		MemoryInputSynapse memInp = new MemoryInputSynapse();
		memInp.setFirstRow(1);
		memInp.setAdvancedColumnSelector("1-" + nNeurons);
		
		input.addInputSynapse(memInp);
		memInp.setInputArray(inputdata);		
		
		// The output layer, represented by a winner takes all  
		WTALayer output = new WTALayer(); 
		output.setLayerHeight(1); // height of the output map 
		output.setLayerWidth(nCharacters); // width of the output map 
		// The Kohonen synapse 
		KohonenSynapse kSynapse = new KohonenSynapse(); 
		// Now we connect the two layers 
		input.addOutputSynapse(kSynapse); 
		output.addInputSynapse(kSynapse); 
		// Put all together 
		
		FileOutputSynapse fileOutput = new FileOutputSynapse();
		fileOutput.setFileName("C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\newout.txt");
		output.addOutputSynapse(fileOutput);		
		
		Monitor monitor = new Monitor();
		monitor.setLearningRate(0.7);
		monitor.setTotCicles(nTrainingCicles);
		monitor.setTrainingPatterns(nPatterns);
		monitor.setLearning(true);
		monitor.addNeuralNetListener(this);
			
		nn = new NeuralNet(); 
		nn.addLayer(input, NeuralNet.INPUT_LAYER); 
		nn.addLayer(output, NeuralNet.OUTPUT_LAYER); 
		nn.setMonitor(monitor);
		
		nn.start();
		monitor.Go();
		
		
		
	}


	private void testNNCC()
	{
		double [][] inputdata = new double[images.size()][Normalizer.getCNWidth()*Normalizer.getCNHeight()];
		
		for (int i=0; i<images.size(); i++)
		{
			DataPanel dp = (DataPanel)images.elementAt(i);
			///Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON);
			Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);
			inputdata[i] = norm.getCMatrix();
		}
		
	//	String path="C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\thinNN\\ocrnn.snet";
		String path="C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\myNN.snet";
		NeuralNetLoader loader = new NeuralNetLoader(path);
		NeuralNet myNN = loader.getNeuralNet();
		
		if (myNN != null) 
		{			
			Layer input = myNN.getInputLayer();
			input.removeAllInputs();
			
			
			MemoryInputSynapse memInp = new MemoryInputSynapse();
			memInp.setFirstRow(1);
			memInp.setAdvancedColumnSelector("1-" + Normalizer.getCNWidth()*Normalizer.getCNHeight());
			
			
			input.addInputSynapse(memInp);
			memInp.setInputArray(inputdata);
			Layer output = myNN.getOutputLayer();
			output.removeAllOutputs();
			
			MemoryOutputSynapse memOut = new MemoryOutputSynapse();
			output.addOutputSynapse(memOut);
						
			myNN.getMonitor().setTotCicles(1);
			myNN.getMonitor().setTrainingPatterns(images.size());
			myNN.getMonitor().setLearning(false);
			myNN.start();
			myNN.getMonitor().Go();

			
			for (int i=0; i < inputdata.length; ++i) {
				double[] pattern = memOut.getNextPattern();
				System.out.print("\nOutput Pattern #"+(i+1)+" = ");
				for (int j=0; j<pattern.length; j++)
					System.out.print(pattern[j] + ";");
			}
			
			myNN.stop();
			System.out.println("\nFinished"); 
		}		
		
	}
	
	//create, train and save the NN
	private void trainNNCC()
	{
		double [][] inputdata = new double[images.size()][Normalizer.getCNWidth()*Normalizer.getCNHeight()];
		
		for (int i=0; i<images.size(); i++)
		{
			DataPanel dp = (DataPanel)images.elementAt(i);
			//Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON);
			Normalizer norm = new Normalizer(dp.getImage(), Normalizer.CON, Integer.parseInt(tfDX.getText()), Integer.parseInt(tfDY.getText()),
						bRaw, bInt, bSq, bSs);
			inputdata[i] = norm.getCMatrix();
		}
		
		
		
		int nCharacters=Integer.parseInt(tfPatterns.getText());
		int nPatterns=images.size();
		int nNeurons=Normalizer.getCNWidth()*Normalizer.getCNHeight();
		int nTrainingCicles=1000;
		
		System.out.println(nCharacters);
		System.out.println(nPatterns);
		System.out.println(nNeurons);
		System.out.println(nTrainingCicles);
		
		// The input layer 
		LinearLayer input = new LinearLayer();  
		input.setRows(nNeurons); // # of input nodes 
			
			
		MemoryInputSynapse memInp = new MemoryInputSynapse();
		memInp.setFirstRow(1);
		memInp.setAdvancedColumnSelector("1-" + nNeurons);
		
		input.addInputSynapse(memInp);
		memInp.setInputArray(inputdata);		
		
		// The output layer, represented by a winner takes all  
		WTALayer output = new WTALayer(); 
		output.setLayerHeight(1); // height of the output map 
		output.setLayerWidth(nCharacters); // width of the output map 
		// The Kohonen synapse 
		KohonenSynapse kSynapse = new KohonenSynapse(); 
		// Now we connect the two layers 
		input.addOutputSynapse(kSynapse); 
		output.addInputSynapse(kSynapse); 
		// Put all together 
		
		//FileOutputSynapse fileOutput = new FileOutputSynapse();
		//fileOutput.setFileName("C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\newout.txt");
		//output.addOutputSynapse(fileOutput);		
		
		Monitor monitor = new Monitor();
		monitor.setLearningRate(0.7);
		monitor.setTotCicles(nTrainingCicles);
		monitor.setTrainingPatterns(nPatterns);
		monitor.setLearning(true);
		monitor.addNeuralNetListener(this);
			
		nn = new NeuralNet(); 
		nn.addLayer(input, NeuralNet.INPUT_LAYER); 
		nn.addLayer(output, NeuralNet.OUTPUT_LAYER); 
		nn.setMonitor(monitor);
		
		nn.start();
		monitor.Go();
		
		
		
	}


	
	NeuralNet nn;
	
	public void netStopped(NeuralNetEvent e) {
		System.out.println("Training finished");
		saveNeuralNet(nn, "C:\\Documents and Settings\\Ahmed Ibrahim\\Desktop\\myNN.snet");
	}
	
	public void cicleTerminated(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
		long c = mon.getCurrentCicle();
		long cl = c / 100;
		/* We want print the results every 1000 cycles */
		if ((cl * 100) == c)
			System.out.println(c + " cycles remaining");
	}
			
	public void saveNeuralNet(NeuralNet nN, String path) {
		try {
			FileOutputStream stream = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(nN);
			out.close();
			System.out.println("NN Saved");
		}
		catch (Exception excp) {
			excp.printStackTrace();
		}
	}
                    
 public void errorChanged(NeuralNetEvent e)    {}         
 public void netStarted(NeuralNetEvent e)     {}        
 public void netStoppedError(NeuralNetEvent e, java.lang.String error)  	{}
	
	
	
	//========================
	
	private void horizontalProjection(jigl.image.Image gsubimage, PrintWriter pw, String id)
	{
		//do horizontal projction for feature extraction
		byte[][] data = new BinaryImage((GrayImage)gsubimage).getData();
		int counter=0;
		
		
		
		for (int y=0; y<VRANGE; y++)
		{
				counter++;
			int hcount=0;
			for (int x=0; x<HRANGE; x++)
			{	
				
				if (y>=data.length || x>=data[0].length)
				{
				//	pw.print("0.0;");
				}
				else
				{
					if(data[y][x]==1) hcount++;
				}
			}
			pw.print(hcount + ";");
		}	
		pw.print(id+"\n");
		System.out.println("columns " + counter);
		
		
	}	
	
	private void norms(Normalizer norm, PrintWriter pw, String id)
	{
		//do horizontal projction for feature extraction
		boolean[][] data = norm.getData();		
		
		int counter=0;
		for (int y = 0; y<data.length; y++)
		{
			for (int x=0; x<data[0].length; x++)
			{
				counter++;
				if (data[y][x]==true) pw.print("1.0;");
				else pw.print("0.0;");
			}
		}
		pw.print(id+"\n");
		System.out.println("columns " + counter);
	}
	
	private void verticalProjection(jigl.image.Image gsubimage, PrintWriter pw, String id)
	{
		//do horizontal projction for feature extraction
		byte[][] data = new BinaryImage((GrayImage)gsubimage).getData();
		int counter=0;
		
		
		
		for (int x=0; x<HRANGE; x++)
		{
				counter++;
				int vcount=0;
			for (int y=0; y<VRANGE; y++)
			{	
				
				if (y>=data.length || x>=data[0].length)
				{
				//	pw.print("0.0;");
				}
				else
				{
					if(data[y][x]==1) vcount++;
				}
			}
			pw.print(vcount + ";");
		}	
		pw.print(id+"\n");
		System.out.println("columns " + counter);
		
		
	}
	
	
	private void hvProjection(jigl.image.Image gsubimage, PrintWriter pw, String id)
	{
		//do horizontal projction for feature extraction
		byte[][] data = new BinaryImage((GrayImage)gsubimage).getData();
		int counter=0;
		
		
		
		for (int y=0; y<VRANGE; y++)
		{
				counter++;
			int hcount=0;
			for (int x=0; x<HRANGE; x++)
			{	
				
				if (y>=data.length || x>=data[0].length)
				{
				//	pw.print("0.0;");
				}
				else
				{
					if(data[y][x]==1) hcount++;
				}
			}
			pw.print(hcount + ";");
		}	
		
		
		for (int x=0; x<HRANGE; x++)
		{
				counter++;
				int vcount=0;
			for (int y=0; y<VRANGE; y++)
			{	
				
				if (y>=data.length || x>=data[0].length)
				{
				//	pw.print("0.0;");
				}
				else
				{
					if(data[y][x]==1) vcount++;
				}
			}
			pw.print(vcount + ";");
		}	
		pw.print(id+"\n");
		System.out.println("columns " + counter);
		
		
	}
	
	
	private void imageToText(jigl.image.Image gsubimage, PrintWriter pw, String id)
	{
		byte[][] outdata = new BinaryImage((GrayImage)gsubimage).getData();
		int counter=0;
		//image will be normalized to 50x50
		for (int y=0; y<VRANGE; y++)
		{
			for (int x=0; x<HRANGE; x++)
			{
				counter++;
				if (y>=outdata.length || x>=outdata[0].length)
				{
					pw.print("0.0;");
				}
				else
				{
					pw.print(outdata[y][x]+".0;");
				}
			}
		}
		pw.print(id+"\n");
		System.out.println("columns " + counter);
	}
	
	class DataPanel extends JPanel
	{
		GrayImage gimage;
		JTextField tfId;
		
		public DataPanel(File file)
		{
			try
			{
		   ImageInputStreamJAI iistream = new ImageInputStreamJAI(file.getPath());
		   jigl.image.Image iimage = iistream.read();
		   
		     gimage=new ImageConverter().toGray(iimage);
		   JImageCanvas canvas=new JImageCanvas();
		   
		   //gimage=new Thinning(gimage).apply();
			try
			{
				Threshold threshold = new Threshold(128);

				gimage = (GrayImage)threshold.apply(gimage);
         	}
			catch(Exception exp)
			{
				System.out.println(exp);
			}
		   canvas.setImage(gimage);
		   
		   setLayout(new BorderLayout());
		   tfId=new JTextField(Integer.toString(txtIndex));
		   add(tfId, BorderLayout.NORTH);
		   add(new JLabel(file.getName()), BorderLayout.SOUTH);
		   add(canvas, BorderLayout.CENTER);
		   setPreferredSize(new Dimension(50, 50));		
			} 
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}	
		}
		
		public GrayImage getImage()
		{
			return gimage;
		}
		
		public String getId()
		{
			return tfId.getText();
		}
	}
	
}
	
	
	
	/*
	 *
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==btNN)
		{
			testNN();
		}
		if (e.getSource()==btLoad)
		{
			txtIndex++;
	          
	          		//open the file
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			
			try
			{
				int ij = chooser.showOpenDialog(null);
				File[] files = chooser.getSelectedFiles();
				
				
				for (int i=0; i<files.length; i++)
				{
					//String filename=files[i].getPath();
					
					DataPanel dp = new DataPanel(files[i]);
					displayPanel.add(dp);
					images.add(dp);
				
				}	
				
			} 
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}	
	          
	          
		}
		if (e.getSource()==btExport)
		{
			try
			{
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tfLoc.getText())));
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);
					imageToText(dp.getImage(), pw, dp.getId());
				}
				pw.flush();
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}		
		}
		if (e.getSource()==btHorizontal)
		{
			try
			{
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tfLoc.getText())));
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);
					horizontalProjection(dp.getImage(), pw, dp.getId());
				}
				pw.flush();
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}		
		}
		if (e.getSource()==btTrain)
		{
			trainNN();		
		}
		if (e.getSource()==btHV)
		{
			try
			{
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tfLoc.getText())));
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);
					hvProjection(dp.getImage(), pw, dp.getId());
				}
				pw.flush();
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}		
		}
		
		if (e.getSource()==btDown)
		{		
			try
			{
				//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tfLoc.getText())));
				
				for (int i=0; i<images.size(); i++)
				{
					DataPanel dp = (DataPanel)images.elementAt(i);
					Normalizer norm = new Normalizer(dp.getImage());
					//norms(norm, pw, dp.getId());
				}
				//pw.flush();
			}
			catch (Exception ee) 
			{
			   ee.printStackTrace();
			}
		}
	}
	 *
	 **/