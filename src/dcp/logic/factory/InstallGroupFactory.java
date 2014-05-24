package dcp.logic.factory;

import java.util.Map;
import java.util.TreeMap;

import dcp.logic.model.InstallGroup;



public class InstallGroupFactory
{
    private static Map<InstallGroup, Integer> installGroups = new TreeMap<InstallGroup, Integer>();
    public static Map<InstallGroup, Integer> getInstallGroups() { return installGroups; }
    
    //Add Install group to list
    public static void addInstallGroup(InstallGroup IG) {
        if (installGroups.get(IG) == null)//First time used
            installGroups.put(IG, 1);
        else//Already used
            installGroups.put(IG, ((Integer) installGroups.get(IG))+1);
    }
    
    //Remove install group from list
    public static void removeInstallGroup(InstallGroup IG) {
        installGroups.remove(IG);
    }
    
    //Get install group from index
    public static InstallGroup get(int INDEX) {
        //return installGroups.get(new Integer(INDEX)).intValue();
        return null;//*
    }
    
}
