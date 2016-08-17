package tools;
import java.io.*;
import java.util.*;

public class ReadParameters {
    public static double[] readPreProbFile(String fileName)
    {
        double[] reArray=null;
        ArrayList<Double>  probList=new ArrayList<Double>();
        try{
            BufferedReader infile=new BufferedReader(new FileReader(fileName));
            String rline=new String();
            while((rline=infile.readLine())!=null)
            {
                String[] splits=rline.split(":");
                probList.add(Double.parseDouble(splits[1]));
            }
            reArray=new double[probList.size()];
            for(int i=0;i<probList.size();i++)
            {
                reArray[i]=probList.get(i);
            }
            infile.close();    
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return reArray;
    }

}
