package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class AbstractController {
	@FXML Pane _rootPane;
	
	/**
	 * This method switches scenes, given an fxml file name
	 * @param fxml the name of the fxml file
	 * @throws IOException
	 */
	public void switchScenes(String fxml, Pane Pane) throws IOException {
		//use fxmlloader to change the fxml file
		Parent pane = FXMLLoader.load(getClass().getResource(fxml));
		Stage stage = (Stage) Pane.getScene().getWindow();
		Scene scene = stage.getScene();
		
		//change and show the scene
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.sizeToScene();
	}
	
	/**
	 * Background worker to create the ffmpeg files and stop any freezing of GUi
	 * 
	 */
	public class Background extends Task<Void>{
		private String _cmd;
		@Override
		protected Void call() throws Exception {
			bash(_cmd);
			return null;
		}
		
		@Override
		protected void done() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					enableButtons();
				}
				
			});
		}
		
		public void setcmd(String cmd) {
			_cmd = cmd;
		}
		
		/**
		 * Process builder method to call a bash function
		 * @param cmd the command that needs to be input
		 */
		public void bash(String cmd) {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				Process process = builder.start();
				
				//Wait for a process to finish before exiting
				int exitStatus = process.waitFor();
				if(exitStatus!=0) {
					return;
				}
			} catch (IOException e) {
				System.out.println("Error: Invalid command");
			} catch (InterruptedException e) {
				System.out.println("Error: Interrupted");
			}
		}
		
	}
	
	/**
	 * To enable buttons after a bash process is finished.
	 */
	protected void enableButtons() {}
	
	public void initialize() {
		//load css file
		 _rootPane.getStylesheets().clear();
		 File theme = new File(Main._workDir + System.getProperty("file.separator") + "theme.txt");
			
			if(theme.length() == 5) {
				  _rootPane.getStylesheets().add(getClass().getResource("LightTheme.css").toExternalForm());
			} else {
				  _rootPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			}
			customInit();
	}
	
	/**
	 * Class specific initialize()
	 */
	protected abstract void customInit();
	   
	
}
