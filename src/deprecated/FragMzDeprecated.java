package deprecated;

import java.util.*;

import spectrum.Peak;
import tools.CompareInfo;
import tools.FetchFeature6;
import tools.PeakEntropyInfo;
import tools.ScoreEntropyResult;
import tools.ScoreModel;
import util.*;
// This class is deprecated.
// Previous name: FragMz
public class FragMzDeprecated {
    
    private ArrayList<FragNode> searchLevelNode(FragNode fragNode,
            int spLevel, ArrayList<Double> multiLevelMzList,int cutTime) {
        ArrayList<FragNode> fragNodeList = new ArrayList<FragNode>();
        ArrayList<FragNode> tmpNodeList = new ArrayList<FragNode>();
        fragNodeList.add(fragNode);
        for (int i = 0; i <= spLevel - 2; i++) {

            for (FragNode iterNode : fragNodeList) {
                double nodeMass=multiLevelMzList.get(i);
                /*
                 *searchSubtree....Mass2: for detect cut two time node
                 *searchSubtree....Mass3: for detect cut more than two time node
                 */
                
//                ArrayList<FragNode> fetchedNodeList = iterNode.searchSubtreeNodeListWithMass(nodeMass);
//                ArrayList<FragNode> fetchedNodeList = iterNode.searchSubtreeNodeListWithMass2(nodeMass);
                ArrayList<FragNode> fetchedNodeList = iterNode.searchSubtreeNodeListWithMass3(nodeMass,cutTime);
                if (fetchedNodeList != null)
                    tmpNodeList.addAll(fetchedNodeList);
            }
            fragNodeList = fragNode.fragNodeListClone(tmpNodeList);
            tmpNodeList.clear();
            
        }
        
        if(fragNodeList.size()<1)
        {
            fragNodeList=null;
        }
        return fragNodeList;
    }
    //给定子树根节点,即候选结构子结构List，计算最多发生cutTime次断裂后形成的理论谱
    private ArrayList<ArrayList<FragNode>> coutTheorySp(ArrayList<ArrayList<FragNode>> subStrucFragNodeList, int cutTime) {
        ArrayList<ArrayList<FragNode>> subStrucTheorySpPeakList=new ArrayList<ArrayList<FragNode>>();
        if(subStrucFragNodeList==null)
        {
            return null;
        }
        for (ArrayList<FragNode> iterList : subStrucFragNodeList) {
            ArrayList<FragNode> theoryPeakNodeList=new ArrayList<FragNode>();
            if(iterList==null)
            {
                subStrucTheorySpPeakList.add(null);
                continue;
            }

            for(FragNode iterNode:iterList)
            {
                /*
                 * @TheorySpList: add filter
                 * @TheoryCutIonList: just cut segments,no filter
                 */
                ArrayList<FragNode> tmpPeakNodeList=iterNode.getCorrespondTheorySpList(cutTime);
//                ArrayList<FragNode> tmpPeakNodeList=iterNode.getCorrespondTheoryCutIonList(cutTime);
                
                
                if (tmpPeakNodeList != null) {
                    theoryPeakNodeList.addAll(tmpPeakNodeList);
                }
            }
            if(theoryPeakNodeList.size()>0)
            {
                subStrucTheorySpPeakList.add(theoryPeakNodeList);
            }
                        
        }
        return subStrucTheorySpPeakList;
    }
    
    //寻找第spLevel级谱对应的子树根节点
    private ArrayList<ArrayList<FragNode>> searchSpLevelNodeListWithMass(ArrayList<FragNode> candiStrucList,ArrayList<Double> multiLevelMzList,int spLevel,int cutTime)
    {
        ArrayList<ArrayList<FragNode>> candiNodeList=new ArrayList<ArrayList<FragNode>>();
        boolean detected=false;
        for(FragNode iterStruc:candiStrucList)
        {
            ArrayList<FragNode> candiNode=this.searchLevelNode(iterStruc, spLevel,multiLevelMzList,cutTime);
            candiNodeList.add(candiNode);
            if(candiNode!=null)
            {
                detected=true;
            }
            
        }
        if(detected)
        {
            return candiNodeList;
        }else
        {
            return null;
        }
        
    }

    //计算理论谱
    public ArrayList<ArrayList<FragNode>> countTheorySp(ArrayList<FragNode> candiStrucList,ArrayList<Double> multiLevelMzList,int spLevel,int cutTime)
    {
        
        return this.coutTheorySp(this.searchSpLevelNodeListWithMass(candiStrucList, multiLevelMzList, spLevel,cutTime),cutTime);
    }
    
    public Peak[] loadSp(String spFile)
    {
        Peak[] expIonArray=null;
        try{
            MzXMLReader mzxmlReader=new MzXMLReader();
            mzxmlReader.init(spFile);
            expIonArray=mzxmlReader.get_peak_list();
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return expIonArray;
    }
    public Peak[] spectrumFilter(Peak[] expIonArray, double filterRatio)
    {
        expIonArray=DataFilter.getNormalizedPeakArray(expIonArray, 1000);
        /*
         * intens filt
         * @filtIntensRatio:0.95 
         */
        
        double filtIntens=filterRatio*1000;
        Peak[] filteredExpIonArray=DataFilter.experiSpPeakFiltMinSinglePeakIntens(expIonArray, filtIntens);
        return filteredExpIonArray;
        
    }
    
    public ArrayList<FragNode> loadStructure(String strucFile)
    {
        SugarFragment candiGlycan=new SugarFragment();
        candiGlycan.read(strucFile);
        candiGlycan.readSugar();
        ArrayList<FragNode> candiStrucFragList=candiGlycan.getFragTreeList();
        return candiStrucFragList;
        
    }
    public ArrayList<PeakEntropyInfo> coutNextStagePeak(Peak[] expIonArray,ArrayList<CompareInfo> scoreList,int cutTime,int peakLevel)
    {
        FetchFeature6 tt=new FetchFeature6();
        ArrayList<PeakEntropyInfo> peakEntropyResult=tt.FetchMatchMzPeak2(scoreList, cutTime,peakLevel);
        
        return peakEntropyResult;
    }
    public double[] getInitEqualProb(int candiNum)
    {
        double[] reArray=new double[candiNum];
        double prob=1.0/candiNum;
        for(int i=0;i<candiNum;i++)
        {
            reArray[i]=prob;
        }
        return reArray;
    }

    
    public ArrayList<ArrayList<FragNode>> coutTheorySpNCore2Cut(ArrayList<FragNode> candiStrucList,ArrayList<Double> multiLevelMzList,int spLevel,int cutTime)
    {
        ArrayList<ArrayList<FragNode>> reList=new ArrayList<ArrayList<FragNode>>();
        for(int i=0;i<candiStrucList.size();i++)
        {
            FragNode tmpNode=candiStrucList.get(i);
            int cut=0;
            if(tmpNode!=null)
            {
                if(tmpNode.isNGlycanCore())
                {
                    cut=2;
                }else
                {
                    cut=1;
                }
                
            }
            ArrayList<FragNode> singleEleList=new ArrayList<FragNode>();
            singleEleList.add(tmpNode);
            reList.addAll(this.coutTheorySp(this.searchSpLevelNodeListWithMass(singleEleList, multiLevelMzList, spLevel,cutTime),cut));
        }
        
        return reList;
    }
    
    public ScoreEntropyResult executeCount(ArrayList<FragNode> candiStrucList,SPComponent iterSP,ArrayList<Double> multiLevelMzList,int cutTime,int spLevel,double WIN,double[] preProbArray,double filterRatio) 
    {
        Peak[] expSPArray=iterSP.getPeakArray();
        ArrayList<ArrayList<FragNode>> candiTheorySPList=countTheorySp(candiStrucList, multiLevelMzList, spLevel, cutTime);
        if(candiTheorySPList==null)
        {
            return null;
        }
        expSPArray=spectrumFilter(expSPArray,filterRatio);
        
        ArrayList<CompareInfo> scoreInfoList=ScoreModel.scoreA(candiTheorySPList, expSPArray, preProbArray, WIN);
        int peakLevel=iterSP.getSpLevel();
        ArrayList<PeakEntropyInfo> peakEntropyList=coutNextStagePeak(expSPArray, scoreInfoList,1,peakLevel);
//        ArrayList<PeakEntropyInfo> peakEntropyList=null;
        
        ScoreEntropyResult tmpResult=new ScoreEntropyResult(scoreInfoList,peakEntropyList);
        return tmpResult;
    }
    
}
