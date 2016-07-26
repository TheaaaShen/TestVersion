package util;
import java.util.*;
public class Normalize {
	public static DataPoint[] NormalizeIntens(DataPoint[]peak_list,double stander)
	{
		double max_intens=0;
		for(DataPoint iter_peak:peak_list)
		{
			if(iter_peak.getIntensity()>max_intens)
				max_intens=iter_peak.getIntensity();
			
		}
		for(DataPoint iter_peak:peak_list)
		{
			iter_peak.setIntensity(iter_peak.getIntensity()*stander/max_intens);
			iter_peak.setMZ(FormatNum.DoubleFormat(iter_peak.getMZ(), 2));
		}
		return peak_list;
	}
	public static ArrayList<double[]> NormalizeIntens(ArrayList<double[]> peakList,double STAND)
	{
		double maxIntens=0;
		ArrayList<double[]> reList=new ArrayList<double[]>();
		for(double[] iterPeak:peakList)
		{
			if(iterPeak[1]>maxIntens)
			{
				maxIntens=iterPeak[1];
			}
		}
		
		for(double[] iterPeak:peakList)
		{
			double[] tmpPeak=new double[2];
			tmpPeak[0]=iterPeak[0];
			tmpPeak[1]=iterPeak[1]*STAND/maxIntens;
			reList.add(tmpPeak);
		}
		
		return reList;
	}
	

}
