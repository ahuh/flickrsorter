package org.ahuh.flickr.sorter.tasks;

import java.util.concurrent.Callable;

import org.ahuh.flickr.sorter.bean.CollectionBean;
import org.ahuh.flickr.sorter.constants.FlickrSorterConstants;
import org.ahuh.flickr.sorter.exception.AppException;
import org.ahuh.flickr.sorter.helper.PropertiesHelper;
import org.ahuh.flickr.sorter.service.AuthenticationService;
import org.ahuh.flickr.sorter.service.CollectionService;
import org.apache.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

/**
 * Reorder Collection content task for multithreading purpose
 * @author Julien
 *
 */
public class ReorderCollectionTask implements Callable<Integer> {
	
	/**
	 * Logger
	 */
	private static Logger log = Logger.getLogger(ReorderCollectionTask.class);
	
	private CollectionBean colBean;
	
	/**
	 * Constructor
	 * @param colBean
	 */
	public ReorderCollectionTask(CollectionBean colBean) {
		this.colBean = colBean;
	}
	
	/**
	 * Call method
	 */
	@Override
	public Integer call() throws AppException {
		try {
			log.debug("[" + Thread.currentThread().getName() + "] Reorder Collection Task ...");
			
			// Initialize flickr API calls
			Flickr flickr = new Flickr(FlickrSorterConstants.API_KEY, FlickrSorterConstants.SHARED_SECRET, new REST());
			
			// Set authentication token in request context of the new thread 
			AuthenticationService.setAuthTokenInContext(PropertiesHelper.getAuthToken(), PropertiesHelper.getAuthTokenSecret());
			
			// Call Collection service
			CollectionService colService = new CollectionService(flickr);
			int countReorderedC = colService.sortAndReorderPhotoSets(colBean);
			return new Integer(countReorderedC);
		}
		catch (Exception e) {
			String message = "Error while reordering photo sets in collection " + colBean.getId() + " (" + colBean.getTitle() + ")";
			log.error(message, e);
			throw new AppException(message, e);
		}		
	}
}
