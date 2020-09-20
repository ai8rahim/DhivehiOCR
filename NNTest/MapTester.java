import java.awt.*;

import java.util.*;

public class MapTester
{
	
	public MapTester()
	{
		System.out.println("running");
		Map vowelMap = new HashMap();
		
		double[] W = {0.0, 0.0, 0.0, 1.0, 0.0}; //aabaa
		double[] w = {0.0, 0.0, 0.0, 0.0, 0.1}; //aba
		double[] e = {1.0, 0.0, 0.0, 0.0, 0.0}; //ebe
		double[] c = {0.0, 0.0, 1.0, 0.0, 0.0}; //sukun
		double[] u = {0.0, 1.0, 0.0, 0.0, 0.0}; //ubu
		
		vowelMap.put(W, new String("W"));
		vowelMap.put(w, new String("w"));
		vowelMap.put(e, new String("e"));
		vowelMap.put(c, new String("c"));
		vowelMap.put(u, new String("u"));
		
		double[] pattern = 	{0.0, 0.0, 0.0, 1.0, 0.0};
		
		Collection set = vowelMap.keySet();
		Iterator it = set.iterator();
		
		int atIndex=0;
		boolean doLoop=true;
		while (it.hasNext() && doLoop)
		{
			atIndex++;				
			if (Arrays.equals(((double[])it.next()),pattern)) doLoop=false;	
		}
		
		Collection vals = vowelMap.values();
		Iterator itt = vals.iterator();
			atIndex--;
		while(itt.hasNext() && atIndex!=0)
		{
			atIndex--;
			itt.next();
		}
		System.out.println((String)itt.next());
	}
	
	public static void main(String[] args)
	{
		new MapTester();
	}
}