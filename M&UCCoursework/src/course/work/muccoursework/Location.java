package course.work.muccoursework;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Location {
	
	//a class to represent a location.

	private int ID;
	private String name;
	private String description;
	private String image;
	private Boolean unlocked;
	private Float latitude;
	private Float longitude;
	private LatLng coordinates;
	private MarkerOptions marker;
	
	public Location() {
	}

	public Location(int ID, String name, String description, Boolean unlocked) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.unlocked = unlocked;		
	}
	
	public int getID(){
		return this.ID;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public Boolean isUnlocked(){
		return this.unlocked;
	}
	
	public void setID(int ID){
		this.ID = ID;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setLock(Boolean isUnLocked){
		this.unlocked = isUnLocked;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(float f) {
		this.latitude = f;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(float f) {
		this.longitude = f;
	}

	public LatLng getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(LatLng coordinates) {
		this.coordinates = coordinates;
	}

	public MarkerOptions getMarker() {
		return marker;
	}

	public void setMarker(MarkerOptions marker) {
		this.marker = marker;
	}
}
