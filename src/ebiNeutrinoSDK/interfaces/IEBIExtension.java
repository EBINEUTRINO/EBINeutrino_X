package ebiNeutrinoSDK.interfaces;

/**
 * Each EBINeutrino business module should implement this interface otherwise the system
 * would't recognize an extension as business module
 *
 */

public interface IEBIExtension {

	public boolean ebiMain(Object obj);
	public Object ebiRemove();
    public void onExit();
    public void onLoad();
	
}
