package ebiCRM.table.models;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class MyOwnCellRederer extends DefaultTableCellRenderer implements TableCellRenderer{
	
	    private boolean isOffer = false;
        private boolean isActivity = false;
	   
		public MyOwnCellRederer(boolean isOffer, boolean isActivity){
			super();
			setHorizontalAlignment( SwingConstants.CENTER );
			setOpaque(true); //MUST do this for background to show up.
			this.isOffer = isOffer;
            this.isActivity = isActivity;
		}


		public Component getTableCellRendererComponent(JTable table,
														 Object sample,
														 boolean isSelected,
														 boolean hasFocus,
														 int row,
														 int column)
		{      

            JPanel lab = new JPanel();
            JLabel lax = new JLabel(sample == null ? "" : (String)sample.toString());
            lab.setLayout(new BorderLayout());
            lab.add(lax,BorderLayout.CENTER);
            
            if(isActivity){
                int r = 255;
                int g = 255;
                int b = 255;

                if(table.getValueAt(row,4) != null && !"".equals(table.getValueAt(row,4).toString())){

                    String[] splCol = table.getValueAt(row,4).toString().split(",");
                    r = Integer.parseInt(splCol[0]);
                    g = Integer.parseInt(splCol[1]);
                    b = Integer.parseInt(splCol[2]);

                }

                if(isSelected){
                    lab.setBackground(new Color(0,0,0));
                    lax.setForeground(new Color(255,255,255));
                }else{
                  lab.setBackground(new Color(r,g,b));
                  if(r <=80 && g <=80 && b <= 80){
                      lax.setForeground(new Color(255,255,255));
                  }else{
                      lax.setForeground(new Color(0,0,0));
                  }
                }
     
            }else{

			 String s  = null;
	          if(isOffer == true){
	        	 MyTableModelOffer  tabModel =  (MyTableModelOffer)table.getModel(); 
	        	 s = tabModel.data[row][6].toString();
	          }else{
		         MyTableModelOrder  tabModel =  (MyTableModelOrder)table.getModel(); 
		         s = tabModel.data[row][6].toString();
	          }
		    
			         if(!isSelected){ 
				      if(s.equalsIgnoreCase("1")) {
				          lab.setForeground(java.awt.Color.BLACK);
				          lab.setBackground(new java.awt.Color(60,40,255));
				      } else if(s.equalsIgnoreCase("0")){
				    	  lab.setForeground(java.awt.Color.BLACK);
				          lab.setBackground(java.awt.Color.green);
				      }else{
				    	  lab.setForeground(java.awt.Color.BLACK);
				    	  lab.setBackground(java.awt.Color.WHITE);
				      }
			         }else{
				    	  lab.setForeground(java.awt.Color.BLACK);
				    	  lab.setBackground(java.awt.Color.WHITE);
			         }

            }
			return lab;
		}
		
	}