package bizcal.Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.hibernate.Query;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmcalendar;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;


public class  EBIGCalSync {

      public BizcalMain bizMain = null;

      private Hashtable<Integer, ArrayList> calProperties  = new Hashtable<Integer,ArrayList>();
      private EBIPropertiesRW properties = null;

      public EBIGCalSync(BizcalMain main){
         bizMain = main;

         bizMain.ebiModule.guiRenderer.loadGUI("CRMDialog/crmCalendarGoogleSync.xml");
         properties = EBIPropertiesRW.getPropertiesInstance();

         //INITIALIZE DATE FROM
         Calendar dateFrom = new GregorianCalendar();
         dateFrom.setTime(new Date());
         dateFrom.set(Calendar.DAY_OF_MONTH,1);

         bizMain.ebiModule.guiRenderer.getTimepicker("fromDateText","googleCalSync").setDate(dateFrom.getTime());

         //INITIALIZE DATE TO
         Calendar dateTo = new GregorianCalendar();
         dateTo.setTime(new Date());
         dateTo.set(Calendar.DAY_OF_MONTH,dateTo.getActualMaximum(Calendar.DAY_OF_MONTH));
          
         bizMain.ebiModule.guiRenderer.getTimepicker("toDateText","googleCalSync").setDate(dateTo.getTime());

         //RESTORE FIELD SETTINGS
         boolean haveUser = false;
         if(!"".equals(properties.getValue("GOOGLE_CAL_USERNAME"))){
            bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").setText(properties.getValue("GOOGLE_CAL_USERNAME"));
            haveUser = true;
         }

         boolean havePassword = false;
         if(!"".equals(properties.getValue("GOOGLE_CAL_PASSWORD"))){
            bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").setText(properties.getValue("GOOGLE_CAL_PASSWORD"));
            havePassword = true;
         }

         if(haveUser && havePassword){

            getCalendars();

         }

      }


      public void setVisible(){

        bizMain.ebiModule.guiRenderer.getLabel("gCalPictures","googleCalSync").setIcon(new ImageIcon("images/googleCalSyncImg.jpg"));
        bizMain.ebiModule.guiRenderer.getLabel("synDescription","googleCalSync").setFont(new Font("SansSerif", Font.BOLD, 12));

        bizMain.ebiModule.guiRenderer.getButton("getCalButton","googleCalSync").addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if(!validateInput()){
                    return;
                }
                getCalendars();
            }
        });

        bizMain.ebiModule.guiRenderer.getButton("startSync","googleCalSync").addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if(!validateInputSync()){
                    return;
                }
                syncGoogleToNeutrino();
                properties.setValue("GOOGLE_CAL_USERNAME",bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").getText());
                properties.setValue("GOOGLE_CAL_PASSWORD",bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").getText());
                properties.setValue("GOOGLE_CAL_SEL_CAL",bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").getSelectedItem().toString());
                properties.saveProperties();

            }
        });

        bizMain.ebiModule.guiRenderer.showGUI();  

    }

    /**
     *  Retrieve Calendars
     */

    public void getCalendars(){
      bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").removeAllItems();
      bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

      final Runnable run = new Runnable(){
       public void run(){
           try {
                enableComponent(false);
                CalendarService myService = new CalendarService("GoogleCalService");
                myService.setUserCredentials(bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").getText(),
                                             bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").getText());

                URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/allcalendars/full");

                CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);

                for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                  CalendarEntry entry = resultFeed.getEntries().get(i);

                  String[] splt = entry.getId().split("/");

                  bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").addItem(entry.getTitle().getPlainText());

                  ArrayList lst = new ArrayList();
                  lst.add(entry.getTitle().getPlainText());
                  lst.add(splt[7]);
                  lst.add(entry.getColor().getValue());
                  calProperties.put(i,lst);

                }

             if(!"".equals(properties.getValue("GOOGLE_CAL_SEL_CAL"))){
               bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").setSelectedItem(properties.getValue("GOOGLE_CAL_SEL_CAL"));
             }

            } catch (MalformedURLException e) {
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (AuthenticationException e) {
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (IOException e) {
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (ServiceException e) {
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            }finally{
              enableComponent(true); 
           }
       }
      };

      Thread readCal = new Thread(run,"ReadCalendars");
      readCal.start();  

    }

    public void enableComponent(boolean enabled){
       bizMain.ebiModule.guiRenderer.getButton("startSync","googleCalSync").setEnabled(enabled); 
    }

    /**
        * Sync google cal with EBI Calendar
        */
    

    public void syncGoogleToNeutrino(){

        bizMain.ebiModule.guiRenderer.getProgressBar("syncProgress","googleCalSync").setIndeterminate(true);
        bizMain.ebiModule.guiRenderer.getProgressBar("syncProgress","googleCalSync").setStringPainted(true);
        bizMain.ebiModule.guiRenderer.getProgressBar("syncProgress","googleCalSync").setString(EBIPGFactory.getLANG("EBI_LANG_GOOGLE_CAL_SYNC_STARTED"));

        final Runnable run = new Runnable(){

          public void run(){
            URL feedUrl;
            try {

                String id = (String)calProperties.get(bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").getSelectedIndex()-1).get(1);
                String colStr = (String)calProperties.get(bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").getSelectedIndex()-1).get(2);
                
                String urlStr = "http://www.google.com/calendar/feeds/"+id+"/private/full";

                Color bColor = Color.decode(colStr);

                StringBuffer color = new StringBuffer();
                color.append(bColor.getRed());
                color.append(",");
                color.append(bColor.getGreen());
                color.append(",");
                color.append(bColor.getBlue());


                feedUrl = new URL(urlStr);

                //RETRIEVE DATE FROM
                Calendar dateFrom = new GregorianCalendar();
                dateFrom.setTime(bizMain.ebiModule.guiRenderer.getTimepicker("fromDateText","googleCalSync").getDate());
                dateFrom.set(Calendar.HOUR_OF_DAY,0);

                //RETRIEVE DATE TO
                Calendar dateTo = new GregorianCalendar();
                dateTo.setTime(bizMain.ebiModule.guiRenderer.getTimepicker("toDateText","googleCalSync").getDate());
                dateTo.set(Calendar.HOUR_OF_DAY,24);

                CalendarQuery myQuery = new CalendarQuery(feedUrl);
                myQuery.setMinimumStartTime(new DateTime(dateFrom.getTime()));
                myQuery.setMaximumStartTime(new DateTime(dateTo.getTime()));


                //GET EVENT FROM GOOGLE CAL
                CalendarService myService = new CalendarService(bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").getSelectedItem().toString());

                myService.setUserCredentials(bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").getText(),
                                             bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").getText());

                // Send the request and receive the response:

                CalendarEventFeed myFeed = myService.query(myQuery, CalendarEventFeed.class);
                             
                //System.out.println(myFeed.getTitle().getPlainText());
                bizMain.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();

                Iterator iter = myFeed.getEntries().iterator();

                HashMap<Integer,ArrayList> gEvent = new HashMap<Integer,ArrayList>();
                int i = 0;
                while(iter.hasNext()){
                   CalendarEventEntry entry = (CalendarEventEntry) iter.next();

                   Query query = bizMain.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Crmcalendar where name=? and startdate=? and enddate=? ");

                   query.setString(0,entry.getTitle().getPlainText());
                   query.setTimestamp(1,new Date((entry.getTimes().get(0)).getStartTime().getValue()));
                   query.setTimestamp(2,new Date((entry.getTimes().get(0)).getEndTime().getValue()));

                   //ADD EVENT TO A TEMPORARY HASHMAP will use to syncronize neutrino event with google events
                   ArrayList<Object> gevn = new ArrayList<Object>();
                   gevn.add(entry.getTitle().getPlainText());
                   gevn.add(new Date((entry.getTimes().get(0)).getStartTime().getValue()));
                   gevn.add(new Date((entry.getTimes().get(0)).getEndTime().getValue()));

                   gEvent.put(i++,gevn);
                   //END ADD

                   Iterator itx = query.iterate();
                   if (itx.hasNext() == false) {

                     Crmcalendar crmcal = new Crmcalendar();
                     crmcal.setCuser(EBIPGFactory.ebiUser);
                     crmcal.setName(entry.getTitle().getPlainText());
                     crmcal.setDescription(entry.getPlainTextContent());
                     crmcal.setColor(color.toString());
                     crmcal.setStartdate(new Date((entry.getTimes().get(0)).getStartTime().getValue()));
                     crmcal.setEnddate(new Date((entry.getTimes().get(0)).getEndTime().getValue()));
                     byte[] by = new byte[1];
                     by[0] = 'G';  
                     crmcal.setCicon(by);
                     bizMain.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").saveOrUpdate(crmcal);

                   }
                }
                bizMain.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();

                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bizMain.reload();
                    }
                });

                bizMain.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
                Query query = bizMain.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Crmcalendar where startdate BETWEEN ? AND ? ");

                query.setTimestamp(0,dateFrom.getTime());
                query.setTimestamp(1,dateTo.getTime());
                
                Iterator itg = query.iterate();
                boolean canInsert = true;
                while(itg.hasNext()){
                   Crmcalendar calv =(Crmcalendar) itg.next();

                   for(int j = 0; j< gEvent.size(); j++){
                        if(gEvent.get(j).get(0).toString().equals(calv.getName()) &&
                                ((Date)gEvent.get(j).get(1)).getTime() == calv.getStartdate().getTime() &&
                                                ((Date)gEvent.get(j).get(2)).getTime() == calv.getEnddate().getTime()){

                            canInsert = false;
                            break;
                            
                        }
                   }
                   // send this event to a google cal
                   if(canInsert){
                       CalendarEventEntry myEntry = new CalendarEventEntry();

                       myEntry.setTitle(new PlainTextConstruct(calv.getName()));
                       myEntry.setContent(new PlainTextConstruct(calv.getDescription()));

                       When eventTimes = new When();

                       eventTimes.setStartTime(new DateTime(calv.getStartdate(),TimeZone.getDefault()));
                       eventTimes.setEndTime(new DateTime(calv.getEnddate(),TimeZone.getDefault()));
                       myEntry.addTime(eventTimes);

                       // Send the request and receive the response:
                       myService.insert(feedUrl, myEntry);
                   }

                  canInsert=true;
                }
               bizMain.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (AuthenticationException e) {
                e.printStackTrace();
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (ServiceException e) {
                e.printStackTrace();
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            }finally{
              bizMain.ebiModule.guiRenderer.getProgressBar("syncProgress","googleCalSync").setIndeterminate(false);
              bizMain.ebiModule.guiRenderer.getProgressBar("syncProgress","googleCalSync").setString(EBIPGFactory.getLANG("EBI_LANG_GOOGLE_CAL_SYNC_FINISHED"));  
            }
          }
        };

       Thread startSync = new Thread(run,"StartSync");
       startSync.start();

    }


    public boolean validateInput(){

        if("".equals(bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").getText()) ||
                "".equals(bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").getText())){

                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_USER_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);

            return false;
        }


      return true;
    }

    public boolean validateInputSync(){
        if("".equals(bizMain.ebiModule.guiRenderer.getTextfield("userNameText","googleCalSync").getText()) ||
                "".equals(bizMain.ebiModule.guiRenderer.getTextfield("passwordText","googleCalSync").getText())){

                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_USER_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);

            return false;
        }

        if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(bizMain.ebiModule.guiRenderer.getComboBox("selectCalText","googleCalSync").getSelectedItem())){

             EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_SELECT_CALENDAR_ERROR")).Show(EBIMessage.ERROR_MESSAGE);

            return false;
        }

        return true;
    }

}
