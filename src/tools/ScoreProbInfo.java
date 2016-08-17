package tools;

import java.util.ArrayList;
import java.util.Hashtable;

import spectrum.Peak;
import util.*;
/*
 * @probScore:experi sp VS. theory sp probability
 * @mzList:the matched experi sp peaks
 * @mzHash:FragNode hash mzList 
 */
public class ScoreProbInfo {
    public double probScore;
    public double smoothScore;
    public ArrayList<Peak> mzList;
    public Hashtable<Peak,ArrayList<FragNode>> mzHash;
    public int ionNum=0;
    public int matchedNum=0;
    public ScoreProbInfo(double probScore,ArrayList<Peak> mzList,Hashtable<Peak,ArrayList<FragNode>> mzHash)
    {
        this.probScore=probScore;
        this.mzList=mzList;
        this.mzHash=mzHash;
    }
    
    public void setSmoothScore(double smoothScore)
    {
        this.smoothScore=smoothScore;
    }
    public double getSmoothScore()
    {
        return this.smoothScore;
    }
    
    public void setMatchedNum(int matchedNum)
    {
        this.matchedNum=matchedNum;
    }
    public int getMatchedNum()
    {
        return this.matchedNum;
    }
    public void setIonNum(int ionNum)
    {
        this.ionNum=ionNum;
    }
    public int getIonNum()
    {
        return this.ionNum;
    }
    
    public double getProbScore()
    {
        return this.probScore;
    }
    public ArrayList<Peak> getPeakList()
    {
        return this.mzList;
    }
    public Hashtable<Peak,ArrayList<FragNode>> getPeakHash()
    {
        return this.mzHash;
    }

}
