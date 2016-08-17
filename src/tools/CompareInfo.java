package tools;
import java.util.*;
import util.*;
public class CompareInfo {
    String candiStrucStr;
    String candiStrucID;
    double score;
    int matchedNum;
    int unionMatchedNum;
    int missMatchedNum;
    String infoStr;
    ArrayList<PeakInfo> matchPeakList;
    public void setCandiID(String candiID)
    {
        this.candiStrucID=candiID;
    }
    public String getCandiID()
    {
        return this.candiStrucID;
    }
    public void setMatchPeakList(ArrayList<PeakInfo> matchPeakList)
    {
        this.matchPeakList=matchPeakList;
    }
    public ArrayList<PeakInfo> getMatchPeakList()
    {
        return this.matchPeakList;
    }
    public void setScore(double score)
    {
        this.score=score;
    }
    public double getScore()
    {
        return this.score;
    }
    public void setMatchedNum(int matchedNum)
    {
        this.matchedNum=matchedNum;
        
    }
    public void setUnionMatchedNum(int unionNum)
    {
        this.unionMatchedNum=unionNum;
    }
    public int getMatchedNum()
    {
        return this.matchedNum;
    }
    public int getUnionMatchedNum()
    {
        return this.unionMatchedNum;
    }
    public void setMissMatchedNum(int missMatchedNum)
    {
        this.missMatchedNum=missMatchedNum;
    }
    public int getMissMatchedNum()
    {
        return this.missMatchedNum;
    }
    public void setScoreInfoStr(String infoStr)
    {
        this.infoStr=infoStr;
    }
    public String getScoreInfoStr()
    {
        return this.infoStr;
    }
}
