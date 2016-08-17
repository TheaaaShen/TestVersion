package util;
import java.util.*;
public class PeakEntropy {

    public double mz;
    public double intensity;
    public double entropy;
    public ArrayList<Integer> strucIdList;
    public PeakEntropy(double mz,double intensity,ArrayList<Integer> strucIdList,double entropy)
    {
        this.mz=mz;
        this.intensity=intensity;
        this.strucIdList=strucIdList;
        this.entropy=entropy;
    }
}
