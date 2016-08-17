package tools;
import java.util.*;
import util.*;
public class ScoreEntropyResult {
    ArrayList<PeakEntropyInfo> peakEntropyList;
    ArrayList<CompareInfo> scoreInfoList;
    public ScoreEntropyResult(ArrayList<CompareInfo> scoreInfoList,ArrayList<PeakEntropyInfo> peakEntropyList)
    {
        this.peakEntropyList=peakEntropyList;
        this.scoreInfoList=scoreInfoList;
        
    }
    public ArrayList<PeakEntropyInfo> getPeakEntropyList()
    {
        return this.peakEntropyList;
    }
    public ArrayList<CompareInfo> getScoreInfoList()
    {
        return this.scoreInfoList;
    }

}
