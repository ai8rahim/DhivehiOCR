package epublisher;

import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.net.*;

import java.util.*;
import java.io.*;

public class OCRNeuralNetwork
{
	private NeuralNet consonantNN;	//the consonant's NN
	private NeuralNet vowelNN;		//the vowel's NN
	
	private Vector consonantSet;	//map to hold consonant data for resolution
	private Vector vowelSet;		//map to hold vowel data for resolution
	
		
	private String CNNPath ="nn\\ConsonantNN.snet";
	private String VNNPath ="nn\\VowelNN.snet";
	
	public OCRNeuralNetwork()
	{
	
		//load consonant stuff
		loadConsonantNN();
		loadConsonantDB();
		
		//load vowel stuff
		loadVowelNN();
		loadVowelDB();
	}

	
	//resolve consonant
	public String getConsonant(double[] data)
	{
		String consonant=" ";
		
		double inputdata[][] = new double[1][data.length];
		inputdata[0] = data;

		if (consonantNN != null) 
		{			
			Layer input = consonantNN.getInputLayer();
			input.removeAllInputs();
			
			MemoryInputSynapse memInp = new MemoryInputSynapse();
			memInp.setFirstRow(1);
			memInp.setAdvancedColumnSelector("1-" + Normalizer.getCNWidth()*Normalizer.getCNHeight());
			
			
			input.addInputSynapse(memInp);
			memInp.setInputArray(inputdata);
			Layer output = consonantNN.getOutputLayer();
			output.removeAllOutputs();
			
			MemoryOutputSynapse memOut = new MemoryOutputSynapse();
			output.addOutputSynapse(memOut);
						
			consonantNN.getMonitor().setTotCicles(1);
			consonantNN.getMonitor().setTrainingPatterns(1);
			consonantNN.getMonitor().setLearning(false);
			consonantNN.start();
			consonantNN.getMonitor().Go();
						
			double[] pattern = memOut.getNextPattern();
				/*System.out.print("\nOutput Pattern   = ");
				for (int j=0; j<pattern.length; j++)
					System.out.print(pattern[j] + ";");					
				System.out.print(" " +pattern.length);*/
	
			
			consonantNN.stop();
			//System.out.println("Finished"); 

			
			for (int i = 0; i < consonantSet.size(); i++)
			{
				NNElement nnElement = (NNElement)consonantSet.elementAt(i);
				if (nnElement.equals(pattern)) return nnElement.getElement();
			}
		}
		
		
		return consonant;
	}	
	
		
	//load ConsonantNN from serialized file
	private void loadConsonantNN()
	{
		NeuralNetLoader loader = new NeuralNetLoader(CNNPath);
		consonantNN = loader.getNeuralNet();
	}
	
	//load consonantMap from serialized file
	private void loadConsonantDB()
	{
		consonantSet = new Vector();
		Vector consonantDB = new CVDBLoader().getCData();
		
		for (int i = 0; i < consonantDB.size(); i++)
		{
			String elm = ((Object[])consonantDB.elementAt(i))[0].toString();
			double[] pat = (double[])((Object[])consonantDB.elementAt(i))[1];
			consonantSet.add(new NNElement(elm, pat));
		}	
		
		
		//COMMENT LATER
		/*for (int i = 0; i < consonantSet.size(); i++)
		{
			NNElement nnElement = (NNElement)consonantSet.elementAt(i);
			System.out.print("\n"+nnElement.getElement());
				for (int j=0; j<nnElement.getPattern().length; j++)
					System.out.print(nnElement.getPattern()[j] + ";");
		}	*/
	}

	
	//get the vowel from the pattern
	public String getVowel(double[] data)
	{
		String vowel=" ";
		
		double inputdata[][] = new double[1][data.length];
		inputdata[0] = data;

		if (vowelNN != null) 
		{			
			Layer input = vowelNN.getInputLayer();
			input.removeAllInputs();
			
			MemoryInputSynapse memInp = new MemoryInputSynapse();
			memInp.setFirstRow(1);
			memInp.setAdvancedColumnSelector("1-" + Normalizer.getVNWidth()*Normalizer.getVNHeight());
			
			
			input.addInputSynapse(memInp);
			memInp.setInputArray(inputdata);
			Layer output = vowelNN.getOutputLayer();
			output.removeAllOutputs();
			
			MemoryOutputSynapse memOut = new MemoryOutputSynapse();
			output.addOutputSynapse(memOut);
						
			vowelNN.getMonitor().setTotCicles(1);
			vowelNN.getMonitor().setTrainingPatterns(1);
			vowelNN.getMonitor().setLearning(false);
			vowelNN.start();
			vowelNN.getMonitor().Go();
						
			double[] pattern = memOut.getNextPattern();
				/*System.out.print("\nOutput Pattern   = ");
				for (int j=0; j<pattern.length; j++)
					System.out.print(pattern[j] + ";");					
				System.out.print(" " +pattern.length);*/
	
			
			vowelNN.stop();
			//System.out.println("Finished"); 

			
			for (int i = 0; i < vowelSet.size(); i++)
			{
				NNElement nnElement = (NNElement)vowelSet.elementAt(i);
				if (nnElement.equals(pattern)) return nnElement.getElement();
			}
		}
		
		
		return vowel;
	}	
	
	//load VowelNN from serialized file
	private void loadVowelNN()
	{
		NeuralNetLoader loader = new NeuralNetLoader(VNNPath);
		vowelNN = loader.getNeuralNet();		
	}
	
	//load vowelMap from serialized file
	private void loadVowelDB()
	{
		vowelSet = new Vector();
		
		Vector vowelDB = new CVDBLoader().getVData();
		
		for (int i = 0; i < vowelDB.size(); i++)
		{
			String elm = ((Object[])vowelDB.elementAt(i))[0].toString();
			double[] pat = (double[])((Object[])vowelDB.elementAt(i))[1];
			vowelSet.add(new NNElement(elm, pat));
		}	
		
		//COMMENT LATER
		/*for (int i = 0; i < vowelSet.size(); i++)
		{
			NNElement nnElement = (NNElement)vowelSet.elementAt(i);
			System.out.print("\n"+nnElement.getElement());
				for (int j=0; j<nnElement.getPattern().length; j++)
					System.out.print(nnElement.getPattern()[j] + ";");			
		}*/
	}
	
	class NNElement
	{
		private String element; //consonant or vowel String
		private double[] pattern; //the double pattern in NN
		
		public NNElement(String element, double[] pattern)
		{
			this.element = element;
			this.pattern = pattern;
		}
		
		 public boolean equals(double[] p)
		 {		 	
		 	return Arrays.equals(pattern, p);
		 }
		 
		 public String getElement()
		 {
		 	return element;
		 }
		 
		 public double[] getPattern()
		 {
		 	return pattern;
		 }
	}
	
	class CVDBLoader
	{
		String CPATH = "nn\\consonantsDB.txt";
		String VPATH = "nn\\vowelsDB.txt";
		
		/*
		 *the returned vectors from the get methods
		 *contain and object array with two elements
		 *obj[0] = element
		 *obj[1] = pattern (double array)
		 */
		
		public Vector getCData()
		{
			Vector cdata = new Vector();
			String record;
			
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(CPATH));		
				while ((record = br.readLine())!=null )
				{
					cdata.add(parse(record));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
						
			return cdata;
		}
		
		public Vector getVData()
		{
			Vector vdata = new Vector();
			String record;
			
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(VPATH));		
				while ((record = br.readLine())!=null )
				{
					vdata.add(parse(record));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return vdata;
		}
		
		private Object[] parse(String record)
		{
			Object[] obj = new Object[2];	//for the element and the pattern
			
			String[] raw = record.split(":");
			obj[0] = new String(raw[0]);
			
			String[] values = raw[1].split(";");
			double[] dvalues = new double[values.length];
			for (int i = 0; i < values.length; i++)
			{
				dvalues[i] = Double.parseDouble(values[i]);
			}
			obj[1] = dvalues;
			
			return obj;
		}
	}	
}
	