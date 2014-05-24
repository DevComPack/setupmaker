package dcp.logic.model;

/**
 * Group of Packs/Groups
 */
public class InstallGroup
{
    private String name;
    
    
    public InstallGroup(String NAME)
    {
        this.name = NAME;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        InstallGroup IG = (InstallGroup) obj;
        return (getName().equals(IG.getName()));
    }
}
