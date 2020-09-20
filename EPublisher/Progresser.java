package epublisher;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class Progresser extends JDialog implements Runnable, ActionListener 
{
	JProgressBar progressBar;
	String message;
	int taskLength=0;
	
	
	final static String WAIT = "Please Wait...";
	
	JLabel lblMessage;
	JButton btCancel;
	
	private volatile Thread thread;
	private volatile Thread pthread;
	
	JFrame parentFrame;
	
	
	public Progresser(Frame owner, String title, boolean modal, Thread pthread)
	{
		super(owner, title, modal);
		parentFrame=(JFrame)owner;
		this.message = message;
		this.pthread= pthread;
		thread = new Thread(this);
		thread.start();
	}

	
	public Progresser(Frame owner, String title, boolean modal, Thread pthread, int max)
	{
		super(owner, title, modal);
		parentFrame=(JFrame)owner;
		taskLength=max;
		thread = new Thread(this);
		this.pthread= pthread;
		thread.start();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		thread=null;
		pthread=null;
		if (parentFrame!=null) parentFrame.dispose();
		dispose();
	}
	
	public void run()
	{
		lblMessage = new JLabel("");
		btCancel = new JButton("Cancel");
		btCancel.addActionListener(this);
		progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
		progressBar.setStringPainted(true); 
		
		if (message == null)
		{
			progressBar.setIndeterminate(true);
			setMessage(WAIT);
		}
		
		if (taskLength != 0)
		{
			progressBar.setMaximum(taskLength);
			progressBar.setValue(0);
		}

		JPanel panel = new JPanel();
		panel.add(progressBar, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel();
		panel2.add(lblMessage, BorderLayout.CENTER);
		
		JPanel panel3 = new JPanel();
		panel3.add(btCancel);
		
		getContentPane().add(panel3, BorderLayout.SOUTH);
		getContentPane().add(panel2, BorderLayout.NORTH);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	

	
	public void setMessage(String s)
	{
		lblMessage.setText(s);
	}
	
	
	public void setProgress(int i)
	{
		progressBar.setValue(i);
		if (progressBar.getValue()>=progressBar.getMaximum()) dispose();
	}
	
	public void setProgress(int min, int max)
	{
		if (progressBar.isIndeterminate())
		{
			progressBar.setIndeterminate(false);
			progressBar.setString(null);
		}
		progressBar.setMaximum(max);
		progressBar.setValue(min);
	}
}

class IPProgresser extends JDialog implements Runnable
{
	
	private JProgressBar ipBar, nnBar, docBar;
	private JTextArea taMessage;
	
	private volatile Thread thread;
	
	public IPProgresser(Frame owner)
	{
		super(owner, "Perforing OCR", true);



		
		ipBar = new JProgressBar(JProgressBar.HORIZONTAL);
		ipBar.setStringPainted(true); 
		ipBar.setIndeterminate(true); 
		ipBar.setBorder(new TitledBorder("Process Image : "));
		
		nnBar = new JProgressBar(JProgressBar.HORIZONTAL);
		nnBar.setStringPainted(true); 
		nnBar.setIndeterminate(true); 
		nnBar.setBorder(new TitledBorder("Recognise Characters : "));
		
		docBar = new JProgressBar(JProgressBar.HORIZONTAL);
		docBar.setStringPainted(true); 
		docBar.setIndeterminate(true); 
		docBar.setBorder(new TitledBorder("Display Characters : "));
			 	
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		JPanel panel3 = new JPanel(new BorderLayout());
		
		panel1.add(ipBar, BorderLayout.CENTER);
		panel2.add(nnBar, BorderLayout.CENTER);
		panel3.add(docBar, BorderLayout.CENTER);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(panel1);
		mainPanel.add(panel2);
		mainPanel.add(panel3);
		
		getContentPane().add(mainPanel, BorderLayout.NORTH);
		taMessage = new JTextArea();
		taMessage.setBorder(new TitledBorder("Details : "));
		taMessage.setEditable(false);
		getContentPane().add(new JScrollPane(taMessage), BorderLayout.CENTER);
		
		thread = new Thread(this);
		thread.start();
	}
		
	public synchronized JProgressBar getIPBar()
	{
		/*try
		{
		
		while(stillLoading)
		{
			wait();
		}
		}
		catch(InterruptedException ie)
		{
			ie.printStackTrace();
			}
		notifyAll();*/
		return ipBar;
	}


	public JProgressBar getNNBar()
	{
		return nnBar;
	}	

	public JProgressBar getDocBar()
	{
		return docBar;
	}
	
	public void appendMessage(String s)
	{
		taMessage.append(s);
	}
		
	public void run()
	{
		setSize(new Dimension(400, 400));
		setVisible(true);
		
	 	
	}
 
}

	