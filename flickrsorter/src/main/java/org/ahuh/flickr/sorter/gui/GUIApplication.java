package org.ahuh.flickr.sorter.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.helper.PropertiesHelper;
import org.apache.log4j.Logger;

/**
 * GUI Application
 * @author Julien
 *
 */
public abstract class GUIApplication {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(GUIApplication.class);
		
	protected static JFrame frame;
	protected static JProgressBar progressBar;
	protected static JPanel panel;
	protected static JButton startButton;
	
	/**
	 * Create and show GUI
	 */
	protected static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e) {
			// Ignore
			log.error(e);
		}
		
        // Create and set up the window.
		String version = "";
		try {
			version += " " + PropertiesHelper.getVersion();
		}
		catch (Exception e) {
			// Ignore
			log.error(e);
		}
        frame = new JFrame(FlickrSorterConstants.APP_TITLE + version);
        frame.setMinimumSize(new Dimension(100, 100));
        frame.setSize(400, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // Panel
        panel = new JPanel();
        frame.setContentPane(panel);
		
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setLayout(new BorderLayout());
		progressBar = new JProgressBar(0,99);
		progressBar.setString("Progress");
		progressBar.setStringPainted(true);
	    //progressBar.setBounds(320,240,250,100);
	    progressBar.setVisible(true);
	    panel.add(progressBar);
	    progressBar.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent evt) {
	          JProgressBar comp = (JProgressBar) evt.getSource();
	          comp.setString ("Progress: " + (int)(comp.getPercentComplete()*100) + "%");
			}		      
		});
	    
	    // Start Button
	    startButton = new JButton("Start");
	    startButton.setVisible(true);
	    panel.add(startButton);		
    }
	
	/**
	 * Terminate GUI
	 */
	protected static void terminateGUI() {
		if (frame != null) {
			frame.dispose();
		}
	}
	
	/**
	 * Display message pop-up
	 * @param message
	 * @throws AppException 
	 */
	protected static void displayMessagePopup(String message) throws AppException {
		try {
			JOptionPane.showMessageDialog(frame, message, FlickrSorterConstants.APP_TITLE + " " + PropertiesHelper.getVersion(),
			JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			String errorMessage = "Error while opening pop-up";
			log.error(errorMessage, e);
			throw new AppException(errorMessage, e);
		}
	}
	
	/**
	 * Display message pop-up
	 * @param message
	 * @throws AppException 
	 */
	protected static void displayWarningPopup(String message) throws AppException {
		try {
			JOptionPane.showMessageDialog(frame, message, FlickrSorterConstants.APP_TITLE + " " + PropertiesHelper.getVersion(),
					JOptionPane.WARNING_MESSAGE);
		}
		catch (Exception e) {
			String errorMessage = "Error while opening pop-up";
			log.error(errorMessage, e);
			throw new AppException(errorMessage, e);
		}
	}
	
	/**
	 * Display message pop-up
	 * @param message
	 * @throws AppException 
	 */
	protected static void displayErrorPopup(String message) {
		try {
			JOptionPane.showMessageDialog(frame, message, FlickrSorterConstants.APP_TITLE + " " + PropertiesHelper.getVersion(),
					JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e) {
			// Ignore
			log.error(e);
		}
	}
	
	/**
	 * Display input pop-up
	 * @param message
	 * @return
	 * @throws AppException 
	 */
	protected static String displayInputPopup(String message) throws AppException {
		try {
			return JOptionPane.showInputDialog(frame, message, FlickrSorterConstants.APP_TITLE + " " + PropertiesHelper.getVersion(),
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			String errorMessage = "Error while opening pop-up";
			log.error(errorMessage, e);
			throw new AppException(errorMessage, e);
		}
	}
	
	/**
	 * Open page in browser
	 * @param url
	 * @throws AppException
	 */
	protected static void openWebpage(URL url) throws AppException {
		try {
		    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        desktop.browse(url.toURI());
		    }
		}
		catch (Exception e) {
			String message = "Error while opening browser to display page";
			log.error(message, e);
			throw new AppException(message, e);
		}
	}
}
