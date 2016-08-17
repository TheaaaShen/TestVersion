package util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import spectrum.Peak;

public class MzXmlConverter {
    private DefaultHandler hander = new pepXMLHandler();

    private StringBuilder charBuffer;

    private String peak_num;

    private String peak_str = "";

    private String spectrum_name;

    private String preTag;
    
    private static DecimalFormat df1 = new DecimalFormat("####");
    private static DecimalFormat df2 = new DecimalFormat("####.##");

    public void init(String pep_xml) throws Exception {
        File pepXML_file = new File(pep_xml);
        charBuffer = new StringBuilder(1 << 20);
        SAXParserFactory sax_fac = SAXParserFactory.newInstance();
        SAXParser sax_parser = sax_fac.newSAXParser();
        sax_parser.parse(pepXML_file, hander);
    }

    public void writeOut(String result_file) {
        Peak[] peak_group = decode_str(this.peak_str,
                Integer.parseInt(this.peak_num));
        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(
                    result_file));
            for (Peak iter : peak_group) {
                // backup: df1.format(iter.Intensity));
                outfile.write(df2.format(iter.getMz()) + "\t" 
                        + df1.format(iter.getIntensity()));
                outfile.newLine();
            }
            outfile.flush();
            outfile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (preTag.equals("peaks") && Integer.parseInt(peak_num) > 1) {
                peak_str = peak_str + new String(ch, start, length);
            }
        }
    }

    public static Peak[] decode_str(String peak_str, int peak_count) {
        if (peak_str == null)
            return null;

        try {
            byte[] b = Base64.decode(peak_str);
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
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void convertFiles(String input_folder,String out_folder)
    {
        File in=new File(input_folder);
        for(File iter:in.listFiles())
        {
            MzXmlConverter conv=new MzXmlConverter();
            try {
                conv.init(iter.getAbsolutePath());
            } catch (Exception e) {
                // TODO 自动生成 catch 块
                e.printStackTrace();
            }
//            conv.writeOut(out_folder+iter.getName().replace("mzXML", "dta"));
            conv.writeOut(out_folder+iter.getName().replace("mzXML", "txt"));
        }
    }
    public static void main(String[] args) throws Exception {
//        String mzXML = "F:/Postgraduate/project/data/1_serum-ms2-1579.46-1-0001.mzXML";
        // String mzXML="I:/test_mz.txt";
//        String result_file = "F:/Postgraduate/project/data/txtData/1579.txt";
//        String mzXML="G:/Oligo/MZXML/mzxml/3_serum-ms2-1783.44-1-0001.mzXML";
//        String result_file="e:/Users/Administrator/Desktop/carbbank/test/3.txt";
//        mzXMLConvert test = new mzXMLConvert();
//        test.init(mzXML);
//        test.writeOut(result_file);
        // byte[] b=Base64.decode("");
        // b.toString();
//        String input_folder="e:/Users/Administrator/Desktop/糖数据管理库开发/糖质谱数据/2014-04-19MS2原始数据/mzxml";
//        String out_folder="e:/Users/Administrator/Desktop/糖数据管理库开发/糖质谱数据/2014-04-19MS2原始数据/dta/";
        MzXmlConverter test = new MzXmlConverter();
        
//        String input_file="e:/Users/Administrator/Desktop/糖数据管理库开发/糖质谱数据/20140419_1579/Permethylated MAN5-no column-ms2-1579-0001.mzXML";
//        String result_file="e:/Users/Administrator/Desktop/糖数据管理库开发/糖质谱数据/20140419_1579/1579.dta";
//        String input_file="E:/Glycan/spectra_data/20140725pre_data/1579/MAN5-MS2-1579-350-0001.mzXML";
//        String result_file="E:/Glycan/spectra_data/20140725pre_data/1579/MAN5-MS2-1579-350-0001.dta";
//        String input_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man6_1783/1783_1506.mzXML";
//        String result_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/experimentDta/1783_1506.dta";
//        String input_file="E:/Glycan/Glycan2/NGA3/mzxml2/1907.mzXML";
//        String result_file="E:/Glycan/Glycan2/NGA3/1907_peaklist.txt";
//        String input_file="E:/Glycan/Glycan2/NGA4/mzxml2/2151_1892_1633_1374.mzXML";
//        String result_file="E:/Glycan/Glycan2/NGA4/2151_1892_1633_1374_peaklist.txt";
//        String input_file="E:/Glycan/Glycan2/hybrid/mzxml2/1824_1565.mzXML";
//        String result_file="E:/Glycan/Glycan2/hybrid/1824_1565_peakList.txt";
//        String input_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man8_2192/2192.mzXML";
//        String result_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man8_2192_peakList.txt";
//        String input_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man5_1579/1579.mzXML";
//        String result_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/peakList/man5_1579_peaklist.txt";
//        String input_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man7_1987/1987_1565.mzXML";
//        String result_file="E:/Glycan/spectra_data/20140725pre_data/MultiData/man7_1987/1987_1565_peaklist.txt";
//        String input_file="E:/Glycan/Glycan3/spectra/man5/mzxml3/1579_1302.mzXML";
//        String result_file="E:/Glycan/Glycan3/1579_1302_peakList.txt";
        String input_file="C:/Users/Frank/Desktop/spectraData/IgG_mzxml/ms1_1.mzXML";
        String result_file="C:/Users/Frank/Desktop/spectraData/IgG_mzxml/ms1_1.txt";
//        String input_file="E:/Glycan/Glycan3/spectra/NGA4/mzxml5/2151_1893.mzXML";
//        String result_file="E:/Glycan/Glycan3/spectra/NGA4/mzxml5/2151_1893_peaklist.txt";
//        test.init(input_file);
//        test.writeOut(result_file);
//        String input_folder="C:/Users/Frank/Desktop/spectraData/bianLi/man6_mz/";
//        String out_folder="C:/Users/Frank/Desktop/spectraData/bianLi/man6_txt/";
        String base="E:/Glycan/Glycan5/2015-06-11/spectra/mzxml4/";
//        String input_folder=base+"1987/";
        String input_folder="E:/Glycan/Glycan5/2015-08-14/spectra/1987_d1/";
//        String out_folder=base+"1987d3_txt/";
        String out_folder="E:/Glycan/Glycan5/2015-08-14/spectra/1987_d1_txt/";
//        String input_folder="C:/Users/Frank/Desktop/spectraData/standard/";
//        String out_folder="C:/Users/Frank/Desktop/spectraData/standard_txt/";
//        String input_folder="C:/Users/Frank/Desktop/spectraData/ms1/";
//        String out_folder="C:/Users/Frank/Desktop/spectraData/ms1_txt/";
//        String input_folder="C:/Users/Frank/Desktop/spectraData/revised_mzxml/";
//        String out_folder="C:/Users/Frank/Desktop/spectraData/revised_txt/";
        test.convertFiles(input_folder, out_folder);
    }
}
