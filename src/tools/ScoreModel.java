package tools;

import java.util.ArrayList;
import java.util.Hashtable;

import debug.Print;
import spectrum.Peak;
import util.*;
public class ScoreModel {
    
    
    /**
     * This function can switch among different scoring functions in this 
     * class.
     * @param candiSpList the list containing the simulated spectrum of each
     *                    candidate.
     * @param expSpData the experimental spectrum(real spectrum) 
     * @param preScoreArray the prior score of each candidate
     * @param WIN the window of matching peaks
     * @return a array containing the posterior score of each candidate
     */
    public static ArrayList<CompareInfo> score(
            ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,
            double[] preScoreArray,double WIN) {
        return 
                //scoreSumLogInts(
                scoreA(
                candiSpList, expSpData, preScoreArray, WIN);
    }
    
    /*
     * 共同匹配峰的个数
     */
    public static int getUnionMatchedPeakNum(ArrayList<CompareInfo> candiSpMatchList) {
        ArrayList<Double> unionList = new ArrayList<Double>();
        for(CompareInfo iterCandi : candiSpMatchList) {
            if(iterCandi != null) {
                for(PeakInfo iterPeak : iterCandi.getMatchPeakList()) {
                    if(unionList.contains(iterPeak.getPeakMz())) {
                        continue;
                    } else {
                        unionList.add(iterPeak.getPeakMz());
                    }
                }
            }
        }
        return unionList.size();
    }

    public static ArrayList<PeakInfo> getTheorPeakInfoList(ArrayList<FragNode> theorSpList)
    {
        ArrayList<PeakInfo> reInfoList=new ArrayList<PeakInfo>();
        Hashtable<Double,ArrayList<FragNode>> peakHash=new Hashtable<Double,ArrayList<FragNode>>();
        ArrayList<Double> mzList=new ArrayList<Double>();
        for(FragNode iterNode:theorSpList)
        {
            double iterMass=iterNode.getSubtreeMass();
            if(mzList.contains(iterMass))
            {
                peakHash.get(iterMass).add(iterNode);
            }else
            {
                mzList.add(iterMass);
                ArrayList<FragNode> valueList=new ArrayList<FragNode>();
                valueList.add(iterNode);
                peakHash.put(iterMass, valueList);
            }
        }
        for(Double iterMz:mzList)
        {
            PeakInfo tmpInfo=new PeakInfo(iterMz,peakHash.get(iterMz));
            reInfoList.add(tmpInfo);
        }
        return reInfoList;
    }

    /**
     * 实验谱峰去match理论谱峰，不同片段对应的相同峰只作为label list保留.
     * This is not used in GIPS!!
     * @param theorSpList the theor sp list
     * @param expSpData the exp sp data
     * @param WIN the win
     * @return the array list
     */
    public static ArrayList<PeakInfo> theroExpSpMatch(
            ArrayList<FragNode> theorSpList, Peak[] expSpData,double WIN){
        theorSpList = DataFilter.filterTheorySpSameMassIon(theorSpList);
        // The list stores the matched peaks in **experimental(real)** spectrum.
        ArrayList<PeakInfo> matchedPeakList=new ArrayList<PeakInfo>();
        // Sum of intensity of all peaks in **experimental(real)** spectrum
        double sumIntens = 0; 
        // For every peak in **experimental(real)** spectrum, find the matched
        // peaks(fragments) in **theoretical(simulated)** spectrum.
        for(Peak iterExpPeak: expSpData) {
            double expMz=iterExpPeak.getMz();
            sumIntens += iterExpPeak.getIntensity();
            // This list stores the matched peaks(fragments) of 
            // **theory(simulated)** spectrum 
            ArrayList<FragNode> candiFragNodeList = new ArrayList<FragNode>();
            for(FragNode theoryFragment: theorSpList) {
                double theorMz = theoryFragment.getSubtreeMass();
                if(expMz > theorMz - WIN && expMz < theorMz + WIN) {
                    candiFragNodeList.add(theoryFragment);
                }
            }
            if(candiFragNodeList.size() > 0) {
                System.out.println("m/z of real matched peak: " + expMz);
                // matchPeak stores the m/z, intensity of one peak in **real**
                //           spectrum, and its matched peaks in **simulated**
                //           spectrum.
                PeakInfo matchedPeak = new PeakInfo(expMz, 
                        iterExpPeak.getIntensity(), candiFragNodeList);
                matchedPeakList.add(matchedPeak);
            }
        }
        for(int i=0;i<matchedPeakList.size();i++) {
            matchedPeakList.get(i).setSumIntens(sumIntens);
        }
        System.out.println("matched & all simulated number:" + matchedPeakList.size() 
                            + "\t" + theorSpList.size());
        return matchedPeakList;
    }

    /**
     * Thero exp sp match 2.
     * GIPS use this function.!!
     * @param theorSpList the theor sp list
     * @param expSpData the exp sp data
     * @param WIN the win
     * @return the array list
     */
    public static ArrayList<PeakInfo> theroExpSpMatch2(
            ArrayList<FragNode> theorSpList, Peak[] expSpData, double WIN) {
        // theorSpList=DataFilter.filterTheorySpSameMZ2(theorSpList);
        theorSpList = DataFilter.filterTheorySpSameMassIon(theorSpList);
        ArrayList<PeakInfo> theorPeakInfoList = getTheorPeakInfoList(theorSpList);
        ArrayList<PeakInfo> matchPeakList = new ArrayList<PeakInfo>();
        ArrayList<Double> mzList = new ArrayList<Double>();
        for(PeakInfo iterInfo : theorPeakInfoList) {
            double theorMz = iterInfo.getPeakMz();
            // System.out.println(theorMz+"\t"+iterTheor.getIonTypeNote()+"\t"+iterTheor.getStrucID());
            double tmpMz = 0;
            double tmpIntens = 0;
            for(Peak iterExpPeak : expSpData) {
                double expMz = iterExpPeak.getMz();

                if(Math.abs(expMz - theorMz) < WIN) {
                    if(iterExpPeak.getIntensity() > tmpIntens) {
                        tmpIntens = iterExpPeak.getIntensity();
                        tmpMz = expMz;
                    }
                } else if(expMz - theorMz > WIN) {
                    break;
                }

            }
            if(!mzList.contains(tmpMz) && tmpMz > 10.0) {
                mzList.add(tmpMz);
                iterInfo.setPeakMz(tmpMz);
                iterInfo.setPeakIntens(tmpIntens);
                matchPeakList.add(iterInfo);
            }

        }
        
        System.out.println("matched & all simulated number:" 
                + matchPeakList.size() + "\t" + theorSpList.size());
        return matchPeakList;
                
    }
    
    /**
     * 实验谱峰去match理论谱峰,保留理论谱中不同片段对应的相同峰
     */
    public static ArrayList<PeakInfo> theroExpSpMatchB(ArrayList<FragNode> theorSpList,Peak[] expSpData,double WIN)
    {
        ArrayList<PeakInfo> matchPeakList=new ArrayList<PeakInfo>();
        for(Peak iterExpPeak:expSpData)
        {
            double expMz=iterExpPeak.getMz();
            
//            System.out.println("exp:"+expMz);
            
            for(FragNode iterTheor:theorSpList)
            {
                ArrayList<FragNode> candiFragNodeList=new ArrayList<FragNode>();
                double theorMz=iterTheor.getSubtreeMass();
//                System.out.println("theor:"+theorMz);
                if(expMz<theorMz+WIN&&expMz>theorMz-WIN)
                {
                    candiFragNodeList.add(iterTheor);
                    PeakInfo matchPeak=new PeakInfo(expMz,iterExpPeak.getIntensity(),candiFragNodeList);
                    matchPeakList.add(matchPeak);
                    
                }
            }
        }
        System.out.println("matched & all:"+matchPeakList.size()+"\t"+theorSpList.size());
        return matchPeakList;
    }
    /**
     * Score model:
     * @P=0.75
     * match peaks： 
     * probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
     * probScore=probScore/sumProbScore;
     */
    public static ArrayList<CompareInfo> scoreA(
            ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,
            double[] preScoreArray,double WIN) {
        double P=0.75;
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++) {
            ArrayList<FragNode> theorySpPeakList = candiSpList.get(i);
            if(theorySpPeakList==null) {
                candiSpMatchList.add(null);    
            }else {
                ArrayList<PeakInfo> matchedPeakList = theroExpSpMatch2(
                        theorySpPeakList,expSpData,WIN);
                // debug code: to get the matched peaks
                Print.pl("Matched peaks of candidate " + i + " :");
                for(PeakInfo peak: matchedPeakList){
                    Print.pl("\t" + peak.getPeakMz());
                }
                CompareInfo matchResult=new CompareInfo();
                matchResult.setCandiID(String.valueOf(i));
                matchResult.setMatchPeakList(matchedPeakList);
            
                candiSpMatchList.add(matchResult);
            }
        }
        
        int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
        double unionScore=0;
        for(int j=0;j<candiSpMatchList.size();j++) {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null) {
                continue;
            }else {
                int matchNum=tmpInfo.getMatchPeakList().size();
                double probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
                System.out.println(preScoreArray == null);
                String infoStr = matchNum + "\t" + unionMatchedNum + "\t" 
                        + expSpData.length + "\t" + probScore + "\t" 
                        + preScoreArray[j];
                
                System.out.println(j + "\t"+infoStr);
                System.out.println(probScore+"\t"+preScoreArray[j]);
                probScore=probScore*preScoreArray[j];
            
                candiSpMatchList.get(j).setScore(probScore);
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
                candiSpMatchList.get(j).setScoreInfoStr(infoStr);
                unionScore=unionScore+probScore;
            }
        }
        for(int k=0;k<candiSpMatchList.size();k++) {
            if(candiSpMatchList.get(k)!=null) {
            double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
            candiSpMatchList.get(k).setScore(normalizedScore);
//            System.out.println("Prob. :"+normalizedScore);
            candiSpMatchList.get(k).setScoreInfoStr(
                    candiSpMatchList.get(k).candiStrucID + "\t" 
                    + normalizedScore + "\t" 
                    + candiSpMatchList.get(k).getScoreInfoStr());
            }
        }
        return candiSpMatchList;
        
    }
      
    public static double[] sumInts;
    public static ArrayList<CompareInfo> scoreSumLogInts(
                ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,
        double[] preScoreArray,double WIN) {
        // make all preScore to uniform
        for(int i = 0; i< preScoreArray.length; i++){
            preScoreArray[i] = 1.0 / preScoreArray.length;
        }
        
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++) {
            ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
            if(theorySpPeakList==null) {
                candiSpMatchList.add(null);    
            }else {
                ArrayList<PeakInfo> matchedPeakList = theroExpSpMatch2(
                        theorySpPeakList,expSpData,WIN);
                // debug code: to get the matched peaks
                Print.pl("Matched peaks of candidate " + i + " :");
                for(PeakInfo peak: matchedPeakList){
                    Print.pl("\t" + peak.getPeakMz() + "\tInts: " + peak.getPeakMz());
                }
                CompareInfo matchResult = new CompareInfo();
                matchResult.setCandiID(String.valueOf(i));
                matchResult.setMatchPeakList(matchedPeakList);
            
                candiSpMatchList.add(matchResult);
            }
        }
        
        double sumScore=0; // for normalizing the scores
        int unionMatchedNum = 0; // This variable is not used!
        for(int j = 0; j < candiSpMatchList.size(); j++) {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null) {
                continue;
            }else {
                int matchNum=tmpInfo.getMatchPeakList().size();// This variable is not used!
                ArrayList<PeakInfo> matchedPeaks=tmpInfo.getMatchPeakList();
                double probScore = 0;
                for(PeakInfo peak: matchedPeaks){
                    double s = Math.log(peak.getPeakIntens());
                    probScore += s;
                }
//                Print.pl("candiSpMatchList.size():" + candiSpMatchList.size()+ "\tj:"+j);
                sumInts[j] += probScore;
                probScore = sumInts[j];
                Print.pl("preScoreArray == null? " + (preScoreArray == null));
                String infoStr = matchNum + "\t" + sumInts[j] + "\t" 
                        + expSpData.length + "\t" + probScore + "\t" 
                        + preScoreArray[j];
                
                System.out.println(j + "\t"+infoStr);
                System.out.println(probScore+"\t"+preScoreArray[j]);
                probScore = probScore * preScoreArray[j];
            
                candiSpMatchList.get(j).setScore(probScore);
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum((int)sumInts[j]);
                candiSpMatchList.get(j).setScoreInfoStr(infoStr);
                sumScore = sumScore + probScore;
            }
        }
        // normalize the scores
        for(int k=0;k<candiSpMatchList.size();k++) {
            if(candiSpMatchList.get(k)!=null) {
            double normalizedScore = candiSpMatchList.get(k).getScore() / sumScore;
            candiSpMatchList.get(k).setScore(normalizedScore);
//            System.out.println("Prob. :"+normalizedScore);
            candiSpMatchList.get(k).setScoreInfoStr(
                    candiSpMatchList.get(k).candiStrucID + "\t" 
                    + normalizedScore + "\t" 
                    + candiSpMatchList.get(k).getScoreInfoStr());
            }
        }
        return candiSpMatchList;
        
    }

    /**
     * scoreMatchedIntensRatio:
     * score = intensity of matched real peaks / intensity of all real peaks
     * 
     */
    public static ArrayList<CompareInfo> scoreMatchedIntensRatio(
            ArrayList<ArrayList<FragNode>> candiSpList,
            Peak[] expSpData, double[] preScoreArray, double WIN){
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        double sumScore=0;
        for(int i = 0; i < candiSpList.size(); i++) {
            ArrayList<PeakInfo> matchedPeakList = theroExpSpMatch(
                    candiSpList.get(i), expSpData, WIN);
            CompareInfo matchResult = new CompareInfo();
            //int matchNum = matchedPeakList.size();
            //int missMatchNum = candiSpList.get(i).size()-matchNum;
            
            double score = ScoreModel.coutSumMatchIntensRatio(matchedPeakList);
            // System.out.print("sumScore:"+sumScore+ "\tscore:"+score);
            sumScore += score;
            
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            matchResult.setScore(score);
            matchResult.setMatchedNum(matchedPeakList.size());
            candiSpMatchList.add(matchResult);
        }
        
        for(int k = 0;k < candiSpMatchList.size(); k++) {
            double probScore = candiSpMatchList.get(k).getScore() / sumScore;
            candiSpMatchList.get(k).setScore(probScore);
            System.out.println("Prob. of candidate "+ k + ": " + probScore 
                    + "  sumScore: " + sumScore);
        }
        return candiSpMatchList;
        
    }

    /**
     * Score model B:
     * @P=0.7
     * probScore=Math.pow(P, matchNum)*Math.pow(1-P, unionMatchedNum-matchNum);
     * probScore=probScore/sumProbScore;
     */
    public static ArrayList<CompareInfo> scoreB(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        double P=0.7;
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        double sumScore=0;
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(candiSpList.get(i),expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            int matchNum=matchedPeakList.size();
            int misMatchNum=candiSpList.get(i).size()-matchNum;
            double score=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum);
            sumScore=sumScore+score;
            
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            matchResult.setScore(score);
            matchResult.setMatchedNum(matchedPeakList.size());
            candiSpMatchList.add(matchResult);
            
        }
        
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            double probScore=candiSpMatchList.get(k).getScore()/sumScore;
            candiSpMatchList.get(k).setScore(probScore);
            System.out.println("Prob. :"+probScore);
        }
        return candiSpMatchList;
        
    }
    
    /**
     * Score Model C:
     * @P=0.7
     * probScore=Math.pow(P, matchNum)*Math.pow(1-P, missMatchNum);
     * probScore=probScore/sumProbScore;
     * 
     */
    
    public static ArrayList<CompareInfo> scoreC(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        double P=0.7;
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        double sumScore=0;
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatchB(candiSpList.get(i),expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            int matchNum=matchedPeakList.size();
            int misMatchNum=candiSpList.get(i).size()-matchNum;
            double score=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum);
            sumScore=sumScore+score;
            
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            matchResult.setScore(score);
            matchResult.setMatchedNum(matchedPeakList.size());
            candiSpMatchList.add(matchResult);
            
        }
        
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            double probScore=candiSpMatchList.get(k).getScore()/sumScore;
            candiSpMatchList.get(k).setScore(probScore);
            System.out.println("Prob. :"+probScore);
        }
        return candiSpMatchList;
        
    }
    /**
     * Score Model E:
     * @P=0.7
     * probScore=Math.pow(P, matchNum)*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
     * probScore=probScore/sumProbScore;
     * 
     */
    public static ArrayList<CompareInfo> scoreE(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        double P=0.65;
        double P_Noise=0.1;
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
            if(theorySpPeakList==null)
            {
                candiSpMatchList.add(null);    
            }else
            {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            candiSpMatchList.add(matchResult);
            }
        }
        
        
        int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
        double unionScore=0;
        for(int j=0;j<candiSpMatchList.size();j++)
        {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null)
            {
                continue;
            }else
            {
                int matchNum=tmpInfo.getMatchPeakList().size();
                int peakNum=DataFilter.filterTheorySpSameMZ(candiSpList.get(j)).size();
                int misMatchNum=peakNum-matchNum;
                double probScore=Math.pow(P, matchNum)*Math.pow(1-P, misMatchNum)*Math.pow(P_Noise, unionMatchedNum-matchNum);
                System.out.println(matchNum+"\t"+peakNum+"\t"+unionMatchedNum);
                System.out.println(probScore+"\t"+preScoreArray[j]);
                probScore=probScore*preScoreArray[j];
            
                candiSpMatchList.get(j).setScore(probScore);
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
                
                unionScore=unionScore+probScore;
            }
            
            
        }
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            if(candiSpMatchList.get(k)!=null)
            {
                double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
                candiSpMatchList.get(k).setScore(normalizedScore);
                System.out.println("Prob. :"+normalizedScore);
            }else
            {
                System.out.println("Prob. :"+0.0);
            }
            
        }
        return candiSpMatchList;
        
    }
    /**
     * Score Model F:
     * @P=0.65
     * @M=same m/z labels' Num
     * P_Enhance=1-(1-P)^M
     * probScore={P_Enhance...}*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
     * probScore=probScore/sumProbScore;
     * 
     */
    public static ArrayList<CompareInfo> scoreF(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        double P=0.65;
        double P_Noise=0.1;
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
            if(theorySpPeakList==null)
            {
                candiSpMatchList.add(null);    
            }else
            {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            candiSpMatchList.add(matchResult);
            }
        }
        
        int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
        double unionScore=0;
        for(int j=0;j<candiSpMatchList.size();j++)
        {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null)
            {
                continue;
            }else
            {
                int matchNum=tmpInfo.getMatchPeakList().size();
                double P_Inhence=1;
                for(int i=0;i<matchNum;i++)
                {
                    PeakInfo matchInfo=tmpInfo.getMatchPeakList().get(i);
                    int candiLabelNum=matchInfo.getCandiFragNodeList().size();
                    P_Inhence=P_Inhence*(1-Math.pow((1-P),candiLabelNum));
                }
                
                
                int peakNum=DataFilter.filterTheorySpSameMZ(candiSpList.get(j)).size();
                int misMatchNum=peakNum-matchNum;
                double probScore=P_Inhence*Math.pow(1-P, misMatchNum)*Math.pow(P_Noise, unionMatchedNum-matchNum);
                System.out.println(matchNum+"\t"+peakNum+"\t"+unionMatchedNum);
                System.out.println(probScore+"\t"+preScoreArray[j]);
                probScore=probScore*preScoreArray[j];
            
                candiSpMatchList.get(j).setScore(probScore);
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
                
                unionScore=unionScore+probScore;
            }
            
            
        }
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            if(candiSpMatchList.get(k)!=null)
            {
                double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
                candiSpMatchList.get(k).setScore(normalizedScore);
                System.out.println("Prob. :"+normalizedScore);
            }else
            {
                System.out.println("Prob. :"+0.0);
            }
            
        }
        return candiSpMatchList;
        
    }
    /**
     * Score Model F:
     * @P=0.65
     * @M=same m/z labels' Num
     * P_Enhance=1-(1-P)^M
     * probScore={P_Enhance...}*Math.pow(1-P, missMatchNum)*Math.pow(P_Noise,unionMatchedNum-matchNum);
     * probScore=probScore/sumProbScore;
     * 
     */
    public static ArrayList<CompareInfo> scoreG(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        double P=0.65;
        double P_Noise=0.5;
        int minMissNum=Integer.MAX_VALUE;

        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
            if(theorySpPeakList==null)
            {
                candiSpMatchList.add(null);    
            }else
            {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            candiSpMatchList.add(matchResult);
            }
        }
        
        int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
        double unionScore=0;
        int labelNum=0;
        for(int j=0;j<candiSpMatchList.size();j++)
        {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null)
            {
                continue;
            }else
            {
                int matchNum=tmpInfo.getMatchPeakList().size();
                double P_Inhence=1;
                for(int i=0;i<matchNum;i++)
                {
                    PeakInfo matchInfo=tmpInfo.getMatchPeakList().get(i);
                    int candiLabelNum=matchInfo.getCandiFragNodeList().size();
                    labelNum=labelNum+candiLabelNum;
                    P_Inhence=P_Inhence*(1-Math.pow((1-P),candiLabelNum));
                }
                
                /*
                 * peakNum different from scoreF
                 */
                int peakNum=candiSpList.get(j).size();
                int misMatchNum=peakNum-matchNum;
                double initScore=P_Inhence*Math.pow(P_Noise, unionMatchedNum-matchNum);
                String infoStr=matchNum+"\t"+labelNum+"\t"+peakNum+"\t"+unionMatchedNum+"\t"+initScore+"\t"+preScoreArray[j];
                
                System.out.println(infoStr);
                initScore=initScore*preScoreArray[j];
            
                if(minMissNum>misMatchNum)
                {
                    minMissNum=misMatchNum;
                }
                candiSpMatchList.get(j).setScore(initScore);
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
                candiSpMatchList.get(j).setMissMatchedNum(misMatchNum);
                candiSpMatchList.get(j).setScoreInfoStr(infoStr);
            }
            
            
        }
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            if(candiSpMatchList.get(k)!=null)
            {
                double probScore=candiSpMatchList.get(k).getScore()*Math.pow(1-P, candiSpMatchList.get(k).getMissMatchedNum()-minMissNum);
                candiSpMatchList.get(k).setScore(probScore);
                unionScore=unionScore+probScore;
            }else
            {
                
            }
            
        }
        for(int k=0;k<candiSpMatchList.size();k++)
        {
            if(candiSpMatchList.get(k)!=null)
            {
                double normalizedScore=candiSpMatchList.get(k).getScore()/unionScore;
                candiSpMatchList.get(k).setScore(normalizedScore);
                System.out.println("Prob. :"+normalizedScore);
            }else
            {
                System.out.println("Prob. :"+0.0);
            }
            
        }
        return candiSpMatchList;
        
    }
    public static ArrayList<CompareInfo> scoreH(ArrayList<ArrayList<FragNode>> candiSpList,Peak[] expSpData,double[] preScoreArray,double WIN)
    {
        ArrayList<CompareInfo> candiSpMatchList=new ArrayList<CompareInfo>();
        for(int i=0;i<candiSpList.size();i++)
        {
            ArrayList<FragNode> theorySpPeakList=candiSpList.get(i);
            if(theorySpPeakList==null)
            {
                candiSpMatchList.add(null);    
            }else
            {
            ArrayList<PeakInfo> matchedPeakList=theroExpSpMatch(theorySpPeakList,expSpData,WIN);
            CompareInfo matchResult=new CompareInfo();
            matchResult.setCandiID(String.valueOf(i));
            matchResult.setMatchPeakList(matchedPeakList);
            candiSpMatchList.add(matchResult);
            
            }
        }
        
        int unionMatchedNum=getUnionMatchedPeakNum(candiSpMatchList);
        double unionScore=0;
        for(int j=0;j<candiSpMatchList.size();j++)
        {
            CompareInfo tmpInfo=candiSpMatchList.get(j);
            if(tmpInfo==null)
            {
                continue;
            }else
            {
                int matchNum=tmpInfo.getMatchPeakList().size();
                
                int peakNum=candiSpList.get(j).size();
                int misMatchNum=peakNum-matchNum;
        
                
                candiSpMatchList.get(j).setMatchedNum(matchNum);
                candiSpMatchList.get(j).setUnionMatchedNum(unionMatchedNum);
            }
        }
        
        return candiSpMatchList;
        
    }
    /**
         * countEntropy for probability
         */
        public static double coutEntropy(ArrayList<CompareInfo> probInfoList)
        {
            ArrayList<Double> probScoreList=new ArrayList<Double>();
            for(int i=0;i<probInfoList.size();i++)
            {
                CompareInfo tmpInfo=probInfoList.get(i);
                if(tmpInfo==null)
                {
                    probScoreList.add(0.0);
                }else{
                    /*
                     * modify different score
                     */
    //                probScoreList.add(tmpInfo.probScore);
                    probScoreList.add(tmpInfo.getScore());
                }
            }
            return Entropy.countEntropy(probScoreList);
        }

    /**
     * This function computes this ratio:</br>
     * intensity of matched real peaks / intensity of all real peaks 
     *
     * @param matchPeakList the matched peaks in real spectrum
     * @return the ratio
     */
    public static double coutSumMatchIntensRatio(
            ArrayList<PeakInfo> matchPeakList) {
        double sumIntens = 0;
        for(int i = 0; i < matchPeakList.size(); i++) {
            sumIntens += matchPeakList.get(i).getPeakIntens();
        }
        System.out.println(matchPeakList.get(0).getSumIntens());
        return sumIntens / matchPeakList.get(0).getSumIntens();
    }

    public static void main(String[] args)
    {
        
    }

}
