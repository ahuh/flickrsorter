package org.ahuh.flickr.sorter.bean;

import java.util.Comparator;

import org.joda.time.DateTime;

/**
 * PhotoSet Bean
 * @author Julien
 *
 */
public class PhotoSetBean {
	
	private String id;
	private String title;
	private DateTime dateCreate;
	private DateTime dateUpdate;
	
	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param dateCreate
	 * @param dateUpdate
	 */
	public PhotoSetBean(String id, String title, DateTime dateCreate, DateTime dateUpdate) {
		super();
		this.id = id;
		this.title = title;
		this.dateCreate = dateCreate;
		this.dateUpdate = dateUpdate;
	}
	
	/**
	 * Comparator for title ascending
	 */
	public static Comparator<PhotoSetBean> PhotoSetTitleAscComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			String objectTitle1 = object1.getTitle().toUpperCase();
			String objectTitle2 = object2.getTitle().toUpperCase();
			
			//ascending order
			return objectTitle1.compareTo(objectTitle2);
		}	
	};
	
	/**
	 * Comparator for title descending
	 */
	public static Comparator<PhotoSetBean> PhotoSetTitleDescComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			String objectTitle1 = object1.getTitle().toUpperCase();
			String objectTitle2 = object2.getTitle().toUpperCase();
						
			//descending order
			return objectTitle2.compareTo(objectTitle1);
		}	
	};
	
	/**
	 * Comparator for date create ascending
	 */
	public static Comparator<PhotoSetBean> PhotoSetDateCreateAscComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			DateTime objectDT1 = object1.getDateCreate();
			DateTime objectDT2 = object2.getDateCreate();
			
			//ascending order
			return objectDT1.compareTo(objectDT2);
		}	
	};
	
	/**
	 * Comparator for date create descending
	 */
	public static Comparator<PhotoSetBean> PhotoSetDateCreateDescComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			DateTime objectDT1 = object1.getDateCreate();
			DateTime objectDT2 = object2.getDateCreate();
			
			//descending order
			return objectDT2.compareTo(objectDT1);
		}	
	};
	
	/**
	 * Comparator for date update ascending
	 */
	public static Comparator<PhotoSetBean> PhotoSetDateUpdateAscComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			DateTime objectDT1 = object1.getDateUpdate();
			DateTime objectDT2 = object2.getDateUpdate();
			
			//ascending order
			return objectDT1.compareTo(objectDT2);
		}	
	};
	
	/**
	 * Comparator for date update descending
	 */
	public static Comparator<PhotoSetBean> PhotoSetDateUpdateDescComparator = new Comparator<PhotoSetBean>() {

		public int compare(PhotoSetBean object1, PhotoSetBean object2) {
			DateTime objectDT1 = object1.getDateUpdate();
			DateTime objectDT2 = object2.getDateUpdate();
			
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
	 * @return the dateCreate
	 */
	public DateTime getDateCreate() {
		return dateCreate;
	}
	/**
	 * @param dateCreate the dateCreate to set
	 */
	public void setDateCreate(DateTime dateCreate) {
		this.dateCreate = dateCreate;
	}
	/**
	 * @return the dateUpdate
	 */
	public DateTime getDateUpdate() {
		return dateUpdate;
	}
	/**
	 * @param dateUpdate the dateUpdate to set
	 */
	public void setDateUpdate(DateTime dateUpdate) {
		this.dateUpdate = dateUpdate;
	}
	
	
}
