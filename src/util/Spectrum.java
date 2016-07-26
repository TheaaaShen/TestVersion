package util;

public class Spectrum {
	public static DataPoint getMaxIntensPeak(DataPoint[] expArray)
	{
		double maxIntens=0;
		int maxIntensIndex=0;
		for(int i=0;i<expArray.length;i++)
		{
			DataPoint iterPeak=expArray[i];
			if(iterPeak.getIntensity()>maxIntens)
			{
				maxIntens=iterPeak.getIntensity();
				maxIntensIndex=i;
			}
		}
		return expArray[maxIntensIndex];
	}

}
