package tools;
import java.util.*;
import util.*;
public class Entropy {
    public static double countEntropy(ArrayList<Double> probList)
    {
        Double[] probArray=(Double[])probList.toArray(new Double[0]);
        
        return(countEntropy(probArray));
    }
    public static double countEntropy(double[] probArray)
    {
        Double[] probArrayConvert=new Double[probArray.length];
        for(int i=0;i<probArray.length;i++)
        {
            probArrayConvert[i]=probArray[i];
        }
        return(countEntropy(probArrayConvert));
    }
    public static double countEntropy(Double[] probArray)
    {
        double entropy=0.0;
        for(int i=0;i<probArray.length;i++)
        {
            if(probArray[i]==0.0)
            {
                continue;
            }
            entropy=entropy+probArray[i]*Math.log(probArray[i])/Math.log(2);
        }
        entropy=-entropy;
//        System.out.println(entropy);
        return FormatNum.DoubleFormat(entropy, 3);
                
    }
    public static void main(String[] args)
    {
        ArrayList<Double> testList=new ArrayList<Double>();
        testList.add(0.2);
        testList.add(0.8);
//        testList.add(0.07);
//        testList.add(0.02);
        System.out.println(Entropy.countEntropy(testList));
        
    }

}
