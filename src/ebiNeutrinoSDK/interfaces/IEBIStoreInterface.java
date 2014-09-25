package ebiNeutrinoSDK.interfaces;

/**
 * Is used to help save data
 * Also it remembers the user with a message dialog
 */
public interface IEBIStoreInterface {
	/**
	 * ebiSave
	 * @return
	 */
	public boolean ebiSave();
	/**
	 * ebiUpdate
	 * @return
	 */
	public boolean ebiUpdate();
	/**
	 * ebiDelete
	 * @return
	 */
	public boolean ebiDelete();
}
