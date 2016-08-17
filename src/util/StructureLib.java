package util;
import java.util.*;

public class StructureLib {
    ArrayList<Double> massIndexList;
    Hashtable<Double,ArrayList<String>> candiStrucHash;
    public StructureLib(ArrayList<Double> massIndexList,Hashtable<Double,ArrayList<String>> candiStrucHash)
    {
        this.massIndexList=massIndexList;
        this.candiStrucHash=candiStrucHash;
    }
    public ArrayList<Double> getMassIndexList()
    {
        return this.massIndexList;
    }
    public Hashtable<Double,ArrayList<String>> getCandiStrucHash()
    {
        return this.candiStrucHash;
    }

}
