package ebiNeutrino.core;


public class EBIVersion {

    private final String majVersion = "5.";
    private final String minVersion = "0";
    private final String build      = "";
    public static EBIVersion version = null;

    public EBIVersion(){}

    public String getVersion(){
        return majVersion + minVersion + build;
    }

    public static EBIVersion getInstance(){
        if(version == null){
            version = new EBIVersion();
        }
        return version;
    }

}