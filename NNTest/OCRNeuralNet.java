import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.net.*;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class OCRNeuralNet implements NeuralNetListener
{
	NeuralNet nnet;
	
	public OCRNeuralNet()
	{
		
		/* The Layers */
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();
			input.setRows(2);
			hidden.setRows(3);
			output.setRows(1);
		
		/* The Synapses */
		FullSynapse synapse_IH = new FullSynapse(); /* Input -> Hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
			input.addOutputSynapse(synapse_IH);
			hidden.addInputSynapse(synapse_IH);
			hidden.addOutputSynapse(synapse_HO);
			output.addInputSynapse(synapse_HO);
		
		/* The I/O components */
		FileInputSynapse inputStream = new FileInputSynapse();
			inputStream.setAdvancedColumnSelector("1,2");
			inputStream.setFileName("xor_input.txt");
			input.addInputSynapse(inputStream);
		
		/* The Trainer and its desired file */
		TeachingSynapse trainer = new TeachingSynapse();
		FileInputSynapse samples = new FileInputSynapse();
			samples.setFileName("xor_input.txt");
			trainer.setDesired(samples);
			samples.setAdvancedColumnSelector("3");
			output.addOutputSynapse(trainer);
			
			
		nnet = new NeuralNet();
			nnet.addLayer(input, NeuralNet.INPUT_LAYER);
			nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
			nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
			nnet.setTeacher(trainer);

		Monitor monitor = nnet.getMonitor();
			monitor.setLearningRate(0.8);
			monitor.setMomentum(0.3);
			monitor.setTrainingPatterns(4); /* # of rows in the input file */
			monitor.setTotCicles(10000); /* How many times the net must be trained*/
			monitor.setLearning(true); /* The net must be trained */
			monitor.addNeuralNetListener(this);

		nnet.start();
		nnet.getMonitor().Go();
			
	}
			
	public void saveNeuralNet() {
		try {
			FileOutputStream stream = new FileOutputStream("xor.snet");
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(nnet);
			out.close();
			System.out.println("NN Saved");
		}
		catch (Exception excp) {
			excp.printStackTrace();
		}
	}		

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Training finished");
	}
	public void netStoppedError(NeuralNetEvent e, String error) {
		System.out.println("Training finished error" + error);
		}
	public void errorChanged(NeuralNetEvent e) {}
	public void netStarted(NeuralNetEvent e) {}

	public void cicleTerminated(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
		long c = mon.getCurrentCicle();
		long cl = c / 1000;
		/* We want print the results every 1000 cycles */
		if ((cl * 1000) == c)
			System.out.println(c + " cycles remaining - Error = " +
		mon.getGlobalError());
	}	
		
		
		
	
}

