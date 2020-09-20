package epublisher.web;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import javax.swing.event.*;

public class EPublisherApplet extends JApplet implements ActionListener, ListSelectionListener
{	
	JTextField tfBookName, tfAuthorName;
	JButton btSearch, btListAll;
	
	JButton btMainPage, btBookView;
	
	JTable table;
	DefaultTableModel tableModel;

	private JPanel cards;
	final static String MAINPAGE = "JPanel mainpage";
	final static String BOOKVIEW = "JPanel bookview";
		
	private JButton btNextPage, btPreviousPage;
	private JTextArea taText;
	private JList lstPages;
	private DefaultListModel lstModel;
	
	private int selectedBookID;
	
	private String imagePath;
	
	public void init()
	{
		//imagePath = getParameter("ImagePath");
		JPanel mainPanel = createMainPanel();
		
		//btMainPage = new JButton("Main Page",new ImageIcon("icons\\main_page.png"));
		//btBookView = new JButton("Book View",new ImageIcon("icons\\book_view.png"));
		
		btMainPage = new JButton("Main Page");//,new ImageIcon("icons\\main_page.png"));
		btBookView = new JButton("Book View");//,new ImageIcon("icons\\book_view.png"));
		
		btMainPage.addActionListener(this);
		btBookView.addActionListener(this);
		
		JPanel navigationPanel = new JPanel();
		navigationPanel.add(btMainPage);
		navigationPanel.add(btBookView);
		
		JPanel bookPanel = createBookPanel();
		
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(mainPanel, MAINPAGE);
		cards.add(bookPanel, BOOKVIEW);
	
	
		add(cards, BorderLayout.CENTER);
		add(navigationPanel, BorderLayout.SOUTH);
	}
	
	private JPanel createBookPanel()
	{
		//btNextPage = new JButton("Next",new ImageIcon("icons\\next.png"));
		//btPreviousPage = new JButton("Previous",new ImageIcon("icons\\previous.png"));
		
		btNextPage = new JButton("Next");//,new ImageIcon("icons\\next.png"));
		btPreviousPage = new JButton("Previous");//,new ImageIcon("icons\\previous.png"));
		
		btNextPage.addActionListener(this);
		btPreviousPage.addActionListener(this);
		
		JPanel txtPanel = new JPanel(new BorderLayout());
			taText = new JTextArea();
			taText.setFont(new Font("A_RANDHOO", Font.PLAIN, 24));
			txtPanel.add(new JScrollPane(taText));
		
		JPanel btPanel = new JPanel();
			btPanel.add(btPreviousPage);
			btPanel.add(btNextPage);
		
		JPanel lstPanel = new JPanel(new BorderLayout());
			lstModel = new DefaultListModel();
			lstPages = new JList(lstModel);
			lstPages.addListSelectionListener(this);
			lstPages.setBorder(new TitledBorder("Book Contents"));
			lstPanel.add(new JScrollPane(lstPages));
		
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(btPanel, BorderLayout.NORTH);
		panel.add(txtPanel, BorderLayout.CENTER);
		panel.add(lstPanel, BorderLayout.WEST);
		
		selectedBookID=-1;
		
		return panel;
	}
    
    private void loadBookData()
    {
		lstModel.clear();
		taText.setText("");
		try
		{
			
			// Load the JDBC driver
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			
			
			
			String hostname = "localhost";
			String databasename = "epublisher";
			String username = "admin";
			String password = "admin";
			
		/*	Connection con = DriverManager.getConnection(
						"jdbc:mysql://" + hostname + 
						"/" + databasename + 
						"?user=" + username +
						"&password=" + password);*/
		/*	Connection con = DriverManager.getConnection(
						"jdbc:mysql://" + hostname + 
						"/" + databasename + 
						"?user=" + username);*/
			Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost/epublisher?user=root&password=root");
			
			
			Statement stmt = con.createStatement();

			
			ResultSet rs = stmt.executeQuery(
			"SELECT * FROM Pages WHERE book_id=" + selectedBookID);
			
			int count=0;
			while (rs.next())
			{
				lstModel.addElement(new Page(rs.getInt("page_id"), rs.getString("ocr_text"), ++count));
			}
			
			rs.close();
			
			stmt.close();
			con.close();

        } 
        catch(ClassNotFoundException cexp)
        {
        	cexp.printStackTrace();
        }
        catch (SQLException ex) 
        {
            // handle any errors 
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            ex.printStackTrace();
        }
    }
    
    //MAINPANEL'S
    private JPanel createMainPanel()
    {
		
    	JPanel searchPanel = new JPanel();
    	searchPanel.setBorder(new EtchedBorder());
    		tfBookName = new JTextField(10);
    		tfAuthorName = new JTextField(10);
    		
    		//btSearch = new JButton("Search",new ImageIcon("icons\\search.png"));
    		//btListAll = new JButton("List All",new ImageIcon("icons\\list_all.png"));
    		
    		btSearch = new JButton("Search");//,new ImageIcon("icons\\search.png"));
    		btListAll = new JButton("List All");//,new ImageIcon("icons\\list_all.png"));
    		
    		btSearch.addActionListener(this);
    		btListAll.addActionListener(this);
    		
    		searchPanel.add(new JLabel("Book Name : "));
    		searchPanel.add(tfBookName);
    		searchPanel.add(new JLabel("Author Name : "));
    		searchPanel.add(tfAuthorName);
    		searchPanel.add(btSearch);
    		searchPanel.add(btListAll);
    		
    	JPanel tablePanel = new JPanel(new BorderLayout());
    		createTable();
    		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
    	
    	JPanel panel = new JPanel();
    	panel.setLayout(new BorderLayout());
    	panel.add(searchPanel, BorderLayout.NORTH);   
    	panel.add(tablePanel, BorderLayout.CENTER);   
    	
    	return panel;
    }
    
    private void createTable()
    {
		Vector columnNames = new Vector();
			columnNames.add("Book ID");
			columnNames.add("Book Name");
			columnNames.add("Author Name");
			columnNames.add("Publication Date");


		tableModel = new MyTableModel(columnNames, 0);

		table = new JTable(tableModel);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
    }
    
    private void clearTable()
    {
    	int r = tableModel.getRowCount();
    	for (int i = 0; i< r; i++)
    	{
    		tableModel.removeRow(0);
    	}
    }
    
	private void listAll()
	{
		System.out.println("asd");
		clearTable();
		try
		{
			
			// Load the JDBC driver
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			
			
			
			String hostname = "localhost";
			String databasename = "epublisher";
			String username = "admin";
			String password = "admin";
			
		/*	Connection con = DriverManager.getConnection(
						"jdbc:mysql://" + hostname + 
						"/" + databasename + 
						"?user=" + username);*/
			Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost/epublisher?user=root&password=root");
		/*Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost/epublisher?user=root&password=root");*/

			Statement stmt = con.createStatement();

			
			ResultSet rs = stmt.executeQuery(
			"SELECT * FROM Books");
			
			while (rs.next())
			{
				Vector data = new Vector();
				data.add(rs.getInt("book_id"));
				data.add(rs.getString("book_name"));
				data.add(rs.getString("author_name"));
				data.add(rs.getTimestamp("publication_date"));
				tableModel.addRow(data);
			}
			
			rs.close();
			
			stmt.close();
			con.close();

        } 
        catch(ClassNotFoundException cexp)
        {
        	cexp.printStackTrace();
        }
        catch (SQLException ex) 
        {
            // handle any errors 
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            ex.printStackTrace();
        }
	}
    
    private void search(String book, String author)
    {
    	String sql = "SELECT * FROM Books WHERE ";
    	if (book.equals("") && author.equals(""))
    	{
    		JOptionPane.showMessageDialog(null,
				    "You must specify either Book or Author Name to be serched.",
				    "Insufficient Information",
				    JOptionPane.WARNING_MESSAGE);
			return;
    	}
    	else if (book.equals("") && !author.equals(""))
    	{
    		sql = sql + "author_name = '" + author +"';";
    	}
    	else if (author.equals("") && !book.equals(""))
    	{
    		sql = sql + "book_name = '" + book + "';";
    	}
    	else if (!author.equals("") && !book.equals(""))
    	{
    		sql = sql + "author_name = '" + author +"' AND ";
    		sql = sql + "book_name = '" + book + "';";
    	}
    	
		try
		{
			
			// Load the JDBC driver
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			
			
			
			String hostname = "localhost";
			String databasename = "epublisher";
			String username = "admin";
			
		/*	Connection con = DriverManager.getConnection(
						"jdbc:mysql://" + hostname + 
						"/" + databasename + 
						"?user=" + username);*/
			Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost/epublisher?user=root&password=root");
			//Connection con = DriverManager.getConnection(
					//	"jdbc:mysql://localhost/epublisher?user=root&password=root");
	

			
			
			Statement stmt = con.createStatement();

			
			ResultSet rs = stmt.executeQuery(sql);
			
			clearTable();
			
			
			int rowCount=0;
			while (rs.next())
			{
				rowCount++;
				Vector data = new Vector();
				data.add(new Integer(rs.getInt("book_id")));
				data.add(rs.getString("book_name"));
				data.add(rs.getString("author_name"));
				data.add(rs.getDate("publication_date"));
				tableModel.addRow(data);
			}
			
			if (rowCount == 0)
			JOptionPane.showMessageDialog(null,
				    "No matches were found for the search.",
				    "No Match",
				    JOptionPane.INFORMATION_MESSAGE);
			
			rs.close();
			
			stmt.close();
			con.close();

        } 
        catch(ClassNotFoundException cexp)
        {
        	cexp.printStackTrace();
        }
        catch (SQLException ex) 
        {
            ex.printStackTrace();
        }    	
    	
    }
    
	public void valueChanged(ListSelectionEvent e) 
	{
	    if (e.getValueIsAdjusting() == true) return;
	    
	    if(e.getSource()==lstPages)
	    {
	    	int index = lstPages.getSelectedIndex();
	        if (index != -1) 
	        {
	        	taText.setText(((Page)lstModel.elementAt(index)).getOCRText());
	        }
	    }
	    else
	    {	    
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        if (!lsm.isSelectionEmpty())
	        {
	            int selectedRow = lsm.getMinSelectionIndex();
	            selectedBookID = ((Integer)tableModel.getValueAt(selectedRow, 0)).intValue();
	            System.out.println(selectedBookID);
	        }
	    
	    }
	    
	    
	    /*
	    {
	    	int index = lstPages.getSelectedIndex();
	        if (index != -1) 
	        {
	        	//((PageContainer)pageListModel.elementAt(index)).moveToFront();
	        }
	    }*/
	}
	
    public void actionPerformed(ActionEvent e)
    {
    	if (e.getSource()==btListAll)
    	{
    		listAll();
    	}
    	else if(e.getSource()==btSearch)
    	{
    		search(tfBookName.getText(), tfAuthorName.getText());
    	}
    	else if(e.getSource()==btMainPage)
    	{
		    CardLayout cl = (CardLayout)(cards.getLayout());
		    cl.show(cards, MAINPAGE);
    	}
    	else if (e.getSource()==btBookView)
    	{
    		loadBookData();
		    CardLayout cl = (CardLayout)(cards.getLayout());
		    cl.show(cards, BOOKVIEW);
    	}
    }
    
    public void start()
    {
    } 
    public void stop() 
    {
    }
    public void destroy()
    {
    } 
    
	class MyTableModel extends DefaultTableModel 
	{
	
		public MyTableModel(Vector v, int i)
		{
			super(v, i);
		}
	    /*public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }*/
	
	    public boolean isCellEditable(int row, int col) {
	        //if (col == 1) {
	         //   return true;
	        //} else {
	            return false;
	        //}
	    }
	}
	
	class Page
	{
		private String ocrText;
		private int id;
		private int count;
		
		public Page(int id, String ocrText, int count)
		{
			this.count=count;
			this.id=id;
			this.ocrText=ocrText;
		}
		
		public String getOCRText()
		{
			return ocrText;
		}
		
		public String toString()
		{
			return "Page " + count;
		}
	}
}

