package songlib.view;

// Savan Patel
// Naveenan Yogeswaran

import java.util.ArrayList;
import java.util.Optional;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import songlib.app.*;

public class SongListController {
	
	@FXML ListView<Song> listView;
	@FXML TextArea detailDisplay;
	
	@FXML TextField addSongName;
	@FXML TextField addArtistName;
	@FXML TextField addAlbumName;
	@FXML TextField addYear;
	@FXML TextField editSongName;
	@FXML TextField editArtistName;
	@FXML TextField editAlbumName;
	@FXML TextField editYear;
	@FXML Button addButton;
	@FXML Button deleteButton;
	@FXML Button editButton;

	private static Stage primaryStage;
	
	private ObservableList<Song> obsList;
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	public static void setPrimaryStage(Stage mainStage) {
		primaryStage = mainStage;
	}
	
	public ObservableList<Song> getObservableList(){
		return obsList;
	}
	

	public void start(Stage mainStage) {  
		setPrimaryStage(mainStage);
		
		// create an ObservableList 
		// from an ArrayList  
		ArrayList<Song> songs = load();
		
		obsList = FXCollections.observableArrayList(songs);
		FXCollections.sort(obsList, new SongComparator());
		
		listView.setItems(obsList); 

		// select the first item
		listView.getSelectionModel().select(0);

		// set default details for text area and edit text fields
		if(obsList.size() > 0) {
			showItem(mainStage);
		}
		
		// set listener for the items
		listView
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) -> 
				showItem(mainStage));
	}
	
	
	/**
	 * Loads all saved songs from the save.txt file.
	 * @return
	 */
	public ArrayList<Song> load(){
		ArrayList<Song> songList = new ArrayList<Song>();
		File file = new File("./save.txt");
		if(!file.exists()) {
			return songList;
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line  = br.readLine()) != null) {
				String songName = br.readLine();
				String artistName = br.readLine();
				String albumName = br.readLine();
				String year = br.readLine();
				
				if(albumName.length() != 0 && year.length() != 0) {
					songList.add(new Song(songName, artistName, albumName, Integer.parseInt(year)));
				}
				else if(albumName.length() != 0) {
					songList.add(new Song(songName, artistName, albumName));
				}
				else if(year.length() != 0) {
					songList.add(new Song(songName, artistName, Integer.parseInt(year)));
				}
				else {
					songList.add(new Song(songName, artistName));
				}
				
			}
	        br.close();
	    }
		catch(FileNotFoundException e){
			System.out.println("Error");
	    }
	    catch(IOException e){
	    	System.out.println("error");
	    }
		
		return songList;
	}
	
	
	/**
	 * Show details, for the selected song, in detailDisplay
	 * @param primaryStage
	 */
	public void showItem(Stage primaryStage) {
		if(obsList.size() == 0) {
			// set text for edit text fields
			editSongName.setText("");
			editArtistName.setText("");
			editAlbumName.setText("");
			editYear.setText("");
			return;
		}
		// get the Song object of the selected Song
		
		// replace text area in "detailDisplay" to show the selected Song's details
		Song item = listView.getSelectionModel().getSelectedItem();
		
		String display = "Name:\n" + item.getName() + "\n\n" + "Artist\n" + item.getArtist() + "\n\n";
		if(item.getAlbum().equals("")) {
			display += "Album:\nNo Specified Album\n\n";
		}
		else {
			display += "Album:\n" + item.getAlbum() + "\n\n";
		}
		if(item.getYear() == -1) {
			display += "Year:\nNo Specified Year";
		}
		else {
			display += "Year:\n" + Integer.toString(item.getYear());
		}

		detailDisplay.setText(display);
		
		// set text for edit text fields
		editSongName.setText(item.getName());
		editArtistName.setText(item.getArtist());
		editAlbumName.setText(item.getAlbum());
		if(item.getYear() == -1) {
			editYear.setText("");
		}
		else {
			editYear.setText(Integer.toString(item.getYear()));
		}
	}
	
	
	/**
	 * Delete the selected song
	 * @param e
	 */
	public void delete(ActionEvent e) {
		// get Song object and index of selected Song
		Song item = listView.getSelectionModel().getSelectedItem();
		if(item == null) {
			errorNotice("No Song Error", "There is no song selected, so the action cannot be performed.");
			return;
		}
		int index = listView.getSelectionModel().getSelectedIndex();
		
		// ask user to confirm if they want to delete the selected song
		if(!deleteConfirmation(SongListController.getPrimaryStage(), item.getName(), item.getArtist())) {
			return;
		}
		
		// remove selected song
		obsList.remove(index);
		
		// select the next item
		if(obsList.size() == 0) {
			detailDisplay.setText("");
		}
		else {
			listView.getSelectionModel().select(index);
			showItem(SongListController.getPrimaryStage());
		}
		
	}
	
	
	/**
	 * Display a confirmation dialog to confirm whether the user wants to delete the song
	 * @param primaryStage
	 * @param songName : Name of the selected song to be deleted
	 * @param artistName : Name of the artist of the song to be deleted
	 * @return True if Confirm, False if Cancel
	 */
	public boolean deleteConfirmation(Stage primaryStage, String songName, String artistName) {
		// get confirmation that user wants to delete the new song
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Delete Song");
		alert.setContentText("Are you sure you want to delete:\n"
				+ "Song Name: " + songName + "\n"
				+ "Artist Name: " + artistName);

		// wait for user to either cancel or confirm
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Add a new song given the inputs
	 * @param e
	 */
	public void add(ActionEvent e) {		
		String songName = addSongName.getText().trim();
		String artistName = addArtistName.getText().trim();
		String albumName = addAlbumName.getText().trim();
		String yearString = addYear.getText().trim(); 
		
		// check year to make sure it can be turned into integer
		if(yearString.length() > 0 && !isInteger(yearString)) {
			errorNotice("Invalid Year Error", "Please enter an integer or nothing into the year textfield.");
			return;
		}
		// check is year was entered (in order to avoid parsing error)
		int year = 0;
		if(yearString.length() != 0) {
			year = Integer.parseInt(yearString);
		}
		
		// check to make sure a song name and artist name was given
		if(songName.length() == 0 || artistName.length() == 0) {
			errorNotice("Missing Requirement Error", "Please enter a Song Name and an Artist Name to add a new Song.");
			return;
		}
		
		// check to make sure song and artist doesn't already exist
		for(Song song : obsList) {
			if(songName.toLowerCase().equals(song.getName().toLowerCase()) && artistName.toLowerCase().equals(song.getArtist().toLowerCase())){
				errorNotice("Song Exist Error", "Song with same name and artist already exists.");
				return;
			}
		}
		
		// confirm with user if they want to add this song
		if (!addConfirmation(SongListController.getPrimaryStage(), songName, artistName, albumName, yearString)) {
			return;
		}
		
		// use appropriate constructor depending on whether user used optional inputs
		Song newSong;
		if(yearString.length() > 0 && albumName.length() > 0) {
			newSong = new Song(songName, artistName, albumName, year);
		}
		else if(yearString.length() > 0){
			newSong = new Song(songName, artistName, year);
		}
		else if(albumName.length() > 0){
			newSong = new Song(songName, artistName, albumName);
		}
		else {
			newSong = new Song(songName, artistName);
		}
		
		// look for sort position
		SongComparator comp = new SongComparator();
		int index = 0;
		for(int i = 0; i < obsList.size(); i++) {
			if(comp.compare(newSong, obsList.get(i)) <= 0) {
				i = obsList.size();
			}
			else {
				index += 1;
			}
		}
		obsList.add(index, newSong);
		
		// select the new item
		listView.getSelectionModel().select(index);
		
	}
	
	
	/**
	 * Display a confirmation dialog to confirm whether the user wants to add the song
	 * @param primaryStage
	 * @param songName : Name of the selected song to be deleted
	 * @param artistName : Name of the artist of the song to be deleted
	 * @param albumName : Name of the album
	 * @param year : The song release year
	 * @return True if Confirm, False if Cancel
	 */
	public boolean addConfirmation(Stage primaryStage, String songName, String artistName, String albumName, String year) {
		// get confirmation that user wants to add the new song
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Add Song");
		alert.setContentText("Are you sure you want to add:\n"
				+ "Song Name: " + songName + "\n"
				+ "Artist Name: " + artistName + "\n"
				+ "Album Name: " + albumName + "\n"
				+ "Year: " + year + "\n");

		// wait for user to either cancel or confirms
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Edit a selected song!
	 * @param e
	 */
	public void edit(ActionEvent e) {
		// first we need to get the specific song we are trying to edit
		Song tune = listView.getSelectionModel().getSelectedItem(); // the song we are working with
		if(tune == null) {
			errorNotice("No Song Error", "There is no song selected, so the action cannot be performed.");
			return;
		}
		int index = listView.getSelectionModel().getSelectedIndex();
		String songName = editSongName.getText().trim(); // getting edited Song name
		String artistName = editArtistName.getText().trim(); // getting edited song artist
		String albumName = editAlbumName.getText().trim(); // getting edited song album
		String yearString = editYear.getText().trim();  // getting edited song year
		// check year to make sure it can be turned into integer
		if(yearString.length() > 0 && !isInteger(yearString)) {
			errorNotice("Invalid Year Error", "Please enter an integer or nothing into the year textfield.");
			return;
		}
		// check is year was entered (in order to avoid parsing error)
		int year = 0;
		if(yearString.length() != 0) {
			year = Integer.parseInt(yearString);
		}
		if(songName.length() == 0 || artistName.length() == 0) {
			errorNotice("Missing Requirement Error", "Please enter a Song Name and an Artist Name to edit a new Song.");
			return;
		}
		// check to make sure the newly edited song and artist doesn't already exist
		for(int i = 0; i < obsList.size(); i++) {
			Song song = obsList.get(i);
			if(i != index && songName.toLowerCase().equals(song.getName().toLowerCase()) && artistName.toLowerCase().equals(song.getArtist().toLowerCase())){
				errorNotice("Song Exist Error", "Song with same name and artist already exists.");
				return;
			}
		}
		
		// confirm with user if they want to add this song
		if (!editConfirmation(SongListController.getPrimaryStage(), songName, artistName, albumName, yearString)) {
			return;
		}
		
		tune.setName(songName); // setting the song name as edited name
		tune.setArtist(artistName); // setting song artist as edited artist
		tune.setAlbum(albumName); // setting the album name as edited album
		if(yearString.length() > 0) {
			tune.setYear(year); // setting the year as the edited year
		}
		else {
			tune.setYear(-1);
		}
		obsList.remove(index); // removing the object from the list but we will re-add it
		// look for proper sort position
		SongComparator comp = new SongComparator();
		index = 0;
		for(int i = 0; i < obsList.size(); i++) {
			if(comp.compare(tune, obsList.get(i)) <= 0) {
				i = obsList.size();
			}
			else {
				index += 1;
			}
		}
		obsList.add(index, tune);
				
		// select the new item
		listView.getSelectionModel().select(index);
			
	}
	
	/**
	 * Display a confirmation dialog to confirm whether the user wants to edit the song
	 * @param primaryStage
	 * @param songName : Name of the selected song to be deleted
	 * @param artistName : Name of the artist of the song to be deleted
	 * @param albumName : Name of the album
	 * @param year : The song release year
	 * @return True if Confirm, False if Cancel
	 */
	public boolean editConfirmation(Stage primaryStage, String songName, String artistName, String albumName, String year) {
		// get confirmation that user wants to add the new song
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Add Song");
		alert.setContentText("Are you sure you want to edit to:\n"
				+ "Song Name: " + songName + "\n"
				+ "Artist Name: " + artistName + "\n"
				+ "Album Name: " + albumName + "\n"
				+ "Year: " + year + "\n");

		// wait for user to either cancel or confirms
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Checks to make sure the year is an integer
	 * @param year : The song release year
	 * @return True if year is integer, False if year isn't integer
	 */
	public boolean isInteger(String year) {
		// checks to make sure year is a valid integer
		for(char digit : year.toCharArray()) {
			if(!Character.isDigit(digit)){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Template for error dialogs
	 * @param errorTitle : Error Title
	 * @param errorDesc : Description of the error
	 */
	public void errorNotice(String errorTitle, String errorDesc) {
		// display custom error message
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(errorTitle);
		alert.setContentText(errorDesc);

		alert.showAndWait();
	}

}
