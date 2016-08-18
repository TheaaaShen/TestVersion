package tools;

import java.util.ArrayList;

import util.PeakInfo;

// TODO: Auto-generated Javadoc. This class should be renamed.
/**
 * The Class CompareInfo.
 * It seems that this class represents the score of one candidate structure.
 * 
 * 
 */
public class CompareInfo {
    
    /** The name of this candidate structure. */
    String candiStrucStr;
    
    /** The ID of this candidate structure. */
    String candiStrucID;
    
    /** The score of this candidate structure. */
    double score;
    
    /** The number of matched peaks. */
    int matchedNum;
    
    /** The union matched number??. */
    int unionMatchedNum;
    
    /** The number of miss matched peaks. */
    int missMatchedNum;
    
    /** The info str. ?? */
    String infoStr;
    
    /** The list contianing all matched peaks. */
    ArrayList<PeakInfo> matchPeakList;

    /**
     * Sets the ID of this candidate structure.
     *
     * @param candiID the new ID of this structure
     */
    public void setCandiID(String candiID) {
        this.candiStrucID = candiID;
    }

    /**
     * Gets the ID of this candidate structure.
     *
     * @return the candi ID
     */
    public String getCandiID() {
        return this.candiStrucID;
    }

    /**
     * Sets the match peak list.
     *
     * @param matchPeakList the new match peak list
     */
    public void setMatchPeakList(ArrayList<PeakInfo> matchPeakList) {
        this.matchPeakList = matchPeakList;
    }

    /**
     * Gets the match peak list.
     *
     * @return the match peak list
     */
    public ArrayList<PeakInfo> getMatchPeakList() {
        return this.matchPeakList;
    }

    /**
     * Sets the score.
     *
     * @param score the new score
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Gets the score.
     *
     * @return the score
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Sets the matched num.
     *
     * @param matchedNum the new matched num
     */
    public void setMatchedNum(int matchedNum) {
        this.matchedNum = matchedNum;

    }

    /**
     * Gets the matched num.
     *
     * @return the matched num
     */
    public int getMatchedNum() {
        return this.matchedNum;
    }

    /**
     * Sets the union matched num.
     *
     * @param unionNum the new union matched num
     */
    public void setUnionMatchedNum(int unionNum) {
        this.unionMatchedNum = unionNum;
    }

    /**
     * Gets the union matched num.
     *
     * @return the union matched num
     */
    public int getUnionMatchedNum() {
        return this.unionMatchedNum;
    }

    /**
     * Sets the miss matched num.
     *
     * @param missMatchedNum the new miss matched num
     */
    public void setMissMatchedNum(int missMatchedNum) {
        this.missMatchedNum = missMatchedNum;
    }

    /**
     * Gets the miss matched num.
     *
     * @return the miss matched num
     */
    public int getMissMatchedNum() {
        return this.missMatchedNum;
    }

    /**
     * Sets the score info str.
     *
     * @param infoStr the new score info str
     */
    public void setScoreInfoStr(String infoStr) {
        this.infoStr = infoStr;
    }

    /**
     * Gets the score info str.
     *
     * @return the score info str
     */
    public String getScoreInfoStr() {
        return this.infoStr;
    }
}
