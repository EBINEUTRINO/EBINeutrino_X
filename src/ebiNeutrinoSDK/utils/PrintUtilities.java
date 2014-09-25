package ebiNeutrinoSDK.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;


public class PrintUtilities implements Printable {

    private Component componentToBePrinted;

    public static void printComponent(Component c) {
        new PrintUtilities(c).print();
    }

    public PrintUtilities(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }

    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog())
            try {
                printJob.print();
            } catch(PrinterException pe) {
                 pe.printStackTrace();
            }
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) {

        pf.setOrientation(PageFormat.LANDSCAPE);

        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;

        // for faster printing, turn off double buffering
        disableDoubleBuffering(componentToBePrinted);

        Dimension d = componentToBePrinted.getSize(); //get size of document
        double panelWidth = d.width; //width in pixels
        double panelHeight = d.height; //height in pixels
        double pageHeight = pf.getImageableHeight(); //height of printer page
        double pageWidth = pf.getImageableWidth(); //width of printer page

        double scaleX = pageWidth / panelWidth;
        double scaleY = pageHeight / panelHeight;

        int totalNumPages = (int) Math.ceil(scaleY * panelHeight / pageHeight);

        // make sure not print empty pages
        if (pageIndex >= totalNumPages) {
            response = NO_SUCH_PAGE;
        } else {
            // shift Graphic to line up with beginning of print-imageable region
            g2.translate(pf.getImageableX(), pf.getImageableY());
            // shift Graphic to line up with beginning of next page to print
            g2.translate(0f, -pageIndex * pageHeight);
            // scale the page so the width fits...
            g2.scale(scaleX, scaleY);
            componentToBePrinted.paint(g2); //repaint the page for printing
            response = Printable.PAGE_EXISTS;
        }
        enableDoubleBuffering(componentToBePrinted);
        return response;
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }


}