package epublisher;

import java.util.*;
import jigl.image.*;

public class PageObject implements java.io.Serializable
{
	private short[][] gimageData;
	private DocumentPanel documentPanel;
	
	public void setGImageData(short[][] gimageData)
	{
		this.gimageData=gimageData;
	}
	
	public void setDocumentPanel(DocumentPanel documentPanel)
	{
		this.documentPanel=documentPanel;
	}
	
	public short[][] getGImageData()
	{
		return gimageData;
		
	}
	
	public DocumentPanel getDocumentPanel()
	{
		return documentPanel;
	}
}
	