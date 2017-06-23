package util;

import java.io.File;
import java.util.ArrayList;

import debug.Print;
import spectrum.Peak;

// TODO: Auto-generated Javadoc
/**
 * The Class SPComponent.
 * This class represents a specific spectrum
 */
public class SPComponent {
    
    /** The file ID of this spectrum. */
    String spFileID;
    
    /** The file ID of previous spectrum. for building tree?? */
    String spPreFileID;
    
    /** 
     * The level of this spectrum. 
     * i.e. spLevel = 2 means this spectrum is generated in MS2 
     */
    int spLevel;
    
    /** The cut tree path. ??*/
    //ArrayList<Integer> cutTreePath;
    
    /** The array containing all peaks of this spectrum. */
    Peak[] peakArray;
    
    /** 
     * A list containing M/Z of all precursor ion(recursively). 
     * This list is currently obtained by the filename of this spectrum.
     */
    ArrayList<Double> preMzList;

    /**
     * Instantiates a new SP component.
     *
     * @param file the spectrum file
     */
    public SPComponent(File file) {
        peakArray = loadSp(file.getAbsolutePath());
        setRelativeAndAbsoluteInts(peakArray);
        spFileID = file.getName().split("\\.")[0];
        String[] splits = spFileID.split("_");
        spLevel = splits.length + 1;
        preMzList = new ArrayList<Double>();
        if (spLevel > 2) {
            spPreFileID = spFileID.substring(0, spFileID.lastIndexOf("_"));
        } else {
            spPreFileID=null;
        }
        for (String iterMzStr : splits) {
            preMzList.add(Double.parseDouble(iterMzStr));
        }
        
    }

    /**
     * Load sp.
     *
     * @param spFile the sp file
     * @return the peak[]
     */
    public Peak[] loadSp(String spFile)
    {
        Peak[] expIonArray=null;
        try{
            MzXMLReader mzxmlReader=new MzXMLReader();
            //Print.pl("debug!");
            mzxmlReader.init(spFile);
            //Print.pl("debug!!");
            expIonArray = mzxmlReader.get_peak_list();
            //Print.pl("debug!!!");
        }catch(Exception e){
            Print.pl("load peaks error!(in SPComponent:loadSp)");
            e.printStackTrace();
        }
        return expIonArray;
    }
    
    private void setRelativeAndAbsoluteInts(Peak[] peaks){
//        if(peaks == null){
//            return;
//        }
        double maxInts = 0;
        for(Peak p: peaks){
            if(p != null){
                //Print.pl("Peak: "+ p);
                if(p.getIntensity() > maxInts){
                    maxInts = p.getIntensity();
                }
            }
        }
        for(Peak p: peaks){
            if(p != null){
                p.setAbsoluteIntens(p.getIntensity());
                p.setRelativeIntens(p.getIntensity() / maxInts * 100);
        
            }
        }
    }
    
    /**
     * Gets the sp level.
     *
     * @return the sp level
     */
    public int getSpLevel() {
        return this.spLevel;
    }

    /**
     * Gets the peak array.
     *
     * @return the peak array
     */
    public Peak[] getPeakArray() {
        return this.peakArray;
    }

    /**
     * Gets the list containing M/Z of all precursor ion(recursively). 
     *
     * @return the list
     */
    public ArrayList<Double> getPreMzList() {
        return this.preMzList;
    }

    /**
     * Gets the sp pre file ID.
     *
     * @return the sp pre file ID
     */
    public String getSpPreFileID() {
        return this.spPreFileID;
    }

    /**
     * Gets the sp file ID.
     *
     * @return the sp file ID
     */
    public String getSpFileID() {
        return this.spFileID;
    }
}
