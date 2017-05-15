package util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import org.eurocarbdb.dataaccess.ms.PeakList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import debug.Print;
import spectrum.Peak;

//TODO Whether this class can be replaced by any existing project?
/**
 * 
 * Whether this class can be replaced by JRAP?
 * see: http://tools.proteomecenter.org/wiki/index.php?title=Software:JRAP
 * and http://tools.proteomecenter.org/wiki/index.php?title=Formats:mzXML#Additional_Resources
 *
 */
public class MzXMLReader {
    private DefaultHandler hander = new pepXMLHandler();

    private StringBuilder charBuffer;

    private String peak_num;

    private String peak_str = "";

    private String preTag;
    
    public void init(String pep_xml) {
        try{
            File pepXML_file = new File(pep_xml);
            charBuffer = new StringBuilder(1 << 20);
            peak_str = new String();
            SAXParserFactory sax_fac = SAXParserFactory.newInstance();
            SAXParser sax_parser = sax_fac.newSAXParser();
            sax_parser.parse(pepXML_file, hander);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public  Peak[] get_peak_list() {
         Peak[] peak_group = decode_str(this.peak_str,
                Integer.parseInt(this.peak_num));
        return peak_group;
    }
    public ArrayList<double[]> getPeakList(){
        Peak[] peak_group = decode_str(this.peak_str,
                Integer.parseInt(this.peak_num));
        ArrayList<double[]> peakList=new ArrayList<double[]>();
        for(Peak iterPeak: peak_group)
        {
            double[] peakPair=new double[2];
            peakPair[0]=iterPeak.getMz(); // backup:iterPeak.mz;
            peakPair[1]=iterPeak.getIntensity(); // backup:iterPeak.Intensity
            peakList.add(peakPair);
        }
        return peakList;
    }

    public Peak[] getPeakData() {
        Peak[] peak_group = decode_str(this.peak_str,
                Integer.parseInt(this.peak_num));
        return peak_group;
    }

    public double[][] getPeakGroup() {
        Peak[] peak_data = decode_str(this.peak_str,
                Integer.parseInt(this.peak_num));
        double[][] peak_group = new double[peak_data.length][2];
        for (int i = 0; i < peak_data.length; i++) {
            peak_group[i][0] = peak_data[i].getMz();
            peak_group[i][1] = peak_data[i].getIntensity();
        }
        return peak_group;
    }

    public class pepXMLHandler extends DefaultHandler {
        public void startElement(String uri, String localName, String qName,
                Attributes attrs) throws SAXException {

            if (qName.equals("scan")) {
                peak_num = attrs.getValue("peaksCount");
                charBuffer.setLength(0);
            }
            if (qName.equals("peaks")) {
                peak_str = "";
                charBuffer.setLength(0);
            }
            preTag = qName;
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            // if (qName.equals("peaks"))
            // {
            // preTag=null;
            // }

        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (preTag.equals("peaks") && Integer.parseInt(peak_num) >= 1) {
                peak_str = peak_str + new String(ch, start, length);
            }
        }
    }

    public Peak[] decode_str(String peak_str, int peak_count) {
        if (peak_str == null)
            return new Peak[0];

        try {
            byte[] b = Base64.decode(peak_str);
//            System.out.println(peak_str);
            DataInputStream peakStream = new DataInputStream(
                    new ByteArrayInputStream(b));
            Peak[] peak_group;
            Vector<Peak> peak_vector = new Vector<Peak>();
            for (int i = 0; i < peak_count; i++) {
                double value_mz = peakStream.readFloat();
                double value_intens = peakStream.readFloat();
                Peak peak = new Peak(value_mz, value_intens);
                peak_vector.add(peak);
            }

            peak_group = peak_vector.toArray(new Peak[0]);
            return peak_group;
        } catch (Exception e) {
            System.out.print("In class MzXMLReader: decode_str: ");
            System.out.println(e.getMessage());
            return new Peak[0];
        }
    }
    
    /**
     * The main method for testing this Class
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        String mzXML="D:/Data/Glycan_Data/GIPS_experiment_dataset_TXT/2017_05_12_resolusion_analysis/mzxml/250/hybrid_1824/nga2f-hybrid-4-4-ms1-ms2-1824-ms3-1565-20001.mzXML";
        MzXMLReader test = new MzXMLReader();
        test.init(mzXML);
        Peak[] pl = test.get_peak_list();
        for(Peak p: pl){
            Print.pl(p.getMz() + "\t" + p.getIntensity());
        }
        
    }
}
