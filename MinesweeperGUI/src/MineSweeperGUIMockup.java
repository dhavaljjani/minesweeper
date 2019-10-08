import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MineSweeperGUIMockup extends Application {
	/**Dhaval Jani - Period 2 - April 7, 2019
	 * I'm not sure how much time this lab took me, cause I worked on it multiple block periods in class, 
	 * and many hours at home. Probably upwards of 5 hours at least.
	 * This lab was really fun overall, even if it did take me a really long time. It was super rewarding t
	 * create a working game with UI in it too, and I enjoyed the challenge. It was fun adding things to the
	 * game like the board turning red when you lose or green when you win, and I'm pretty proud of the final
	 * version. I added some optional things, like expert mode, but I was unsure of the other optional
	 * things.
	 */
	P2_Jani_Dhaval_MinesweeperModel model = null;
	Group view = new Group();
	int rSize = 40;
	final int offset = (rSize / 8);
	ImageView[][] imageArray;
	Rectangle r;
	Label bombsLeft;
	int boardSize = 0;
	int numBombs = 0;
	
	Timer secondsLeft = new Timer("Seconds");
	int seconds = 100;
	Text timeLeft = new Text("Seconds left: " + seconds);
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			seconds--;
			timeLeft.setText("Seconds left: " + seconds);
		}
	};
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane borderPane = new BorderPane();

		VBox vBox = new VBox();
		Scene scene = new Scene(borderPane, 500, 500);
		MenuBar bar = new MenuBar();
		Menu difficulty = new Menu("Difficulty");
		MenuItem beginner = new MenuItem("New Beginner Game");
		MenuItem expert = new MenuItem("Expert");
		MenuItem exit = new MenuItem("Exit");
		MenuItem about = new MenuItem("About");
		Menu options = new Menu("Options");
		Menu help = new Menu("Help");
		MenuItem restart = new MenuItem("Restart");
		MenuItem howToPlay = new MenuItem("How to Play");
		difficulty.getItems().addAll(beginner, expert);
		options.getItems().addAll(restart, exit);
		help.getItems().addAll(howToPlay, about);
		bar.getMenus().addAll(difficulty, options, help);
		vBox.getChildren().addAll(bar);

		String answer = "a";
		while(!((int)(answer.charAt(0)) >= 49 && (int)(answer.charAt(0)) <= 57)) {
			TextInputDialog input = new TextInputDialog();
			input.setHeaderText("What size would you like the board to be? (NxN board)");
			input.showAndWait();
			answer = input.getEditor().getText();
		}
		boardSize = Integer.parseInt(answer);
		String bombs = "a";
		numBombs = 0;
		while(!((int)(bombs.charAt(0)) >= 49 && (int)(bombs.charAt(0)) <= 57)) {
			TextInputDialog input = new TextInputDialog();
			input.setHeaderText("How many bombs would you like to have? (a number only)");
			input.showAndWait();
			bombs = input.getEditor().getText();
			numBombs = Integer.parseInt(bombs);
			if(numBombs >= (boardSize * boardSize)) {
				Alert a = new Alert(AlertType.ERROR, "There can't be " + numBombs + " bombs in"
						+ " a " + boardSize + " by " + boardSize + " board!", ButtonType.OK);
				a.showAndWait();
			}
		}
		model = new P2_Jani_Dhaval_MinesweeperModel(boardSize, boardSize, numBombs, rSize);
		model.addBombs(numBombs);
		model.addNeighborNumbers();

		buildBoard(boardSize);
		
		MouseClickHandler click = new MouseClickHandler();
		vBox.setOnMouseClicked(click);
		MenuItemHandler action = new MenuItemHandler();
		howToPlay.setOnAction(action);
		restart.setOnAction(action);
		beginner.setOnAction(action);
		exit.setOnAction(action);
		about.setOnAction(action);
		expert.setOnAction(action);
		
		HBox hBox = new HBox();
		bombsLeft = new Label("Bombs left: " + model.numBombsRemaining());
		bombsLeft.setFont(new Font(30));
		secondsLeft.scheduleAtFixedRate(task, 1000, 1000);
		timeLeft.setFont(new Font(30));
		hBox.setSpacing(70);
		hBox.getChildren().addAll(bombsLeft, timeLeft);

		borderPane.setTop(vBox);
		borderPane.setCenter(view);
		borderPane.setBottom(hBox);

		stage.setTitle("MINESWEEPEER");
		stage.setScene(scene);
		stage.show();
	}
	
	private void buildBoard(int boardSize) {
		imageArray = new ImageView[boardSize][boardSize];
		double x = 0;
		double y = 0;
		r = new Rectangle();
		r.setWidth(boardSize * rSize);
		r.setHeight(boardSize * rSize);
		r.setX(0);
		r.setY(0);
		r.setFill(Color.HOTPINK);
		view.getChildren().add(r);
		for (int i = 0; i < boardSize; i++) {
			x = i * rSize;
			for (int h = 0; h < boardSize; h++) {
				y = h * rSize;
				Image blankTile = new Image("file:Images/blank.gif");
				ImageView blankTileView = new ImageView();
				blankTileView.setX(x + (offset / 2));
				blankTileView.setY(y + (offset / 2));
				blankTileView.setFitWidth(rSize - offset);
				blankTileView.setFitHeight(rSize - offset);
				blankTileView.setImage(blankTile);
				view.getChildren().addAll(blankTileView);
				imageArray[h][i] = blankTileView;
			}
		}	
		MouseClickHandler click = new MouseClickHandler();
		view.setOnMouseClicked(click);
	}

	private class MenuItemHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent args) {
			MenuItem m = (MenuItem)(args.getSource());
			if ("How to Play".equalsIgnoreCase(m.getText())) {
				WebView webView = new WebView();
				String url = "file:///";
				url+=new File("HTML/website.html").getAbsolutePath();
				webView.getEngine().load(url);
				Stage stage = new Stage();
				VBox v = new VBox();
				v.getChildren().addAll(webView);
				Scene webPage = new Scene(v, 500, 500);
				stage.setTitle("How to Play: Minesweeper");
				stage.setScene(webPage);
				stage.show();
            } else if ("Restart".equalsIgnoreCase(m.getText())) {
            	seconds = 100;
            	model.hasWon = false;
				buildBoard(boardSize);
				model.clear();
            	model = new P2_Jani_Dhaval_MinesweeperModel(boardSize, boardSize, numBombs, rSize);
            	model.addBombs(numBombs);
        		model.addNeighborNumbers();
            } else if ("New Beginner Game".equalsIgnoreCase(m.getText())) {
            	seconds = 100;
            	model.hasWon = false;
            	buildBoard(8);
            	model.clear();
            	model = new P2_Jani_Dhaval_MinesweeperModel(8, 8, 10, rSize);
            	model.addBombs(10);
        		model.addNeighborNumbers();
            } else if ("Expert".equalsIgnoreCase(m.getText())) {
            	seconds = 100;
            	model.hasWon = false;
            	rSize = 20;
            	boardSize = 15;
            	numBombs = 20;
            	buildBoard(boardSize);
            	model.clear();
            	model = new P2_Jani_Dhaval_MinesweeperModel(boardSize, boardSize, numBombs, rSize);
            	model.addBombs(numBombs);
        		model.addNeighborNumbers();
            } else if ("About".equalsIgnoreCase(m.getText())) {
				Stage stage = new Stage();
				Text about = new Text("Minesweeper (GUI Version)");
				about.setFont(new Font(15));
				Text moreAbout = new Text("made in April 2019 by Dhaval Jani");
				moreAbout.setFont(new Font(15));
				VBox v = new VBox();
				v.getChildren().addAll(about, moreAbout);
				Scene howToPlay = new Scene(v, 240, 50);
				stage.setTitle("About");
				stage.setScene(howToPlay);
				stage.show();
            } else if ("Exit".equalsIgnoreCase(m.getText())) {
				System.exit(0);
            }
			
		}
		
	}
	
	
	private class MouseClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent args) {
			if (args.getButton() == MouseButton.PRIMARY) {
				int x = (int) args.getX();
				int y = (int) args.getY();
				if (args.getSource() instanceof Group) {
					if(!model.hasWon && !model.hasLost()) {
						model.reveal(model.colForXPos(x), model.rowForYPos(y));
					}
				}
			} else if (args.getButton() == MouseButton.SECONDARY) {
				int x = (int) args.getX();
				int y = (int) args.getY();
				if (args.getSource() instanceof Group) {
					if(!model.hasWon && !model.hasLost()) {
						if (model.getUserScreenValueAt(model.colForXPos(x), model.rowForYPos(y)).equals("F")) {
							model.removeFlag(model.colForXPos(x), model.rowForYPos(y));
							bombsLeft.setText("Bombs left: " + model.numBombsRemaining());
						} else {
							model.setFlag(model.colForXPos(x), model.rowForYPos(y));
							bombsLeft.setText("Bombs left: " + model.numBombsRemaining());
						}
					}
				}
			}
			if(seconds <= 0) {
				model.hasLost = true;
			}
			if(model.numRevealed == ((model.getNumRows() * model.getNumRows()) - model.numBombs)) {
				model.hasWon = true;
			}
			if(model.hasWon) {
				r.setFill(Color.GREEN);	
				for(int i = 0 ; i < model.getNumRows() ; i++) {
					for(int h = 0 ; h < model.getNumCols() ; h++) {
						if (model.getUserScreenValueAt(i, h).equals("F")) {
							Image bombRevealed = new Image("file:Images/bomb_revealed.gif");
							ImageView bombRevealedView = new ImageView();
							bombRevealedView.setX((i * rSize) + 2.5);
							bombRevealedView.setY((h * rSize) + 2.5);
							bombRevealedView.setFitWidth(rSize - 5);
							bombRevealedView.setFitHeight(rSize - 5);
							bombRevealedView.setImage(bombRevealed);
							view.getChildren().addAll(bombRevealedView);
						}
					}
				}
				Alert a = new Alert(AlertType.INFORMATION, "You won the game! Try another beginner game,"
						+ " or restart for the same size!", ButtonType.OK);
				a.showAndWait();
				seconds = 100;
			}
			if(model.hasLost) {
				r.setFill(Color.RED);
				seconds = 100;
				for(int i = 0 ; i < model.getNumRows() ; i++) {
					for(int h = 0 ; h < model.getNumCols() ; h++) {
						if (model.getUserScreenValueAt(i, h).equals("B")) {
							Image bombRevealed = new Image("file:Images/bomb_death.gif");
							ImageView bombRevealedView = new ImageView();
							bombRevealedView.setX((i * rSize) + 2.5);
							bombRevealedView.setY((h * rSize) + 2.5);
							bombRevealedView.setFitWidth(rSize - 5);
							bombRevealedView.setFitHeight(rSize - 5);
							bombRevealedView.setImage(bombRevealed);
							view.getChildren().addAll(bombRevealedView);
						} else if( model.getUserScreenValueAt(i, h).equals("F")) {
							Image bombRevealed = new Image("file:Images/bomb_revealed.gif");
							ImageView bombRevealedView = new ImageView();
							bombRevealedView.setX((i * rSize) + 2.5);
							bombRevealedView.setY((h * rSize) + 2.5);
							bombRevealedView.setFitWidth(rSize - 5);
							bombRevealedView.setFitHeight(rSize - 5);
							bombRevealedView.setImage(bombRevealed);
							view.getChildren().addAll(bombRevealedView);
						}
					}
				}
				Alert a = new Alert(AlertType.INFORMATION, "You lost! :( Restart to try again, with"
						+ " the same size board and same amount of bombs.", ButtonType.OK);
				a.showAndWait();
			}
			updateView();
		}
		
		void updateView() {
			for (int i = 0; i < model.getNumRows(); i++) {
				for (int h = 0; h < model.getNumCols(); h++) {
					if (model.getUserScreenValueAt(i, h).equals("0")) {
						Image zero = new Image("file:Images/num_0.gif");
						ImageView zeroView = new ImageView();
						zeroView.setX((i * rSize) + (offset / 2));
						zeroView.setY((h * rSize) + (offset / 2));
						zeroView.setFitWidth(rSize - 5);
						zeroView.setFitHeight(rSize - 5);
						zeroView.setImage(zero);
						view.getChildren().addAll(zeroView);
					} else if (model.getUserScreenValueAt(i, h).equals("1")) {
						Image one = new Image("file:Images/num_1.gif");
						ImageView oneView = new ImageView();
						oneView.setX((i * rSize) + (offset / 2));
						oneView.setY((h * rSize) + (offset / 2));
						oneView.setFitWidth(rSize - 5);
						oneView.setFitHeight(rSize - 5);
						oneView.setImage(one);
						view.getChildren().addAll(oneView);
					} else if (model.getUserScreenValueAt(i, h).equals("2")) {
						Image two = new Image("file:Images/num_2.gif");
						ImageView twoView = new ImageView();
						twoView.setX((i * rSize) + (offset / 2));
						twoView.setY((h * rSize) + (offset / 2));
						twoView.setFitWidth(rSize - 5);
						twoView.setFitHeight(rSize - 5);
						twoView.setImage(two);
						view.getChildren().addAll(twoView);
					} else if (model.getUserScreenValueAt(i, h).equals("3")) {
						Image three = new Image("file:Images/num_3.gif");
						ImageView threeView = new ImageView();
						threeView.setX((i * rSize) + (offset / 2));
						threeView.setY((h * rSize) + (offset / 2));
						threeView.setFitWidth(rSize);
						threeView.setFitHeight(rSize);
						threeView.setImage(three);
						view.getChildren().addAll(threeView);
					} else if (model.getUserScreenValueAt(i, h).equals("B")) {
						Image bomb = new Image("file:Images/bomb_death.gif");
						ImageView bombView = new ImageView();
						bombView.setX((i * rSize) + (offset / 2));
						bombView.setY((h * rSize) + (offset / 2));
						bombView.setFitWidth(rSize - 5);
						bombView.setFitHeight(rSize - 5);
						bombView.setImage(bomb);
						view.getChildren().addAll(bombView);
						model.hasLost = true;
					} else if (model.getUserScreenValueAt(i, h).equals("F")) {
						Image flag = new Image("file:Images/bomb_flagged.gif");
						ImageView flagView = new ImageView();
						flagView.setX((i * rSize) + (offset / 2));
						flagView.setY((h * rSize) + (offset / 2));
						flagView.setFitWidth(rSize - 5);
						flagView.setFitHeight(rSize - 5);
						flagView.setImage(flag);
						view.getChildren().addAll(flagView);
					} else if (model.getUserScreenValueAt(i, h).equals("*")) {
						Image blank = new Image("file:Images/blank.gif");
						ImageView blankView = new ImageView();
						blankView.setX((i * rSize) + (offset / 2));
						blankView.setY((h * rSize) + (offset / 2));
						blankView.setFitWidth(rSize - 5);
						blankView.setFitHeight(rSize - 5);
						blankView.setImage(blank);
						view.getChildren().addAll(blankView);
					} else if (model.getUserScreenValueAt(i, h).equals("4")) {
						Image four = new Image("file:Images/num_4.gif");
						ImageView fourView = new ImageView();
						fourView.setX((i * rSize) + (offset / 2));
						fourView.setY((h * rSize) + (offset / 2));
						fourView.setFitWidth(rSize);
						fourView.setFitHeight(rSize);
						fourView.setImage(four);
						view.getChildren().addAll(fourView);
					} else if (model.getUserScreenValueAt(i, h).equals("5")) {
						Image five = new Image("file:Images/num_5.gif");
						ImageView fiveView = new ImageView();
						fiveView.setX((i * rSize) + (offset / 2));
						fiveView.setY((h * rSize) + (offset / 2));
						fiveView.setFitWidth(rSize);
						fiveView.setFitHeight(rSize);
						fiveView.setImage(five);
						view.getChildren().addAll(fiveView);
					} else if (model.getUserScreenValueAt(i, h).equals("6")) {
						Image six = new Image("file:Images/num_6.gif");
						ImageView sixView = new ImageView();
						sixView.setX((i * rSize) + (offset / 2));
						sixView.setY((h * rSize) + (offset / 2));
						sixView.setFitWidth(rSize);
						sixView.setFitHeight(rSize);
						sixView.setImage(six);
						view.getChildren().addAll(sixView);
					} else if (model.getUserScreenValueAt(i, h).equals("7")) {
						Image seven = new Image("file:Images/num_7.gif");
						ImageView sevenView = new ImageView();
						sevenView.setX((i * rSize) + (offset / 2));
						sevenView.setY((h * rSize) + (offset / 2));
						sevenView.setFitWidth(rSize);
						sevenView.setFitHeight(rSize);
						sevenView.setImage(seven);
						view.getChildren().addAll(sevenView);
					} else if (model.getUserScreenValueAt(i, h).equals("8")) {
						Image eight = new Image("file:Images/num_8.gif");
						ImageView eightView = new ImageView();
						eightView.setX((i * rSize) + (offset / 2));
						eightView.setY((h * rSize) + (offset / 2));
						eightView.setFitWidth(rSize);
						eightView.setFitHeight(rSize);
						eightView.setImage(eight);
						view.getChildren().addAll(eightView);
					}
				}
			}
		}
	}
}
