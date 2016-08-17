package tools;
import java.io.*;
import java.util.*;

import util.StructureLib;
import util.SugarFragment;
public class CandidateSetSearch {
    public StructureLib strucLib;
    public Hashtable<Double,ArrayList<String>> struHash=new Hashtable<Double,ArrayList<String>>();
    public ArrayList<Double> mzList=new ArrayList<Double>();
    
    
        public boolean loadStrucLib(String strucFile)
        {
            SugarFragment candiGlycan=new SugarFragment();
            strucLib=candiGlycan.loadStrucLib(strucFile);
            return true;
        }
        public void buildSubLib(String libPath,double preMZ,double WIN)
        {
            ArrayList<Double> mzList=new ArrayList<Double>();
            ArrayList<Double> libMzList=strucLib.getMassIndexList();
            for(int i=0;i<libMzList.size();i++)
            {
                double tmpMz=libMzList.get(i);
                if(Math.abs(tmpMz-preMZ)<WIN)
                {
                    mzList.add(tmpMz);
                }
            }
            
            try{
                BufferedWriter outfile=new BufferedWriter(new FileWriter(libPath));
                int ID=0;
                for(int j=0;j<mzList.size();j++)
                {
                    double key=mzList.get(j);
                    for(String iterStruc:strucLib.getCandiStrucHash().get(key))
                    {
//                        if(this.filter(iterStruc))
                        {
                            outfile.write("start\r\n"+ID+++"\r\n"+key+"\r\n"+iterStruc+"\r\nend\r\n");
                            outfile.flush();
                        }
                        
                    }
                    
                }
                outfile.close();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    public ArrayList<String> getFilterList()
    {
        ArrayList<String> reList=new ArrayList<String>();
        reList.add("-ol");
        return reList;
    }
    public boolean filter(String strucStr)
    {
        for(String iterFilter:this.getFilterList())
        {
            if(strucStr.contains(iterFilter))
                return false;
        }
        return true;
    }
    public static void main(String[] args)
    {
        String strucFile="E:/Glycan/Glycan4/lib/carbbankNlink_filtedLib_filtSym.txt";
        String preMZ="1982";
        double WIN=1.5;
        String libPath="E:/Glycan/Glycan4/lib/"+String.valueOf(preMZ)+"_candiset.txt";
        
        CandidateSetSearch test=new CandidateSetSearch();
        test.loadStrucLib(strucFile);
        test.buildSubLib(libPath, Double.parseDouble(preMZ), WIN);
        
    }


}
