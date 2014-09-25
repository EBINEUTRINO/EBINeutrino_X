
import javax.swing.*
import java.awt.BorderLayout
import org.jdesktop.swingx.JXEditorPane

try{
    
      Address_showAddress.actionPerformed ={

           address ="http://maps.google.com/maps?f=q&source=s_q&&geocode=&q="+
                   Address_streetText.getText()+"+"+
                          Address_zipText.getText()+"+"+
                              Address_LocationText.getText()+"+"+
                                 Address_countryText.getText()
          
          java.awt.Desktop.getDesktop().browse(java.net.URI.create(address.replaceAll(" ","+")));
          
      }

}catch(Exception ex){}
