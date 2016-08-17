package util;

public class FeatureNode {
    private double mass;
    private int id;
    private FragNode fragNode;
    public FeatureNode()
    {
        mass=-1;
        id=-1;
        fragNode=new FragNode();
    }
    public void setMass(double mass)
    {
        this.mass=mass;
    }
    public double getMass()
    {
        return this.mass;
    }
    public void setID(int id)
    {
        this.id=id;
    }
    public int getID()
    {
        return this.id;
    }
    public void setFragNode(FragNode fragNode)
    {
        this.fragNode=fragNode;
    }

}
