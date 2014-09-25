package ebiNeutrinoSDK.interfaces;

/**
 * Interface which secure important system functionality 
 *
 */

public interface IEBISecurity {
	public boolean checkCanReleaseModules();
	public boolean secureModule();
}
