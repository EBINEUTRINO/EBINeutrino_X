package ebiNeutrino.core.utils;

import java.sql.PreparedStatement;
import java.util.TimerTask;

import ebiNeutrinoSDK.EBIPGFactory;


public class EBIKeepDatabaseAliveTask extends TimerTask {

    private EBIPGFactory ebiPGFactory = null;

    public EBIKeepDatabaseAliveTask(EBIPGFactory ebiFunc){
       ebiPGFactory = ebiFunc;
    }

    public void run(){
        PreparedStatement ps =  ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT ID FROM EBIUSER");
        ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps);
    }

}
