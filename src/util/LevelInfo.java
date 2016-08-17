package util;
import java.util.*;
public class LevelInfo {
    double peakMz;
    ArrayList<FragNode> candiStruList;
    public LevelInfo(double peakMz,ArrayList<FragNode> candiStruList)
    {
        this.peakMz=peakMz;
        this.candiStruList=candiStruList;
    }

}
