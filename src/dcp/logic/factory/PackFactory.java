package dcp.logic.factory;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;

import dcp.logic.model.Group;
import dcp.logic.model.Pack;


public class PackFactory
{
    //Data
    private static List<Pack> packs = new ArrayList<Pack>();//List of packs
    
    //Get all Packs data
    public static List<Pack> getPacks() { return packs; }//Get packs
    
    //Returns the pack of index
    public static Pack get(int INDEX) {
        if (INDEX >= 0 && INDEX < packs.getLength())
            return packs.get(INDEX);
        return null;
    }
    public static List<Pack> getByName(String NAME) {
        if (NAME==null) return null;
        List<Pack> list = new ArrayList<Pack>();
        for(Pack P:packs)
            if (P.getName().equalsIgnoreCase(NAME))
                list.add(P);
        return list;
    }
    public static List<Pack> getByGroup(Group GROUP) {
        if (GROUP==null) return null;
        List<Pack> list = new ArrayList<Pack>();
        for(Pack P:packs)
            if (P.getGroup() != null && P.getGroup().equals(GROUP))
                list.add(P);
        return list;
    }
    
    //Returns the index of a given Pack, or -1
    public static int indexOf(Pack PACK) {
        for(int i = 0; i<packs.getLength(); i++)
            if (packs.get(i).equals(PACK))
                return i;
        return -1;
    }
    
    //Add a pack
    public static void addPack(Pack P) {
        PackFactory.packs.add(P);
        System.out.println("Pack added: " + P.getName());
    }
    
    //Removes a pack
    public static void removePack(Pack PACK) {
        packs.remove(PACK);
    }
    
    //Clear all packs
    public static void clear() {
        packs.clear();
    }
    
}
