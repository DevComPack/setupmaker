package dcp.gui.pivot.frames;

import java.net.URL;
import java.util.Iterator;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.collections.Sequence.Tree.Path;
import org.apache.pivot.util.Filter;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.ButtonStateListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseListener;
import org.apache.pivot.wtk.ComponentStateListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.DialogCloseListener;
import org.apache.pivot.wtk.DragSource;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListButtonSelectionListener;
import org.apache.pivot.wtk.LocalManifest;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.SplitPane;
import org.apache.pivot.wtk.SplitPaneListener;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TableViewRowListener;
import org.apache.pivot.wtk.TableViewSelectionListener;
import org.apache.pivot.wtk.TableViewSortListener;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextAreaContentListener;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.TreeView;
import org.apache.pivot.wtk.TreeViewNodeListener;
import org.apache.pivot.wtk.TreeViewSelectionListener;
import org.apache.pivot.wtk.Visual;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Button.State;
import org.apache.pivot.wtk.content.TableViewRowComparator;
import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;
import org.apache.pivot.wtk.validation.Validator;

import dcp.logic.factory.TypeFactory.FILE_TYPE;
import dcp.gui.pivot.Master;
import dcp.gui.pivot.dad.TextInputDrop;
import dcp.gui.pivot.facades.SetFacade;
import dcp.logic.factory.CastFactory;
import dcp.logic.factory.GroupFactory;
import dcp.logic.factory.PackFactory;
import dcp.logic.factory.TypeFactory.PLATFORM;
import dcp.logic.model.Group;
import dcp.logic.model.Pack;
import dcp.logic.factory.TypeFactory.INSTALL_TYPE;
import dcp.logic.model.config.AppConfig.SCAN_MODE;
import dcp.main.log.Out;


public class SetFrame extends FillPane implements Bindable
{
    //Singleton reference
    private static SetFrame singleton;
    public static SetFrame getSingleton() { return singleton; }
    private SetFacade facade;
    
    //------DATA
    //Constants
    private final static int TABLEVIEW_GROUP_COLUMN_INDEX = 3;
    //Flags
    private boolean modified = false;//True if tab processed data
    public void setModified(boolean VALUE) { modified = VALUE; }
    public boolean isModified() { return modified; }
    private boolean multi_selection = false;//If multiple packs selected or only one
    private boolean drag_enabled = false;//If a component is being dragged
    private boolean isGroupDependency() { return ((String)cbDepType.getButtonData()).equals("Group"); }
    //Packs
    private List<Pack> getPacks() { return PackFactory.getPacks(); }//Read-only Packs data
    private Pack getSelectedPack() { return (Pack) tableView.getSelectedRow(); }
    @SuppressWarnings("unchecked")
    private Sequence<Pack> getSelectedPacks() { return (Sequence<Pack>) tableView.getSelectedRows(); }
    //Groups
    private List<TreeBranch> treeData = new ArrayList<TreeBranch>();//Groups tree view root element
    private Group getSelectedGroup() {//Return selected group or selected pack's group from treeView
        TreeNode node = (TreeNode) treeView.getSelectedNode();//Get selected branch
        if (node != null) {
            TreeBranch branch;
            if (!(node instanceof TreeBranch))//If node is a pack
                branch = node.getParent();//Get parent branch/group
            else branch = (TreeBranch) node;
            return facade.getGroup(branch);//Get group mapped to this branch
        }
        return null;
    }
    
    //Panel Buttons
    //Pack options
    @BXML private PushButton btSort;//Packs Sorting Dialog open
    @BXML private PushButton btSelectAll;//Select all packs in table view
    @BXML private PushButton btSelectNone;//Clear pack selection in table view
    @BXML private PushButton btAdd;//Add Pack to selected group
    @BXML private PushButton btDelete;//Delete selected Pack(s)
    //Group options
    @BXML private PushButton btImport;//Groups import from Recursive scan
    @BXML private PushButton btNew;//Add new group
    @BXML private PushButton btRename;//Rename group
    @BXML private PushButton btRemove;//Remove selected Pack from group
    @BXML private PushButton btClear;//Remove all groups
    //Displays
    @BXML private SplitPane vSplitPane;//Split Pane between Table(left) and Tree(right) view
    @BXML private SplitPane hSplitPane;//Split Pane between Table(left) and Tree(right) view
    @BXML private TableView tableView;//Table View for scanned directory
    @BXML private TreeView treeView;//Tree View for created groups hierarchy
    @BXML private Border propertiesPane;//Properties Border pane view
    //Dialogs
    @BXML private SortDialog sortDialog;//Packs Sorting Dialog
    @BXML private NGDialog ngdialog;//Dialog user input for new group add
    @BXML private RGDialog rgdialog;//Dialog user input for group rename
    @BXML private ShortcutDialog shortcutDialog;//Shortcut advanced options Dialog
    //Buttons
    @BXML private PushButton btShortcutAdvanced;//Advanced Shortcut options
    //Checkboxes
    @BXML private Checkbox cbRequired;//If pack is required or not
    @BXML private Checkbox cbSelected;//If pack is selected or not
    @BXML private Checkbox cbHidden;//If pack is hidden or not
    @BXML private Checkbox cbOverride;//If pack will override existing install or not
    @BXML private Checkbox cbShortcut;//If a shortcut to this pack is created
    //Radio Buttons
    @BXML private RadioButton rbOsAll;//OS install platform
    @BXML private RadioButton rbOsWin;//OS install platform
    @BXML private RadioButton rbOsLin;//OS install platform
    @BXML private RadioButton rbOsMac;//OS install platform
    @BXML private RadioButton rbExecute;//Execute the pack executable
    @BXML private Checkbox cbSilent;//Execute msi setup with passive par
    @BXML private RadioButton rbExtract;//Extract the pack archive
    @BXML private RadioButton rbCopy;//Copy the pack to the install directory
    //Text Inputs
    @BXML private TextInput inName;//Pack install name
    @BXML private TextInput inPInstallPath;//Pack's install Path directory
    @BXML private TextInput inInstallGroups;//Pack/Group's install Groups
    @BXML private LinkButton cbDepType;//Dependency type (group or pack)
    @BXML private ListButton lbDependency;//Group list for dependency
    @BXML private PushButton btDepErase;//lbDependency remove
    @BXML private PushButton btIGErase;//Install Groups remove
    @BXML private PushButton btIPErase;//Install Path remove
    @BXML private TextArea inDescription;//Pack/Group's description
    //Actions
    private Action ADataImport;//Import groups from recursive scan
    private Action AAddToGroup;//Add pack to group Action
    private Action ARemove;//Remove Group/Pack from tree view Action
    private Action ADeletePacks;//Delete selected packs
    private Action ASetInstallType;//Changes the default install type of a pack Action
    private Action ASetInstallOS;//Changes the destiny install OS of a pack Action
    
    //=========================================
    public SetFrame()//Constructor
    {
        if (singleton == null) singleton = this;
        
        ADataImport = new Action() {//Import groups from recursive scan
            @Override public void perform(Component source)
            {
                //Groups Import
                GroupFactory.clear(); treeData.clear();
                for(Group G:ScanFrame.getGroups())//Fill Data from Scan groups
                    facade.newGroup(G.getName(), G.getParent());
                //Packs Import
                PackFactory.clear();
                for(Pack P:ScanFrame.getPacks()) {
                    PackFactory.addPack(P);
                    if (P.getGroup() != null) {
                        Group G = P.getGroup();
                        P.setGroup(null);
                        if (facade.addPackToGroup(P, G) == 0)
                            if (!multi_selection) setPackProperties(P);
                    }
                }
                treeView.expandAll();//Expand branches
                //tableView.setSort("icon", SortDirection.ASCENDING); tableView.clearSort();//Sort by pack type
            }
        };
        
        AAddToGroup = new Action() {//Pack add to Group Button Action
            @Override public void perform(Component source)
            {
                boolean added = false;
                Sequence<Pack> pList = getSelectedPacks();//Get selected packs
                TreeNode node = (TreeNode) treeView.getSelectedNode();//Get selected branch
                Pack p;
                for(int i=0; i< pList.getLength(); i++) {
                    p = pList.get(i);
                    if (node != null) {//If selected node/branch
                        Group g = getSelectedGroup();//Get selected group or node's parent group
                        
                        int res = facade.addPackToGroup(p, g);//Add Pack p to group g:branch
                        if (res == 0) {//success
                            if (!multi_selection) setPackProperties(p);
                            //Group column name refresh
                            tableView.repaint(tableView.getCellBounds( PackFactory.indexOf(p), TABLEVIEW_GROUP_COLUMN_INDEX) );
                            if (!added) added = true;
                        }
                        else {
                            if (res == 1) {//Pack already affected to a group error
                                Alert.alert("Pack '"+ p.getName() +"' already affected to the group '"+ p.getGroupPath() +
                                            "' Remove it first!", SetFrame.this.getWindow());
                            }
                            else if (res == 2) {//dependency error
                                Alert.alert("Pack '"+ p.getName() +"' is dependent on '"+ p.getGroupDependency().getPath() +"'!",
                                            SetFrame.this.getWindow());
                            }
                            break;
                        }
                    }
                }
                if (added) tableView.clearSelection();
            }
        };
        
        ARemove = new Action() {//Remove TreeView Group/Pack button Action
            @Override public void perform(Component source)
            {
                facade.removeNode((TreeNode) treeView.getSelectedNode());
                
                ngdialog.setHierarchy(false, "");//Clean new group hierarchy
                tableView.clearSelection();//Clear selection on packs
                tableView.repaint(tableView.getColumnBounds(TABLEVIEW_GROUP_COLUMN_INDEX));//Packs Group display update
            }//perform()
        };
        
        ADeletePacks = new Action() {//Delete all selected packs
            private boolean removePack(Pack pack) {
                System.out.println("Pack deleted: "+pack.getName());
                if (pack.getGroup() != null) {
                    Alert.alert("Pack "+pack.getName()+" is within a group. Remove it first.", SetFrame.this.getWindow());
                    return false;
                }
                getPacks().remove(pack);
                return true;
            }
            
            @Override public void perform(Component source)
            {
                if (!multi_selection) {//one pack selected
                    removePack(getSelectedPack());
                }
                else {//multi packs selected
                    Sequence<Pack> ps = getSelectedPacks();
                    for(int i = 0; i < ps.getLength(); i++) {
                        if (!removePack(ps.get(i))) break;
                    }
                }
            }
        };
        
        ASetInstallType = new Action() {//Install Type Radio buttons Action
          @Override public void perform(Component source)
          {
              INSTALL_TYPE IT = INSTALL_TYPE.DEFAULT;
              if (rbCopy.isSelected())
                  IT = INSTALL_TYPE.COPY;
              else if (rbExtract.isSelected())
                  IT = INSTALL_TYPE.EXTRACT;
              else if (rbExecute.isSelected())
                  IT = INSTALL_TYPE.EXECUTE;
              
              if (multi_selection) {//multi packs selected
                  Sequence<Pack> list = getSelectedPacks();
                  if (list!=null) {
                      for(int i = 0; i < list.getLength(); i++) {
                          list.get(i).setInstallType(IT);
                      }
                      setModified(true);//Modified flag
                  }
              }
              else {//1 pack selected
                  Pack p = getSelectedPack();
                  if (p != null) {
                      p.setInstallType(IT);
                      setModified(true);//Modified flag
                  }
              }
          }
        };
        
        ASetInstallOS = new Action() {//Install OS Radio buttons Action
          @Override public void perform(Component source)
          {
              PLATFORM OS = PLATFORM.ALL;
              if (rbOsAll.isSelected())
                  OS = PLATFORM.ALL;
              else if (rbOsWin.isSelected())
                  OS = PLATFORM.WINDOWS;
              else if (rbOsLin.isSelected())
                  OS = PLATFORM.LINUX;
              else if (rbOsMac.isSelected())
                  OS = PLATFORM.MAC;
              
              if (multi_selection) {//multi packs selected
                  Sequence<Pack> list = getSelectedPacks();
                  if (list!=null) {
                      for(int i = 0; i < list.getLength(); i++) {
                          list.get(i).setInstallOs(OS);
                      }
                      setModified(true);//Modified flag
                  }
              }
              else {//1 pack selected
                  Pack p = getSelectedPack();
                  if (p != null) {
                      p.setInstallOs(OS);
                      setModified(true);//Modified flag
                  }
              }
          }
        };
    }
    
    @Override public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2)
    {
        facade = new SetFacade(treeData);
        
        //Workspace set
        vSplitPane.setSplitRatio(Master.appConfig.getSetVerSplitPaneRatio());
        hSplitPane.setSplitRatio(Master.appConfig.getSetHorSplitPaneRatio());
        
        //Data Binding
        tableView.setTableData(getPacks());//Bind table view to packs
        treeView.setTreeData(treeData);//Bind root to tree view
        dependencyFill(isGroupDependency());//Bind Groups data to List Button for dependency
        ngdialog.setHierarchy(false, "");//Initialize NewGroup Hierarchy to none
        
        //Actions Binding
        btAdd.setAction(AAddToGroup);
        btDelete.setAction(ADeletePacks);
        btRemove.setAction(ARemove);
        
        //Workspace Splitpanes ratio value save to appconfig
        vSplitPane.getSplitPaneListeners().add(new SplitPaneListener.Adapter() {
            @Override public void splitRatioChanged(SplitPane sp, float ratio)
            {
                Master.appConfig.setSetVerSplitPaneRatio(sp.getSplitRatio());
            }
        });
        hSplitPane.getSplitPaneListeners().add(new SplitPaneListener.Adapter() {
            @Override public void splitRatioChanged(SplitPane sp, float ratio)
            {
                Master.appConfig.setSetHorSplitPaneRatio(sp.getSplitRatio());
            }
        });
        
        //Validators
        inName.setValidator(new Validator() {
            @Override public boolean isValid(String str)
            {
                assert !multi_selection;
                for(Pack p:getPacks())
                    if (!p.equals(getSelectedPack()) && p.getInstallName().equals(str))
                        return false;
                return true;
            }
        });
        
        //Packs panel buttons
        btSort.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                sortDialog.updatePriorities();//Update packs priorities from current list
                sortDialog.open(SetFrame.this.getDisplay(), SetFrame.this.getWindow(), null);
            }
        });
        btSelectAll.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                int n = tableView.getTableData().getLength();
                if (n>0) tableView.setSelectedRange(0, n-1);
                tableView.requestFocus();
            }
        });
        btSelectNone.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                tableView.clearSelection();
            }
        });
        
        //Groups panel buttons
        btNew.getButtonPressListeners().add(new ButtonPressListener() {//Add a new group to the tree view
            @Override public void buttonPressed(Button bt)
            {
                if (treeView.getSelectedNode() == null) ngdialog.setHierarchy(false, "");//Initialize hierarchy
                ngdialog.open(SetFrame.this.getDisplay(), SetFrame.this.getWindow(), new DialogCloseListener() {
                    @Override public void dialogClosed(Dialog dialog, boolean modal)
                    {
                        if (ngdialog.isValidated()) {//If pushed the OK button
                            if (!facade.newGroup(ngdialog.getText(), GroupFactory.get(ngdialog.getHierarchy())))
                                Alert.alert("Group already created!", Window.getActiveWindow());
                        } else Out.print("PIVOT_SET", "Dialog not Validated");
                    }
                });
            }
        });
        btRename.getButtonPressListeners().add(new ButtonPressListener() {//Rename a selected group
            @Override public void buttonPressed(Button bt)
            {
                Group selectedG = getSelectedGroup();
                if (selectedG != null) {
                    final String old_name = selectedG.getName();
                    rgdialog.setText(old_name);
                    rgdialog.open(SetFrame.this.getDisplay(), SetFrame.this.getWindow(), new DialogCloseListener() {
                        @Override public void dialogClosed(Dialog dialog, boolean modal)
                        { 
                            facade.renameGroup(getSelectedGroup(), rgdialog.getText());
                        }
                    });
                }
            }
        });
        btClear.getButtonPressListeners().add(new ButtonPressListener() {//Remove all groups from tree view
            @Override public void buttonPressed(Button bt)
            {
                facade.clearTreeView();
                ngdialog.setHierarchy(false, "");//Clean new group hierarchy
                tableView.clearSelection();//Clear selection on packs
                tableView.repaint(tableView.getColumnBounds(TABLEVIEW_GROUP_COLUMN_INDEX));//Packs Group display update
            }
        });
        
        //Table View Drag Source
        tableView.setDragSource(new DragSource() {
            private LocalManifest content = null;
            
            @Override public boolean isNative() { return true; }
            @Override public int getSupportedDropActions() { return DropAction.COPY.getMask(); }
            @Override public Visual getRepresentation() { return null; }
            @Override public Point getOffset() { return null; }
            @Override public LocalManifest getContent() { return content; }
            
            @Override public boolean beginDrag(Component component, int x, int y)
            {
                drag_enabled = true;//Flag
                Bounds tvb = tableView.getBounds();
                if (x > tvb.x && x < tvb.x+tvb.width) {//If mouse x in bounds
                    Pack pack = getSelectedPack();
                    if (pack != null) {
                        content = new LocalManifest();
                        content.putText(pack.getName());
                        if (!multi_selection) Out.print("PIVOT_SET", "Begin Drag on pack " + pack.getName());//one pack selected
                        else Out.print("PIVOT_SET", "Begin Drag on selected packs");//multi packs selected
                    }
    
                    return (content != null);
                }
                return false;
            }
            
            @Override public void endDrag(Component component, DropAction dropAction)
            { 
                content = null;
                drag_enabled = false;
                Out.print("PIVOT_SET", "Drag end.");
            }
        });
        
        // Table view row listener (adds tooltip text for pack's path to tableview)
        tableView.getComponentMouseListeners().add(new ComponentMouseListener.Adapter() {
            @Override public boolean mouseMove(Component component, int x, int y)
            {
                Pack p = (Pack) tableView.getTableData().get(tableView.getRowAt(y));
                tableView.setTooltipText(p.getPath());
                return super.mouseMove(component, x, y);
            }
        });
        //Table view Sort listener (+ Update priorities of Sort tab)
        tableView.getTableViewSortListeners().add(new TableViewSortListener.Adapter() {
            @Override public void sortChanged(TableView tableView)
            {
                @SuppressWarnings("unchecked")
                List<Object> tableData = (List<Object>)tableView.getTableData();
                tableData.setComparator(new TableViewRowComparator(tableView));
                setModified(true);//Modified flag
            }
        });
        //Table View Row listener from Scan - Sort clear (+ properties update)
        tableView.getTableViewRowListeners().add(new TableViewRowListener.Adapter() {
            @Override public void rowInserted(TableView tableView, int index)
            {
                //tableView.setSort("name", SortDirection.ASCENDING);//Sort packs by name first
                tableView.clearSort();//Desactivate Sort on table view
                tableView.clearSelection();//Clear selection
            }
            @Override public void rowsRemoved(TableView tableView, int index, int count)
            {
                nullProperties();//Properties update
            }
        });
        //Table View Pack(s) Selection Event Listener (properties)
        tableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
            @Override public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges)
            {
                multi_selection = false;
                @SuppressWarnings("unchecked")
                Sequence<Pack> pList = (Sequence<Pack>) tableView.getSelectedRows();//Get selected packs
                if (pList != null) {
                    int n = pList.getLength();
                    if (n == 1) {//If one row selected
                        Pack p = pList.get(0);
                        propertiesPane.setTitle("Properties : " +
                                                ((p.getGroup()!=null)?p.getGroupPath():"") + p.getName());
                        
                        packInstallModes(p.getFileType());//Enable install mode relevant properties
                        enableProperties();//Enable properties options
                        setPackProperties(p);//Update properties from pack's data
                    }
                    else {//Initialize properties
                        nullProperties();
                        
                        if (n==0) {//No pack selected
                            propertiesPane.setTitle("Properties");
                        }
                        else if (n > 1) {//Multi pack selection
                            multi_selection = true;
                            propertiesPane.setTitle("Properties : " + n + " packs selected");
                            setMultiProperties(pList);
                        }
                    }
                }
            }
        });
        
        //Tree view drop target
        treeView.setDropTarget(new DropTarget() {
            @Override public DropAction dragEnter(Component component, Manifest dragContent,
                    int supportedDropActions, DropAction userDropAction)
            {
                DropAction dropAction = null;
                if (dragContent.containsText() && DropAction.COPY.isSelected(supportedDropActions)) {
                    dropAction = DropAction.COPY;
                }
                return dropAction;
            }
            
            @Override
            public DropAction dragMove(Component component, Manifest dragContent,
                    int supportedDropActions, int x, int y, DropAction dropActions)
            {
                if (treeData.getLength() > 0) {//If tree view contains nodes
                    TreeView treeView = ((TreeView) component);
                    Path path = treeView.getNodeAt(y);
                    
                    if (path != null) {//If over a node
                        Iterator<Integer> it = path.iterator();
                        TreeNode node = treeData.get(it.next());
                        
                        //Get hover node
                        while(it.hasNext()) node = ((TreeBranch) node).get(it.next());
                        
                        //Select hover node if is a branch
                        if (node instanceof TreeBranch) {
                            treeView.setSelectedPath(treeView.getNodeAt(y));
                        }
                        //Else select hover node's branch
                        else {
                            Path parPath = new Path();
                            for(int i=0; i < path.getLength()-1; i++) {
                                parPath.add(path.get(i));
                            }
                            treeView.setSelectedPath(parPath);
                        }
                    }
                    else {//If not over a node
                        if (treeData.getLength() == 1)//If only one branch in treeView
                            treeView.setSelectedPath(new Path(0));
                    }
                }
                
                return (dragContent.containsText() ? DropAction.COPY : null);
            }
            
            @Override public DropAction userDropActionChange(Component component, Manifest dragContent,
                    int supportedDropActions, int x, int y, DropAction dropActions)
            {
                return (dragContent.containsText() ? DropAction.COPY : null);
            }
            
            @Override public DropAction drop(Component component, Manifest dragContent,
                    int supportedDropActions, int x, int y, DropAction dropActions)
            {
                TreeView treeView = (TreeView) component;
                DropAction dropAction = null;
                
                if (treeData.getLength() > 0) {//If tree view contains nodes
                    TreeBranch branch = (TreeBranch) treeView.getSelectedNode();
                    if (branch != null) {//If selected branch
                        if (dragContent.containsText()) {
                            AAddToGroup.perform(component);
                            /*Group group = GroupFactory.get(branch);
                            
                            if (multi_selection) {
                                Sequence<Pack> list = getSelectedPacks();
                                for(int i = 0; i< list.getLength(); i++) {
                                    addPackToGroup(list.get(i), group);
                                }
                            }
                            else addPackToGroup(getSelectedPack(), group);*/
                            dropAction = DropAction.COPY;
                        }
                    }
                }
    
                dragExit(component);
    
                return dropAction;
            }
            
            @Override public void dragExit(Component component) {}
        });
        //Tree View Group Selection listener (group add in hierarchy/pack selection)
        treeView.getTreeViewSelectionListeners().add(new TreeViewSelectionListener.Adapter() {
            @Override public void selectedNodeChanged(TreeView treeView, Object previousSelectedNode)
            {
                TreeNode node = (TreeNode) treeView.getSelectedNode();
                if (node != null) {
                    if (node instanceof TreeBranch) {//If Group selected
                        //Send selected node hierarchy to NewGroupDialog
                        ngdialog.setHierarchy(true, CastFactory.pathToString(CastFactory.nodeToPath(node)));
                        if (!drag_enabled) {
                            //Select mapped Packs of Group from table view
                            Group g = facade.getGroup((TreeBranch) node);
                            Sequence<Span> range = new ArrayList<Span>();
                            for(Pack p : PackFactory.getByGroup(g))
                                range.add(new Span(PackFactory.indexOf(p)));
                            tableView.setSelectedRanges(range);
                        }
                    }
                    else {//If Pack selected
                        //Send parent node hierarchy to NewGroupDialog
                        ngdialog.setHierarchy(true, CastFactory.pathToString(CastFactory.nodeToPath(node.getParent())));
                        //Select mapped Pack from table view
                        tableView.setSelectedIndex(PackFactory.indexOf(facade.getPack(node)));
                    }
                }
                else ngdialog.setHierarchy(false, "");//Initialize NewGroup Hierarchy
            }
            @Override public void selectedPathRemoved(TreeView treeView, Path path)
            {
                ngdialog.setHierarchy(false, "");//Initialize NewGroup Hierarchy
            }
        });
        //New Group inserted (expand path of inserted node) - Modified Flag(*)
        treeView.getTreeViewNodeListeners().add(new TreeViewNodeListener.Adapter() {
            @Override public void nodeInserted(TreeView treeView, Path path, int index)
            {
                if(path != null) {
                    Path newPath = new Path();
                    for(Integer i : path) {
                        newPath.add(i);
                        treeView.expandBranch(newPath);
                    }
                }
                setModified(true);//Modified flag
            }
            @Override public void nodeUpdated(TreeView treeView, Path path, int index)
            {
                setModified(true);//Modified flag
            }
            @Override public void nodesRemoved(TreeView treeView, Path path, int index, int count)
            {
                setModified(true);//Modified flag
            }
        });
        
        //Recursive Groups Import from Scan button event
        btImport.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                nullProperties();//Unselect pack
                ADataImport.perform(bt);
                btImport.setEnabled(false);//Disable button
            }
        });
        
        //Set silent setup install press event
        cbSilent.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) getSelectedPack().setSilentInstall(bt.isSelected());//1 selected pack
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setSilentInstall(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        
        //Checkbox buttons press events
        cbOverride.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) getSelectedPack().setOverride(bt.isSelected());//1 selected pack
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setOverride(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        
        cbShortcut.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) {//1 selected pack
                    getSelectedPack().setShortcut(bt.isSelected());
                }
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setShortcut(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        cbShortcut.getButtonStateListeners().add(new ButtonStateListener() {
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isEnabled() && !bt.isTriState())//enable advanced options button for single contender pack selection
                    btShortcutAdvanced.setEnabled(bt.isSelected() && 
                            (getSelectedPack().getFileType() == FILE_TYPE.Folder || 
                                (getSelectedPack().getFileType() == FILE_TYPE.Archive && getSelectedPack().getInstallType() == INSTALL_TYPE.EXTRACT) ) );
            }
        });
        //Shortcut advanced options dialog open
        btShortcutAdvanced.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                shortcutDialog.initData(getSelectedPack());
                shortcutDialog.open(SetFrame.this.getDisplay(), SetFrame.this.getWindow(), null);
            }
        });
        
        cbRequired.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) getSelectedPack().setRequired(bt.isSelected());//1 selected pack
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setRequired(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        cbSelected.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) getSelectedPack().setSelected(bt.isSelected());//1 selected pack
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setSelected(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        cbHidden.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                //Disable tristate
                if (bt.getState() == State.MIXED) { bt.setTriState(false); bt.setSelected(true); }
                
                if (!multi_selection) getSelectedPack().setHidden(bt.isSelected());//1 selected pack
                else {//Multi packs selected
                    Sequence<Pack> list = getSelectedPacks();
                    for(int i = 0; i < list.getLength(); i++)
                        list.get(i).setHidden(bt.isSelected());
                }
                setModified(true);//Modified flag
            }
        });
        
        //Radio buttons press events
        rbOsAll.getButtonPressListeners().add(new ButtonPressListener() {//All OS
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallOS.perform(bt);
            }
        });
        rbOsWin.getButtonPressListeners().add(new ButtonPressListener() {//Windows OS
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallOS.perform(bt);
            }
        });
        rbOsLin.getButtonPressListeners().add(new ButtonPressListener() {//Linux OS
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallOS.perform(bt);
            }
        });
        rbOsMac.getButtonPressListeners().add(new ButtonPressListener() {//Mac OS
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallOS.perform(bt);
            }
        });
        
        rbCopy.getButtonPressListeners().add(new ButtonPressListener() {//Copy
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallType.perform(bt);
                btShortcutAdvanced.setEnabled(getSelectedPack().getFileType()!=FILE_TYPE.Archive);
            }
        });
        rbExtract.getButtonPressListeners().add(new ButtonPressListener() {//Extract
            @Override public void buttonPressed(Button bt)
            {
                if (bt.isSelected()) ASetInstallType.perform(bt);
                btShortcutAdvanced.setEnabled(cbShortcut.isSelected());
            }
        });
        rbExecute.getButtonStateListeners().add(new ButtonStateListener() {//Execute
            @Override public void stateChanged(Button bt, State st)
            {
                if (bt.isEnabled()) {
                    if (bt.isSelected()) {//Selected
                        ASetInstallType.perform(bt);
                        inPInstallPath.setEnabled(false);
                        btIPErase.setEnabled(false);
                        cbOverride.setEnabled(false);
                        cbShortcut.setEnabled(false);
                    }
                    else {
                        inPInstallPath.setEnabled(true);
                        btIPErase.setEnabled(true);
                        cbOverride.setEnabled(true);
                        cbShortcut.setEnabled(true);
                    }
                }
            }
        });
        
        //Pack install name
        inName.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                if (TI.isValid()) {
                    Pack P = getSelectedPack();
                    if (P != null && TI.isEnabled()) {
                        String newName = (TI.getText().equals(""))?P.getName():TI.getText();
                        P.setInstallName(newName);//Change pack install name
                    }
                }
            }
        });
        inName.setDropTarget(new TextInputDrop() {
            @Override
            public void dragExit(Component component)
            {
                drag_enabled = false;
                super.dragExit(component);
            }
        });
        
        //Description text change save into pack data
        inDescription.getTextAreaContentListeners().add(new TextAreaContentListener.Adapter() {
            @Override public void textChanged(TextArea TA)
            {
                if (TA.isEnabled()) {
                    Pack P = getSelectedPack();
                    if (P != null) P.setDescription(inDescription.getText());
                }
            }
        });
        inDescription.setDropTarget(new TextInputDrop() {
            @Override public void dragExit(Component component)
            {
                drag_enabled = false;
                super.dragExit(component);
            }
        });
        
        //Pack's install path set
        inPInstallPath.getComponentStateListeners().add(new ComponentStateListener.Adapter() {
            @Override public void enabledChanged(Component cp)
            {
                if (!cp.isEnabled()) btIPErase.setEnabled(false);
            }
        });
        inPInstallPath.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                Pack P = getSelectedPack();
                if (P != null && TI.isEnabled()) {
                    btIPErase.setEnabled(TI.getText().length() > 0);
                    if (!multi_selection) {//If 1 pack selected
                        P.setInstallPath(TI.getText());
                    }
                    else {//Multi packs selected
                        Sequence<Pack> list = getSelectedPacks();
                        if (list != null)
                            for (int i = 0; i < list.getLength(); i++)
                                list.get(i).setInstallPath(TI.getText());
                    }
                }
            }
            //Make path suggestions
            @Override public void textInserted(TextInput textInput, int index, int count)
            {
                String text = textInput.getText().toLowerCase();
                boolean found = false;
                String suggestion = "";
                
                for(Group G:GroupFactory.getGroups()) {//Suggestions from Group paths
                    if (G.getPath().toLowerCase().startsWith(text)) {
                        found = true;
                        suggestion = G.getPath();
                        break;
                    }
                }
                if (!found) {//Suggestions from already attributed install paths to Packs
                    for(Pack P:getPacks()) {
                        if (P.getInstallPath().toLowerCase().startsWith(text)) {
                            found = true;
                            suggestion = P.getInstallPath();
                            break;
                        }
                    }
                }
                
                if (found) {
                    int selectionStart = text.length();
                    int selectionLength = suggestion.length() - selectionStart;
                    
                    textInput.insertText(suggestion.subSequence(text.length(), suggestion.length()), selectionStart);
                    textInput.setSelection(selectionStart, selectionLength);
                }
            }
        });
        inPInstallPath.setDropTarget(new TextInputDrop() {
            @Override
            public void dragExit(Component component)
            {
                drag_enabled = false;
                super.dragExit(component);
            }
        });
        
        //Install Groups text input listener (save to Pack/Group)
        inInstallGroups.getComponentStateListeners().add(new ComponentStateListener.Adapter() {
            @Override public void enabledChanged(Component cp)
            {
                if (!cp.isEnabled()) btIGErase.setEnabled(false);
            }
        });
        inInstallGroups.getComponentStateListeners().add(new ComponentStateListener.Adapter() {
            @Override public void focusedChanged(Component component, Component obverseComponent)
            {
                if (component.isFocused() && !multi_selection) {//Simplify new install group add on single pack
                    if (!inInstallGroups.getText().trim().equals("") &&
                       (!inInstallGroups.getText().trim().endsWith(",") && !inInstallGroups.getText().trim().endsWith(";")) )
                        inInstallGroups.setText(inInstallGroups.getText().concat(", "));
                }
            }
        });
        inInstallGroups.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override public void textChanged(TextInput TI)
            {
                Pack P = getSelectedPack();
                if (P != null && TI.isEnabled()) {
                    btIGErase.setEnabled(TI.getText().length() > 0);
                    if (!multi_selection) {//If single pack selected
                        P.setInstallGroups(TI.getText());
                    }
                    else {//Multi packs selected
                        Sequence<Pack> list = getSelectedPacks();
                        String ig = "";
                        for (int i = 0; i < list.getLength(); i++) {//Sets install group when not affected
                            ig = list.get(i).getInstallGroups();
                            if (!ig.toLowerCase().contains(TI.getText().trim().toLowerCase()))
                                list.get(i).setInstallGroups(TI.getText());
                        }
                    }
                }
            }
        });
        inInstallGroups.setDropTarget(new TextInputDrop() {
            @Override
            public void dragExit(Component component)
            {
                drag_enabled = false;
                super.dragExit(component);
            }
        });
        
        //Dependency type change
        cbDepType.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                String text = (String) bt.getButtonData();
                if (text.equals("Pack")) {
                    text = "Group";
                    if (bt.isEnabled()) dependencyFill(true);
                }
                else if (text.equals("Group")) {
                    text = "Pack";
                    if (bt.isEnabled()) dependencyFill(false);
                }
                bt.setButtonData(text);
            }
        });
        
        //Dependency Group List Button Listener (set Pack dependency)
        lbDependency.getComponentStateListeners().add(new ComponentStateListener.Adapter() {
            @Override public void enabledChanged(Component cp)
            {
                if (!cp.isEnabled()) btDepErase.setEnabled(false);
            }
        });
        lbDependency.getListButtonSelectionListeners().add(new ListButtonSelectionListener.Adapter() {
            @Override public void selectedItemChanged(ListButton lb, Object obj)
            {
                Pack P = getSelectedPack();
                if (P != null && lb.isEnabled()) {
                    btDepErase.setEnabled(lb.getSelectedIndex()>=0);
                    if (!multi_selection) {//1 pack selected
                        if (isGroupDependency())
                            P.setGroupDependency((lb.getSelectedItem() != null)?(Group) lb.getSelectedItem():null);
                        else
                            P.setPackDependency((lb.getSelectedItem() != null)?(Pack) lb.getSelectedItem():null);
                    }
                    else {//Multi packs selected
                        Sequence<Pack> list = getSelectedPacks();
                        for (int i = 0; i < list.getLength(); i++)
                            if (isGroupDependency())
                                list.get(i).setGroupDependency((lb.getSelectedItem() != null)?(Group) lb.getSelectedItem():null);
                            else
                                list.get(i).setPackDependency((lb.getSelectedItem() != null)?(Pack) lb.getSelectedItem():null);
                    }
                }
            }
        });
        
        //Dependency clear button listener
        btDepErase.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                if (lbDependency.getSelectedIndex() > -1)//If dependency affected
                    lbDependency.setSelectedIndex(-1);
                else {//If different dependencies on multi packs selected
                    if (multi_selection) {
                        Sequence<Pack> list = getSelectedPacks();
                        for (int i = 0; i < list.getLength(); i++) {
                            list.get(i).setGroupDependency(null);
                            list.get(i).setPackDependency(null);
                        }
                        btDepErase.setEnabled(false);
                    }
                }
            }
        });
        //Install Groups clear button listener
        btIGErase.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                inInstallGroups.setText("");
            }
        });
        //Install Path clear button listener
        btIPErase.getButtonPressListeners().add(new ButtonPressListener() {
            @Override public void buttonPressed(Button bt)
            {
                inPInstallPath.setText("");
            }
        });
        
    }
    
    /**
     * Fill list button with dependency
     * Groups or Packs
     */
    private void dependencyFill(boolean from_groups) {
        boolean enabled = lbDependency.isEnabled();
        if (enabled) lbDependency.setEnabled(false);
        if (from_groups) {//From Groups
            lbDependency.setListData(GroupFactory.getGroups());//Data binding
            if (!multi_selection) {//one pack selected
                lbDependency.setDisabledItemFilter(new Filter<Group>() {
                    @Override public boolean include(Group group)//Groups to filter (disable)
                    {
                        Pack P = getSelectedPack();
                        assert P!=null;
                        if (P.getGroup() != null && P.getGroup().hasParent(group))
                                return true;//Disable parent Group
                        return false;
                    }
                });
            }
            else {//multi packs selected
                lbDependency.setDisabledItemFilter(new Filter<Group>() {
                    @Override public boolean include(Group group)//Groups to filter (disable)
                    {
                        Pack P = null;
                        Sequence<Pack> list = getSelectedPacks();
                        for(int i = 0; i < list.getLength(); i++) {
                            P = list.get(i);
                            if (P.getGroup() != null && P.getGroup().hasParent(group))
                                    return true;//Disable parent Group
                        }
                        return false;
                    }
                });
            }
        }
        else {//From Packs
            lbDependency.setListData(getPacks());//Data binding
            if (!multi_selection) {//one pack selected
                lbDependency.setDisabledItemFilter(new Filter<Pack>() {
                    @Override public boolean include(Pack pack)//Groups to filter (disable)
                    {
                        Pack P = getSelectedPack();
                        assert P!=null;
                        if (P.equals(pack))
                            return true;//Disable Pack
                        return false;
                    }
                });
            }
            else {//multi packs selected
                lbDependency.setDisabledItemFilter(new Filter<Pack>() {
                    @Override public boolean include(Pack pack)//Groups to filter (disable)
                    {
                        Pack P = null;
                        Sequence<Pack> list = getSelectedPacks();
                        for(int i = 0; i < list.getLength(); i++) {
                            P = list.get(i);
                            if (P.equals(pack))
                                return true;//Disable Pack
                        }
                        return false;
                    }
                });
            }
        }
        if (enabled) lbDependency.setEnabled(true);
    }
    
    /**
     * Properties Packs functions
     */
    private void nullProperties() {//no pack selected properties
        rbOsAll.setEnabled(false);
        rbOsWin.setEnabled(false);
        rbOsLin.setEnabled(false);
        rbOsMac.setEnabled(false);
        rbExecute.setEnabled(false);
        rbExtract.setEnabled(false);
        rbCopy.setEnabled(false);
        cbOverride.setEnabled(false);
        cbShortcut.setEnabled(false);
        btShortcutAdvanced.setEnabled(false);
        cbRequired.setEnabled(false);
        cbSelected.setEnabled(false);
        cbHidden.setEnabled(false);
        cbSilent.setEnabled(false);
        
        rbOsAll.setSelected(false);
        rbOsWin.setSelected(false);
        rbOsLin.setSelected(false);
        rbOsMac.setSelected(false);
        rbExecute.setSelected(false);
        rbExtract.setSelected(false);
        rbCopy.setSelected(false);
        cbOverride.setSelected(false);
        cbShortcut.setSelected(false);
        cbRequired.setSelected(false);
        cbSelected.setSelected(false);
        cbHidden.setSelected(false);
        
        inName.setEnabled(false);
        inName.setText("");
        inPInstallPath.setEnabled(false);
        inPInstallPath.setText("");

        cbDepType.setEnabled(false);
        lbDependency.setEnabled(false);
        lbDependency.setSelectedIndex(-1);
        inInstallGroups.setEnabled(false);
        inInstallGroups.setText("");

        inDescription.setEnabled(false);
        inDescription.setText("");
    }
    private void exeInstall(boolean SETUP) {//Executable/Setup properties (default:execute)
        rbExecute.setEnabled(true);
        rbExtract.setEnabled(false);
        rbCopy.setEnabled(true);
        
        if (SETUP) cbSilent.setEnabled(true);
        else cbSilent.setEnabled(false);
    }
    private void archiveInstall() {//Archive properties (default:copy)
        rbExecute.setEnabled(false);
        rbExtract.setEnabled(true);
        rbCopy.setEnabled(true);
        cbSilent.setEnabled(false);
    }
    private void fileInstall() {//File properties (default:copy)
        rbExecute.setEnabled(false);
        rbExtract.setEnabled(false);
        rbCopy.setEnabled(true);
        cbSilent.setEnabled(false);
    }
    private void packInstallModes(FILE_TYPE FT) {//Enable relevant install mode properties
        if (FT == FILE_TYPE.Executable) {//Executable
            exeInstall(false);
        } else if(FT == FILE_TYPE.Setup) {//Setup
            exeInstall(true);
        }else if (FT == FILE_TYPE.Folder || FT == FILE_TYPE.Archive) {//Archive, Folder
            archiveInstall();
        } else {//File
            fileInstall();
        }
    }
    private void enableProperties() {//Enable properties buttons/options for single pack selection
        cbRequired.setTriState(false); cbRequired.setEnabled(true);//Set required
        cbSelected.setTriState(false); cbSelected.setEnabled(true);//Set selected
        cbHidden.setTriState(false); cbHidden.setEnabled(true);//Set hidden
        cbOverride.setTriState(false); cbOverride.setEnabled(true);//Set override
        cbShortcut.setTriState(false); cbShortcut.setEnabled(true);//Set Shortcut
        inDescription.setEnabled(true);//Set Description
        inPInstallPath.setEnabled(true);//Set Install Path
        inName.setEnabled(true);//Set Pack Name
        inInstallGroups.setEnabled(true);//Set Install Groups
        lbDependency.setEnabled(true);//Set Dependency
        cbDepType.setEnabled(true);//Dependency Type
        rbOsAll.setEnabled(true);
        rbOsWin.setEnabled(true);
        rbOsLin.setEnabled(true);
        rbOsMac.setEnabled(true);
    }
    /**
     * Init properties for selected Pack
     * @param pack
     */
    private void setPackProperties(Pack pack) {
        //Global: bind radio button to install type
        assert pack != null;
        
        INSTALL_TYPE IT = pack.getInstallType();
        switch(IT) {
        case COPY:
            rbCopy.setSelected(true);
            break;
        case EXTRACT:
            rbExtract.setSelected(true);
            break;
        case EXECUTE:
            rbExecute.setSelected(true);
            cbOverride.setEnabled(false);
            cbShortcut.setEnabled(false);
            inPInstallPath.setEnabled(false);
        break;
        case DEFAULT:
            rbCopy.setSelected(true);
            break;
        }
        
        PLATFORM OS = pack.getInstallOs();
        switch(OS) {
        case ALL:
            rbOsAll.setSelected(true);
            break;
        case WINDOWS:
            rbOsWin.setSelected(true);
            break;
        case LINUX:
            rbOsLin.setSelected(true);
            break;
        case MAC:
            rbOsMac.setSelected(true);
            break;
        }
        
        cbSilent.setSelected(pack.isSilentInstall());
        cbRequired.setSelected(pack.isRequired());
        cbSelected.setSelected(pack.isSelected());
        cbHidden.setSelected(pack.isHidden());
        cbOverride.setSelected(pack.isOverride());
        cbShortcut.setSelected(pack.isShortcut());
        inDescription.setText(pack.getDescription());
        inPInstallPath.setText(pack.getInstallPath());
        inName.setText(pack.getInstallName());
        inInstallGroups.setText(pack.getInstallGroups());
        
        boolean GD = isGroupDependency();
        dependencyFill(GD);//update dependency filter
        if (pack.getGroupDependency() != null) {
            if (!GD) cbDepType.press();
            lbDependency.setSelectedItem(pack.getGroupDependency());
            btDepErase.setEnabled(true);
        }
        else if (pack.getPackDependency() != null) {
            if (GD) cbDepType.press();
            lbDependency.setSelectedItem(pack.getPackDependency());
            btDepErase.setEnabled(true);
        }
        else {
            lbDependency.setSelectedIndex(-1);
            btDepErase.setEnabled(false);
        }
    }
    
    /**
     * Init properties for list of selected packs
     * @param list: selected packs
     */
    private void setMultiProperties(Sequence<Pack> list) {
        //packs data equivalence check variables
        boolean req = list.get(0).isRequired();
        boolean sel = list.get(0).isSelected();
        boolean hid = list.get(0).isHidden();
        boolean over = list.get(0).isOverride();
        boolean shortcut = list.get(0).isShortcut();
        boolean silentinstall = list.get(0).isSilentInstall();
        boolean reqMix=false, selMix=false, hidMix=false, overMix=false, shortMix=false, silMix=false;
        
        FILE_TYPE FT = list.get(0).getFileType();
        boolean isSameFT = true;
        
        INSTALL_TYPE IT = list.get(0).getInstallType();
        boolean isSameIT = true;

        PLATFORM OS = list.get(0).getInstallOs();
        boolean isSameOS = true;
        
        //Dependency
        Group sameGDep; Pack samePDep;
        sameGDep = list.get(0).getGroupDependency();
        samePDep = list.get(0).getPackDependency();
        boolean isSameDep = ((isGroupDependency() && sameGDep!=null) || (!isGroupDependency() && samePDep!=null))?true:false;
        boolean hasValueDep = (sameGDep != null);
        //Install groups
        String sameIG = list.get(0).getInstallGroups();
        boolean isSameIG = true;
        boolean hasValueIG = (!sameIG.equals(""));
        String[] commonIG = list.get(0).getInstallGroups().split(",");
        //Install path
        String sameIP = list.get(0).getInstallPath();
        boolean isSameIP = true;
        boolean hasValueIP = (!sameIP.equals(""));
        
        //data equivalence check
        for(int i=1; i<list.getLength(); i++) {//loop on selected packs
            if (list.get(i).isRequired() != req)
                reqMix = true;
            if (list.get(i).isSelected() != sel)
                selMix = true;
            if (list.get(i).isHidden() != hid)
                hidMix = true;
            if (list.get(i).isOverride() != over)
                overMix = true;
            if (list.get(i).isShortcut() != shortcut)
                shortMix = true;
            if (list.get(i).isSilentInstall() != silentinstall)
                silMix = true;
            if (list.get(i).getFileType() != FT)
                isSameFT = false;
            if (list.get(i).getInstallType() != IT)
                isSameIT = false;
            if (list.get(i).getInstallOs() != OS)
                isSameOS = false;
            
            if (isSameDep) {
                if (isGroupDependency()) {//Group dependency
                    if ( (list.get(i).getGroupDependency()!=null && !list.get(i).getGroupDependency().equals(sameGDep)) ||
                            list.get(i).getGroupDependency()==null)
                        isSameDep = false;
                }
                else {//Pack dependency
                    if ( (list.get(i).getPackDependency()!=null && !list.get(i).getPackDependency().equals(samePDep)) ||
                            list.get(i).getPackDependency()==null)
                        isSameDep = false;
                }
            }
            if (!hasValueDep && (list.get(i).getGroupDependency()!=null || list.get(i).getPackDependency()!=null))
                hasValueDep = true;
            
            //Get only common install groups from all packs
            if (isSameIG && !list.get(i).getInstallGroups().equals(sameIG)) {
                sameIG = "";
                for(String S:commonIG) {
                    if (list.get(i).getInstallGroups().contains(S.trim()))
                        sameIG = sameIG.concat(S.trim()+", ");
                }
                if (sameIG.equals("")) isSameIG = false;
                else sameIG = sameIG.substring(0, sameIG.lastIndexOf(","));
            }
            if (!hasValueIG && !list.get(i).getInstallGroups().equals(""))
                hasValueIG = true;
            
            if (isSameIP && !list.get(i).getInstallPath().equals(sameIP))
                isSameIP = false;
            if (!hasValueIP && !list.get(i).getInstallPath().equals(""))
                hasValueIP = true;
        }
        
        //default value set from equivalence
        if (!reqMix) cbRequired.setSelected(req);
        else { cbRequired.setTriState(true); cbRequired.setState(State.MIXED); }

        if (!selMix) cbSelected.setSelected(sel);
        else { cbSelected.setTriState(true); cbSelected.setState(State.MIXED); }

        if (!hidMix) cbHidden.setSelected(hid);
        else { cbHidden.setTriState(true); cbHidden.setState(State.MIXED); }
        
        if (!overMix) cbOverride.setSelected(over);
        else { cbOverride.setTriState(true); cbOverride.setState(State.MIXED); }

        if (!shortMix) cbShortcut.setSelected(shortcut);
        else { cbShortcut.setTriState(true); cbShortcut.setState(State.MIXED); }

        if (!silMix) cbSilent.setSelected(silentinstall);
        else { cbSilent.setTriState(true); cbSilent.setState(State.MIXED); }

        if (isSameFT) {//Packs of same file type selected
            packInstallModes(FT);
        }
        else rbCopy.setEnabled(true);
        
        if (isSameIT) {//Packs of same install mode selected
            switch(IT) {
            case EXECUTE:
                rbExecute.setSelected(true);
                break;
            case EXTRACT:
                rbExtract.setSelected(true);
                break;
            case COPY:
                rbCopy.setSelected(true);
                break;
            default:
                rbCopy.setSelected(true);
                break;
            }
        }
        
        if (isSameOS) {//Packs of same install OS selected
            switch(OS) {
            case ALL:
                rbOsAll.setSelected(true);
                break;
            case WINDOWS:
                rbOsWin.setSelected(true);
                break;
            case LINUX:
                rbOsLin.setSelected(true);
                break;
            case MAC:
                rbOsMac.setSelected(true);
                break;
            }
        }
        
        if (isSameDep && isGroupDependency()) lbDependency.setSelectedItem(sameGDep);
        else if (isSameDep && !isGroupDependency()) lbDependency.setSelectedItem(samePDep);
        else lbDependency.setSelectedIndex(-1);
        if (hasValueDep) btDepErase.setEnabled(true);
        
        if (isSameIG) {//Display common install groups without edit
            inInstallGroups.setEnabled(false);
            inInstallGroups.setText(sameIG);
            inInstallGroups.setEnabled(true);
        }
        if (hasValueIG) btIGErase.setEnabled(true);
        
        if (isSameIP) inPInstallPath.setText(sameIP);
        if (hasValueIP) btIPErase.setEnabled(true);
        
        dependencyFill(isGroupDependency());//update dependency filter
        
        //Enable components
        cbOverride.setEnabled(true);//Set override
        cbShortcut.setEnabled(true);//Set shortcut
        cbRequired.setEnabled(true);//Set required
        cbSelected.setEnabled(true);//Set selected
        cbHidden.setEnabled(true);//Set hidden
        rbOsAll.setEnabled(true);//Set OS platforms
        rbOsWin.setEnabled(true);
        rbOsLin.setEnabled(true);
        rbOsMac.setEnabled(true);
        lbDependency.setEnabled(true);//Set dependency
        inInstallGroups.setEnabled(true);//Set Install Groups
        inPInstallPath.setEnabled(true);//Set Pack Install Paths
    }
    
    /**
     * Initialize Tab Data from Scan tab
     */
    public void scanInit() {
        System.out.println("scan init");
        nullProperties();//Initialize properties values
        ngdialog.setHierarchy(false, "");//Initialize NewGroup Hierarchy
        
        GroupFactory.clear();//Group Model Data clear
        treeData.clear();//TreeView Data clear
        if (ScanFrame.getScanMode() == SCAN_MODE.RECURSIVE_SCAN) {//If Recursive Scan, Folders > Groups
            btImport.setEnabled(true);//Enable import button
        }
        else {
            btImport.setEnabled(false);//Disable import button
        }
        
        PackFactory.clear();//Clear all packs
        for(Pack P:ScanFrame.getPacks()) {//Add Packs one by one
            Pack pack = new Pack(P);
            pack.setGroup(null);
            PackFactory.addPack(pack);
        }
        
        setModified(true);//Modified flag
    }

    /**
     * Initialize Tab Data from loaded save file
     */
    public void loadInit() {
        System.out.println("load init");
        nullProperties();//Initialize properties values
        ngdialog.setHierarchy(false, "");//Initialize NewGroup Hierarchy
        
        ADataImport.perform(this);
        
        setModified(true);//Modified flag
    }

}
