package com.dcp.sm.logic.factory;

import java.util.ListIterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

import com.dcp.sm.logic.factory.TypeFactory.LOG_LEVEL;
import com.dcp.sm.logic.model.Group;
import com.dcp.sm.main.log.Out;


public class GroupFactory
{
    private static List<Group> groups = new ArrayList<Group>();
    public static List<Group> getGroups() { return groups; }
    
    //Returns Group of PATH
    public static Group get(String[] path) {
        return get(CastFactory.pathToString(path));
    }
    public static Group get(String path) {
        if (path == null) return null;
        for(Group G:groups)
            if (G.getPath().equalsIgnoreCase(path))
                return G;
        return null;
    }
    public static List<Group> getByName(String name) {
        if (name==null) return null;
        List<Group> list = new ArrayList<Group>();
        for(Group G:groups)
            if (G.getName().equalsIgnoreCase(name))
                list.add(G);
        return list;
    }
    
    //Returns index of Group or -1 if not found
    public static int indexOf(Group G) {
        return groups.indexOf(G);
    }
    
    //Add a new group
    public static boolean addGroup(Group group) {
        if (indexOf(group) == -1) {//Group not already created
            groups.add(group);
            if (group.getParent() != null) {//Has parent group
                group.getParent().addChild(group);//Set the parent's child
            }
            Out.print(LOG_LEVEL.DEBUG, "Group added: " + group.getName());
            return true;
        }
        else {//Group already present
            return false;
        }
    }
    
    //Remove a group (+sub groups)
    public static void removeGroup(Group group) {
        if (group.getParent() != null) {//If has parent
            Group PARENT = group.getParent();
            PARENT.removeChild(group);//Cascade delete
        }
        for (Group G:group.getChildren())//Childs delete
            groups.remove(G);
        
        String name = group.getName();
        groups.remove(group);
        Out.print(LOG_LEVEL.DEBUG, "Group deleted with cascade: " + name);
    }
    
    //Clear all groups
    public static void clear() {
        groups.clear();
        Out.print(LOG_LEVEL.DEBUG, "All Groups erased.");
    }
    
    //Returns Group root of G
    public static Group getRoot(Group group) {
        if (group.getParent() != null) {
            Group ROOT = group.getParent();
            while(ROOT.getParent() != null)
                ROOT = ROOT.getParent();
            return ROOT;
        }
        else return group;//Is a root
    }
    
    //Returns the roots
    public static List<Group> getRoots() {
        List<Group> ROOTS = new ArrayList<Group>();
        for(Group G:groups)
            if (G.getParent() == null)
                ROOTS.add(G);
        return ROOTS;
    }
    
    public static boolean hasParent(Group src, Group trgt) {
        assert trgt != null;
        if (src.equals(trgt))
            return true;
        if (src.getParent() != null)
            return hasParent(src.getParent(), trgt);
        return false;
    }
    
    //Returns path from root to Group
    public static String[] getPath(Group group) {
        java.util.List<String> backList = new java.util.ArrayList<String>();
        //Return back in hierarchy
        Group Gtmp = group;
        do {
            backList.add(Gtmp.getName());
            Gtmp = Gtmp.getParent();
        } while(Gtmp != null);
        //Copy the hierarchy from top to bottom
        String[] PATH = new String[backList.size()];
        int i = 0;
        for(ListIterator<String> it = backList.listIterator(backList.size());
            it.hasPrevious();) {
            PATH[i++] = it.previous();
        }
        return PATH;
    }

    /**
     * @return groups length
     */
    public static int getCount()
    {
        return groups.getLength();
    }
    
}
