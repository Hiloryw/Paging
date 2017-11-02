/*
Write a program that simulates a paging system using the aging algorithm. The number of page frames is a parameter. The sequence of page references should be read from a file. For a given input file, plot the number of page faults per 1000 memory references as a function of the number of page frames available.

Answer:  
Assumption: Clock interval occurs at the time of memory reference only
*/

package paging;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class Paging 
{

	private static int[][] frame;
	private static int NumofFaults=0;

	public static void main(String[] args) throws IOException 
        {

		File file=new File("reference.txt");

		FileReader f=new FileReader(file);
		BufferedReader b = new BufferedReader(f);

		String line=null;
		String[] pageRef=null;

		while((line=b.readLine())!=null) 
                {
			pageRef = line.trim().split(",");
		}
		b.close();

                Scanner s=new Scanner(System.in);
		System.out.print("Enter the Number of Inputs for Graph : ");
		int inputs=s.nextInt();
		double[][] pageTable=new double[inputs][4];

		for (int i = 0; i < inputs; i++) 
                {

			System.out.print("\nEnter the Number of Page Frames for input"+(i+1)+" : ");
			int pageFrames=s.nextInt();
			frame=new int[pageFrames][2];

			for (int x = 0; x < frame.length; x++) 
                        {
				frame[x][0]=-1;
				frame[x][1]=0;
			}

			Paging pag=new Paging();

			for (int j = 0; j < pageRef.length; j++) 
                        {
				pag.agingAlgo(Integer.parseInt(pageRef[j]));
			}

			pageTable[i][0]=pageFrames;			
			pageTable[i][1]=NumofFaults;
			pageTable[i][2]=NumofFaults*1000/(double)(pageRef.length);

			System.out.println("Total Page Faults for input"+(i+1)+" is "+NumofFaults);

			NumofFaults=0;
			frame=null;
		}

		System.out.println();
		String formatter="%20s\t%20s\t%50s\n";
		System.out.format(formatter, "Number of PageFrames","Total PageFaults","Number of Page Faults/1000 Memory Reference");

		int[] x=new int[pageTable.length];
		double[] y=new double[pageTable.length];

		for (int i = 0; i < pageTable.length; i++) 
                {
			System.out.format(formatter, (int)pageTable[i][0],(int)pageTable[i][1],pageTable[i][2]);
			x[i]=(int) pageTable[i][0];
			y[i]= pageTable[i][2];
		}

		s.close();
		graph(x,y);
	}

	private static void graph(int[] x,double[] y) {
            
		XYgraph chart = new XYgraph("XY Graph", "Page Fault Rate","Number of Frames","Page Fault/1000 Memory Ref.","Fault Rate",x,y);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}

	void agingAlgo(int pageNumber){

		boolean isPageAssigned=false;
		for (int i = 0; i < frame.length; i++) 
                {
			frame[i][1]=frame[i][1]>>1;
			if(frame[i][0]==pageNumber)
                        {
                            isPageAssigned=true;
                            frame[i][0] |= 1 << 32;
                            //frame[i][0] |= 1;
                        }
		}

		if(!isPageAssigned)
                {
		    NumofFaults++;
                    for (int i = 0; i < frame.length; i++) 
                    {
                        if(frame[i][0]==-1)
                        {
                            frame[i][0]=pageNumber;
                            isPageAssigned=true;
                            frame[i][1]=1;
                            break;
        		}
                    }

                    if(!isPageAssigned)
                    {
                        int minCount=frame[0][1];
                        int minCountIndex=0;
                        for (int i = (frame.length-1); i >= 0 ; i--) 
                        {
                            if(frame[i][1]<minCount)
                            {
				minCount=frame[i][1];
				minCountIndex=i;
			    }
			}	

			frame[minCountIndex][0]=pageNumber;
			frame[minCountIndex][1]=1;
        		isPageAssigned=true;
		    }
		}
	}
}


class XYgraph extends ApplicationFrame
{

	public XYgraph( String applicationTitle, String graphTitle,String xLine,String yLine,String datasetName,int[] x,double[] y)
	{
		super(applicationTitle);
		JFreeChart xyGraph = ChartFactory.createXYLineChart(
				graphTitle ,
				xLine,
				yLine,
				createDataset(datasetName,x,y) ,
				PlotOrientation.VERTICAL ,
				true , true , false);

		ChartPanel chartPanel = new ChartPanel( xyGraph );
		chartPanel.setPreferredSize( new java.awt.Dimension( 500 , 350 ) );
		final XYPlot plot = xyGraph.getXYPlot( );
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
		renderer.setSeriesPaint( 0 , Color.BLUE );
		plot.setRenderer( renderer ); 
		setContentPane( chartPanel ); 
	}

	private XYDataset createDataset(String datasetName, int[] x, double[] y){
		final XYSeries faultRate = new XYSeries(datasetName);

		for (int i = 0; i < x.length; i++) 
                {
			faultRate.add( x[i] , y[i]);          
		}

		final XYSeriesCollection dataset = new XYSeriesCollection( );          
		dataset.addSeries( faultRate );          
		return dataset;
	}
}