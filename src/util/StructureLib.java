package util;

import java.util.ArrayList;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class StructureLib.
 * A library containing all glycan structures.
 */
public class StructureLib {
    
    /** The list containing masses of all structures. */
    ArrayList<Double> massIndexList;
    
    /** 
     * This table stores all structures and their masses. 
     * The first double is the mass. The second list stores all structures(>=1)
     * corresponding to this mass value.
     * One structure is stored by a string linking lines of input file.
     * like below: 
     *          a-D-Manp-(1-6)+
                       |
                  a-D-Manp-(1-6)+
                       |        |
         a-D-Manp-(1-3)+   b-D-Manp-(1-4)-b-D-GlcpNAc-(1-4)-D-GlcNAc
                                |
                  a-D-Manp-(1-3)+
     */
    Hashtable<Double,ArrayList<String>> candiStrucHash;
    
    /**
     * Instantiates a new structure library.
     *
     * @param massIndexList the mass index list
     * @param candiStrucHash the candi struc hash
     */
    public StructureLib(ArrayList<Double> massIndexList,Hashtable<Double,ArrayList<String>> candiStrucHash)
    {
        this.massIndexList=massIndexList;
        this.candiStrucHash=candiStrucHash;
    }
    
    /**
     * Gets the list containing masses of all structures.
     *
     * @return the mass index list
     */
    public ArrayList<Double> getMassIndexList()
    {
        return this.massIndexList;
    }
    
    /**
     * Gets the candidate structure hashtable.
     *
     * @return the candi struc hash
     */
    public Hashtable<Double,ArrayList<String>> getCandiStrucHash()
    {
        return this.candiStrucHash;
    }

}
