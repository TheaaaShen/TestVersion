package util;

import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.analytical.mass.MassComponents;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

import java.util.*;
public class ConvertSugar {
	protected MassComponents m_objMasses = new MassComponents();
	protected boolean m_bMonoisotopic = true;
	public ArrayList<SugarNode> Node_list=new ArrayList<SugarNode>();
	int ID=0;
	
	/*
	 * convert:convert structure data type "sugar" to "FragmentTree"
	 */
	
	public FragNode convert(Sugar su)
	{
		FragNode big_node=new FragNode();
		
		try{
			/*
			 * GlycoNode-->SugarNode
			 */
			ArrayList<GlycoNode> root_list=su.getRootNodes();
			GlycoNode root=root_list.get(0);
			SugarNode tree_root=this.traverse(root,0);
			NormalizeTree(tree_root);
			this.resetNodeID(tree_root);
			tree_root.setTreeMass(SugarTree.CountMass(tree_root)+69);
			/*
			 * SugarNode-->FragmentTree
			 * FragmentTree2 considered the cz ions
			 */
			
//			FragmentTree test_tree=new FragmentTree();
			FragmentTree2 test_tree=new FragmentTree2();
			big_node.setSubtreeMass(tree_root.getTreeMass());
			big_node.setSugarNode(tree_root);
			big_node.setIonType(IonType.PIon);
			test_tree.recurs(tree_root, 1, big_node);
			
//			FragmentTree.outPrint2(big_node);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return big_node;
	}
	
	/*
	 * traverse the sugar tree and to build SugarNode
	 * SugarNode:@NodeMass,@NodeType,@NodeLevel,@NodeId
	 * prepare for fragment tree build
	 */
	  public SugarNode traverse(GlycoNode node,int level) throws GlycoVisitorException
	    {
		  
		  GlycoVisitor_wyjNode vist2=new GlycoVisitor_wyjNode();
		  node.accept(vist2);
		  double node_mass=vist2.getMass();
		  if(vist2.getNodeType().equalsIgnoreCase("Mono"))
		  {

			  if(node_mass>340&&node_mass<342)
			  {
				  node_mass=node_mass-2;
			  }
			  node_mass=node_mass-18;
			 
		  }else{
			  node_mass=node_mass-17;
		  }
//		  System.out.println(node_mass);
		  
		  SugarNode sugar_node=new SugarNode();
		  sugar_node.setNodeMass(node_mass);
		  sugar_node.setNodeType(vist2.getNodeType());
		  sugar_node.setNodeLevel(level);
		  sugar_node.setNodeId(ID++);
		  /*
		   * for N glycan core detect
		   */
		  sugar_node.setNodeStr("M");
		  
//		  System.out.println(sugar_node.getNodeId());
//		  System.out.println(node.getChildNodes().size()+"\t"+node_mass);
		  ArrayList<SugarNode> child_node_list=new ArrayList<SugarNode>();
		  level++;
		  for (int i=0;i<node.getChildNodes().size();i++) 
	      {
	          child_node_list.add(traverse(node.getChildNodes().get(i), level));
	      }	
		  
	      sugar_node.setChildList(child_node_list);  
	      /*
	       * why use Node_list?
	       */
		  Node_list.add(sugar_node);
	      return sugar_node;
	      
	    }
	  /*
	   * NormalizeTree: for delete Sub node,and add sub node mass to their parent node mass
	   * 
	   */
	  public void NormalizeTree(SugarNode root_node)
	  {
		  
//		  System.out.println("node_num:"+root_node.getChildList().size()+"\t"+root_node.getNodeType()+"\t"
//				  +root_node.getNodeId()+"\t"+root_node.getNodeLevel()+"\t"+root_node.getTreeMass());
//		  
		  ArrayList<SugarNode> nodeList=root_node.getChildList();
//		  System.out.println(nodeList);
//		  for(SugarNode iter:nodeList)
		  for(int i=0;i<nodeList.size();i++)
		  {
			  SugarNode iter=nodeList.get(i);
			  if(iter.getNodeType().equalsIgnoreCase("Subs"))
			  {
				 root_node.setNodeMass(root_node.getNodeMass()+iter.getNodeMass());
				 root_node.getChildList().remove(iter);
				 root_node.setNodeStr("D");
				 NormalizeTree(root_node);
			 
			  }else 
			  {
				 NormalizeTree(iter);
			  }
		  }
	  }
	  int tmpID=1;
	  public void resetNodeID(SugarNode root_node)
	  {
		  root_node.setNodeId(tmpID++);
		  if(root_node.getChildList()!=null)
		  {
			  for(int i=0;i<root_node.getChildList().size();i++)
			  {
				  resetNodeID(root_node.getChildList().get(i));
				  
			  }
		  }
	  }
	  /*
	   * print: recursion print the fragment tree's node mass and node ID
	   */
	  public void print(SugarNode node)
	  {
		System.out.println(node.getNodeLevel()+"\t"+node.getNodeMass()+"\t"+node.getNodeId());
		for(SugarNode iter_node:node.getChildList())
		{
			print(iter_node);
		}
	  }
}
