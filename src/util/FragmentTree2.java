package util;
import java.util.*;

public class FragmentTree2 {
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
//		this.test2(root_node);
		if(root_node.getChildList().size()>0&& big_node.getIonTypeNote().length()<5)
//		if(root_node.getChildList().size()>0)
		{
			for(int i=0;i<root_node.getChildList().size();i++)
			{
				
				SugarNode child_node=SugarNode.cloneObject(root_node.getChildList().get(i));
				SugarNode tt_node=SugarNode.cloneObject(big_node.getSugarNode());
				SugarNode child_node2=SugarNode.cloneObject(root_node.getChildList().get(i));
				SugarNode tt_node2=SugarNode.cloneObject(big_node.getSugarNode());
				
				/*
				 * trim b ion, then get the y ion 
				 */
				SugarNode tmp_node=trim(tt_node,child_node);
				
				SugarNode tmp_node2=trim2(tt_node2,child_node2); 
				
				FragNode frag_node_y=new FragNode();
				FragNode frag_node_b=new FragNode();
				
				FragNode frag_node_z=new FragNode();
				FragNode frag_node_c=new FragNode();
				
				/*
				 * count b c ion setMass
				 */
				
				double subTreeMass=getSubTreeMass(child_node);
				frag_node_b.setSubtreeMass(subTreeMass+37);
				frag_node_b.setIonType(IonType.BIon);
				
				frag_node_b.setIonTypeNote(big_node.getIonTypeNote()+"B");
				
				frag_node_c.setSubtreeMass(subTreeMass+37+18);
				frag_node_c.setIonType(IonType.CIon);
				frag_node_c.setIonTypeNote(big_node.getIonTypeNote()+"C");
				
				
				frag_node_y.setSubtreeMass(big_node.getSubtreeMass()-frag_node_b.getSubtreeMass()+23);
				frag_node_y.setIonType(IonType.YIon);
				frag_node_y.setIonTypeNote(big_node.getIonTypeNote()+"Y");
				
				frag_node_z.setSubtreeMass(big_node.getSubtreeMass()-frag_node_c.getSubtreeMass()+23);
				frag_node_z.setIonType(IonType.ZIon);
				frag_node_z.setIonTypeNote(big_node.getIonTypeNote()+"Z");
				
//				System.out.println(frag_node_b.getMass()+"\t"+frag_node_y.getMass());
				
				frag_node_b.setSugarNode(child_node);
				frag_node_y.setSugarNode(tmp_node);
				
				frag_node_b.setCutPos(child_node.node_ID);
				frag_node_y.setCutPos(child_node.node_ID);
				
				frag_node_b.setStrucID(this.getStrucIDStr(child_node));
				frag_node_y.setStrucID(this.getStrucIDStr(tmp_node));
				

				frag_node_c.setSugarNode(child_node2);
				frag_node_z.setSugarNode(tmp_node2);
				
				frag_node_c.setCutPos(child_node.node_ID);
				frag_node_z.setCutPos(child_node.node_ID);
				
				frag_node_c.setStrucID(this.getStrucIDStr(child_node2));
				frag_node_z.setStrucID(this.getStrucIDStr(tmp_node2));

				
				big_node.getSubTreeNodeList().add(frag_node_b);
				big_node.getSubTreeNodeList().add(frag_node_y);
				
				big_node.getSubTreeNodeList().add(frag_node_c);
				big_node.getSubTreeNodeList().add(frag_node_z);
				
				
				fragment(child_node,cut_time,big_node);
				fragment(tmp_node,1,frag_node_y);
				fragment(child_node,1,frag_node_b);
				
				fragment(tmp_node2,1,frag_node_z);
				fragment(child_node2,1,frag_node_c);
				
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
//		for(FragNode iter:big_node.getSubTreeNodeList())
		{
//			fragment(iter.getNode(),1,iter);
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
	public SugarNode trim2(SugarNode big_node,SugarNode branch_node)
	{
		
		/*
		System.out.println("big_node_ID_a:"+big_node.node_ID);
		this.test2(big_node);
		System.out.println("branch_node_ID_a:"+big_node.node_ID);
		this.test2(branch_node);
		int origNum=this.getNodeNum(big_node);
		*/
		trimTree2(big_node,branch_node);
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
	public void trimTree2(SugarNode big_node,SugarNode branch_node)
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
				big_node.bond_cut_type=big_node.bond_cut_type+"z";
				not_find=false;
				return;				
			}
		}
		if(not_find)
		{
			for(SugarNode iter_node:big_node.child_list)
			trimTree2(iter_node,branch_node);
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
	
	public static void outPrint(FragNode frag_node)
	{
		ArrayList<Double> frag_mass_list=new ArrayList<Double>();
		getNodeSet(frag_node,frag_mass_list);
//		Collections.sort(frag_mass_list);
		for(Double iter:frag_mass_list)
		{
			System.out.println(iter);
		}
	}
}
