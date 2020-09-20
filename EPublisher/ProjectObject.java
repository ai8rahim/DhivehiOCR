package epublisher;

import java.util.*;
import jigl.image.*;

public class ProjectObject implements java.io.Serializable
{
	private String projectName;
	private String bookName;
	private String authorName;
	private Calendar publicationDate;
	transient private PageContainer[] pages;
	
	//for serialization purpose
	private Vector pageObjects; //contains the page objects
	
	public ProjectObject()
	{
		projectName="";
		bookName="";
		authorName="";
		publicationDate=null;
	}
	
	public void setProjectName(String projectName)
	{
		this.projectName=projectName;
	}
	
	public void setBookName(String bookName)
	{
		this.bookName = bookName;
	}
	
	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}
	
	public void addPage(PageContainer page)
	{
		if(pages == null)
		{
			pages = new PageContainer[1];
			pages[0] = page;
		}
		else
		{
			PageContainer[] temp = new PageContainer[pages.length+1];
			System.arraycopy(pages, 0, temp, 0, pages.length);
			temp[temp.length-1] = page;
			pages = temp;
		}
	}
	
	public void setPublicationDate(Calendar publicationDate)
	{
		this.publicationDate=publicationDate;
	}
	
	public void setPages(PageContainer[] pages)
	{
		this.pages=pages;
	}
	
	public String getBookName()
	{
		return bookName;
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	public String getAuthorName()
	{
		return authorName;
	}
	
	public Calendar getPublicationDate()
	{
		return publicationDate;
	}
	
	public PageContainer[] getPages()
	{
		return pages;
	}
	
	//called while saving the project for
	//object serialization purposes
	public void setPageObjects(Vector pageObjects)
	{
		this.pageObjects=pageObjects;
	}
	
	public Vector getPageObjects()
	{
		return pageObjects;
	}
}
