package util;
import java.util.*;
public class FragmentTree {
    ArrayList<FragNode> fragnode_list=new ArrayList<FragNode>();
    public void build()
    {
        /*
        FragNode big_node=new FragNode();
        big_node.setMass(1579);
        big_node.setNode(node);
        */
    }
    
    public void fragment(SugarNode root_node, int cut_time,FragNode big_node)
    {
//        this.test2(root_node);
        if(root_node.getChildList().size()>0&& big_node.getSugarNode().bond_cut_time<5)
//        if(root_node.getChildList().size()>0)
        {
            for(int i=0;i<root_node.getChildList().size();i++)
            {
                
                SugarNode child_node=SugarNode.cloneObject(root_node.getChildList().get(i));
                SugarNode tt_node=SugarNode.cloneObject(big_node.getSugarNode());
                
                
                /*
                 * trim b ion, then get the y ion 
                 */
                SugarNode tmp_node=trim(tt_node,child_node);
                
                
                FragNode frag_node_y=new FragNode();
                FragNode frag_node_b=new FragNode();
                
                /*
                 * count b ion setMass
                 */
                
                double subTreeMass=getSubTreeMass(child_node);
                frag_node_b.setSubtreeMass(subTreeMass+37);
                frag_node_b.setIonType(IonType.BIon);
                frag_node_b.setIonTypeNote(big_node.getIonTypeNote()+"B");
                
                frag_node_y.setSubtreeMass(big_node.getSubtreeMass()-frag_node_b.getSubtreeMass()+23);
                frag_node_y.setIonType(IonType.YIon);
                frag_node_y.setIonTypeNote(big_node.getIonTypeNote()+"Y");
                
//                System.out.println(frag_node_b.getMass()+"\t"+frag_node_y.getMass());
                child_node.bond_cut_time=big_node.getSugarNode().bond_cut_time+1;
                tmp_node.bond_cut_time=big_node.getSugarNode().bond_cut_time+1;
                
                
                
                frag_node_b.setSugarNode(child_node);
                frag_node_y.setSugarNode(tmp_node);
                
                frag_node_b.setCutPos(child_node.node_ID);
                frag_node_y.setCutPos(child_node.node_ID);
                
                
                frag_node_b.setStrucID(this.getStrucIDStr(child_node));
                frag_node_y.setStrucID(this.getStrucIDStr(tmp_node));
//                System.out.println("b_ID:"+child_node.node_ID+"\t"+"y_ID:"+tmp_node.node_ID+"\t"+child_node.getTreeMass()+"\t");
                /*
                System.out.println("b_ID:"+child_node.node_ID);
                this.test2(child_node);
                System.out.println("y_ID:"+tmp_node.node_ID);
                this.test2(tmp_node);
                */
                
                
                big_node.getSubTreeNodeList().add(frag_node_b);
                big_node.getSubTreeNodeList().add(frag_node_y);
                
                fragment(child_node,cut_time,big_node);
                
//                fragment(tmp_node,cut_time,big_node);
                
                fragment(tmp_node,1,frag_node_y);
                fragment(child_node,1,frag_node_b);
                
            }
        }
    }
    public String getStrucIDStr(SugarNode rootNode)
    {
        
        
        String strucID=String.valueOf(rootNode.getNodeId());
        if(rootNode.getChildList()!=null)
        {
            for(SugarNode iterNode:rootNode.getChildList())
            {
                strucID=strucID+getStrucIDStr(iterNode);
            }
        }
        return strucID;
    }
    
    public void test2(SugarNode root_node)
    {
        System.out.println(root_node.getNodeId()+"\t"+root_node.node_level);
        if(root_node.getChildList().size()>0)
        {
            for(SugarNode iter:root_node.getChildList())
            {
                test2(iter);
            }
        }
    }
    public void recurs(SugarNode root_node, int cut_time,FragNode big_node)
    {
        big_node.setStrucID(this.getStrucIDStr(root_node));
        fragment(root_node, cut_time, big_node);
//        for(FragNode iter:big_node.getSubTreeNodeList())
        {
//            fragment(iter.getNode(),1,iter);
        }
    }
    public double getSubTreeMass(SugarNode node)
    {
        double re_mass=SugarTree.CountMass(node);
        return re_mass;
    }
    public SugarNode trim(SugarNode big_node,SugarNode branch_node)
    {
        
        /*
        System.out.println("big_node_ID_a:"+big_node.node_ID);
        this.test2(big_node);
        System.out.println("branch_node_ID_a:"+big_node.node_ID);
        this.test2(branch_node);
        int origNum=this.getNodeNum(big_node);
        */
        trimTree(big_node,branch_node);
        /*
        int trimNum=this.getNodeNum(big_node);
        System.out.println("big_node_ID_b:"+big_node.node_ID);
        this.test2(big_node);
        if(origNum==trimNum)
        {
            System.out.println("node_num:"+this.getNodeNum(big_node));
        }
        */
        return big_node;
    }
    public void trimTree(SugarNode big_node,SugarNode branch_node)
    {
        if(big_node==null||big_node.child_list.size()<1)
        {
            return;
        }
        
        
        boolean not_find=true;
        for(SugarNode iter_node:big_node.child_list)
        {
            if(iter_node.getNodeId()==branch_node.getNodeId())
            {
                big_node.child_list.remove(iter_node);
                big_node.bond_cut_time++;
                not_find=false;
                return;                
            }
        }
        if(not_find)
        {
            for(SugarNode iter_node:big_node.child_list)
            trimTree(iter_node,branch_node);
        }
        /*
        System.out.println("not find");
        System.out.println("big_node_ID:"+big_node.node_ID);
        this.test2(big_node);
        System.out.println("branch_node_ID:"+branch_node.node_ID);
        this.test2(branch_node);
        System.out.print("not fin2");
        */
        
    }
    public int getNodeNum(SugarNode rootNode)
    {
        int num=1;
        if(rootNode.child_list.size()>0)
        {
            for(SugarNode iterNode:rootNode.child_list)
            {
                num=num+getNodeNum(iterNode);
            }
            
        }
        
        return num;
        
    }
    public static void getNodeSet(FragNode frag_node,ArrayList<Double> frag_mass_list)
    {
        double frag_mass=FormatNum.DoubleFormat(frag_node.getSubtreeMass(), 3);
        if(!frag_mass_list.contains(frag_mass))
            
            frag_mass_list.add(frag_mass);
            
        if(frag_node.getSubTreeNodeList().size()>0)
        {
            for(FragNode iter:frag_node.getSubTreeNodeList())
            {
                getNodeSet(iter,frag_mass_list);
            }
        }
    }
    public static void getFragNodeSet(FragNode frag_node,ArrayList<FragNode> frag_mass_list)
    {
        
        if(!frag_mass_list.contains(frag_node))
            
            frag_mass_list.add(frag_node);
            
        if(frag_node.getSubTreeNodeList().size()>0)
        {
            for(FragNode iter:frag_node.getSubTreeNodeList())
            {
                getFragNodeSet(iter,frag_mass_list);
            }
        }
    }
    public static void outPrint2(FragNode frag_node)
    {
        ArrayList<FragNode> frag_mass_list=new ArrayList<FragNode>();
        getFragNodeSet(frag_node,frag_mass_list);
//        Collections.sort(frag_mass_list);
        DataFilter.filterTheorySpSameMassIon(frag_mass_list);
        for(FragNode iter:frag_mass_list)
        {
            System.out.println(iter.getSubtreeMass()+"\t"+iter.getSugarNode().bond_cut_time+"\t"+iter.getIonTypeNote()+"\t"+iter.getStrucID());
        }
    }
    public static void outPrint(FragNode frag_node)
    {
        ArrayList<Double> frag_mass_list=new ArrayList<Double>();
        getNodeSet(frag_node,frag_mass_list);
//        Collections.sort(frag_mass_list);
        for(Double iter:frag_mass_list)
        {
            System.out.println(iter);
        }
    }
}
