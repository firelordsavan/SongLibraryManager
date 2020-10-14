package songlib.app;

// Savan Patel
// Naveenan Yogeswaran

import javafx.application.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.collections.ObservableList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import songlib.view.SongListController;

public class SongLib extends Application {
	
	SongListController listController;

	@Override
	public void start(Stage primaryStage) throws Exception{
		// TODO Auto-generated method stub
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation( getClass().getResource("/songlib/view/SongSelector.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		listController = loader.getController();
		listController.start(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	@Override
	public void stop(){
	    ObservableList<Song> obsList = listController.getObservableList();
	    try {
	    	File file = new File("./save.txt");
	    	FileWriter writer = new FileWriter(file);
	    	
		    for(int i = 0; i < obsList.size(); i++) {
		    	writer.write("Song " + i + "\n");
		    	writer.write(obsList.get(i).getName() + "\n");
		    	writer.write(obsList.get(i).getArtist() + "\n");
		    	writer.write(obsList.get(i).getAlbum() + "\n");
		    	if(obsList.get(i).getYear() != -1) {
		    		writer.write(obsList.get(i).getYear() + "\n");
		    	}
		    	else {
		    		writer.write("\n");
		    	}
		    }
		    
		    writer.close();
	    }
	    catch(IOException e) {
	    	System.out.println("Save error");
	    }
	}

}
