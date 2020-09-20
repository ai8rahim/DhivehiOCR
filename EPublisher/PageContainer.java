package epublisher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;

class PageContainer extends JInternalFrame implements Runnable, InternalFrameListener
{
	static int PAGE_COUNT=0;
	String pageName;
	
	private JSplitPane splitPane;
	private JScrollPane imageScrollPane, documentScrollPane;
	
	private ImagePanel imagePanel;
	private DocumentPanel documentPanel;
	String imgFileName; //image file name
	private jigl.image.GrayImage scannedImage;
	
	//for progessbar
	Thread thread;
	Progresser progressBar;
	
	ProjectObject po;	//the parent--to set the pages in po

	public PageContainer(String imgFileName, ProjectObject po)
	{
		super("", true, true, true, true);
		this.addInternalFrameListener(this);
		
		this.imgFileName=imgFileName;
		this.po=po;
		
		PageContainer.PAGE_COUNT++;
		pageName = "Page " + PageContainer.PAGE_COUNT;
		this.setTitle(pageName);
		
		//initialise
		setLayout(new BorderLayout());
		
		thread = new Thread (this);
			progressBar = new Progresser(null, "Loading Image", true, thread);
		thread.start();
	}

	
	public PageContainer(ProjectObject po, PageObject pgo)
	{
		super("", true, true, true, true);
		this.addInternalFrameListener(this);
		
		this.imgFileName=imgFileName;
		this.po=po;
		
		PageContainer.PAGE_COUNT++;
		pageName = "Page " + PageContainer.PAGE_COUNT;
		this.setTitle(pageName);
		
		//initialise
		setLayout(new BorderLayout());
		

		imagePanel = new ImagePanel(pgo);

		documentPanel = pgo.getDocumentPanel();

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setLeftComponent(imagePanel);
		splitPane.setRightComponent(documentPanel);
		
		//splitPane.setDividerLocation(900);

		//add to gui
		add(splitPane, BorderLayout.CENTER);
		int w = 500;
		int h = 500;
		setSize(w,h);
		setVisible(true);
			
	}
	
	public void run()
	{
		//if (scannedImage==null)
			imagePanel = new ImagePanel(imgFileName, progressBar);
		//else
		//	imagePanel = new ImagePanel(scannedImage, progressBar);

		documentPanel = new DocumentPanel();

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setLeftComponent(imagePanel);
		splitPane.setRightComponent(documentPanel);
		
		//splitPane.setDividerLocation(900);

		//add to gui
		add(splitPane, BorderLayout.CENTER);
		int w = 800;
		int h = 600;
		setSize(w,h);
		setVisible(true);
	}
	
	public String getPageName()
	{
		return pageName;
	}
	
	public String toString()
	{
		return pageName;
	}
	
	public ImagePanel getImagePanel()
	{
		return imagePanel;
	}
	
	//this is called from the ocr wizard
	public void setImagePanel(ImagePanel imagePanel)
	{
		this.imagePanel = imagePanel;
		splitPane.setLeftComponent(imagePanel);
		
	}
	
	public DocumentPanel getDocumentPanel()
	{
		return documentPanel;
	}

	public void internalFrameClosing(InternalFrameEvent e) {
    }

    public void internalFrameClosed(InternalFrameEvent e) {
    }

    public void internalFrameOpened(InternalFrameEvent e) {
    	po.addPage(this);
    }

    public void internalFrameIconified(InternalFrameEvent e) {
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    public void internalFrameActivated(InternalFrameEvent e) {
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
    }	
}


	