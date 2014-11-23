package org.ahuh.flickr.sorter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.ahuh.flickr.sorter.bean.CollectionBean;
import org.ahuh.flickr.sorter.bean.PhotoSetBean;
import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.gui.GUIApplication;
import org.ahuh.flickr.sorter.service.AuthenticationService;
import org.ahuh.flickr.sorter.service.CollectionService;
import org.ahuh.flickr.sorter.service.PhotoSetService;
import org.ahuh.flickr.sorter.tasks.ReorderCollectionTask;
import org.ahuh.flickr.sorter.tasks.ReorderPhotoSetTask;
import org.apache.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

public class FlickrSorterLauncher extends GUIApplication {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(FlickrSorterLauncher.class);

	/**
     * Static properties (shared)
     */	
    private static boolean processIsRunning = false; 
	private static boolean errorsInTasks = false;
	
	/**
	 * Main execution method
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			// Prevent several instances of same program
	     	checkLock();
	     	
			// Schedule a job for the event-dispatching thread:
	        // adding TrayIcon
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	                plugGUIToProcess();
	            }
	        });			
        }
		catch (Exception e) {
			String errorMessage = "An error occurred:\n\n" + e.getMessage();
			log.error(errorMessage, e);
			displayErrorPopup(errorMessage);
        }
		finally {
			terminateGUI();
		}
	}
	
	/**
	 * Plug GUI to process
	 */
	protected static void plugGUIToProcess() {
		// Launch process on Start button click
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				process();
			}
		});
	}
	
	/**
	 * Process
	 * @param args
	 */
	protected static void process() {
		
		// Declare new thread
		Thread thread = new Thread(new Runnable()
		{
			// Run in new thread
			public void run() {
				if (processIsRunning) {
					// Process is already running
					log.debug("Trying to launch new process : blocked because a process is already running");
				}
				else {
					// Process is not running : launch it and lock the running flag
					processIsRunning = true;
					
					startButton.setEnabled(false);
	
					AuthenticationService authService = null;
					PhotoSetService psService = null;
					CollectionService colService = null;
					
					try {			
						// Init Flickr API calls
						Flickr flickr = new Flickr(FlickrSorterConstants.API_KEY, FlickrSorterConstants.SHARED_SECRET, new REST());
						
						// Authentication
						authenticate(flickr, authService);
						
						// Reorder PhotoSets
						List<PhotoSetBean> psBeanList = new ArrayList<PhotoSetBean>();
						int countReorderPS = reorderPhotoSets(flickr, psService, psBeanList);
						
						// Reorder Collections (multithreading)
						int countReorderC = reorderCollections(flickr, colService);
									
						// Reorder Photos (multithreading)
						int countReorderP = reorderPhotos(flickr, psBeanList);
						
						if (errorsInTasks) {
							String errorMessage = "Errors occurred during the process: check logs to see the details";
							displayErrorPopup(errorMessage);
						}
						
						displayMessagePopup("Number of photo sets reordered: " + countReorderPS + "\n" +
								"Number of photo sets reordered in collections: " + countReorderC + "\n" +
								"Number of photos reordered in photo sets: " + countReorderP);
			        }
					catch (AppException e) {
						displayErrorPopup(e.getMessage());
					}
					catch (Exception e) {
						String errorMessage = "An error occurred during the process:\n\n" + e.getMessage();
						log.error(errorMessage, e);
						displayErrorPopup(errorMessage);
			        }
					finally {
						if (authService != null) {
							authService.destroy();
						}
						if (psService != null) {
							psService.destroy();
						}
						if (colService != null) {
							colService.destroy();
						}
						errorsInTasks = false;
						processIsRunning = false;
						startButton.setEnabled(true);
					}
				}
			}
		});
		
		// Execute new thread
		thread.start();
	}
	
	/**
	 * Authenticate
	 * @param flickr
	 * @param authService
	 * @throws AppException
	 */
	private static void authenticate(Flickr flickr, AuthenticationService authService) throws AppException {
		authService = new AuthenticationService(flickr);
		if (!authService.checkAuth()) {
			// Handle authentication to Flickr API
			// - Phase 1
			displayMessagePopup("FlickrSorter requires write permissions to your Flickr account.\n"
							+ "You will be redirected to the authorization request page.");
			
			URL requestTokenURL = authService.authenticatePhase1();
			openWebpage(requestTokenURL);
			
			// - Phase 2
			String verifyCode = displayInputPopup("Please accept to authorize the application,\n"
												+ "copy the verification code provided by the\n"
												+ "Flickr web site, and paste it here:");
			
			// - Phase 3
			authService.authenticatePhase3(verifyCode);
			displayMessagePopup("You are authorized to use FlickSorter with your Flickr account.\n"
							+ "The authorization access has been save in the configuration file.");
		}
	}	

	/**
	 * Reorder PhotoSets
	 * @param flickr
	 * @param psService
	 * @param psBeanList
	 * @return
	 * @throws AppException
	 */
	private static int reorderPhotoSets(Flickr flickr, PhotoSetService psService, 
			List<PhotoSetBean> psBeanList) throws AppException {
		
		int countReorderPS = 0;
		psService = new PhotoSetService(flickr);
		psBeanList.addAll(psService.listPhotoSets());
		countReorderPS = psService.sortAndReorderPhotoSets(psBeanList);
		
		// Progress bar
		progressBar.setValue(33);
		
		return countReorderPS;
	}
	
	/**
	 * Reorder Collections (multithreading)
	 * @param flickr
	 * @param colService
	 * @return
	 * @throws AppException
	 */
	private static int reorderCollections(Flickr flickr, CollectionService colService) throws AppException {
		
		int countReorderC = 0;
		colService = new CollectionService(flickr);
		List<CollectionBean> colBeanList = colService.listCollections();		
		int n = colBeanList.size();
		
		// Mono-threaded code
		/*for (CollectionBean colBean : colBeanList) {
			countReorderC += colService.sortAndReorderPhotoSets(colBean);
		}*/
		
		// Multi-threaded code
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		CompletionService<Integer> pool = new ExecutorCompletionService<Integer>(threadPool);
		
		
		for (CollectionBean colBean : colBeanList) {
		   pool.submit(new ReorderCollectionTask(colBean));
		}

		int i = 0;
		for (CollectionBean colBean : colBeanList) {
			try {
				Integer result = pool.take().get();
				if (result != null) {
					countReorderC += result.intValue();
				}

				// Progress bar
				i++;
				progressBar.setValue(33 + ((33 * i) / n));
			}
			catch (Exception e) {
				if (e.getCause() == null || !(e.getCause() instanceof AppException)) {
					String errorMessage = "An error occurred during collection reordering:\n\n" + e.getMessage();
					log.error(errorMessage, e);
				}
				errorsInTasks = true;
			}
		}
		
		threadPool.shutdown();
		
		return countReorderC;
	}

	/**
	 * Reorder Photos (multithreading)
	 * @param flickr
	 * @param psBeanList
	 * @return
	 * @throws AppException
	 */
	private static int reorderPhotos(Flickr flickr, List<PhotoSetBean> psBeanList) throws AppException {

		int countReorderP = 0;
		int n = psBeanList.size();
		
		// Mono-threaded code
		/*for (PhotoSetBean psBean : psBeanList) {
			countReorderP += psService.sortAndReorderPhotos(psBean);
		}*/
		
		// Multi-threaded code
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		CompletionService<Integer> pool = new ExecutorCompletionService<Integer>(threadPool);
		
		
		for (PhotoSetBean psBean : psBeanList) {
		   pool.submit(new ReorderPhotoSetTask(psBean));
		}
		
		int i = 0;
		for (PhotoSetBean psBean : psBeanList) {
			try {
				Integer result = pool.take().get();
				if (result != null) {
					countReorderP += result.intValue();
				}

				// Progress bar
				i++;
				progressBar.setValue(66 + ((33 * i) / n));
			}
			catch (Exception e) {
				if (e.getCause() == null || !(e.getCause() instanceof AppException)) {
					String errorMessage = "An error occurred during photo reordering:\n\n" + e.getMessage();
					log.error(errorMessage, e);
				}
				errorsInTasks = true;
			}
		}
		
		threadPool.shutdown();
		
		return countReorderP;
	}
	
	/**
	 * Prevent several instances of same program
	 */
	private static void checkLock() {
		try
        {
            @SuppressWarnings("unused")
			ProgramLock lock = new ProgramLock();
        }
        catch(RuntimeException e)
        {
            // Exit main app
        	log.debug("Program is already running : abort new program execution");
            return;
        }
        catch (Exception e)
        {
        	log.error("Error with the lock process", e);
        }
	}

}
