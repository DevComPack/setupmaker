package dcp.logic.model;

import java.io.Serializable;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;


public class Group implements Serializable
{
    /**
     * Class written into save file
     */
    private static final long serialVersionUID = 7019635543387148548L;
    
    //Groups
    public String installGroups = "";//Install Groups *
    //Attributes
    public String name;//Group name
    public Group parent = null;//Parent group, if any
    public String description = "";//Group's description
    //Childs
    public List<Group> children = new ArrayList<Group>();//Childs, if is parent
    
}
