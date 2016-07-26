package tools;

import java.util.ArrayList;
import java.util.Hashtable;

import util.*;
/*
 * @probScore:experi sp VS. theory sp probability
 * @mzList:the matched experi sp peaks
 * @mzHash:FragNode hash mzList 
 */
public class ScoreProbInfo {
	public double probScore;
	public double smoothScore;
	public ArrayList<DataPoint> mzList;
	public Hashtable<DataPoint,ArrayList<FragNode>> mzHash;
	public int ionNum=0;
	public int matchedNum=0;
	public ScoreProbInfo(double probScore,ArrayList<DataPoint> mzList,Hashtable<DataPoint,ArrayList<FragNode>> mzHash)
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
	public ArrayList<DataPoint> getPeakList()
	{
		return this.mzList;
	}
	public Hashtable<DataPoint,ArrayList<FragNode>> getPeakHash()
	{
		return this.mzHash;
	}

}
