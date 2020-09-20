package epublisher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;

import jigl.image.ops.levelOps.*;
import jigl.image.ops.morph.*;
import jigl.image.warp.*;
import jigl.image.ops.*;
import jigl.image.*;

public class OCRWizardForm extends JDialog implements ActionListener, WindowListener
{
	private EPublisherUI parent;
	private ImagePanel imagePanel;
	
	private MessagePanel messagePanel;
	private DetailsPanel detailsPanel;
	private StraightenPanel straightPanel;
	private SelectionPanel selectPanel;
	
	private JButton btForward, btBack, btReset;

	
	final String HEAD1 = "OCR WIZARD: STEP 1";
	final String MSG1 = "Welcome to OCR Wizard. \n"+
			"This wizard will help you through\n" +
			"step by step to perform the OCR.";
	
	final String HEAD2 = "OCR WIZARD: STEP 2";
	final String MSG2 = "Welcome to OCR Wizard. \n"+
			"This wizard will help you through\n" +
			"step by step to perform the OCR.";
	
	final String HEAD3 = "OCR WIZARD: STEP 3";
	final String MSG3 = "Welcome to OCR Wizard. \n"+
			"This wizard will help you through\n" +
			"step by step to perform the OCR.";
			

	private JPanel cards;
	final static String STEP1 = "straighten image";
	final static String STEP2 = "roi selection";
	final static String STEP3 = "confirm wizard";
	private String currentCard;
	
	private boolean isWizardComplete=false;
	
	private GrayImage backupGImage;
	
	public OCRWizardForm(EPublisherUI parent, ImagePanel imagePanel)
	{
		//super("OCR Wizard", true, true, true, true); 
		super(parent, "OCR Wizard", true);
		
		this.parent = parent;
		this.imagePanel = imagePanel;
		backupGImage = (GrayImage)imagePanel.getGImage().copy();
		
		btReset = new JButton("Reset");
		btForward = new JButton("Forward >");
		btBack = new JButton("< Back");
		btBack.setEnabled(false);
		
		btForward.addActionListener(this);
		btBack.addActionListener(this);
		btReset.addActionListener(this);
		
		//message panel
		messagePanel = new MessagePanel(500,100);
		messagePanel.setMessage(HEAD1, MSG1);
		
		//details panel
		detailsPanel = new DetailsPanel();
		
		straightPanel = new StraightenPanel();
		
		selectPanel = new SelectionPanel();
		
		//cards panel
		cards = new JPanel(new CardLayout());
			cards.add(straightPanel, STEP1);
			cards.add(selectPanel, STEP2);
			cards.add(detailsPanel, STEP3);
			currentCard=STEP1;
		
		JPanel previewPanel = new JPanel(new BorderLayout());
			previewPanel.add(cards, BorderLayout.EAST);
			previewPanel.add(imagePanel, BorderLayout.CENTER);
			previewPanel.add(messagePanel, BorderLayout.NORTH);
		
		JPanel btPanel = new JPanel();
			EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			btPanel.setBorder(new CompoundBorder(etborder, emborder));			
			btPanel.setLayout(new BoxLayout(btPanel, BoxLayout.X_AXIS));
			btPanel.add(btReset);
			btPanel.add(Box.createHorizontalGlue());
			btPanel.add(btBack);
			btPanel.add(Box.createRigidArea(new Dimension(6, 0)));
			btPanel.add(btForward);
			
		setLayout(new BorderLayout());
		//addInternalFrameListener(this);
		addWindowListener(this);
		add(btPanel, BorderLayout.SOUTH);
		add(previewPanel, BorderLayout.CENTER);
		//pack();
		setSize(new Dimension(500,500));
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		
		if (e.getSource()==btReset)
		{
			try
			{
				imagePanel.setGImage((GrayImage)backupGImage.copy());
				imagePanel.getCanvas().setImage(imagePanel.getGImage());
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
			
		else if (e.getSource()==btForward)
		{
			if (currentCard.equals(STEP3))
			{
				isWizardComplete=true;
				dispose();
			}
			
		    CardLayout cl = (CardLayout)(cards.getLayout());
		    if (currentCard.equals(STEP1))
		    {
		    	cl.show(cards, STEP2);
		    	currentCard = STEP2;
		    	messagePanel.setMessage(HEAD2, MSG2);
		    }
		    else if (currentCard.equals(STEP2))
		    {
		    	cl.show(cards, STEP3);
		    	currentCard = STEP3;
		    	messagePanel.setMessage(HEAD3, MSG3);		    	
		    }
		}
		else if (e.getSource()==btBack)
		{
		    CardLayout cl = (CardLayout)(cards.getLayout());
		    if (currentCard.equals(STEP2))
		    {
		    	cl.show(cards, STEP1);
		    	currentCard = STEP1;
		    	messagePanel.setMessage(HEAD1, MSG1);
		    }
		    else if (currentCard.equals(STEP3))
		    {
		    	cl.show(cards, STEP2);
		    	currentCard = STEP2;
		    	messagePanel.setMessage(HEAD2, MSG2);		    	
		    }		    
		}
		
		//handle the buttons display
		if (currentCard.equals(STEP1))
		{
			btBack.setEnabled(false);
			imagePanel.enableROISelection(false);
			
		}
		else if(currentCard.equals(STEP2))
		{
			btBack.setEnabled(true);
			btForward.setEnabled(true);
			btForward.setText("Forward >");
			imagePanel.enableROISelection(true);
		}
		else if(currentCard.equals(STEP3))
		{
			btForward.setText("Finish");
			
			imagePanel.enableROISelection(false);
		}
	}
	
	 public void  windowActivated(WindowEvent e) {} 
	 public void  windowClosed(WindowEvent e) {
		if (isWizardComplete)
		{
			imagePanel.finaliseWizard();
			parent.getControl().ocrWizardComplete(imagePanel);
		}
		else
		{
			try
			{
				imagePanel.setGImage((GrayImage)backupGImage.copy());
				imagePanel.getCanvas().setImage(imagePanel.getGImage());
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
			parent.getControl().ocrWizardComplete(imagePanel);
		}		
	 } 
	 public void  windowClosing(WindowEvent e) {} 
	 public void  windowDeactivated(WindowEvent e) {} 
	 public void  windowDeiconified(WindowEvent e) {} 
	 public void  windowIconified(WindowEvent e) {} 
	 public void  windowOpened(WindowEvent e) {} 
	 
	 
	 class DetailsPanel extends JPanel
	 {
	 	private JTextArea taDetails;
	 	
	 	public DetailsPanel()
	 	{
	 		
	 		taDetails = new JTextArea("You have completed\n"+
	 								"the wizard.\n"+
	 								"Press Finish\n"+
	 								"and select OCR Automatic");
	 		taDetails.setEditable(false);
	 		taDetails.setBorder(new TitledBorder("Summary :"));
	 	
	 		EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			setBorder(new CompoundBorder(etborder, emborder));
	 		setLayout(new BorderLayout());
	 		add(new JScrollPane(taDetails), BorderLayout.CENTER);
	 		
	 	}
	 	
	 	public void setDetails(String s)
	 	{
	 		taDetails.setText(s);
	 	}
	 }
	 
	 class MessagePanel extends JPanel
	 {	
		private JTextArea taMessageText;
		private JLabel lblHeadingText;
		
		private java.awt.Image backImage;
		
		private int width;
		private int height;
		
		
		public MessagePanel(int width, int height)
		{
			setLayout(new BorderLayout());
			
			EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			setBorder(new CompoundBorder(etborder, emborder));	
			
			this.width=width;
			this.height=height;
			
			backImage = new ImageIcon("images\\message_header.png").getImage();
			
			lblHeadingText = new JLabel();
			taMessageText = new JTextArea();
			taMessageText.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(taMessageText);
				 		
	
			setPreferredSize(new Dimension(width, height));
			add(lblHeadingText, BorderLayout.NORTH);
			add(scrollPane, BorderLayout.CENTER);
			
		}
	 	public void paintComponent(Graphics g)
	 	{
	 		super.paintComponent(g);
	 		setBackground(Color.white);
	 		
			g.drawImage(backImage, 0,0,width, height, this);	 		
	 	}
	 	
	 	public void setMessage(String h, String m)
	 	{
	 		lblHeadingText.setText(h);
	 		taMessageText.setText(m);
	 	}
	 }
	 
	 class StraightenPanel extends JPanel implements ActionListener
	 {
	 	JButton btRotate;
	 	JTextArea taHelp;
	 	JTextField tfDegrees;
	 	
	 	public StraightenPanel()
	 	{
	 		String text ="Enter the degrees\n"+
	 					"you would like to\n"+
	 					"rotate and press Rotate.";
	 		taHelp = new JTextArea(text);
	 		taHelp.setEditable(false);
	 		taHelp.setBorder(new TitledBorder("Help :"));
	 		btRotate = new JButton("Rotate");
	 		tfDegrees = new JTextField(10);
	 		
	 		btRotate.addActionListener(this);
	 		
	 		
	 		JPanel fieldsPanel = new JPanel();
	 			fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
	 			fieldsPanel.add(tfDegrees);
	 			fieldsPanel.add(btRotate);
	 		
	 		EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			setBorder(new CompoundBorder(etborder, emborder));	
	 		setLayout(new BorderLayout());
	 		add(new JScrollPane(taHelp), BorderLayout.CENTER);
	 		add(fieldsPanel, BorderLayout.SOUTH);
	 		
	 	}
	 	
	 	public void actionPerformed(ActionEvent e)
	 	{
	 		try 
			{ 
				/*ScaleTransform scale = new ScaleTransform(200,200); 
				Mapper mapper = new Mapper(scale,Mapper.LINEAR); 
				
				GrayImage gimage = (GrayImage)mapper.apply(imagePanel.getGImage()); 
				
				imagePanel.getCanvas().setImage(gimage);*/
				
				//java.awt.Image img = createImage(((GrayImage)imagePanel.getGImage()).getJavaImage());
				
				//AffineTransform at = new AffineTransform();
				//at.rotate(Math.toRadians(45))
				
				//Graphics2D g2D = imagePanel.getCanvas().getGraphics();
				
				
			} 
			catch(Exception err) 
			{
				err.printStackTrace();
			
			} 

	 	}
	 }
	 
	 class SelectionPanel extends JPanel implements ActionListener
	 {
	 	private JButton btSelect;
	 	private JTextArea taHelp;
	 	
	 	public SelectionPanel()
	 	{
	 		String text ="Select the area\n"+
	 				"of interest by draging\n"+
	 				"the mouse over the image\n"+
	 				"and press Select.";
	 		taHelp = new JTextArea(text);
	 		taHelp.setBorder(new TitledBorder("Help :"));
	 		btSelect = new JButton("Select");
	 		btSelect.addActionListener(this);
	 		
	 		JPanel fieldsPanel = new JPanel();
	 			fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
	 			fieldsPanel.add(btSelect);
	 		
	 		EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			setBorder(new CompoundBorder(etborder, emborder));
	 		setLayout(new BorderLayout());
	 		add(new JScrollPane(taHelp), BorderLayout.CENTER);
	 		add(fieldsPanel, BorderLayout.SOUTH);	 	
	 	}
	 	
	 	public void actionPerformed(ActionEvent e)
	 	{
			Rectangle boundary = imagePanel.getCanvas().getSelectionBox();
			if (boundary!=null)
			{
				jigl.image.ROI roi = new jigl.image.ROI(
								(int)boundary.getX() ,
								(int)boundary.getY() ,
								(int)boundary.getX()+(int)boundary.getWidth() ,
								(int)boundary.getY()+(int)boundary.getHeight() );
				imagePanel.setSelection(roi);
			}
			else
				JOptionPane.showMessageDialog(this,
				    "No area of interest has been selected.",
				    "Selection Area Empty",
				    JOptionPane.INFORMATION_MESSAGE);
	 	
		}
	 }
	
}
	