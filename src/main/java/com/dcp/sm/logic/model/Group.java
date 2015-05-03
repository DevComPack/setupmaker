package com.dcp.sm.logic.model;

import java.io.Serializable;



import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.wtk.content.TreeBranch;

import com.dcp.sm.logic.factory.CastFactory;
import com.dcp.sm.logic.factory.GroupFactory;

/**
 * Group Model
 * @author Said
 *
 */
public class Group implements Comparable<Group>, Serializable
{
    /**
     * Class written into save file
     */
    private static final long serialVersionUID = 7019635543387148548L;
    
    //Groups
    private String installGroups = "";//Install Groups *
    //Attributes
    private String name;//Group name
    private Group parent = null;//Parent group, if any
    private String description = "";//Group's description
    //Childs
    private List<Group> children = new ArrayList<Group>();//Childs, if is parent
    public void addChild(Group G) { children.add(G); }//Add child to Group
    public void removeChild(Group G) { children.remove(G); }//Delete Child Cascade
    public List<Group> getChildren() { return children; }//Returns the childs
    
    //Constructors
    public Group() {
    }
    public Group(String name) {
        this.name = name;
    }
    public Group(String name, Group parent) {
        this.name = name;
        setParent(parent);
    }
    public Group(Group group) {
        this.name = group.getName();
        if (group.getParent() != null)
        	setParent(group.getParent());
        setDescription(group.getDescription());
        setInstallGroups(group.getInstallGroups());
    }
    
    /**
     * Cast group model from Non-Maven data (<v1.2.1)
     * @param group
     */
    public Group(dcp.logic.model.Group group) {
        assert group != null;
        this.name = group.name;
        if (group.parent != null)
        	setParent(new Group(group.parent));
        setDescription(group.description);
        setInstallGroups(group.installGroups);
	}
	//Getters & Setters
    public String getInstallGroups() { return installGroups; }
    public void setInstallGroups(String installGroups) { this.installGroups = installGroups; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Group getParent() { return parent; }
    public void setParent(Group PARENT) { this.parent = PARENT; }
    public boolean hasParent(Group PARENT) {
        return GroupFactory.hasParent(this, PARENT);
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPath() {
        return CastFactory.pathToString(GroupFactory.getPath(this));
    }
    
    //Comparison function
    @Override
    public boolean equals(Object obj)//Groups compare
    {
        if (obj==null) return false;
        if (obj instanceof Group) {
            Group G = (Group) obj;
            if (G.getParent() != null)
                return this.name.equalsIgnoreCase(G.getName()) && this.getPath().equalsIgnoreCase(G.getPath());
            else//Group has no parent
                return this.name.equalsIgnoreCase(G.getName()) && this.parent == null;
        }
        else if (obj instanceof TreeBranch) {
            TreeBranch branch = (TreeBranch) obj;
            return this.name.equalsIgnoreCase(branch.getText()) &&
                    ( (this.getParent() != null && branch.getParent() != null && this.getParent().equals(branch.getParent())) ||
                            (this.getParent() == null && branch.getParent() == null) );
        }
        else return false;
    }
    
    @Override
    public String toString()
    {
        return this.getPath();
    }
    @Override
    public int compareTo(Group o)
    {
        if (this.equals(o))
            return 0;//Equal
        else {
            int string_compare = (this.getName().compareTo(o.getName()));//Name comparison
            if (string_compare != 0)
                return string_compare;
            else
                return -1;
        }
    }
    
}
