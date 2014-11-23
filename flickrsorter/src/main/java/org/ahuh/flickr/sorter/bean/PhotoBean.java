package org.ahuh.flickr.sorter.bean;

import java.util.Comparator;
import java.util.Date;

/**
 * Photo Bean
 * @author Julien
 *
 */
public class PhotoBean {
	
	private String id;
	private String title;
	private Date dateTaken;
	private Date dateUpload;
	
	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param dateTaken
	 * @param dateUpload
	 */
	public PhotoBean(String id, String title, Date dateTaken, Date dateUpload) {
		super();
		this.id = id;
		this.title = title;
		this.dateTaken = dateTaken;
		this.dateUpload = dateUpload;
	}
	
	/**
	 * Comparator for title ascending
	 */
	public static Comparator<PhotoBean> PhotoTitleAscComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			String objectTitle1 = object1.getTitle().toUpperCase();
			String objectTitle2 = object2.getTitle().toUpperCase();
			
			//ascending order
			return objectTitle1.compareTo(objectTitle2);
		}	
	};
	
	/**
	 * Comparator for title descending
	 */
	public static Comparator<PhotoBean> PhotoTitleDescComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			String objectTitle1 = object1.getTitle().toUpperCase();
			String objectTitle2 = object2.getTitle().toUpperCase();
						
			//descending order
			return objectTitle2.compareTo(objectTitle1);
		}	
	};
	
	/**
	 * Comparator for date taken ascending
	 */
	public static Comparator<PhotoBean> PhotoDateTakenAscComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			Date objectDT1 = object1.getDateTaken();
			Date objectDT2 = object2.getDateTaken();
			
			//ascending order
			return objectDT1.compareTo(objectDT2);
		}	
	};
	
	/**
	 * Comparator for date taken descending
	 */
	public static Comparator<PhotoBean> PhotoDateTakenDescComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			Date objectDT1 = object1.getDateTaken();
			Date objectDT2 = object2.getDateTaken();
			
			//descending order
			return objectDT2.compareTo(objectDT1);
		}	
	};
	
	/**
	 * Comparator for date upload ascending
	 */
	public static Comparator<PhotoBean> PhotoDateUploadAscComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			Date objectDT1 = object1.getDateUpload();
			Date objectDT2 = object2.getDateUpload();
			
			//ascending order
			return objectDT1.compareTo(objectDT2);
		}	
	};
	
	/**
	 * Comparator for date upload descending
	 */
	public static Comparator<PhotoBean> PhotoDateUploadDescComparator = new Comparator<PhotoBean>() {

		public int compare(PhotoBean object1, PhotoBean object2) {
			Date objectDT1 = object1.getDateUpload();
			Date objectDT2 = object2.getDateUpload();
			
			//descending order
			return objectDT2.compareTo(objectDT1);
		}	
	};
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the dateTaken
	 */
	public Date getDateTaken() {
		return dateTaken;
	}
	/**
	 * @param dateTaken the dateTaken to set
	 */
	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}
	/**
	 * @return the dateUpload
	 */
	public Date getDateUpload() {
		return dateUpload;
	}
	/**
	 * @param dateUpload the dateUpload to set
	 */
	public void setDateUpload(Date dateUpload) {
		this.dateUpload = dateUpload;
	}
		
}
