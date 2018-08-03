package factions.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Minecraft Factions launcher. Intended for use alongside an existing copy
 * of Minecraft and any required libraries. Launches a Minecraft client
 * in offline mode with a given username and maximum memory allocation.
 * @author Alec
 */
public class Launcher extends Application {
	
	/** The title of the launcher window. */
	private static final String TITLE = "Factions Launcher";
	
	/** The dimensions of the launcher window. */
	private static final int WIDTH = 280, HEIGHT = 450;
	
	/** The name of the data file for the launcher. */
	private static final String DATA_FILE = "launcher.dat";
	
	/** Default settings, to be used when no settings file is available. */
	private static final Map<String, String> DEF_SETTINGS = new HashMap<>();
	static {
		DEF_SETTINGS.put("username", "");
		DEF_SETTINGS.put("memory", "6");
	}
	
	/** Map containing settings loaded from file. */
	private Map<String, String> settings;
	
	//Open a new launcher window.
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	//Initialize the launcher window.
	public void start(Stage stage) throws Exception {
		
		//Title
		Label title = getLabel("Factions", 28, Color.DARKSLATEBLUE);
		Label version = getLabel(" " + getValue("version"), 28,
				Color.color(0.95, 0.95, 1.0));
		HBox titleLayout = new HBox(title, version);
		
		//Username field
		Label usernameLabel = getLabel("Username", 12, Color.DARKSLATEBLUE);
		TextField usernameBox = getTextField();
		usernameBox.setText(getValue("username"));
		
		//Memory slider
		Label memoryLabel = getLabel("Memory Allocation",
				12, Color.DARKSLATEBLUE);
		int defMem = Integer.parseInt(getValue("memory"));
		Slider memorySlider = getSlider(0, 32, defMem, 8);
		
		Label memoryAmount = new Label(defMem + " GB");
		memorySlider.valueProperty().addListener(l -> {
			memoryAmount.setText((int) memorySlider.getValue() + " GB");
		});
		
		//Launch button
		Button launchButton = getButton("Launch Game", h -> {
			launchGame(usernameBox.getText(), (int) memorySlider.getValue());
		});
		
		//Layout
		VBox layout = getLayout(titleLayout, new Separator(), usernameLabel,
				usernameBox, new Separator(), memoryLabel, memorySlider,
				memoryAmount, new Separator(), launchButton);
		
		//Setup stage
		setupStage(stage, layout);
	}
	
	/**
	 * Initializes a stage (window) with properties relevant to the launcher.
	 * @param stage the stage to initialize.
	 */
	private void setupStage(Stage stage, VBox layout) {
		
		stage.setTitle(TITLE);
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setResizable(false);
		stage.getIcons().add(new Image(Launcher.class
				.getResourceAsStream("icon.png")));
		
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Creates a new label with the prescribed properties.
	 * @param text the text shown by the label.
	 * @param size the font size of the text.
	 * @param color the color of the text.
	 * @return the new label.
	 */
	private Label getLabel(String text, int size, Color color) {
		Label label = new Label(text);
		label.setFont(new Font(size));
		label.setTextFill(color);
		return label;
	}
	
	/**
	 * Creates a new text field in the style of the launcher.
	 * @return the new text field.
	 */
	private TextField getTextField() {
		TextField field = new TextField();
		field.setBackground(new Background(new BackgroundFill(
				Color.ALICEBLUE, new CornerRadii(6), new Insets(0))));
		return field;
	}
	
	/**
	 * Creates a new slider with the prescribed properties.
	 * @param min the minimum slider value.
	 * @param max the maximum slider value.
	 * @param def the default slider value.
	 * @param step the distance between labels on the slider.
	 * @return the new slider.
	 */
	private Slider getSlider(int min, int max, int def, int step) {
		Slider slider = new Slider(min, max, def);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(step);
		slider.setBlockIncrement(1);
		return slider;
	}
	
	/**
	 * Creates a new button in the style of the launcher,
	 * with the given text and on-click event handler.
	 * @param text the text to display on the button.
	 * @param handler the handler to be called when the button is pressed.
	 * @return the new button.
	 */
	private Button getButton(String text,
			EventHandler<? super MouseEvent> handler) {
		
		Button button = new Button(text);
		button.setPrefWidth(250);
		button.setBackground(new Background(new BackgroundFill(
				Color.ROYALBLUE, new CornerRadii(6), new Insets(0))));
		button.setTextFill(Color.WHITESMOKE);
		button.setOnMouseClicked(handler);
		return button;
	}
	
	/**
	 * Creates a new vertical layout in the style of the launcher.
	 * @param elements the elements to put in the layout.
	 * @return the new layout.
	 */
	private VBox getLayout(Node... elements) {
		
		//Spacing
		VBox layout = new VBox(elements);
		layout.setPadding(new Insets(40, 25, 40, 25));
		layout.setSpacing(15);
		
		//Background
		Stop[] stops = new Stop[] {new Stop(0.0, Color.LIGHTSTEELBLUE),
				new Stop(1.0, Color.color(0.9, 0.9, 0.95))};
		LinearGradient background = new LinearGradient(0, 0, 0, 1,
				true, CycleMethod.NO_CYCLE, stops);
		layout.setBackground(new Background(new BackgroundFill(
				background, new CornerRadii(0), new Insets(0))));
		
		return layout;
	}
	
	/**
	 * Start the game and close the launcher.
	 * @param username the username with which to start the game.
	 * @param memory the maximum memory allocation for Minecraft.
	 */
	private void launchGame(String username, int memory) {
		
		//Remove whitespace and special characters from username.
		username = username.replace("\\s+", "_").replace("[^a-zA-Z\\d]", "");
		
		//Export settings.
		setValue("username", username);
		setValue("memory", "" + memory);
		saveDataFile();
		
		//Include username and memory allocation in launch command.
		String cmd = loadFile("launch.cmd").replace("{name}", username)
				.replace("{ram}", "" + memory)
				.replace("%cd%", System.getProperty("user.dir"));
		try {
			//Launch the game.
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Returns a data value, loaded from file or default.
	 * @param name the name of the property to get.
	 * @return the value of the property.
	 */
	private String getValue(String name) {
		
		if(settings == null) {
			loadDataFile();
		}
		if(settings.get(name) == null) {
			settings.put(name, DEF_SETTINGS.get(name));
		}
		return settings.get(name);
	}
	
	/**
	 * Adds/modifies a data value.
	 * @param name the name of the property.
	 * @param value the new value of the property.
	 */
	private void setValue(String name, String value) {
		settings.put(name, value);
	}
	
	/**
	 * Load all data from file.
	 */
	private void loadDataFile() {
		
		settings = new HashMap<>();
		
		forEachLine(DATA_FILE, s -> {
			String[] value = s.split("=");
			if(value.length == 2) {
				settings.put(value[0], value[1]);
			}
		});
	}
	
	/**
	 * Export all current settings to data file.
	 */
	private void saveDataFile() {
		
		try {
			
			//Wipe the data file.
			File file = new File(DATA_FILE);
			file.delete();
			file.createNewFile();
			
			//Write all properties to the data file.
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(Entry<String, String> entry : settings.entrySet()) {
				System.out.print(entry.getKey() + "=" + entry.getValue() + "\n");
				bw.write(entry.getKey() + "=" + entry.getValue() + "\n");
			}
			bw.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Execute a consumer for each line in the file of a given name.
	 * @param file the name of the file.
	 * @param consumer the consumer to execute.
	 */
	private void forEachLine(String fileName, Consumer<String> consumer) {
		
		try {
			File file = new File(fileName);
			file.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.lines().forEach(consumer);
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the contents of a file and returns it as a string.
	 * @param fileName the name of the file to load.
	 * @return the contents of the file.
	 */
	private String loadFile(String fileName) {
		
		String text = "";
		
		try {
			
			InputStream is = getClass().getResourceAsStream("launch.cmd");
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
			String line = "";
			
			while((line = br.readLine()) != null) {
				text += line + "\n";
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
}