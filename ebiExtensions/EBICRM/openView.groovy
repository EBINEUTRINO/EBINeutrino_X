
import javax.swing.*
import java.awt.BorderLayout


Project_openView.setLocation(system.gui.getVisualPanel("Project").
                             getWidth()-system.gui.getButton("openView","Project").getWidth()-20,
                                        system.gui.getButton("openView","Project").getY())

JLabel lb = new JLabel(system.getLANG("EBI_LANG_FULLMODE_VIEW"))
system.gui.getVisualPanel("Project").add(lb,null)
lb.setSize(Project_taskGraph.getSize())
lb.setLocation(Project_taskGraph.getLocation())
lb.setVisible(false)   


Project_openView.actionPerformed={
 
    JFrame frm = new JFrame()
    
    frm.setTitle(system.getLANG("EBI_LANG_C_TAB_PROJECT"))
    frm.setSize(800,600)
    frm.setLocation(150,50)
    frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
    frm.setVisible(true)  
   
    frm.getContentPane().setLayout(new BorderLayout())
    frm.getContentPane().add(Project_taskGraph,BorderLayout.CENTER)
    
    lb.setVisible(true)   
    lb.setLocation(Project_taskGraph.getLocation())
    lb.setSize(Project_taskGraph.getSize())
    lb.updateUI()
    
    frm.windowClosing={
          lb.setVisible(false) 
          Project_taskGraph.setLocation(lb.getLocation())
          Project_taskGraph.setSize(lb.getSize())
          Project_taskGraph.setVisible(true)
         
          system.gui.getVisualPanel("Project").add(Project_taskGraph,BorderLayout.CENTER)  
          system.gui.getVisualPanel("Project").updateUI()
          system.gui.getVisualPanel("Project").repaint()
          frm=null;
    }
    
    
}



