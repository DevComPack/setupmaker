package dcp.logic.factory;

import java.util.ListIterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

import dcp.logic.model.Group;


public class GroupFactory
{
    private static List<Group> groups = new ArrayList<Group>();
    public static List<Group> getGroups() { return groups; }
    
    //Returns Group of PATH
    public static Group get(String[] PATH) {
        return get(CastFactory.pathToString(PATH));
    }
    public static Group get(String path) {
        if (path==null) return null;
        for(Group G:groups)
            if (G.getPath().equalsIgnoreCase(path))
                return G;
        return null;
    }
    public static List<Group> getByName(String NAME) {
        if (NAME==null) return null;
        List<Group> list = new ArrayList<Group>();
        for(Group G:groups)
            if (G.getName().equalsIgnoreCase(NAME))
                list.add(G);
        return list;
    }
    
    //Returns index of Group or -1 if not found
    public static int indexOf(Group G) {
        return groups.indexOf(G);
    }
    
    //Add a new group
    public static boolean addGroup(Group G) {
        if (indexOf(G) == -1) {//Group not already created
            groups.add(G);
            if (G.getParent()!=null) {//Has parent group
                G.getParent().addChild(G);//Set the parent's child
            }
            return true;
        }
        else {//Group already present
            return false;
        }
    }
    
    //Remove a group (+sub groups)
    public static void removeGroup(Group GROUP) {
        if (GROUP.getParent() != null) {//If has parent
            Group PARENT = GROUP.getParent();
            PARENT.removeChild(GROUP);//Cascade delete
        }
        for (Group G:GROUP.getChildren())//Childs delete
            groups.remove(G);
        groups.remove(GROUP);
    }
    
    //Clear all groups
    public static void clear() {
        groups.clear();
    }
    
    //Returns Group root of G
    public static Group getRoot(Group GROUP) {
        if (GROUP.getParent() != null) {
            Group ROOT = GROUP.getParent();
            while(ROOT.getParent() != null)
                ROOT = ROOT.getParent();
            return ROOT;
        }
        else return GROUP;//Is a root
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
    public static String[] getPath(Group G) {
        java.util.List<String> backList = new java.util.ArrayList<String>();
        //Return back in hierarchy
        Group Gtmp = G;
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
    
}
