package ebiNeutrinoSDK.gui.dialogs;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;


/**
 * Set a value for the specified combobox
 *
 */
public class EBIDialogValueSetter {

	private MyTableModelValueSetter tabMod = null;
	private String Tab = "";
	private String Title = "";
	private int id= -1;
	private boolean isSaveOrUpdate = false;
	private int selRow = -1;
	private EBIPGFactory ebiPGFactory = null;

	
	public EBIDialogValueSetter(EBIPGFactory main,String tab,String title) {

		tabMod = new MyTableModelValueSetter();
		ebiPGFactory = main;

        ebiPGFactory.getIEBIGUIRendererInstance().loadGUI("CRMDialog/valueSetDialog.xml");

		Tab = tab.toUpperCase();
		Title = title;
		initialize();
		load();
		ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").setEnabled(false);
        ebiPGFactory.getIEBIGUIRendererInstance().getButton("editBnt","valueSetterDialog").setEnabled(false);
		ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").requestFocus();
	}

	private void initialize() {

            ListSelectionModel rowSM = ebiPGFactory.getIEBIGUIRendererInstance().getTable("valueTable","valueSetterDialog").getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").setEnabled(false);
                ebiPGFactory.getIEBIGUIRendererInstance().getButton("editBnt","valueSetterDialog").setEnabled(false);
            } else {
              selRow = lsm.getMinSelectionIndex();
              if(selRow <= tabMod.data.length && selRow != -1){
                  if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiPGFactory.getIEBIGUIRendererInstance().getTable("valueTable","valueSetterDialog").getValueAt(selRow, 0).toString())){
                      ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").setEnabled(true);
                      ebiPGFactory.getIEBIGUIRendererInstance().getButton("editBnt","valueSetterDialog").setEnabled(true);
                      isSaveOrUpdate = true;
                      loadEdit();
                      ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").requestFocus();
                  }
              }
            }
        }
      });
	}

	public void setVisible(){
       ebiPGFactory.getIEBIGUIRendererInstance().getEBIDialog("valueSetterDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_SETTINGS_FOR") +":" + Title);
       ebiPGFactory.getIEBIGUIRendererInstance().getVisualPanel("valueSetterDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_SETTINGS_FOR") +":" + Title);

       ebiPGFactory.getIEBIGUIRendererInstance().getLabel("name","valueSetterDialog").setText(EBIPGFactory.getLANG("EBI_LANG_NAME"));

       ebiPGFactory.getIEBIGUIRendererInstance().getButton("saveValue","valueSetterDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
	   ebiPGFactory.getIEBIGUIRendererInstance().getButton("saveValue","valueSetterDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(isSaveOrUpdate == false){
					    save();
					}else{
					    update();
					}

				}
		});
        
       ebiPGFactory.getIEBIGUIRendererInstance().getButton("closeDialog","valueSetterDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLOSE"));
	   ebiPGFactory.getIEBIGUIRendererInstance().getButton("closeDialog","valueSetterDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ebiPGFactory.getIEBIGUIRendererInstance().getEBIDialog("valueSetterDialog").setVisible(false);
				}
	   });

       ebiPGFactory.getIEBIGUIRendererInstance().getButton("newBnt","valueSetterDialog").setIcon(EBIConstant.ICON_NEW);
	   ebiPGFactory.getIEBIGUIRendererInstance().getButton("newBnt","valueSetterDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					newRecord();
				}
	   });

       ebiPGFactory.getIEBIGUIRendererInstance().getButton("editBnt","valueSetterDialog").setIcon(EBIConstant.ICON_EDIT);
	   ebiPGFactory.getIEBIGUIRendererInstance().getButton("editBnt","valueSetterDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiPGFactory.getIEBIGUIRendererInstance().getTable("valueTable","valueSetterDialog").getValueAt(selRow, 0).toString())){
					    loadEdit();
                    }
				}
	   });

       ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").setIcon(EBIConstant.ICON_DELETE);
	   ebiPGFactory.getIEBIGUIRendererInstance().getButton("deleteBnt","valueSetterDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					delete();
					newRecord();
				}
		});

       ebiPGFactory.getIEBIGUIRendererInstance().getTable("valueTable","valueSetterDialog").setModel(tabMod);

       ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").addKeyListener(new java.awt.event.KeyAdapter() {
			    public void keyPressed(KeyEvent e) {
                   if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        if(isSaveOrUpdate == false){
                            save();
                        }else{
                            update();
                        }

                        load();
                        newRecord();
                        ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").requestFocus();
                    }
			    }
		});
        
       ebiPGFactory.getIEBIGUIRendererInstance().showGUI(); 
    }
	
	private boolean validateInput(){
		if("".equals(ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").getText())){
			EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
        if (!isSaveOrUpdate){
            for(int i=0; i<tabMod.data.length; i++){
                if(tabMod.data[i][1].toString().toLowerCase().equals(ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").getText().toLowerCase())){
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }

	   return true;
	}
	
	
	private void save(){
	   if(!validateInput()){
		   return;
	   }
           try {

               ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
               PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("INSERT INTO "+Tab+"  (NAME) VALUES(?)");
               ps1.setString(1,ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").getText());
               ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps1);

           } catch (Exception e) {
            e.printStackTrace();  
           }finally{
               ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
               load();
			   newRecord();
           }
		
	}
	
	private void update(){
		if(!validateInput()){
			   return;
		   }
        try {
                ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
                PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("UPDATE "+Tab+"  SET  NAME=? where id=?");
                ps1.setString(1,ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").getText());
                ps1.setInt(2,id);
                ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
            load();
			newRecord();
        }
	}
	
	private void delete(){
       try {
           ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
           PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("DELETE FROM "+Tab+" WHERE id=?");
           ps1.setInt(1,id);
           if(!ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps1)){
              EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_DELETE_RECORD")).Show(EBIMessage.ERROR_MESSAGE);
           }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
          ebiPGFactory.getIEBIDatabase().setAutoCommit(false); 
       }
	}
	
	private void load(){
        ResultSet set = null;
      try{
    	  PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM "+this.Tab);
          set = ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);
    	  set.last();
    	  if(set.getRow() > 0){
    		  tabMod.data = new Object[set.getRow()][2];
    	   set.beforeFirst();
    	   int i=0;
    	   while(set.next()){
    		   tabMod.data[i][0] = set.getString("ID");
    		   tabMod.data[i][1] = set.getString("NAME");
    	       i++;
    	   }
    	  }else{
    		tabMod.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
    	  }
          tabMod.fireTableDataChanged();

      }catch(SQLException ex){
    	 EBIExceptionDialog.getInstance(ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
    	 return;
      }finally{
          if(set != null){
              try {
                  set.close();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          }

      }
	}
	
	private void loadEdit(){
        ResultSet set = null;
		  try{

              PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM "+this.Tab+" WHERE ID=?");
              ps1.setString(1,tabMod.data[ebiPGFactory.getIEBIGUIRendererInstance().getTable("valueTable","valueSetterDialog").convertRowIndexToModel(selRow)][0].toString());
              set = ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);
	    	   
	    	 set.last();
	    	 if(set.getRow() > 0){
	    	   set.beforeFirst();
	    	   set.next();
	   		   this.id = set.getInt("ID");
	   		   ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").setText(set.getString("NAME"));
	    	 }
	    	 set.close();
	      }catch(SQLException ex){
	    	 EBIExceptionDialog.getInstance(ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
	    	 return;
	      }finally{
              if(set != null){
                  try {
                      set.close();
                  } catch (SQLException e) {
                      e.printStackTrace();
                  }
              }
          }
	}
	
	
	private void newRecord(){
		isSaveOrUpdate = false;
		ebiPGFactory.getIEBIGUIRendererInstance().getTextfield("nameValue","valueSetterDialog").setText("");
		this.id = 0;
		load();
	}
}
