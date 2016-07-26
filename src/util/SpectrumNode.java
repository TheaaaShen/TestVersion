package util;
import java.util.*;
public class SpectrumNode {
	int commMzNum;
	ArrayList<Double> featureMzList;
	public SpectrumNode(int commMzNum,ArrayList<Double> featureMzList)
	{
		this.commMzNum=commMzNum;
		this.featureMzList=featureMzList;
	}
	public int getCommPeakNum()
	{
		return this.commMzNum;
	}
	public ArrayList<Double> getFeatuPeakList()
	{
		return this.featureMzList;
	}


}
