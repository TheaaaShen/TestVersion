package util;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * @peakMz: a peak's mz
 * @candiStruList: the peak can generated child peak node (noted by FragNode)
 * @featureNodeList: the peak corresponded candidate Structures can generated child distinguished child peak  Disjunction of @candiStruList
 * @candiSpecList: use the @featureNodeList information and the @candiStruList to Create all possible Therory Sp of @peakMz
 * @probList: use @candiStruList and @candiSpecList to count the simlularity Prob Score
 * @subTreeList: contains the possible sub SpectrumTree;
 */
public class SpectrumTree {
    double peakMz;
    ArrayList<FragNode> candiStruList;
    ArrayList<FeatureNode> featureNodeList;
    ArrayList<SpectrumNode> candiSpecList;
    //ArrayList<SpectrumTree> subTreeList;
    ArrayList<double[]> probList;
    
    ArrayList<double[]> preProbList;
    ArrayList<double[]> baseProbList;
    //ArrayList<Integer> featureNumList;
    
    int commNum=0;
    int cutTime=1;
    int cutLevel=0;
    double cutTimeProb=1;
    String levelNum="0";
    
    public SpectrumTree(double peakMz,ArrayList<FragNode> candiStruList,String levelNum,ArrayList<double[]> preProbList,int cutLevel,double preCutTimeProb)
    {
        this.peakMz=peakMz;
        this.levelNum=levelNum;
        this.preProbList=preProbList;
        this.candiStruList=candiStruList;
        this.cutLevel=cutLevel;
        this.initCandiSpecList();
        this.coutBaseProbList();
        
    }
    public SpectrumTree(double peakMz,String levelNum,ArrayList<double[]> baseProbList)
    {
        this.peakMz=peakMz;
        this.levelNum=levelNum;
        this.baseProbList=baseProbList;
        
    }
   // public void setSubTreeList(ArrayList<SpectrumTree> subTreeList)
    //{
        //this.subTreeList=subTreeList;
        
    //}
    public void coutBaseProbList()
    {
        
        this.baseProbList=new ArrayList<double[]>();
        if(this.levelNum.split("\\.").length>6)
        {
            this.baseProbList=null;
            return;
        }
        
        for(int i=0;i<this.preProbList.size();i++)
        {
            for(int k=0;k<probList.size();k++)
            {
                double[] baseProb=new double[this.candiStruList.size()];
                double fullProb=0;
                for(int j=0;j<baseProb.length;j++)
                {
                    baseProb[j]=probList.get(k)[j]*this.preProbList.get(i)[j];
                    fullProb=fullProb+baseProb[j];
                }
                for(int m=0;m<baseProb.length;m++)
                {
                    baseProb[m]=FormatNum.DoubleFormat2(baseProb[m]/fullProb, 4);
                }
                this.baseProbList.add(baseProb);
            }
        }
    }
    
    public void initCandiSpecList()
    {
        int nn=this.levelNum.split("\\.").length;
        if(nn>6)
        {
            this.candiSpecList=null;
            return;
        }
//        FeatureInfo featureInfo=Disjunction.getFeatureNode(this.candiStruList, this.cutTime);
//        this.featureNodeList=featureInfo.getFeatureNodeList();
//        this.featureNumList=featureInfo.getFeatureEnumNum();
//        this.commNum=featureInfo.getCommNum();

    }
    
    
    public double getPeakMz()
    {
        return this.peakMz;
    }
}
