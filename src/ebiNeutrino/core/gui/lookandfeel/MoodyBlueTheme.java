package ebiNeutrino.core.gui.lookandfeel;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;


public class MoodyBlueTheme extends DefaultMetalTheme
{
	public String getName() { return "Moody Dark"; }
	
		// blue shades
	private final ColorUIResource primary1 		= new ColorUIResource(255,255,255);
	private final ColorUIResource primary2 		= new ColorUIResource(120,120,120);
    private final ColorUIResource primary3 		= new ColorUIResource(120,120,120);

    private final ColorUIResource secondary1 	= new ColorUIResource(180,180,180);
    private final ColorUIResource secondary2 	= new ColorUIResource(120,120,120);
    private final ColorUIResource secondary3 	= new ColorUIResource(180,180,180);

    private final FontUIResource windowTitleFont = new FontUIResource("verdana", Font.PLAIN, 12);
    private final FontUIResource controlFont = new FontUIResource("verdana", Font.PLAIN, 10);

    // the functions overridden from the base class => DefaultMetalTheme
			
    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }


    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }


    public javax.swing.plaf.FontUIResource getControlTextFont() { return controlFont; }

    public javax.swing.plaf.FontUIResource getSystemTextFont() { return controlFont; }

    public javax.swing.plaf.FontUIResource getUserTextFont() { return controlFont; }

    public javax.swing.plaf.FontUIResource getMenuTextFont() { return controlFont; }

    public javax.swing.plaf.FontUIResource getWindowTitleFont() { return windowTitleFont; }

    public javax.swing.plaf.FontUIResource getSubTextFont() { return controlFont; }


}