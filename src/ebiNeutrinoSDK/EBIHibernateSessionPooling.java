package ebiNeutrinoSDK;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;


/**
 * Hibernate session pooling class
 * (Experimental state)
 */
public class EBIHibernateSessionPooling {

    private Hashtable<String, Object> sessionList = null;
    public Configuration cfg = null;
    private SessionFactory sessionFactory = null;
    private EBIPGFactory ebiPGFactory = null;

    public EBIHibernateSessionPooling(EBIPGFactory ebiFactory, Configuration cfg) {
        ebiPGFactory = ebiFactory;
        this.cfg = cfg;
        sessionList = new Hashtable<String, Object>();
        sessionFactory = cfg.buildSessionFactory();
    }

    public void openHibernateSession(String Name) {
        
        if(!sessionList.containsKey(Name)){
        	Object[] sessionObject = new Object[2];
        	sessionObject[0] = sessionFactory.openSession(ebiPGFactory.getIEBIDatabase().getActiveConnection());
            sessionObject[1] = ((Session) sessionObject[0]).beginTransaction();
            sessionList.put(Name, sessionObject);
        }
        
    }

    public void removeAllHibernateSessions() {
       if(sessionList != null || sessionList.size() > 0){
        Iterator iter = sessionList.keySet().iterator();

        while (iter.hasNext()) {
            String alias = (String) iter.next();

            try {
              if(((Object[]) sessionList.get(alias))[1] != null &&
                   ((Object[]) sessionList.get(alias))[0] != null  ){
                Transaction act = ((Transaction) ((Object[]) sessionList.get(alias))[1]);
                Session ses = ((Session) ((Object[]) sessionList.get(alias))[0]);

                if (act.isActive()) {
                    if (ebiPGFactory.getIEBIDatabase().getActiveConnection().getAutoCommit()) {
                        ebiPGFactory.getIEBIDatabase().getActiveConnection().setAutoCommit(false);
                    }
                    act.rollback();
                }

                if (ses.isOpen()) {
                    ses.close();
                }
             }
            } catch (SQLException x) {
            } catch (HibernateException ex) {
            }

        }

        sessionList.clear();
       }
    }

    public boolean removeHibernateSession(String index) {
        if(((Object[]) sessionList.get(index))[0] != null){
            ((Session) ((Object[]) sessionList.get(index))[0]).close();
            sessionList.remove(index);
        }else{
            return false;
        }
        return true;
    }

    public Session getHibernateSession(String index) {
         try{
            if(((Object[]) sessionList.get(index))[0] == null &&
                    !((Session) ((Object[]) sessionList.get(index))[0]).isConnected() ||
                    !((Session) ((Object[]) sessionList.get(index))[0]).isOpen() ||
                    !ebiPGFactory.getIEBIDatabase().isValidConnection()) {

                if(ebiPGFactory.getIEBIDatabase().getActiveConnection().getAutoCommit()
                                            || ebiPGFactory.getIEBIDatabase().isAutoCommit()){
                    ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
                }

                openHibernateSession(index);
            }

     }catch(Exception ex) {}
        return ((Session) ((Object[]) sessionList.get(index))[0]);
    }

    public Transaction getHibernateTransaction(String index) {
         try{
                if(((Object[]) sessionList.get(index))[1] == null
                        || !ebiPGFactory.getIEBIDatabase().isValidConnection()){
                  openHibernateSession(index);
                }

                if(ebiPGFactory.getIEBIDatabase().getActiveConnection().getAutoCommit()
                                                || ebiPGFactory.getIEBIDatabase().isAutoCommit()){
                    ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
                }
         }catch(Exception ex){}

        return ((Transaction) ((Object[]) sessionList.get(index))[1]);
    }
}
