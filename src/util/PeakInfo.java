package util;
import java.util.*;

public class PeakInfo {
	double peakMz;
	double peakIntens;
	ArrayList<FragNode> candiFragNodeList;
	double sumIntens;
	public PeakInfo(double peakMz,double peakIntens,ArrayList<FragNode> candiFragNodeList)
	{
		this.peakMz=peakMz;
		this.peakIntens=peakIntens;
		this.candiFragNodeList=candiFragNodeList;
	}
	public PeakInfo(double peakMz,ArrayList<FragNode> candiFragNodeList)
	{
		this.peakMz=peakMz;
		this.candiFragNodeList=candiFragNodeList;
	}
	public double getPeakMz()
	{
		return this.peakMz;
	}
	public double getPeakIntens()
	{
		return this.peakIntens;
	}
	public ArrayList<FragNode> getCandiFragNodeList()
	{
		return this.candiFragNodeList;
	}
	public void setSumIntens(double sumIntens)
	{
		this.sumIntens=sumIntens;
	}
	public double getSumIntens()
	{
		return this.sumIntens;
	}
	public void setPeakMz(double peakMz)
	{
		this.peakMz=peakMz;
	}
	public void setPeakIntens(double peakIntens)
	{
		this.peakIntens=peakIntens;
	}
	

}
