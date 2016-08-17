package util;

import spectrum.Peak;

public class Spectrum {
    public static Peak getMaxIntensPeak(Peak[] expArray)
    {
        double maxIntens=0;
        int maxIntensIndex=0;
        for(int i=0;i<expArray.length;i++)
        {
            Peak iterPeak=expArray[i];
            if(iterPeak.getIntensity()>maxIntens)
            {
                maxIntens=iterPeak.getIntensity();
                maxIntensIndex=i;
            }
        }
        return expArray[maxIntensIndex];
    }

}
