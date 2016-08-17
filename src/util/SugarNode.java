package util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
public class SugarNode implements Cloneable,Serializable{
    final static long serialVersionUID=111112;
    public String node_string;
    public double node_mass;
    public int node_level;
    public String node_type;
    public SugarNode parent_node;
    public ArrayList<SugarNode> child_list;
    public double tree_mass;
    public int bond_cut_time=0;
    public int node_ID=0;
    public String bond_cut_type="";
    public boolean equals(SugarNode tmpSu)
    {
        if(tmpSu.node_type.equals(this.node_type)
                &&tmpSu.node_mass==this.node_mass
                &&tmpSu.node_level==this.node_level
                &&this.listEqual(tmpSu.child_list))
            
        return true;
        else return false;
    }
    private boolean listEqual(ArrayList<SugarNode> tmpList)
    {
        boolean re=true;
        if(tmpList.size()!=this.child_list.size())
        {
            return false;
        }else
        {
            for(int i=0;i<this.child_list.size();i++)
            {
                if(!this.child_list.get(i).equals(tmpList.get(i)))
                    return false;
            }
        }
        return re;
    }
    public SugarNode clone() { 
        SugarNode o = null; 
        try { 
        o = (SugarNode)super.clone(); 
        } catch (CloneNotSupportedException e) {} 
        return o;

        }
    /*
     * realize the complete object clone through serialize
     * 
     */
    public static SugarNode cloneObject(Object obj){
        try{
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); 
            ObjectOutputStream out = new ObjectOutputStream(byteOut); 
            out.writeObject(obj); 
            
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray()); 
            ObjectInputStream in =new ObjectInputStream(byteIn);
            
            return (SugarNode)in.readObject();
            
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        
    }
    public SugarNode()
    {
        this.node_string=new String();
        this.node_mass=0;
        this.node_level=-1;
        this.parent_node =null;
        this.child_list=null;
    }
    public SugarNode(String node_string,double node_mass,int node_level,SugarNode parent_node,ArrayList<SugarNode> child_list)
    {
        this.node_string =node_string;
        this.node_mass=node_mass;
        this.node_level=node_level;
        this.parent_node=parent_node;
        this.child_list=child_list;
        
    }
    public void setTreeMass(double tree_mass)
    {
        this.tree_mass=tree_mass;
    }
    public double getTreeMass()
    {
        return this.tree_mass;
    }
    public void setNodeStr(String node_str)
    {
        this.node_string=node_str;
        
    }
    public String getNodeStr()
    {
        return this.node_string;
    }
    
    public void setNodeType(String node_type)
    {
        this.node_type=node_type;
    }
    public String getNodeType()
    {
        return this.node_type;
    }
    
    public void setNodeMass(double node_mass)
    {
        this.node_mass=node_mass;
    }
    public double getNodeMass()
    {
        return this.node_mass;
    }
    public void setNodeLevel(int node_level)
    {
        this.node_level=node_level;
    }
    public int getNodeLevel()
    {
        return this.node_level;
    }
    public void setParentNode(SugarNode parent_node)
    {
        this.parent_node=parent_node;
    }
    public SugarNode getParentNode()
    {
        return this.parent_node;
    }
    public void setChildList(ArrayList<SugarNode> child_list)
    {
        this.child_list=child_list;
    }
    public ArrayList<SugarNode> getChildList()
    {
        return this.child_list;
    }
    public void setNodeId(int id)
    {
        this.node_ID=id;
    }
    public int getNodeId()
    {
        return this.node_ID;
    }
    public void setBondCutType(String cutType)
    {
        this.bond_cut_type=cutType;
    }

}
