package ebiCRM.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.dialogs.EBIDialogSearchCompany;
import ebiCRM.table.models.MyTableModelCRMAddress;
import ebiCRM.table.models.MyTableModelCRMContact;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.model.hibernate.Companyhirarchie;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMCompanyPane {

	public static String[] categories     = null;
	public static String[] cooperations   = null;
	public static String[] classification = null;
	private EBICRMModule ebiModule = null;
    private MyTableModelCRMAddress tabModel = null;
	private Companyhirarchie hComp = null;
	public Set<Companyhirarchie> listH = null;
	public MyTableModelCRMContact ctabModel = null;
    private IEBIGUIRenderer guiRenderer = null;

    /**
	 * This is the default constructor
	 */
	public EBICRMCompanyPane(EBICRMModule main) {
        guiRenderer = main.guiRenderer;
		ebiModule = main;
        tabModel = new MyTableModelCRMAddress();
        ctabModel = new MyTableModelCRMContact();
	}

    public void initializeAction() {

        //Configure contact table
        guiRenderer.getTable("companyTableContactViewX","Company").setModel(ctabModel);
        guiRenderer.getTable("companyTableContactViewX","Company").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //Configure address table
        guiRenderer.getTable("companyTableAddressView","Company").setModel(tabModel);
        guiRenderer.getTable("companyTableAddressView","Company").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        guiRenderer.getVisualPanel("Company").addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				guiRenderer.getTextfield("rootText","Company").grabFocus();
			}
        });

       guiRenderer.getButton("selectRoot","Company").setIcon(EBIConstant.ICON_SEARCH);
       guiRenderer.getButton("selectRoot","Company").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    new EBIDialogSearchCompany(ebiModule,true,false);
				}
		});

        guiRenderer.getTextfield("nameText","Company").setText("");
        guiRenderer.getTextfield("nameText","Company").addKeyListener(new KeyAdapter() {
			    public void keyTyped(KeyEvent e) {
                     EBIPGFactory.canRelease = false;
			    }
		});

        // Configuring the Category combobox

        guiRenderer.getComboBox("categoryText","Company").addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});

        guiRenderer.getComboBox("categoryText","Company").addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});

        guiRenderer.getComboBox("categoryText","Company").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    if(!guiRenderer.getComboBox("categoryText","Company").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
                        if(!EBICRMModule.isExistCompany){
                            Object[] obj = ebiModule.dynMethod.getInternNumber(guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString(),false);
                            ebiModule.beginChar = obj[1].toString();
						    guiRenderer.getTextfield("internalNrText","Company").setText(obj[1].toString()+obj[0].toString());
						}else if(EBICRMModule.isExistCompany && !guiRenderer.getComboBox("categoryText","Company").getSelectedItem().equals(ebiModule.ebiPGFactory.company.getCategory())){
							Object[] obj = ebiModule.dynMethod.getInternNumber(guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString(),false);
                            ebiModule.beginChar = obj[1].toString();
						    guiRenderer.getTextfield("internalNrText","Company").setText(obj[1]+obj[0].toString());
						}else if(EBICRMModule.isExistCompany && guiRenderer.getComboBox("categoryText","Company").getSelectedItem().equals(ebiModule.ebiPGFactory.company.getCategory())){
							Object[] obj = ebiModule.dynMethod.getInternNumber(guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString(),false);
                            if(ebiModule.ebiPGFactory.company.getBeginchar() == null || "".equals(ebiModule.ebiPGFactory.company.getBeginchar())){
                                ebiModule.beginChar = obj[1].toString();
                            }else{
                               ebiModule.beginChar = ebiModule.ebiPGFactory.company.getBeginchar(); 
                            }

                            guiRenderer.getTextfield("internalNrText","Company").setText(ebiModule.ebiPGFactory.company.getCompanynumber() == null ? "-1" : ebiModule.beginChar +String.valueOf(ebiModule.ebiPGFactory.company.getCompanynumber() == -1 ? obj[0] : ebiModule.ebiPGFactory.company.getCompanynumber() ));
						}
                    }else{
                        guiRenderer.getTextfield("internalNrText","Company").setText("-1");
                    }
                }
            });

        guiRenderer.getComboBox("cooperationText","Company").addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});

        guiRenderer.getComboBox("cooperationText","Company").addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});


        guiRenderer.getComboBox("classificationText","Company").addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});

        guiRenderer.getComboBox("classificationText","Company").addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					EBIPGFactory.canRelease = false;
				}
		});
    }

    public void initialize() {
        listH = new HashSet<Companyhirarchie>();
        hComp = new Companyhirarchie();

        guiRenderer.getVisualPanel("Company").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        guiRenderer.getVisualPanel("Company").setCreatedFrom(EBIPGFactory.ebiUser);
        guiRenderer.getVisualPanel("Company").setChangedDate("");
        guiRenderer.getVisualPanel("Company").setChangedFrom("");

        guiRenderer.getComboBox("categoryText","Company").setModel(new DefaultComboBoxModel(categories));
        guiRenderer.getComboBox("cooperationText","Company").setModel(new DefaultComboBoxModel(cooperations));
        guiRenderer.getComboBox("classificationText","Company").setModel(new DefaultComboBoxModel(classification));
        guiRenderer.getComboBox("categoryText","Company").setSelectedIndex(0);
        guiRenderer.getComboBox("classificationText","Company").setSelectedIndex(0);
        guiRenderer.getComboBox("cooperationText","Company").setSelectedIndex(0);
        guiRenderer.getTextfield("internalNrText","Company").setText("");
        guiRenderer.getTextfield("rootText","Company").setText("");
        guiRenderer.getTextfield("rootText","Company").requestFocus();
        guiRenderer.getTextfield("custNrText","Company").setText("");
        guiRenderer.getTextfield("nameText","Company").setText("");
        guiRenderer.getTextfield("name1Text","Company").setText("");
        guiRenderer.getTextfield("taxIDText","Company").setText("");
        guiRenderer.getTextfield("employeeText","Company").setText("");
        guiRenderer.getTextfield("telephoneText","Company").setText("");
        guiRenderer.getTextfield("faxText","Company").setText("");
        guiRenderer.getTextfield("internetText","Company").setText("");
        guiRenderer.getTextfield("emailText","Company").setText("");
        guiRenderer.getCheckBox("lockCompany","Company").setSelected(false);
        guiRenderer.getTextarea("companyDescription","Company").setText("");
        tabModel = new MyTableModelCRMAddress();
        tabModel.fireTableDataChanged();
        //Configure address table
        guiRenderer.getTable("companyTableAddressView","Company").setModel(tabModel);
        guiRenderer.getTable("companyTableAddressView","Company").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        ctabModel = new MyTableModelCRMContact();
        ctabModel.fireTableDataChanged();

        guiRenderer.getTable("companyTableContactViewX","Company").setModel(ctabModel);
        guiRenderer.getTable("companyTableContactViewX","Company").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        EBIPGFactory.canRelease = true;
    }

	public boolean setHierarchies(String name,int parent){
        this.hComp.setCompany(this.ebiModule.ebiPGFactory.company);
		this.hComp.setParent(parent);
		this.hComp.setName(name);
		guiRenderer.getTextfield("rootText","Company").setText(this.hComp.getName());
		this.hComp.setCreateddate(new Date());
		this.hComp.setCreatedfrom(EBIPGFactory.ebiUser);
		listH.add(this.hComp);
        return true;
	}
	
	public void showHierarchies(){
      try{
        if(this.listH != null){
            Iterator itr = this.listH.iterator();
            while(itr.hasNext()){
                 this.hComp = (Companyhirarchie)itr.next();
                 guiRenderer.getTextfield("rootText","Company").setText(this.hComp.getName());

            }
        }
      }catch(Exception ex){}
	}

    public MyTableModelCRMAddress getDataTableCompany(){

        if(tabModel == null){
            tabModel = new MyTableModelCRMAddress();
        }

        return tabModel;
    }

    public void ebiNew(){
        ebiModule.resetUI(false,false);
    }

    public void ebiSave(){
        ebiModule.saveCompany();
    }

    public void ebiDelete(){
        if(ebiModule.ebiPGFactory.company != null){
            if(ebiModule.deleteCRM()==true){
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_DELETED")).Show(EBIMessage.INFO_MESSAGE);
            }
        }
    }

}