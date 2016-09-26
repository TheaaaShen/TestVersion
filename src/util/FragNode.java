package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class FragNode implements Cloneable, Serializable {
    final static long serialVersionUID = 111113;
    SugarNode node;
    int frag_time;
    double subtree_mass;
    IonType ionType;
    String ionTypeNote;
    String strucID;
    int cutPos;
    public ArrayList<FragNode> sub_node_list;
    public ArrayList<Character> cutTypeList;

    public FragNode clone() {
        FragNode o = null;
        try {
            o = (FragNode) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return o;
    }

    public static FragNode cloneObject(Object obj) {
        try {

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);

            return (FragNode) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setIonTypeNote(String ionTypeNote) {
        this.ionTypeNote = ionTypeNote;
    }

    public String getIonTypeNote() {
        return this.ionTypeNote;
    }

    public void setCutPos(int cutPos) {
        this.cutPos = cutPos;
    }

    public int getCutPos() {
        return this.cutPos;
    }

    public void setStrucID(String strucID) {
        this.strucID = strucID;
    }

    public String getStrucID() {
        return this.strucID;
    }

    public void setIonType(IonType type) {
        this.ionType = type;
    }

    public IonType getIonType() {
        return this.ionType;
    }

    public FragNode() {
        frag_time = 0;
        subtree_mass = 0;
        sub_node_list = new ArrayList<FragNode>();
        cutTypeList = new ArrayList<Character>();
        this.ionTypeNote = new String();
    }

    public void setSugarNode(SugarNode node) {
        this.node = node;
    }

    public void setSubtreeMass(double mass) {
        this.subtree_mass = mass;
    }

    public void setSubNodeList(ArrayList<FragNode> node_list) {
        this.sub_node_list = node_list;
    }

    public SugarNode getSugarNode() {
        return this.node;
    }

    public double getSubtreeMass() {
        return FormatNum.DoubleFormat(this.subtree_mass, 2);
    }

    public ArrayList<FragNode> getSubTreeNodeList() {
        return this.sub_node_list;
    }

    public static ArrayList<FragNode> fragNodeListClone(ArrayList<FragNode> fragNodeList) {
        ArrayList<FragNode> reNodeList = new ArrayList<FragNode>();
        for(FragNode iterNode : fragNodeList) {
            reNodeList.add(FragNode.cloneObject(iterNode));
        }

        return reNodeList;
    }

    public ArrayList<FragNode> searchSubtreeNodeListWithMass(double nodeMass) {
        FragNode targetNode = null;
        /*
         * the nodeMass tolerance
         */
        // double massTolerance = 0.5;
        double massTolerance = 1.2;
        ArrayList<FragNode> nodeList = new ArrayList<FragNode>();
        /*
         * for ms2 correspond FragNode
         */

        if(Math.abs(this.getSubtreeMass() - nodeMass) < massTolerance) {
            nodeList.add(this);
            return nodeList;
        }
        ArrayList<FragNode> subNodeList = this.getSubTreeNodeList();
        for(int i = 0; i < subNodeList.size(); i++) {
            FragNode tmpNode = subNodeList.get(i);
            if(Math.abs(tmpNode.getSubtreeMass() - nodeMass) < massTolerance) {
                targetNode = tmpNode;
                nodeList.add(tmpNode);
            }
        }

        if(nodeList.size() < 1) {
            return null;
        }
        return nodeList;
    }

    public ArrayList<FragNode> searchSubtreeNodeListWithMass2(double nodeMass) {
        ArrayList<FragNode> nodeList = new ArrayList<FragNode>();
        nodeList = this.searchSubtreeNodeListWithMass(nodeMass);
        if(nodeList != null) {

        } else {
            nodeList = new ArrayList<FragNode>();
            ArrayList<FragNode> subNodeList = this.getSubTreeNodeList();
            if(subNodeList != null) {
                for(int i = 0; i < subNodeList.size(); i++) {
                    ArrayList<FragNode> tmpNodeList = subNodeList.get(i).searchSubtreeNodeListWithMass(nodeMass);
                    if(tmpNodeList != null) {
                        nodeList.addAll(tmpNodeList);
                    }
                }
            }
        }
        if(nodeList != null && nodeList.size() < 1) {
            nodeList = null;
        }
        return nodeList;
    }

    public ArrayList<FragNode> searchSubtreeNodeListWithMass3(double nodeMass, int cutTime) {

        double WIN = 1.5;
        ArrayList<FragNode> reList = new ArrayList<FragNode>();
        if(Math.abs(this.getSubtreeMass() - nodeMass) < WIN) {
            reList.add(this);
            return reList;
        }
        ArrayList<FragNode> nodeList = this.getCorrespondTheorySpList(cutTime);
        for(FragNode iterNode : nodeList) {
            double candiMass = iterNode.getSubtreeMass();
            if(Math.abs(candiMass - nodeMass) < WIN) {
                reList.add(iterNode);
            }
        }
        if(reList.size() > 0) {
            return reList;
        } else {
            return null;
        }

    }

    /*
     * @nodeMass:frag time return the fragment child tree ions based on the frag
     * times @nodeMass
     */

    public ArrayList<FragNode> getCorrespondTheorySpList(int cutTime) {
        GenerateTheorySp_Test countTheorySp = new GenerateTheorySp_Test();
        ArrayList<FragNode> returnList = countTheorySp.countMultiStageCutSpNoceList(this, cutTime);
        return returnList;
    }

    public ArrayList<FragNode> getCorrespondTheoryCutIonList(int cutTime) {
        GenerateTheorySp_Test countTheorySp = new GenerateTheorySp_Test();
        ArrayList<FragNode> returnList = countTheorySp.countMultiStageCutTheoryIons(this, cutTime);
        return returnList;
    }

    public boolean isNGlycanCore() {
        boolean glycanCore = false;
        SugarNode tmpSugarNode = this.getSugarNode();
        if(tmpSugarNode.getNodeStr().equalsIgnoreCase("D")) {
            if(tmpSugarNode.getChildList().size() > 0
                    && tmpSugarNode.getChildList().get(0).getNodeStr().equalsIgnoreCase("D")) {
                SugarNode level2Node = tmpSugarNode.getChildList().get(0);
                if(level2Node.getChildList().size() > 0 && level2Node.getChildList().size() == 1) {
                    SugarNode level3Node = level2Node.getChildList().get(0);
                    if(level3Node.getNodeStr().equalsIgnoreCase("M")) {
                        if(level3Node.getChildList().size() > 0 && level3Node.getChildList().size() == 2) {
                            ArrayList<SugarNode> level4NodeList = level3Node.getChildList();
                            if(level4NodeList.get(0).getNodeStr().equalsIgnoreCase("M")
                                    && level4NodeList.get(1).getNodeStr().equalsIgnoreCase("M"))
                                glycanCore = true;
                        }
                    }
                }

            }
        }
        return glycanCore;
    }
}
