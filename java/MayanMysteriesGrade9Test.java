package sikuliwebdrivertests;

import static org.sikuli.api.API.browse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.Target;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.sikuli.api.visual.Canvas;
import org.sikuli.api.visual.DesktopCanvas;

public class MayanMysteriesGrade9Test {
	
	String testURL = "http://mayanmysteries.fablevision-dev.com/phase2/index.html?usernumber=23";
	Target target;
	ScreenRegion screen;
	ScreenRegion element;
	ScreenLocation dragLocation;
	ScreenLocation dragDestination;
	Mouse mouse;
	Canvas canvas;
	static double minScore = 0.8;
	
	@Before
	public void startBrowser() throws MalformedURLException {
		mouse = new DesktopMouse();
		screen = new DesktopScreenRegion();
		canvas = new DesktopCanvas();
		
		// open url
		browse(new URL(testURL));
		
		// wait for Loading.. to avoid false starts
		waitFor("img/loadingWait.png");
	}
	
	@After
	public void quitBrowserTab() throws InterruptedException {
		// COMMAND W to close Safari window
		// TODO put in a browser/platform check to make
		// this flexible on mac/pc/safari/firefox/etc
		
		// click off fullscreen first
		// otherwise key command won't work
//		clickFuzzyOn("img/fullScreen.png");
//		
//		Keyboard keys = new DesktopKeyboard();
//		keys.keyDown(Key.CMD);
//		keys.type("w");
	}
	
	
	@Test
	public void policeStationBoyPuzlleSuccess() throws InterruptedException {
		startNewGame();
		
		// click boy exclamation icon
		clickOn("img/policeStation/policeStation_boy.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationBoy();
	}
	
	@Test
	public void policeStationOfficerPuzzleSuccess() throws InterruptedException {
		startNewGame();
		
		// click officer exclamation icon
		clickOn("img/policeStation/policeStation_officer.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationOfficer();
	}
	
	@Test
	public void policeStationManPuzzleSuccess() throws InterruptedException {
		startNewGame();
		
		// click officer exclamation icon
		clickOn("img/policeStation/policeStation_man.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationMan();
	}
	
	@Test
	public void policeStationGirlPuzzleSuccess() throws InterruptedException {
		startNewGame();
		
		// click girl exclamation icon
		clickOn("img/policeStation/policeStation_girl.png");
		
		// click Yes for 'do you want to play?'
		clickOn("img/doYouWantToPlay_yes.png");
		
		// solve Race Puzzle
		solveMapPuzzlePoliceStationGirl();
		
		// click book Next
		clickOn("img/book_next.png");
		
		// close Book
		clickOn("img/book_close.png");
	}
	
	@Test
	public void policeStationAllSuccessful() throws InterruptedException {
		startNewGame();
		
		//
		// BOY
		//
		
		// click boy exclamation icon
		clickOn("img/policeStation/policeStation_boy.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationBoy();
		
		//
		// OFFICER
		//
		
		// click officer exclamation icon
		clickOn("img/policeStation/policeStation_officer.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationOfficer();
		
		//
		// MAN
		//
		
		// click officer exclamation icon
		clickOn("img/policeStation/policeStation_man.png");
		
		// click book Next
		clickOn("img/book_next.png");
		
		// click book Challenge
		clickOn("img/book_challenge.png");
		
		// solve Race Puzzle
		solveRacePuzzlePoliceStationMan();
		
		// click to see Clues
		clickOn("img/policeStation/racePuzzle_clues.png");
		
		// click Clues Back button
		clickOn("img/clues_Back.png");
		
		//
		// GIRL
		//
		
		// click girl exclamation icon
		clickOn("img/policeStation/policeStation_girl.png");
		
		// click Yes for 'do you want to play?'
		clickOn("img/doYouWantToPlay_yes.png");
		
		// solve Race Puzzle
		solveMapPuzzlePoliceStationGirl();
		
		// click book Next
		clickOn("img/book_next.png");
		
		// close Book
		clickOn("img/book_close.png");
		
		//
		// DONE
		//
		
		// click Menu to go to main menu
		clickOn("img/menu.png");
		
	}
	
	//
	// Class methods
	//
	
	public void solveRacePuzzlePoliceStationBoy() {
		System.out.println("\n\nSolving race puzzle...");
		// put all known questions and answers into arrays
		// question/answer png's must be in same position
		// in each array
		String questions[] = { 
				// boy questions
				"img/policeStation/racePuzzle_bigRockQuestion.png",
				"img/policeStation/racePuzzle_biodegradableQuestion.png",
				"img/policeStation/racePuzzle_canBeArtifactQuestion.png",
				"img/policeStation/racePuzzle_durableQuestion.png",
				"img/policeStation/racePuzzle_whichArtifactQuestion.png",
				"img/policeStation/racePuzzle_leaveBehindQuestion.png",
				"img/policeStation/racePuzzle_allArtifactsQuestion.png",
				"img/policeStation/racePuzzle_lastForeverQuestion.png",
				"img/policeStation/racePuzzle_notAnArtifactQuestion.png",
		};
		String answers[] = {
				// boy questions
				"img/policeStation/racePuzzle_bigRockAnswer.png",
				"img/policeStation/racePuzzle_biodegradableAnswer.png",
				"img/policeStation/racePuzzle_canBeArtifactAnswer.png",
				"img/policeStation/racePuzzle_durableAnswer.png",
				"img/policeStation/racePuzzle_whichArtifactAnswer.png",
				"img/policeStation/racePuzzle_leaveBehindAnswer.png",
				"img/policeStation/racePuzzle_allArtifactsAnswer.png",
				"img/policeStation/racePuzzle_lastForeverAnswer.png",
				"img/policeStation/racePuzzle_notAnArtifactAnswer.png",
				"img/policeStation/racePuzzle_looterLikeQuestion",
		};
		// solve 4 questions per game
		for (int i=0; i<4; i++) {
			// wait for question and click border to clear mouse position
			// away from dialog text
			waitForAndNotify("img/policeStation/racePuzzleQuestionExists.png", 10000);
			clickOn("img/policeStation/racePuzzleQuestionExists.png");
			
			// solve question
			solveCurrentRacePuzzleQuestion(questions, answers);
		}
		// click precise match racePuzzle Success close button
		waitPreciseFor("img/policeStation/racePuzzle_successClose.png");
		clickPreciseOn("img/policeStation/racePuzzle_successClose.png");
	}
		
	public void solveRacePuzzlePoliceStationOfficer() {
		System.out.println("\n\nSolving race puzzle...");
		// put all known questions and answers into arrays
		// question/answer png's must be in same position
		// in each array
		String questions[] = { 
				// officer questions
				"img/policeStation/racePuzzle_looterQuestion.png",
				"img/policeStation/racePuzzle_stillMayaQuestion.png",
				"img/policeStation/racePuzzle_mateoWantQuestion.png",
				"img/policeStation/racePuzzle_lootersPayQuestion.png",
				"img/policeStation/racePuzzle_lootersThrowQuestion.png",
				"img/policeStation/racePuzzle_looterLikeQuestion.png",
				"img/policeStation/racePuzzle_hardToStopQuestion.png",
				"img/policeStation/racePuzzle_lootersGetQuestion.png"
		};
		String answers[] = {
				// officer questions
				"img/policeStation/racePuzzle_looterAnswer.png",
				"img/policeStation/racePuzzle_stillMayaAnswer.png",
				"img/policeStation/racePuzzle_mateoWantAnswer.png",
				"img/policeStation/racePuzzle_lootersPayAnswer.png",
				"img/policeStation/racePuzzle_lootersThrowAnswer.png",
				"img/policeStation/racePuzzle_looterLikeAnswer.png",
				"img/policeStation/racePuzzle_hardToStopAnswer.png",
				"img/policeStation/racePuzzle_lootersGetAnswer.png"
		};
		// solve 4 questions per game
		for (int i=0; i<4; i++) {
			// wait for question and click border to clear mouse position
			// away from dialog text
			waitForAndNotify("img/policeStation/racePuzzleQuestionExists.png", 10000);
			clickOn("img/policeStation/racePuzzleQuestionExists.png");
			
			// solve question
			solveCurrentRacePuzzleQuestion(questions, answers);
		}
		// click precise match racePuzzle Success close button
		waitPreciseFor("img/policeStation/racePuzzle_successClose.png");
		clickPreciseOn("img/policeStation/racePuzzle_successClose.png");
	}

	public void solveRacePuzzlePoliceStationMan() {
		System.out.println("\n\nSolving race puzzle...");
		// put all known questions and answers into arrays
		// question/answer png's must be in same position
		// in each array
		String questions[] = { 
				// officer questions
				"img/policeStation/racePuzzle_realAccomplishmentQuestion.png",
				"img/policeStation/racePuzzle_culturalAttributeQuestion.png",
				"img/policeStation/racePuzzle_fewOtherCulturesQuestion.png",
				"img/policeStation/racePuzzle_mayaEngineersQuestion.png",
				"img/policeStation/racePuzzle_astronomersPredictQuestion.png",
				"img/policeStation/racePuzzle_peakCivQuestion.png",
				"img/policeStation/racePuzzle_peopleFindQuestion.png",
				"img/policeStation/racePuzzle_kingsFoughtQuestion.png"
		};
		String answers[] = {
				// officer questions
				"img/policeStation/racePuzzle_realAccomplishmentAnswer.png",
				"img/policeStation/racePuzzle_culturalAttributeAnswer.png",
				"img/policeStation/racePuzzle_fewOtherCulturesAnswer.png",
				"img/policeStation/racePuzzle_mayaEngineersAnswer.png",
				"img/policeStation/racePuzzle_astronomersPredictAnswer.png",
				"img/policeStation/racePuzzle_peakCivAnswer.png",
				"img/policeStation/racePuzzle_peopleFindAnswer.png",
				"img/policeStation/racePuzzle_kingsFoughtAnswer.png"
		};
		// solve 4 questions per game
		for (int i=0; i<4; i++) {
			// wait for question and click border to clear mouse position
			// away from dialog text
			waitForAndNotify("img/policeStation/racePuzzleQuestionExists.png", 10000);
			clickOn("img/policeStation/racePuzzleQuestionExists.png");
			
			// solve question
			solveCurrentRacePuzzleQuestion(questions, answers);
		}
		// click precise match racePuzzle Success close button
		waitPreciseFor("img/policeStation/racePuzzle_successClose.png");
		clickPreciseOn("img/policeStation/racePuzzle_successClose.png");
	}
	
	public void solveMapPuzzlePoliceStationGirl() {
		System.out.println("Solving map puzzle");
		// For a new game, there is a tutorial that must be clicked through
		if (imageExists("img/policeStation/mapTutorial.png")) {
			// click through tutorial
			clickOn("img/policeStation/mapTutorialText1.png");
			clickOn("img/policeStation/mapTutorialText2.png");
		}
		
		// wait for narration to finish by waiting for puzzle instructions
		// to appear
		waitForAndNotify("img/policeStation/mapPuzzleInstructions.png", 50000);
		
		// drag labels to destinations
		dragAndDrop("img/policeStation/mapPuzzle_mexicoLabel.png",
					"img/policeStation/mapPuzzle_mexicoDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_panemaLabel.png",
					"img/policeStation/mapPuzzle_panemaDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_elSalvadorLabel.png",
					"img/policeStation/mapPuzzle_elSalvadorDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_nicaraguaLabel.png",
					"img/policeStation/mapPuzzle_nicaraguaDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_guatemalaLabel.png",
					"img/policeStation/mapPuzzle_guatemalaDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_hondurasLabel.png",
					"img/policeStation/mapPuzzle_hondurasDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_belizeLabel.png",
					"img/policeStation/mapPuzzle_belizeDestination.png");
		dragAndDrop("img/policeStation/mapPuzzle_costaRicaLabel.png",
					"img/policeStation/mapPuzzle_costaRicaDestination.png");
		
		// click Submit
		clickOn("img/policeStation/mapPuzzleSubmit.png");
		
		// wait for next narration and instructions
		waitForAndNotify("img/policeStation/mapPuzzleInstructions2.png", 30000);
		
		// drag feature labels to destinations
				dragAndDrop("img/policeStation/mapPuzzle_featureCaracol.png",
							"img/policeStation/mapPuzzle_featureDest2.png");
				dragAndDrop("img/policeStation/mapPuzzle_featureCeren.png",
							"img/policeStation/mapPuzzle_featureDest4.png");
				dragAndDrop("img/policeStation/mapPuzzle_featureCopan.png",
							"img/policeStation/mapPuzzle_featureDest3.png");
				dragAndDrop("img/policeStation/mapPuzzle_featureTikal.png",
							"img/policeStation/mapPuzzle_featureDest1.png");
				
		// click Submit
		clickOn("img/policeStation/mapPuzzleSubmit.png");
		
		// wait for next narration and instructions
		waitForAndNotify("img/policeStation/mapPuzzleInstructions3.png", 30000);
		
		// click map puzzle color button
		clickOn("img/policeStation/mapPuzzleColorButton.png");
		
		// color in map puzzle
		for (int i=1; i<=12; i++) {
			clickFuzzyOn("img/policeStation/mapPuzzleShade" + i + ".png");
		}
		
		// click Submit
		clickOn("img/policeStation/mapPuzzleSubmit.png");
		
		// wait for Close button
		clickOn("img/policeStation/mapPuzzle_close.png");
	}
	
	private void dragAndDrop(String img, String imgDest) {
		// get location
		//
		waitFor(img);
		target = new ImageTarget(new File(img));
		target.setMinScore(0.9);
		clickOn(img);
		dragLocation = mouse.getLocation();
		
		// get destination
		//
		target = new ImageTarget(new File(imgDest));
		target.setMinScore(0.9);
		clickOn(imgDest);
		dragDestination = mouse.getLocation();
		
		// now do the drag and drop
		//
		mouse.drag(dragLocation);
		mouse.drop(dragDestination);
	}

	private boolean imageExists(String img) {
		try {
			waitForAndNotify(img, 2000);
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void solveCurrentRacePuzzleQuestion(String[] questions, String[] answers) {
		// takes in arrays of all questions and answers and goes through
		// them until the one displayed is found, and then clicks the
		// correct answer
		for (int i=0; i < questions.length; i++) {
			if (appearsOnScreen(questions[i])) {
				waitForAndNotify(answers[i], 1000);
				clickOn(answers[i]);
				
				// wait for current dialog to disappear
				waitForAndNotify("img/policeStation/racePuzzleClearScreen.png", 3000);
				break;
			}
		}		
	}
	
	
	public boolean appearsOnScreen(String img) {
		System.out.println("Checking to see if " + img + " appears on screen...");
		try {
			waitForAndNotify(img, 1000);
			System.out.println("   " + img + " is there");
			return true;
		} catch (NullPointerException e) {
			System.out.println("   " + img + " is not there");
			return false;
		}
	}

	public void waitForAndNotify(String img, int time) {
		target = new ImageTarget(new File(img));
		target.setMinScore(0.95);
		element = screen.wait(target, time);
		//canvas.clear();
		canvas.addBox(element);
		canvas.addLabel(element, "We found it!");
		canvas.display(1);
		canvas.clear();
	}
	
	public void waitFor(String img) {
		System.out.println("Waiting for " + img);
		target = new ImageTarget(new File(img));
		target.setMinScore(minScore);
		element = screen.wait(target, 20000);
	}
	
	public void waitPreciseFor(String img) {
		System.out.println("Waiting for " + img);
		target = new ImageTarget(new File(img));
		target.setMinScore(0.9);
		element = screen.wait(target, 20000);
	}

	public void clickOn(String img) {
		waitFor(img);
		System.out.println("Clicking on " + img);
		target = new ImageTarget(new File(img));
		target.setMinScore(minScore);
		element = screen.find(target);
		mouse.click(element.getCenter());
	}
	
	public void clickPreciseOn(String img) {
		waitFor(img);
		System.out.println("Clicking on " + img);
		target = new ImageTarget(new File(img));
		target.setMinScore(0.9);
		element = screen.find(target);
		mouse.click(element.getCenter());
	}
	
	public void clickFuzzyOn(String img) {
		waitFor(img);
		System.out.println("Clicking on " + img);
		target = new ImageTarget(new File(img));
		target.setMinScore(0.7);
		element = screen.find(target);
		mouse.click(element.getCenter());
	}

	public void startNewGame() {
		// wait for fullScreen
		//waitFor("img/fullScreen.png");
		
		// click fullScreen
		clickOn("img/fullScreen.png");
		
		// click clickToPlay
		clickOn("img/clickToPlay.png");
		
		// wait for homeTitle
		waitForAndNotify("img/homeTitle.png", 30000);
		
		// click Start New (or Resume?)
		clickOn("img/home_startNew.png");
		
		// click Yes for 'Are You Sure?' dialog
		clickOn("img/areYouSure_yes.png");
		
		// click story_next 8 times
		for (int i=1; i <= 7; i++) {
			clickOn("img/story_next.png");
		}
		
		// check for Tutorial title
		waitFor("img/policeStation/policeStation_tutorialTitle.png");
		
		// click through Tutorial
		clickOn("img/policeStation/policeStation_tutorialText.png");
		
		// wait for all characters ready
		waitForAndNotify("img/policeStation/policeStationReady.png", 30000);	
	}

}
