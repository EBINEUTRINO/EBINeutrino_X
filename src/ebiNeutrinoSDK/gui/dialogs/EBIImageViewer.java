package ebiNeutrinoSDK.gui.dialogs;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.HandScrollListener;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;


/**
 * Show a image file
 *
 */
public class EBIImageViewer extends EBIDialogExt {

	private EBIVisualPanelTemplate jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JLabel imageConstainer = null;


	/**
	 * This is the xxx default constructor
	 */
	public EBIImageViewer(EBIMain main,ImageIcon image) {
		super(main);

		this.setResizable(true);
        setName("EBIImageViewer");
		storeLocation(true);
		storeSize(true);
		initialize();
        jContentPane.setModuleTitle("EBI Image Viewer");
        jContentPane.setEnableChangeComponent(false);
        jContentPane.setClosable(true);
        jContentPane.setModuleIcon(EBIConstant.ICON_NEW);
        jContentPane.setBackgroundColor(EBIPGFactory.systemColor);
		this.imageConstainer.setIcon(image);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(634, 462);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new EBIVisualPanelTemplate();
			jContentPane.getPanel().setLayout(new BorderLayout());
			jContentPane.setBackground(Color.black);
			jContentPane.getPanel().add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
            HandScrollListener scrollListener = new HandScrollListener(getImageContainer());
            jScrollPane.getViewport().addMouseMotionListener(scrollListener);
            jScrollPane.getViewport().addMouseListener(scrollListener);
            jScrollPane.setBackground(Color.black);
			jScrollPane.setViewportView(getImageContainer());

		}
		return jScrollPane;
	}
	
	private JLabel getImageContainer(){
		if(this.imageConstainer == null){
			imageConstainer = new JLabel();
			imageConstainer.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return this.imageConstainer;
	}
}
