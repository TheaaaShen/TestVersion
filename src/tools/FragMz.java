package tools;


import java.util.ArrayList;

import spectrum.Peak;
import util.DataFilter;
import util.FragNode;
import util.MzXMLReader;
import util.SPComponent;
import util.SugarFragment;


// TODO: Auto-generated Javadoc
/**
 * The Class FragMz.
 * Previous name FragMz2
 */
public class FragMz {
    
    /**
     * Search level node.
     *
     * @param fragNode the frag node
     * @param spLevel the sp level
     * @param multiLevelMzList the multi level mz list
     * @param cutTime the cut time
     * @return the array list
     */
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
    
    /**
     * Search level node 2.
     *
     * @param fragNode the frag node
     * @param spLevel the sp level
     * @param multiLevelMzList the multi level mz list
     * @param cutTime the cut time
     * @return the array list
     */
    private ArrayList<FragNode> searchLevelNode2(ArrayList<FragNode> fragNode,
            int spLevel, ArrayList<Double> multiLevelMzList,int cutTime) {
        ArrayList<FragNode> fragNodeList = new ArrayList<FragNode>();
        ArrayList<FragNode> tmpNodeList = new ArrayList<FragNode>();
        fragNodeList=fragNode;
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
            fragNodeList = FragNode.fragNodeListClone(tmpNodeList);
            tmpNodeList.clear();
            
        }
        
        if(fragNodeList.size()<1)
        {
            fragNodeList=null;
        }
        return fragNodeList;
    }
    
    /**
     * Cout theory sp.
     *
     * @param subStrucFragNodeList the sub struc frag node list
     * @param cutTime the cut time
     * @return the array list
     */
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
    
    /**
     * Search sp level node list with mass.
     *
     * @param candiStrucList the candi struc list
     * @param multiLevelMzList the multi level mz list
     * @param spLevel the sp level
     * @param cutTime the cut time
     * @return the array list
     */
    //寻找第spLevel级谱对应的子树根节点
    private ArrayList<ArrayList<FragNode>> searchSpLevelNodeListWithMass(ArrayList<FragNode> candiStrucList,ArrayList<Double> multiLevelMzList,int spLevel,int cutTime)
    {
        ArrayList<ArrayList<FragNode>> candiNodeList=new ArrayList<ArrayList<FragNode>>();
        boolean detected=false;
//        for(FragNode iterStruc:candiStrucList)
        for(int i=1;i<candiStrucList.size();i++)
        {
            ArrayList<FragNode> iterStrucList=new ArrayList<FragNode>();
            if(i==1)
            {
                iterStrucList.add(candiStrucList.get(0));
                iterStrucList.add(candiStrucList.get(i));
                
            }else
            {
                iterStrucList.add(candiStrucList.get(i));
            }
            ArrayList<FragNode> candiNode=this.searchLevelNode2(iterStrucList, spLevel,multiLevelMzList,cutTime);
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

    /**
     * Execute count.
     *
     * @param candiStrucList the candidate structure list
     * @param spectrum the spectrum dealing with
     * @param multiLevelMzList the multi level M/Z list
     * @param cutTime the cut time ??
     * @param spLevel the level of current spectrum 
     * @param WIN the window ??
     * @param preProbArray the prior probabilities array
     * @param filterRatio the filter ratio
     * @return the score entropy result
     */
    public ScoreEntropyResult executeCount(ArrayList<FragNode> candiStrucList,
            SPComponent spectrum, ArrayList<Double> multiLevelMzList,
            int cutTime,int spLevel,double WIN,
            double[] preProbArray,double filterRatio){
            // Get all peaks 这个是指实验谱
            Peak[] expSPArray = spectrum.getPeakArray();
            ArrayList<ArrayList<FragNode>> candiTheorySPList = countTheorySp(
                    candiStrucList, multiLevelMzList, spLevel, cutTime);
            if(candiTheorySPList==null) {
                return null;
            }
            expSPArray=spectrumFilter(expSPArray,filterRatio);
            
            ArrayList<CompareInfo> scoreInfoList=ScoreModel.
                    scoreA(candiTheorySPList, expSPArray, preProbArray, WIN);
            int peakLevel=spectrum.getSpLevel();
            ArrayList<PeakEntropyInfo> peakEntropyList = 
                    coutNextStagePeak(expSPArray, scoreInfoList,1,peakLevel);
    //        ArrayList<PeakEntropyInfo> peakEntropyList=null;
            ScoreEntropyResult tmpResult=new ScoreEntropyResult(scoreInfoList,peakEntropyList);
            return tmpResult;
    
        }
    
    /**
     * Count theory sp.
     *
     * @param candiStrucList the candi struc list
     * @param multiLevelMzList the multi level mz list
     * @param spLevel the sp level
     * @param cutTime the cut time
     * @return the array list
     */
    //计算理论谱
    public ArrayList<ArrayList<FragNode>> countTheorySp(ArrayList<FragNode> candiStrucList,
            ArrayList<Double> multiLevelMzList,int spLevel,int cutTime){
        
        return this.coutTheorySp(this.searchSpLevelNodeListWithMass(candiStrucList, multiLevelMzList, spLevel,cutTime),cutTime);
    }
    
    /**
     * Load sp.
     *
     * @param spFile the sp file
     * @return the peak[]
     */
    public Peak[] loadSp(String spFile){
        Peak[] expIonArray=null;
        try{
            MzXMLReader mzxmlReader=new MzXMLReader();
            mzxmlReader.init(spFile);
            expIonArray=mzxmlReader.get_peak_list();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return expIonArray;
    }
    
    /**
     * Spectrum filter.
     *
     * @param expIonArray the exp ion array
     * @param filterRatio the filter ratio
     * @return the peak[]
     */
    public Peak[] spectrumFilter(Peak[] expIonArray, double filterRatio){
        expIonArray=DataFilter.getNormalizedPeakArray(expIonArray, 1000);
        /*
         * intens filt
         * @filtIntensRatio:0.95 
         */
        
        double filtIntens=filterRatio*1000;
        Peak[] filteredExpIonArray=DataFilter.experiSpPeakFiltMinSinglePeakIntens(expIonArray, filtIntens);
        return filteredExpIonArray;
        
    }
    
    /**
     * Load structure.
     *
     * @param strucFile the struc file
     * @return the array list
     */
    public ArrayList<FragNode> loadStructure(String strucFile) {
        SugarFragment candiGlycan=new SugarFragment();
        candiGlycan.read(strucFile);
        candiGlycan.readSugar();
        ArrayList<FragNode> candiStrucFragList=candiGlycan.getFragTreeList();
        return candiStrucFragList;
        
    }
    
    /**
     * Cout next stage peak.
     *
     * @param expIonArray the exp ion array
     * @param scoreList the score list
     * @param cutTime the cut time
     * @param peakLevel the peak level
     * @return the array list
     */
    public ArrayList<PeakEntropyInfo> coutNextStagePeak(Peak[] expIonArray,
            ArrayList<CompareInfo> scoreList,int cutTime,int peakLevel){
        FetchFeature6 tt=new FetchFeature6();
        ArrayList<PeakEntropyInfo> peakEntropyResult
            =tt.FetchMatchMzPeak2(scoreList, cutTime,peakLevel);
        
        return peakEntropyResult;
    }
    
    /**
     * Gets the inits the equal prob.
     *
     * @param candiNum the candi num
     * @return the inits the equal prob
     */
    public double[] getInitEqualProb(int candiNum){
        double[] reArray=new double[candiNum];
        double prob=1.0/candiNum;
        for(int i=0;i<candiNum;i++){
            reArray[i]=prob;
        }
        return reArray;
    }

    
    /**
     * Cout theory sp N core 2 cut.
     *
     * @param candiStrucList the candi struc list
     * @param multiLevelMzList the multi level mz list
     * @param spLevel the sp level
     * @param cutTime the cut time
     * @return the array list
     */
    public ArrayList<ArrayList<FragNode>> coutTheorySpNCore2Cut(ArrayList<FragNode> candiStrucList,
            ArrayList<Double> multiLevelMzList,int spLevel,int cutTime) {
        ArrayList<ArrayList<FragNode>> reList=new ArrayList<ArrayList<FragNode>>();
        for(int i=0;i<candiStrucList.size();i++) {
            FragNode tmpNode=candiStrucList.get(i);
            int cut=0;
            if(tmpNode!=null){
                if(tmpNode.isNGlycanCore()) {
                    cut=2;
                } else {
                    cut=1;
                }
                
            }
            ArrayList<FragNode> singleEleList=new ArrayList<FragNode>();
            singleEleList.add(tmpNode);
            reList.addAll(this.coutTheorySp(this.searchSpLevelNodeListWithMass(singleEleList, multiLevelMzList, spLevel,cutTime),cut));
        }
        
        return reList;
    }
    
}
