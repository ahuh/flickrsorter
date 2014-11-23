package org.ahuh.flickr.sorter.tasks;

import java.util.concurrent.Callable;

import org.ahuh.flickr.sorter.bean.PhotoSetBean;
import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.helper.PropertiesHelper;
import org.ahuh.flickr.sorter.service.AuthenticationService;
import org.ahuh.flickr.sorter.service.PhotoSetService;
import org.apache.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

/**
 * Reorder PhotoSet content task for multithreading purpose
 * @author Julien
 *
 */
public class ReorderPhotoSetTask implements Callable<Integer> {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(ReorderPhotoSetTask.class);
	
	private PhotoSetBean psBean;
	
	/**
	 * Constructor
	 * @param psBean
	 */
	public ReorderPhotoSetTask(PhotoSetBean psBean) {
		this.psBean = psBean;
	}
	
	/**
	 * Call method
	 */
	@Override
	public Integer call() throws AppException {
		try {
			log.debug("[" + Thread.currentThread().getName() + "] Reorder PhotoSet Task ...");
			
			// Initialize flickr API calls
			Flickr flickr = new Flickr(FlickrSorterConstants.API_KEY, FlickrSorterConstants.SHARED_SECRET, new REST());
			
			// Set authentication token in request context of the new thread 
			AuthenticationService.setAuthTokenInContext(PropertiesHelper.getAuthToken(), PropertiesHelper.getAuthTokenSecret());
			
			// Call PhotoSet service
			PhotoSetService psService = new PhotoSetService(flickr);
			int countReorderedP = psService.sortAndReorderPhotos(psBean);
			return new Integer(countReorderedP);
		}
		catch (Exception e) {
			String message = "Error while reordering photos in photo set " + psBean.getId() + " (" + psBean.getTitle() + ")";
			log.error(message, e);
			throw new AppException(message, e);
		}		
	}
}
