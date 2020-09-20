/*
 *main interface of the program
 */

package epublisher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;


import java.awt.image.*;
import java.awt.color.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;

import java.sql.*;

import epublisher.twain.EPublisherTwain;

import SK.gnome.twain.TwainException;
import SK.gnome.twain.TwainManager;
import SK.gnome.twain.TwainSource;


public class EPublisherUI extends JFrame implements ActionListener, ListSelectionListener
{
	private EPublisherControl control;
	private EPublisherSplashScreen splashScreen;
	
	public EPublisherUI()
	{
		splashScreen = new EPublisherSplashScreen(this);
		
		try
		{
			Thread.sleep(2000);
		}
		catch(Exception e)
		{			
		}
		
		splashScreen.dispose();
		
		constructGUI();
		control = new EPublisherControl(this);
	}
	
	//gui centric
	private void constructGUI()
	{
		//initialise
		getContentPane().setLayout(new BorderLayout());
		
		
		
		//page list
		pageListModel = new DefaultListModel();
		lstPages = new JList(pageListModel);
		lstPages.addListSelectionListener(this);
		lstPages.setBorder(new TitledBorder("Book Contents"));
		
		//split pane
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(new JScrollPane(lstPages));


		//tool bar
		toolBar = new JToolBar();
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
		toolBar.setFloatable(false);
			btNew = new JButton(new ImageIcon("icons\\new.png")); 
			btOpen = new JButton(new ImageIcon("icons\\open.png")); 
			btSave = new JButton(new ImageIcon("icons\\save.png")); 
			btLoad = new JButton(new ImageIcon("icons\\load.png"));
			btScan = new JButton(new ImageIcon("icons\\scan.png"));
			btPublish = new JButton(new ImageIcon("icons\\publish.png"));
			btOCRAuto = new JButton(new ImageIcon("icons\\ocr_auto.png"));
			btOCRStep = new JButton(new ImageIcon("icons\\ocr_step.png"));
			
			btSave.addActionListener(this);
			btOpen.addActionListener(this);
			btNew.addActionListener(this);
			btLoad.addActionListener(this);
			btOCRAuto.addActionListener(this);
			btOCRStep.addActionListener(this);
			btPublish.addActionListener(this);
			btScan.addActionListener(this);


			enableButtons(false);
			
			//tool tips
			btNew.setToolTipText("Create New Project");
			btOpen.setToolTipText("Open Existing Project");
			btSave.setToolTipText("Save Current Project"); 
			btLoad.setToolTipText("Load Image File");
			btScan.setToolTipText("Scan Document"); 
			btPublish.setToolTipText("Publish Project"); 
			btOCRAuto.setToolTipText("Perform OCR Automatically"); 
			btOCRStep.setToolTipText("Perform OCR Step by Step"); 
			
						
			toolBar.add(btNew);
			toolBar.add(btSave);
			toolBar.add(btOpen);
			toolBar.add(Box.createRigidArea(new Dimension(6,6)));
			toolBar.add(btLoad);
			toolBar.add(btScan);
			toolBar.add(Box.createRigidArea(new Dimension(6,6)));
			toolBar.add(btOCRAuto);
			toolBar.add(btOCRStep);
			toolBar.add(Box.createRigidArea(new Dimension(6,6)));
			toolBar.add(btPublish);

		//menu bar
		menuBar = new JMenuBar();
			fileMenu = new JMenu("File");
			fileMenu.setMnemonic('F');
				mnuNew = new JMenuItem("New");
				mnuNew.setMnemonic('N');
				mnuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
				mnuOpen = new JMenuItem("Open");
				mnuOpen.setMnemonic('O');
				mnuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
				mnuOpen.addActionListener(this);
				mnuSave = new JMenuItem("Save");
				mnuSave.setMnemonic('S');
				mnuAcquire = new JMenu("Acquire");
				mnuAcquire.setMnemonic('A');
					mnuLoad = new JMenuItem("Load");
					mnuLoad.setMnemonic('L');
					mnuLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
					mnuLoad.addActionListener(this);
					mnuScan = new JMenuItem("Scan");
					mnuScan.setMnemonic('S');
					
				mnuOCR = new JMenu("OCR");
				mnuOCR.setMnemonic('O');
					mnuStepByStep = new JMenuItem("Step by Step");
					mnuStepByStep.setMnemonic('S');
					mnuAutomatic = new JMenuItem("Automatic");
					mnuAutomatic.setMnemonic('A');
				mnuPublish = new JMenuItem("Publish");
				mnuPublish.setMnemonic('P');
				mnuExit = new JMenuItem("Exit");
				mnuExit.setMnemonic('x');
				mnuExit.addActionListener(this);
				mnuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			viewMenu = new JMenu("View");
			viewMenu.setMnemonic('V');
				mnuToolBar = new JCheckBoxMenuItem("Tool Bar", true);
				mnuBookContents = new JCheckBoxMenuItem("Book Contents", true);
				mnuPanels = new JMenu("Panels");
					mnuShowBoth = new JRadioButtonMenuItem("Show Both", true);
					mnuHideImagePanel = new JRadioButtonMenuItem("Hide Image Panel");
					mnuHideDocumentPanel = new JRadioButtonMenuItem("Hide Document Panel");
					ButtonGroup buttonGroup = new ButtonGroup();
					buttonGroup.add(mnuShowBoth);
					buttonGroup.add(mnuHideImagePanel);
					buttonGroup.add(mnuHideDocumentPanel);
				mnuProjectDetails = new JCheckBoxMenuItem("Project Details",false);
				mnuProjectDetails.addActionListener(this);
			helpMenu = new JMenu("Help");
			helpMenu.setMnemonic('H');
				mnuFormHelp = new JMenuItem("Form Help");
				mnuMainHelp = new JMenuItem("Main Help");
				mnuAbout = new JMenuItem("About");
				mnuAbout.addActionListener(this);


		
		desktop = new JDesktopPane();
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		splitPane.setRightComponent(desktop);




		//add to GUI
		menuBar.add(fileMenu);
			fileMenu.add(mnuNew);
			fileMenu.add(mnuSave);
			fileMenu.add(mnuOpen);
			fileMenu.addSeparator();
			fileMenu.add(mnuAcquire);
				mnuAcquire.add(mnuLoad);
				mnuAcquire.add(mnuScan);
			fileMenu.add(mnuOCR);
				mnuOCR.add(mnuStepByStep);
				mnuOCR.add(mnuAutomatic);
			fileMenu.add(mnuPublish);
			fileMenu.addSeparator();
			fileMenu.add(mnuExit);
		menuBar.add(viewMenu);
			//viewMenu.add(mnuToolBar);
			//viewMenu.add(mnuBookContents);
			//viewMenu.addSeparator();
			//viewMenu.add(mnuPanels);
			//	mnuPanels.add(mnuShowBoth);
			//	mnuPanels.add(mnuHideImagePanel);
			//	mnuPanels.add(mnuHideDocumentPanel);
			//viewMenu.addSeparator();
			viewMenu.add(mnuProjectDetails);
		menuBar.add(helpMenu);
			helpMenu.add(mnuFormHelp);
		//	helpMenu.add(mnuMainHelp);
		//	helpMenu.addSeparator();
			helpMenu.add(mnuAbout);
		setJMenuBar(menuBar);
		mnuFormHelp.addActionListener(this);


		getContentPane().add(splitPane, BorderLayout.CENTER);

		getContentPane().add(toolBar, BorderLayout.NORTH);


		Toolkit kit = Toolkit.getDefaultToolkit();		
		Dimension screenSize = kit.getScreenSize();		
		int screenHeight = screenSize.height;		
		int screenWidth = screenSize.width; 

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(screenWidth, screenHeight);
		setLocation(0,0);
		setTitle("Electronic Publisher");
		setVisible(true);
	}
	
	public EPublisherControl getControl()
	{
		return control;
	}
	
	public void scanComplete()
	{
		control.scanComplete();
	}
	
	public void valueChanged(ListSelectionEvent e) 
	{
	    if (e.getValueIsAdjusting() == false) 
	    {
	    	int index = lstPages.getSelectedIndex();
	        if (index != -1) 
	        {
	        	((PageContainer)pageListModel.elementAt(index)).moveToFront();
	        }
	    }
	}
	
	public void enableButtons(boolean val)
	{
		btOCRStep.setEnabled(val);
		btOCRAuto.setEnabled(val);
		btSave.setEnabled(val);
		btPublish.setEnabled(val);
		//btScan.setEnabled(val);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==mnuExit) 
			System.exit(0);
		if (e.getSource()==mnuAbout) 
		{
			JOptionPane.showMessageDialog(this,
						    "About",
						    "EPublisher v 1.0\n" +
						    "Developed by:-\n"+
						    "Ahmed Ibrahim",
						    JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource()==mnuLoad || e.getSource()==btLoad)
			control.loadImage();		
		else if (e.getSource() == btOCRAuto || e.getSource() == mnuOCR)
			control.performOCR();	
		else if (e.getSource() == btOCRStep || e.getSource() == mnuStepByStep)
			control.showOCRWizard();			
		else if (e.getSource()==mnuProjectDetails)
			control.showProjectDetails();
		else if (e.getSource()==btSave || e.getSource()==mnuSave)
			control.saveProject();
		else if (e.getSource()==btOpen || e.getSource()==mnuOpen )
			control.openProject();
		else if (e.getSource()==btPublish || e.getSource()==mnuPublish)
			control.publishProject();
		else if (e.getSource()==btScan || e.getSource()==mnuScan)
		{
			control.scanImage();
		}
		else if (e.getSource()==mnuFormHelp)
			new HelpUI();
	}
	

	
	public DefaultListModel getPageListModel()
	{
		return pageListModel;
	}
	
	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		
        JDialog.setDefaultLookAndFeelDecorated(true);
                
		new EPublisherUI();
	}
    
   // private ProjectObject projectObject;

	// Variables declaration
    private JMenuBar menuBar;
    	private JMenu fileMenu;
	   		private JMenuItem mnuNew, mnuOpen, mnuSave;
	   		private JMenu mnuAcquire;
	   			private JMenuItem mnuLoad, mnuScan;
	   		private JMenu mnuOCR;
	   			private JMenuItem mnuStepByStep, mnuAutomatic;
	   		private JMenuItem mnuPublish;
	   		private JMenuItem mnuExit;
	   	private JMenu viewMenu;
	   		private JCheckBoxMenuItem mnuToolBar, mnuBookContents;
	   		private JMenu mnuPanels;
		   		private JRadioButtonMenuItem mnuShowBoth, mnuHideImagePanel, mnuHideDocumentPanel;
	   		private JCheckBoxMenuItem mnuProjectDetails;
	   	private JMenu helpMenu;
	   		private JMenuItem mnuFormHelp, mnuMainHelp, mnuAbout;

	private JToolBar toolBar;
	private JButton btNew, btOpen, btSave, btLoad, btOCRAuto, btOCRStep, btScan, btPublish, btExit;

   private JSplitPane splitPane;
   
   private JDesktopPane desktop;
  
	private JList lstPages;
	private DefaultListModel pageListModel;


	class EPublisherControl
	{
		private EPublisherUI epublisherUI;
		private ProjectObject projectObject;
		
		private String scanPath = "scan.jpg";
		
		public EPublisherControl(EPublisherUI epublisherUI)
	{
		this.epublisherUI=epublisherUI;
	}				
		
		public void loadImage()
		{
				
				//open the file
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				try
				{
					chooser.setCurrentDirectory(new java.io.File("./images"));
					int ii = chooser.showOpenDialog(EPublisherUI.this);
					File[] files = chooser.getSelectedFiles();
					
					for (int i=0; i<files.length; i++)
					{	
						String fileName = files[i].getPath();
						
						if (projectObject == null) projectObject = new ProjectObject();
						
						PageContainer page = new PageContainer(fileName, projectObject);
						desktop.add(page);
						pageListModel.addElement(page);
						enableButtons(true);
					}
					
				}
				catch(Exception exp)
				{
					//null pointer when no files are selected
					exp.printStackTrace();
				}
				
		}
		
		public void scanImage()
		{
			//System.out.println("adasdadad");
			new EPublisherTwain(epublisherUI, scanPath);
		}
		
		public void scanComplete()
		{
			if (projectObject == null) projectObject = new ProjectObject();
			
			PageContainer page = new PageContainer(scanPath, projectObject);
			desktop.add(page);
			pageListModel.addElement(page);
			enableButtons(true);		
			
		}
		
		public void performOCR()
		{
			JInternalFrame[] pages = desktop.getAllFrames();
			for (int i = 0; i < pages.length; i++)
			{
				if (pages[i].isSelected()) 
				{
					ImagePanel imagePanel = ((PageContainer)pages[i]).getImagePanel();
					
					if (imagePanel.isOCRDone())
					{
						JOptionPane.showMessageDialog(epublisherUI,
						    "OCR has been performed on this page.\n" +
						    "Please load or scan this image if you would like to perform OCR on this again.",
						    "OCR Performed",
						    JOptionPane.INFORMATION_MESSAGE);
						    return;
					}
					
					IPProgresser ipProgresser = new IPProgresser(epublisherUI);
					
					DocumentPanel docPanel = ((PageContainer)pages[i]).getDocumentPanel();
					
					imagePanel.ocr(ipProgresser, docPanel);
					
					//Vector characterSet = ((PageContainer)pages[i]).getImagePanel().getCharacterSet();
					//((PageContainer)pages[i]).getDocumentPanel().setCharacterSet(characterSet);
				}
			}
		}
		
		public void showOCRWizard()
		{	
			JInternalFrame[] pages = desktop.getAllFrames();
			for (int i = 0; i < pages.length; i++)
			{
				if (pages[i].isSelected()) 
				{
					ImagePanel imagePanel = ((PageContainer)pages[i]).getImagePanel();
					
					OCRWizardForm ocrWizardForm = new OCRWizardForm(epublisherUI, imagePanel);
				}
			}	
			
		}
		
		//called when ocr wizard is complete
		public void ocrWizardComplete(ImagePanel imagePanel)
		{
			btOCRStep.setEnabled(true);	
			JInternalFrame[] pages = desktop.getAllFrames();
			for (int i = 0; i < pages.length; i++)
			{
				if (pages[i].isSelected()) 
				{
					((PageContainer)pages[i]).setImagePanel(imagePanel);
				}
			}
		}
		
		
		
		public void publishProject()
		{
			//validation
			System.out.println(projectObject);
			if(projectObject.getProjectName().equals("") ||
				projectObject.getBookName().equals("") ||
				projectObject.getAuthorName().equals(""))			
			{				    
				int n = JOptionPane.showConfirmDialog(
				    epublisherUI,
				   "You haven't entered the project details.\n" +
				   "\n In order to publish, all the necessary details in the project details must be entered.\n" +
				   "\n Press OK to enter the project details.",
				    "Project Details Missing",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.INFORMATION_MESSAGE);
				
				if (n == JOptionPane.YES_OPTION)
				{
					showProjectDetails();
				}
				else
					return;
			}
			
			
			
			try
			{
				
				// Load the JDBC driver
				String driverName = "com.mysql.jdbc.Driver";
				Class.forName(driverName);
				
				
				
			String hostname = "localhost";
			String databasename = "epublisher";
			String username = "admin";
			String password = "admin";
			
			/*Connection con = DriverManager.getConnection(
						"jdbc:mysql://" + hostname + 
						"/" + databasename + 
						"?user=" + username);*/
			Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost/epublisher?user=root&password=root");
			
			//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			//Connection con = DriverManager.getConnection("jdbc:odbc:EPublisher");
		
				//PREAPRE FOR PUBLISHING
				Vector pageObjects = createSerializedPageObjects();
				
				Statement stmt = con.createStatement();
				
				//for (int i = 0; i<pageObjects.size(); i++)
				{
					stmt.executeUpdate(
						"INSERT INTO books " +
						"(book_name, author_name, publication_date) VALUES ('" +
						projectObject.getBookName() + "','" +
						 projectObject.getAuthorName() + "','" + new java.sql.Timestamp((new java.util.Date().getTime())) + "');"
						);
				}
				
				ResultSet rs = stmt.executeQuery(
				"SELECT book_id from books ORDER BY book_id DESC;");
				
				if (rs.next())
				{
					int bookid = rs.getInt("book_id");
					
					for (int i = 0; i<pageObjects.size(); i++)
						stmt.executeUpdate(
							"INSERT INTO pages " +
							"(book_id, ocr_text) VALUES (" +
								bookid + ",'" +
							 	((PageObject)pageObjects.elementAt(i)).getDocumentPanel().getPlainText() + "');"
							);
				}
				
				rs.close();
				
				stmt.close();
				con.close();
				
				JOptionPane.showMessageDialog(epublisherUI,
						    "Publish Project",
						    "The project has been published successfully.",
						    JOptionPane.INFORMATION_MESSAGE);
	
	        } 
	        catch(ClassNotFoundException cexp)
	        {
	        	cexp.printStackTrace();
	        }
	        catch (SQLException ex) 
	        {
	        	/*JOptionPane.showMessageDialog(epublisherUI,
						    "PUblish Project",
						    "SQLException: " + ex.getMessage() +
						    "\nSQLState: " + ex.getSQLState()+
						    "\nVendorError: " + ex.getErrorCode()
						    ,
						    JOptionPane.INFORMATION_MESSAGE);*/
	            // handle any errors 
	            System.out.println("SQLException: " + ex.getMessage()); 
	            System.out.println("SQLState: " + ex.getSQLState()); 
	            System.out.println("VendorError: " + ex.getErrorCode()); 
	            ex.printStackTrace();
	        }
		}
		
		public void openProject()
		{
				JFileChooser chooser = new JFileChooser();
			try
			{
				
				int ii = chooser.showOpenDialog(null);
				
				if (ii!=JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				
				File file = chooser.getSelectedFile();
				
				// Read from disk using FileInputStream.
				FileInputStream f_in = new FileInputStream (file.getPath());
				
				// Read object using ObjectInputStream.
				ObjectInputStream obj_in = new ObjectInputStream (f_in);
				
				// Read an object.
				Object obj = obj_in.readObject();
				System.out.println(obj.getClass());
				// Is the object that you read in, say, an instance
				// of the Vector class?
				if (obj instanceof ProjectObject)
				{
				  // Cast object to a Vector
				  projectObject = (ProjectObject) obj;
				  Vector pageObjects = projectObject.getPageObjects();
				  System.out.println(pageObjects.size());
				  for (int i=0; i<pageObjects.size(); i++)
				  {
				  		PageObject pageObject = (PageObject)pageObjects.elementAt(i);
						PageContainer page = new PageContainer(projectObject, pageObject);
						desktop.add(page);
						pageListModel.addElement(page);
						enableButtons(true);
				  }
				}
				else
				{
					
			JOptionPane.showMessageDialog(epublisherUI,
						    "Open Project",
						    "This is not a valid file format.",
						    JOptionPane.WARNING_MESSAGE);
				//	System.out.println("not valid file format");
				}
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(epublisherUI,
						    "Open Project",
						    "The file is not a valid file format",
						    JOptionPane.WARNING_MESSAGE);
				//e.printStackTrace();
			}
			
		}
		
		public void saveProject()
		{
			
			JFileChooser chooser = new JFileChooser();
			File file=null;
			try
			{
				
				int ii = chooser.showSaveDialog(null);
				
				if (ii!=JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				
				file = chooser.getSelectedFile();
			}
			catch(Exception ex)
			{
				
			}
			
			
			//PREPARE FOR SAVE
			Vector cpo = createSerializedPageObjects();
			if (cpo == null) return;
			
			projectObject.setPageObjects(cpo);
			
			
			
			//saving
			try {
				FileOutputStream stream = new FileOutputStream(file.getPath() + ".ocr");
				ObjectOutputStream out = new ObjectOutputStream(stream);
				out.writeObject(projectObject);
				out.close();
				//System.out.println("Saved");
			JOptionPane.showMessageDialog(epublisherUI,
						    "Save Project",
						    "The project has been saved to the selected location.",
							    JOptionPane.INFORMATION_MESSAGE);
				
			}
			catch (Exception excp) {
				excp.printStackTrace();
			}
		}
		
		private Vector createSerializedPageObjects()
		{
			
			//first set the pageobjects in the projectobject
			PageContainer[] pc = projectObject.getPages();
			
			if (pc==null)
			{
				//show some message
				return null;
			}
			
			Vector pageObjects = new Vector();
			for (int i = 0; i<pc.length; i++)
			{
				PageObject pageObject = new PageObject();
				pageObject.setGImageData(pc[i].getImagePanel().getGImageData());
				pageObject.setDocumentPanel(pc[i].getDocumentPanel());
				pageObjects.add(pageObject);
			}
			
			return pageObjects;
		}
		
		public void showProjectDetails()
		{
			ProjectForm projectForm;
			if (projectObject == null)
			{
				projectForm = new ProjectForm(epublisherUI);		
			}
			else
			{
				projectForm = new ProjectForm(epublisherUI, projectObject); 
			}
			desktop.add(projectForm);	
			projectForm.moveToFront();
			mnuProjectDetails.setState(true);
			mnuProjectDetails.setEnabled(false);
		}
		
		public void createProject()
		{
			desktop.add(new ProjectForm(epublisherUI));
		}
	
		public void setProjectObject(ProjectObject po)
		{
			this.projectObject = po;
			mnuProjectDetails.setState(false);
			mnuProjectDetails.setEnabled(true);
		}
		
		public DefaultListModel getPageListModel()
		{
			return pageListModel;
		}
	}



	
	class EPublisherSplashScreen extends JWindow
	{
		private SplashPanel panel;
		
		public EPublisherSplashScreen(EPublisherUI parent)
		{
			//super(parent,  true);
			panel = new SplashPanel();
			add(panel, BorderLayout.CENTER);
			
			
			Toolkit kit = Toolkit.getDefaultToolkit();
			
			Dimension screenSize = kit.getScreenSize();
			
			int screenHeight = screenSize.height;
			
			int screenWidth = screenSize.width; 
			
			// center frame in screen
			
			int width = 426;
			int height = 430;
			
			int x = (screenWidth/2) - width/2;
			int y = (screenHeight/2) - height/2;
			
			setLocation(x, y); 
			
			setSize(new Dimension(426, 430));
			setVisible(true);
		}	
		
			
	}
	 class SplashPanel extends JPanel
	 {
	 	java.awt.Image img;		
		
		public SplashPanel()
		{
			img = new ImageIcon("images\\splash_screen.png").getImage();
			setBorder(new LineBorder(Color.black));
		}
	 	public void paintComponent(Graphics g)
	 	{
	 		super.paintComponent(g);
	 		setBackground(Color.white);
	 		
	 		int width = img.getWidth(this);
	 		int height = img.getHeight(this);
			g.drawImage(img, 0,0,width, height, this);	 		
	 	}
	 	
	 
	 }
}
