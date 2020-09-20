import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class NNTester extends JFrame implements ActionListener
{
	JButton btTrain=new JButton("train");
	JButton btTest=new JButton("test");
	JButton btSaveNN=new JButton("save");
 	
 	NeuralNet xor;
 	
 	OCRNeuralNet ocrnet;
 	
	public static void main(String[] args)
	{
 		new NNTester();
	}
	
	public NNTester()
	{
	
		JPanel panel=new JPanel();
		panel.add(btTrain);
		panel.add(btTest);
		panel.add(btSaveNN);
		btSaveNN.addActionListener(this);
		btTrain.addActionListener(this);
		btTest.addActionListener(this);
		
		getContentPane().setLayout(new BorderLayout());
		//getContentPane().add(panel, BorderLayout.SOUTH);
		getContentPane().add(new ImageCollection(), BorderLayout.CENTER);
		setSize(900,500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
 
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==btTrain)
		{
			train();
		}
		else if (e.getSource()==btTest)
		{
			test();
			//arrayTest("xor.snet");
		}
		else if (e.getSource()==btSaveNN)
		{
			ocrnet.saveNeuralNet();
		}
	}
	
	
	
	public NeuralNet loadNN(String path)
	{
		
		/* We need just to provide the serialized NN file name */
		NeuralNetLoader loader = new NeuralNetLoader(path);
		NeuralNet myNet = loader.getNeuralNet();
		return myNet;
		
	}

	
	
	
	/*
	 *testing a NN using an array as input
	 */
	private void arrayTest(String fileName) {
	// The input array used for this example
	 double[][] inputArray = { {0, 0}, {0, 1}, {1, 0}, {1, 1} };
	// We load the serialized XOR neural net
	 xor = restoreNeuralNet(fileName);
	
	if (xor != null) {
		
		/* We get the first layer of the net (the input layer),
		then remove all the input synapses attached to it
		and attach a MemoryInputSynapse */
		Layer input = xor.getInputLayer();
		input.removeAllInputs();
		MemoryInputSynapse memInp = new MemoryInputSynapse();
		memInp.setFirstRow(1);
		memInp.setAdvancedColumnSelector("1,2");
		input.addInputSynapse(memInp);
		memInp.setInputArray(inputArray);
		
		/* We get the last layer of the net (the output layer),
		then remove all the output synapses attached to it
		and attach a MemoryOutputSynapse */
		Layer output = xor.getOutputLayer();
		
		// Remove all the output synapses attached to it...
		output.removeAllOutputs();
		
		//...and attach a MemoryOutputSynapse
		MemoryOutputSynapse memOut = new MemoryOutputSynapse();
		output.addOutputSynapse(memOut);
		
		// Now we interrogate the net
		xor.getMonitor().setTotCicles(1);
		xor.getMonitor().setTrainingPatterns(4);
		xor.getMonitor().setLearning(false);
		xor.start();
		xor.getMonitor().Go();
		
		for (int i=0; i < inputArray.length; ++i) {
			// Read the next pattern and print out it
			double[] pattern = memOut.getNextPattern();
			System.out.println("Output Pattern #"+(i+1)+" = "+pattern[0]);
		}
		
		xor.stop();
		System.out.println("Finished"); 
	}
}	
	
	private NeuralNet restoreNeuralNet(String fileName) {
		NeuralNet nnet = null;
		try {
			FileInputStream stream = new FileInputStream(fileName);
			ObjectInputStream inp = new ObjectInputStream(stream);
			nnet = (NeuralNet)inp.readObject();
		}
		catch (Exception excp) {
			excp.printStackTrace();
		}
		return nnet;
	}	
	
	
	
	void train()
	{
		 ocrnet=new OCRNeuralNet();

	}
	
	
	void test()
	{
		NeuralNet xorNNet = this.restoreNeuralNet("xor.snet");
		if (xorNNet != null) {
			
			
			// we get the output layer				
			Layer output = xorNNet.getOutputLayer();
				// we create an output synapse
				FileOutputSynapse fileOutput = new FileOutputSynapse();
				fileOutput.setFileName("xor_out.txt");
				// we attach the output synapse to the last layer of the NN
				output.addOutputSynapse(fileOutput);
			
			
			
			
			// we run the neural network for only one cycle in recall mode
			xorNNet.getMonitor().setTotCicles(1);
			xorNNet.getMonitor().setLearning(false);
			xorNNet.start();
			xorNNet.getMonitor().Go();
		}
	}
	
}
