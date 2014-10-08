package ebiNeutrinoSDK.gui.dialogs;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.HandScrollListener;
import org.jdesktop.swingx.JXPanel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;


/**
 * Show a image file
 *
 */
public class EBIImageViewer extends EBIDialog {

	private JXPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JLabel imageContainer = null;
    private ImageIcon image=null;


	/**
	 * This is the xxx default constructor
	 */
	public EBIImageViewer(EBIMain main,ImageIcon image) {
		super(main);

		this.setResizable(true);
        setName("EBIImageViewer");
		storeLocation(true);
		storeSize(true);
        this.image = image;
		initialize();
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
	private JXPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JXPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.setBackground(Color.black);
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
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
			jScrollPane.setViewportView(getImageContainer());
            jScrollPane.revalidate();
		}
		return jScrollPane;
	}
	
	private JLabel getImageContainer(){
		if(this.imageContainer == null){
			imageContainer = new JLabel();
			imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
            imageContainer.setIcon(this.image);
		}
		return this.imageContainer;
	}
}
