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

public class ProjectForm extends JInternalFrame implements ActionListener, InternalFrameListener
{
	private JTextField tfProjectName;
	private JTextField tfAuthorName;
	private JTextField tfBookName;
	private JTextField tfPublicationDate;
	
	
	private JList lstPages;
	private DefaultListModel lstModel;
	private JButton btUP, btDown;
	
	private JButton btClear, btSet;
	
	private ProjectObject projectObject;
	
	private EPublisherUI parent;
	
	public ProjectForm(EPublisherUI parent)
	{	
		super("Project Details", true, true, true, true);  
		this.parent = parent;
		projectObject = new ProjectObject();
		
		createUI();
		
	}
	
	public ProjectForm(EPublisherUI parent, ProjectObject projectObject)
	{	
		super("Project Details", true, true, true, true);  
		this.parent = parent;
		this.projectObject = projectObject;
		
		createUI();
		
		
		tfProjectName.setText(projectObject.getProjectName());
		tfAuthorName.setText(projectObject.getAuthorName());
		tfBookName.setText(projectObject.getBookName());
		//tfPublicationDate.setText(projectObject.get());
	}
	
	private void clearFields()
	{
		tfProjectName.setText("");
		tfAuthorName.setText("");
		tfBookName.setText("");
		tfPublicationDate.setText("");
	}
	
	private void setFields()
	{
		projectObject.setProjectName(tfProjectName.getText());
		projectObject.setAuthorName(tfAuthorName.getText());
		projectObject.setBookName(tfBookName.getText());
		//projectObject.setPublicationDate(tfPublicationDate.getText());
	 	parent.getControl().setProjectObject(projectObject);
	 	dispose();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==btClear)
			clearFields();
		else if (e.getSource()==btSet)
			setFields();
	}
	
	private void createUI()
	{       
        int numPairs = 4;
		JPanel fieldsPanel = new JPanel(new SpringLayout());
		
			tfProjectName = new JTextField(10);
			tfAuthorName = new JTextField(10);
			tfBookName = new JTextField(10);
			tfPublicationDate = new JTextField(10);
			
			fieldsPanel.add(new JLabel("Project Name : "));
			fieldsPanel.add(tfProjectName);
			fieldsPanel.add(new JLabel("Author Name : "));
			fieldsPanel.add(tfAuthorName);
			fieldsPanel.add(new JLabel("Book Name : "));
			fieldsPanel.add(tfBookName);
			fieldsPanel.add(new JLabel("Publication Date : "));
			fieldsPanel.add(tfPublicationDate);
	
	        //Lay out the panel.
	        SpringUtilities.makeCompactGrid(fieldsPanel,
	                                        numPairs, 2, //rows, cols
	                                        6, 6,        //initX, initY
	                                        6, 6);       //xPad, yPad
			
		
		JPanel pagePanel = new JPanel(new SpringLayout());
			pagePanel.setBorder(new TitledBorder("Pages :"));
			lstPages = new JList(parent.getPageListModel());

			
			JPanel pbtPanel = new JPanel();
			pbtPanel.setLayout(new SpringLayout());
			btUP = new JButton("Move Up");
			btDown = new JButton("Move Down");
			pbtPanel.add(btUP);
			pbtPanel.add(btDown);			
	        SpringUtilities.makeCompactGrid(pbtPanel,
	                                        2, 1, //rows, cols
	                                        6, 6,        //initX, initY
	                                        6, 6);       //xPad, yPad
			
			//pagePanel.add(new JScrollPane(lstPages), BorderLayout.CENTER);
			//pagePanel.add(pbtPanel, BorderLayout.EAST);
			pagePanel.add(new JScrollPane(lstPages));
			pagePanel.add(pbtPanel);
	        SpringUtilities.makeCompactGrid(pagePanel,
	                                        1, 2, //rows, cols
	                                        6, 0,        //initX, initY
	                                        0, 6);       //xPad, yPad
	                                        
		JPanel btPanel = new JPanel();
			EmptyBorder emborder = new EmptyBorder(6,6,6,6);
			EtchedBorder etborder = new EtchedBorder();
			btPanel.setBorder(new CompoundBorder(etborder, emborder));
			btClear = new JButton("Clear");
			btSet = new JButton("Set");
			
			btPanel.setLayout(new BoxLayout(btPanel, BoxLayout.X_AXIS));
			btPanel.add(Box.createHorizontalGlue());
			btPanel.add(btClear);
			btPanel.add(Box.createRigidArea(new Dimension(6, 0)));
			btPanel.add(btSet);
			
		//add actionListenrs
		btClear.addActionListener(this);
		btSet.addActionListener(this);

		setLayout(new BorderLayout());
		add(fieldsPanel, BorderLayout.NORTH);
		add(pagePanel, BorderLayout.CENTER);
		add(btPanel, BorderLayout.SOUTH);
		addInternalFrameListener(this);
		pack();
		setVisible(true);
	}
	
	
	
	 public void  internalFrameActivated(InternalFrameEvent e) {} 
	 public void  internalFrameClosed(InternalFrameEvent e) {
	 	parent.getControl().setProjectObject(projectObject);
	 } 
	 public void  internalFrameClosing(InternalFrameEvent e)
	 {} 
	 public void  internalFrameDeactivated(InternalFrameEvent e) {} 
	 public void  internalFrameDeiconified(InternalFrameEvent e) {} 
	 public void  internalFrameIconified(InternalFrameEvent e) {} 
	 public void  internalFrameOpened(InternalFrameEvent e) {} 
	
	
}
