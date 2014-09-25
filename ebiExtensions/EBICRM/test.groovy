

import javax.swing.JButton;

button = new JButton("Hallo");
button.actionPerformed ={
 def i =0;
 i+=20
 system.message.info("hello from an app $i xy zxy"+i);
}

system.getIEBIToolBarInstance().addCustomToolBarComponent(button);
