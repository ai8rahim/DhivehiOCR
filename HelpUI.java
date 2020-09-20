package epublisher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;



public class HelpUI extends JFrame
{
	public HelpUI()
	{
		super("Uer Manual");
		JEditorPane txtReport = new JEditorPane();
		txtReport.setEditable(false);

		String s = null;
		try {
			s = "file:"
				+ System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "User Manual.htm";
			java.net.URL reportURL = new java.net.URL(s);
			txtReport.setPage(reportURL);
		} catch (Exception etxt) {
			JOptionPane.showMessageDialog(null, etxt, "Error", JOptionPane.ERROR_MESSAGE);
		}	
		
		getContentPane().add(new JScrollPane(txtReport));
		setSize(500,500);
		setVisible(true);
	}
}