package epublisher;

import jigl.gui.*;
import java.awt.*;
import jigl.image.*;
import java.util.*;

import javax.swing.*;
import java.awt.event.*;

public class JCustomImageCanvas extends JImageCanvas implements MouseMotionListener, Scrollable
{
	boolean isLineBreaks=false;
	int[] lineBreaks;
	
	boolean isCharacterBreaks=false;
	int[][] characterBreaks;
	
	boolean isCharacterSet=false;
	Vector characterSet;
	
	private JScrollPane scroll;
	private int maxUnitIncrement = 1;
	
	public JCustomImageCanvas(GrayImage image, JScrollPane scroll) throws ImageNotSupportedException
	{
		super(image);
		this.scroll = scroll;
		addMouseMotionListener(this);
	}

	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if (isLineBreaks)
		{
			g.setColor (Color.red);
			for (int i =0; i< lineBreaks.length; i++)
			{
			//	System.out.println(lineBreaks[i]);
				g.drawLine(0,lineBreaks[i], jimage.X(), lineBreaks[i]);
			}			
		}
		
		if (isCharacterBreaks)
		{
			g.setColor(Color.yellow);
			for (int n=0; n<characterBreaks.length; n++)
			{
				for (int i=0; i<characterBreaks[n].length;i++)
				{
					//System.out.print(characterbreaks[n][i]);
					g.drawLine(characterBreaks[n][i], lineBreaks[n*2], characterBreaks[n][i], lineBreaks[n*2+1]);
				}
				//System.out.print("\n");
			}
		}
		
		if (isCharacterSet)
		{
			for(int i=0; i<characterSet.size(); i++)
			{
				CharacterObject co = (CharacterObject)characterSet.elementAt(i);
				if (co.getCVCount() <=2)
				{
					g.setColor(Color.gray);
					g2.draw(co.getBoundaryRectangle());
		
					
					//DRAW THE CONSONANT AND VOWEL
					/*Vector cvSet = co.getCVSet();
					for (int j=0; j<cvSet.size();j++)
					{
						g2.draw(((CharacterObject.CVBoundary)cvSet.elementAt(j)).getBoundary());
					}*/
					
					//draw the actual consonant and the boundary
					g.setColor(Color.green);
					g2.draw(co.getConsonantBoundary());
					g.setColor(Color.yellow);
					if (co.getVowelBoundary()!=null)
					{
						 g2.draw(co.getVowelBoundary());
					}
				}
				else
				{
					g.setColor(Color.red);
					g2.draw(co.getBoundaryRectangle());
	
				}
				

			}
			
		}
	}
	
	private void doNothing()
	{
		return;
	}
	
	public void drawCharacterSet(Vector characterSet)
	{
		this.characterSet=characterSet;
		isCharacterSet=true;
		repaint();
	}
	
	public void drawLinebreaks(int[] lineBreaks)
	{
		this.lineBreaks=lineBreaks;
		isLineBreaks=true;
		repaint();
	}
	
	public void drawCharacterbreaks(int[][] characterBreaks, int[] lineBreaks)
	{
		drawLinebreaks(lineBreaks);
		
		this.characterBreaks=characterBreaks;
		isCharacterBreaks=true;
		repaint();
	}
	
	public void mouseMoved(MouseEvent e){}
    public void mouseDragged(MouseEvent e) {
        //The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }
    
    
	public Dimension getPreferredSize() {

            return super.getPreferredSize();

    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    
}

