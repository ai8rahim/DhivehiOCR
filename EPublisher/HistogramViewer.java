package epublisher;

import javax.swing.*;
import java.awt.*;
import jigl.image.*;

class HistogramViewer extends JPanel
{
	int histogramOP=0;	//constants to decide what type of histogram to view
		final int GRAYSCALE = 1;
		final int LINE = 2;
		final int CHAR = 3;
		
	int[] grayscaleData;
	
	int[] linebreaksData;
	int[] linebreaks;
	
	int[][] characterbreaksData;
	int[][] characterbreaks;

	int originX = 10, originY = 10;

	JTextArea ta = new JTextArea(5,20);
	
	Dimension preferredDimension;
	
	int max=0; //maximum value of the histogram
	
	//grayscale histogram constructor
	public HistogramViewer(Histogram histogram)
	{
		histogramOP = GRAYSCALE;
		grayscaleData = histogram.getData();
		
		max =0;
		for (int i = 0; i<grayscaleData.length; i++)
		{
			if (grayscaleData[i]>max) max = grayscaleData[i];
		}
		preferredDimension = new Dimension(max+originX+originX, grayscaleData.length+originY+originY);
		
		showHistogram();
	}
	
	public HistogramViewer(LinebreaksHistogram histogram)
	{
		histogramOP = LINE;
		linebreaksData = histogram.getData();
		linebreaks = histogram.getLinebreaks();
		
		max = histogram.getMax();
		preferredDimension = new Dimension(max+originX+originX, linebreaksData.length+originY+originY);
		
		showHistogram();
	}
	
	public HistogramViewer(CharacterbreaksHistogram histogram)
	{
		LinebreaksHistogram linebreaksHistogram = histogram.getLinebreaksHistogram();
		linebreaksData = linebreaksHistogram.getData();
		linebreaks = linebreaksHistogram.getLinebreaks();
		
		histogramOP = CHAR;
		characterbreaksData = histogram.getData();
		characterbreaks = histogram.getCharacterbreaks();
		
		max = characterbreaksData[0].length;
		preferredDimension = new Dimension(max+originX+originX, linebreaks[linebreaks.length-1]+originY+originY);
		
		showHistogram();
	}
	
	private void showHistogram()
	{
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("Histogram");
		frame.setSize(800, 400);
		JScrollPane scrollPane = new JScrollPane(this);
		this.setPreferredSize(preferredDimension);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(new JScrollPane(ta), BorderLayout.EAST);

		frame.setVisible(true);		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		switch(histogramOP)
		{
			case GRAYSCALE:
				drawGrayscaleHistogram(g);
				break;
			case LINE:
				drawLinebreaksHistogram(g);
				break;
			case CHAR:
				drawCharacterbreaksHistogram(g);
				break;
		}
	}
	
	private void drawCharacterbreaksHistogram(Graphics g)
	{
		int nonZero =0;

		ta.setText("");
		
		for (int n=0; n<characterbreaksData.length; n++)
		{
			originY = linebreaks[n*2];
			for (int i=0; i<characterbreaksData[n].length;i++)
			{
				g.drawLine(originX+i, originY, originX+i, originY+characterbreaksData[n][i]);
	
				if (characterbreaksData[n][i]==max)
					ta.append(i + " - " + characterbreaksData[n][i] + " >>max\n");
				else
					ta.append(i + " - " + characterbreaksData[n][i] + "\n");
	
				if (characterbreaksData[n][i]!=0) nonZero++;
			}
		}

		ta.append("NonZero:" + nonZero +"\n");
		
		g.setColor(Color.red);
		for (int n=0; n<characterbreaks.length; n++)
		{
			for (int i=0; i<characterbreaks[n].length;i++)
			{
				//System.out.print(characterbreaks[n][i]);
				g.drawLine(originX+characterbreaks[n][i], linebreaks[n*2], originX+characterbreaks[n][i], linebreaks[n*2+1]);
			}
			//System.out.print("\n");
		}

	}
	
	private void drawLinebreaksHistogram(Graphics g)
	{
		int nonZero =0;

		ta.setText("");

		for (int i=0; i<linebreaksData.length;i++)
		{
			g.drawLine(originX, originY+i, originX+linebreaksData[i], originY+i);

			if (linebreaksData[i]==max)
				ta.append(i + " - " + linebreaksData[i] + " >>max\n");
			else
				ta.append(i + " - " + linebreaksData[i] + "\n");

			if (linebreaksData[i]!=0) nonZero++;
		}

		ta.append("NonZero:" + nonZero +"\n");

		
		
		ta.append("======\n");
		g.setColor(Color.red);
		for (int i=0; i<linebreaks.length; i++)
		{
			ta.append(i+" - "+linebreaks[i]+"\n");
			g.drawLine(originX, originY+linebreaks[i], max, originY+linebreaks[i]);
		}
		ta.append("lines:"+linebreaks.length/2);
	}
	
	private void drawGrayscaleHistogram(Graphics g)
	{
		int nonZero =0;

		ta.setText("");

		for (int i=0; i<grayscaleData.length;i++)
		{
			g.drawLine(originX, originY+i, originX+grayscaleData[i], originY+i);

			if (grayscaleData[i]==max)
				ta.append(i + " - " + grayscaleData[i] + " >>max\n");
			else
				ta.append(i + " - " + grayscaleData[i] + "\n");

			if (grayscaleData[i]!=0) nonZero++;
		}

		ta.append("NonZero:" + nonZero);
	}
	
}