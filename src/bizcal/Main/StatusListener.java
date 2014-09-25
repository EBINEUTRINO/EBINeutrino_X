package bizcal.Main;

public interface StatusListener {
    public void status(String str)
            throws Exception;

    public void refresh()
            throws Exception;
}