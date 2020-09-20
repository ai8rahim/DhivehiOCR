package epublisher;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

class DocumentPanel extends JPanel
{
	JTextArea taText;
	
	private Vector characterSet;
	
	public DocumentPanel()
	{
		setLayout(new BorderLayout());
		taText = new JTextArea();
		taText.setFont(new Font("A_RANDHOO", Font.PLAIN, 24));
		
		add(new JScrollPane(taText), BorderLayout.CENTER);
	}
	
	public String getPlainText()
	{
		return taText.getText();
	}
	
	
	
	public void setCharacterSet(Vector characterSet, IPProgresser ipProgresser)
	{	
		JProgressBar progress = ipProgresser.getDocBar();	
		progress.setIndeterminate(false);	
		int progVal=0;
		progress.setMaximum(characterSet.size()-1);

		this.characterSet=characterSet;
		
		int prevY=-1; //storeds the previous y coordinate of the co
		Rectangle prevCB=null; //previous character's boundary
		Rectangle currCB=null; //current character's boundary
		
		for (int i = 0; i < characterSet.size(); i++)
		{
			progress.setValue(i);
			
			CharacterObject co = (CharacterObject)characterSet.elementAt(i);
			
			//check if the character has been correctly recognised
			if (co.getCVCount() <=2)
			{
				//cater for new lines
				if (prevY==-1) prevY = co.getY1();
				if (prevY<co.getY1()) 
				{
					taText.append("\n");
					prevCB = null;
				}
				prevY=co.getY1();
								
				//cater for whitespaces
				if (prevCB == null) prevCB = co.getBoundaryRectangle();
				currCB = co.getBoundaryRectangle();
				double pcDistance = currCB.getX() - prevCB.getX();
				if ((pcDistance/prevCB.getWidth())>prevCB.getWidth()/2)
				{
					int whitespaces = (int)Math.round(pcDistance/CharacterObject.AVG_WIDTH);
					for (int wi=0; wi<whitespaces; wi++) taText.append(" ");
				}
				prevCB = currCB;
				
				//check if a vowel exists
				if (co.getVowelBoundary()!=null)
				{
					taText.append(co.getVowel());
				}
				taText.append(co.getConsonant());
			}
		}			
	}
}
	