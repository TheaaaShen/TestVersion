package util;
import java.util.*;
public class SugarTree {
	public ArrayList<SugarNode> node_list=new ArrayList<SugarNode>();
	
	public void setNodeList(ArrayList<SugarNode> node_list)
	{
		this.node_list =node_list;
	}
	/*
	 * recursion to add the child tree mass
	 * the node cut time considered
	 * delete cut_time*14
	 */
	public  static double CountMass(SugarNode root_node)
	{
		double mass=0;
		if(root_node.getChildList().size()>0)
		{
			for(SugarNode iter_node:root_node.getChildList())
			{
				mass=mass+CountMass(iter_node);
			}
		}
		char[] typeArray=root_node.bond_cut_type.toCharArray();
		if(typeArray.length>0)
		{
			
			for(int i=0;i<typeArray.length;i++)
			{
				if(typeArray[i]=='c')
				{
					mass=mass+18;
				}else if(typeArray[i]=='z')
				{
					mass=mass-18;
				}
			}
		}
			
		return mass+root_node.getNodeMass()-root_node.bond_cut_time*14;
	}
	
}
