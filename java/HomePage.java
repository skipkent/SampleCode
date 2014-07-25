package com.ca.auto.im.ui8.home;

import java.util.List;

import jsystem.framework.report.Reporter;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ca.auto.im.ui8.common.BasicNavigationPage;
import com.ca.auto.im.ui8.common.SearchHelperPage;
import com.ca.auto.im.ui8.common.SearchHelperPage.SearchTypes;
import com.ca.auto.im.ui8.common.ThrobberHelperPage;
import com.ca.auto.im.ui8.common.UserActionsHelperPage;
import com.ca.auto.selenium.webdriver.AbstractPageObject;

public class HomePage extends AbstractPageObject {
	
//	@FindBy(linkText="Yaval Nissan")
//	public WebElement userDropDown;
	
	@FindBy(partialLinkText="My Store")
	public WebElement myStoreLinkElement;
	
	@FindBy(partialLinkText="My Apps")
	public WebElement myAppsLinkElement;
	
	@FindBy(partialLinkText="My Approvals")
	public WebElement myApprovalsLinkElement;
	
	@FindBy(id="my-approvals-counter")
	public WebElement myApprovalsCounter;
	
	public HomePage() {
		super();
	}
	
	public WebElement getMyAppsElement() {
		List<WebElement> buttons = driver.findElements(By.cssSelector("[id^='button'].x-btn-icon-el"));
		return buttons.get(3);
	}
	
	public WebElement getMyStoreElement() {
		List<WebElement> buttons = driver.findElements(By.cssSelector("[id^='button'].x-btn-icon-el"));
		return buttons.get(4);
	}
	
	public WebElement getMyApprovalsElement() {
		List<WebElement> buttons = driver.findElements(By.cssSelector("[id^='button'].x-btn-icon-el"));
		return buttons.get(5);
	}
	
	//
	// BEGIN Tests
	//
	
	public boolean dragAndDrop1AppToFavorites(int expectedCount) throws Exception {
		
		navigateToPage();
		HomeMyAppsPage myApps = new HomeMyAppsPage();

		myApps.waitForAppsToLoad();
		//
		// first clear out existing favorites
		//
		if (myApps.getAppsInFavoritesCount() != 0) {
			myApps.dragAllFavoritesAway(); 
		}
		//
		// now put in 3
		//
		Thread.sleep(4000);
		myApps.addAppToFavoritesDrag();
		//
		// now verify count
		//
		return (myApps.getAppsInFavoritesCount() > 0);
		// return (myApps.getAppsInFavoritesCount() = expectedCount);
	}
	
	public boolean dragAllFavoritesAway() throws Exception {
		
		navigateToPage();
		HomeMyAppsPage myApps = new HomeMyAppsPage();
		
		myApps.waitForAppsToLoad();
		Thread.sleep(3000);
		myApps.dragAllFavoritesAway();
		// should all be gone
		return  (myApps.getAppsInFavoritesCount() == 0);
	}
	
	public boolean homeMyAppsSearchAndHitReturnSuccessful(String searchText, int expectedResults) throws Exception {
		
		navigateToPage();
		HomePage home = new HomePage();
		home.getMyAppsElement().click();
		//
		// Now submit simple search and hit Return
		//
		SearchHelperPage search = new SearchHelperPage(driver);
		new ThrobberHelperPage(driver).waitForThrobberStop();
		search.appsSearchInputElement.sendKeys(searchText);
		search.appsSearchInputElement.sendKeys(Keys.RETURN);
		search.appsSearchInputElement.sendKeys(Keys.RETURN);
		new ThrobberHelperPage(driver).waitForThrobberStop();
		//
		// assert for correct amount in grid
		//
		int resultsReturned = new SearchHelperPage(driver).returnHomeMyAppsSearchResultsCount(SearchTypes.GRID);
		return (resultsReturned > 0);  // return a boolean for JSystem
	}
	
	public boolean homeMyStoreSearchAndHitReturnSuccessful(String searchText, int expectedResults) throws Exception {
		
		navigateToPage();
		HomePage home = new HomePage();
		ThrobberHelperPage throbber = new ThrobberHelperPage(driver);
		throbber.waitForThrobberStop();
		HomeMyStorePage storePanel = new HomeMyStorePage();
		home.myStoreLinkElement.click();
		new WebDriverWait(driver, 10).until(
				ExpectedConditions.visibilityOf(storePanel.browseAssetsTextElement));
		//
		// Now submit simple search and hit Return
		//
		throbber.waitForThrobberStop();
		storePanel.submitSearchText(searchText);
		throbber.waitForThrobberStop();  
		//
		// assert for correct amount in grid
		//
		int resultsReturned = new SearchHelperPage(driver).returnHomeMyStoreSearchResultsCount(SearchTypes.GRID);
		return (resultsReturned > 0);
	}
	
	public boolean homeMyApprovalsContentCheck(int expectedCount) throws Exception {
		navigateToPage();
		WebDriverWait wait = new WebDriverWait(driver, 20);
		UserActionsHelperPage helper = new UserActionsHelperPage(driver);
		BasicNavigationPage nav = new BasicNavigationPage(driver);
		nav.clickHomeButtonAndWaitForMyApprovalsCounterPopulate();
		HomeMyApprovalsPage panel = new HomeMyApprovalsPage();
		
		// click to view My Approvals
		driver.findElement(By.cssSelector("#my-approvals-counter")).click();
		
		// wait for page to load
		waitForThrobberStop();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[placeholder=' Sort By Newest']")));
		int rowsOnPage = driver.findElements(By.cssSelector(panel.approvalQueueCss)).size();
		
		// verify content
		//boolean headerFooter = new HeaderFooterPage().checkHeaderAndFooterContent();
		boolean sortByDropdown = tryByCss("[placeholder=' Sort By Newest']");
		if (!sortByDropdown) { report.report("problem with Sort By dropdown", Reporter.FAIL); }
		// click dropdown to check content
		helper.moveToElementAndClick(driver.findElement(By.cssSelector(panel.dropDownsCss)));
		boolean sortByContent1 = tryByXpath("//*[text()=' Sort By Newest']");
		boolean sortByContent2 = tryByXpath("//*[text()=' Sort By Oldest']");
		if (!sortByContent1 || ! sortByContent2) { report.report("problem with Sort By dropdown content", Reporter.FAIL); }
		boolean tenRowsOnPage = rowsOnPage == expectedCount;
		if (!tenRowsOnPage) { 
			report.report("problem with tenRowsOnPage check, expected " + 
					expectedCount + ", got " + rowsOnPage, Reporter.FAIL); 
		}
		boolean loadMoreButton = panel.loadMoreButtonElement.isDisplayed();
		
		if (!(sortByDropdown || sortByContent1 || sortByContent2 || tenRowsOnPage || loadMoreButton)) { 
			takeScreenshot();
		}
		
		return sortByDropdown && sortByContent1 && sortByContent2 && tenRowsOnPage && loadMoreButton;
	}
	
	public boolean homeMyApprovalsReassignModalAndConfirmationTest() throws Exception {
		navigateToPage();
		WebDriverWait wait = new WebDriverWait(driver, 20);
		UserActionsHelperPage helper = new UserActionsHelperPage(driver);
		BasicNavigationPage nav = new BasicNavigationPage(driver);
		HomeMyApprovalsPage panel = new HomeMyApprovalsPage();
		
		// make sure approvals are loaded
		nav.clickHomeButtonAndWaitForMyApprovalsCounterPopulate();

		// click to view My Approvals
		moveToElementAndClick(driver.findElement(By.cssSelector("#my-approvals-counter")));
		
		// wait for page to load
		new ThrobberHelperPage(driver).waitForThrobberStop();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[placeholder=' Sort By Newest']")));
		
		WebElement firstApproval = driver.findElement(By.cssSelector(".approval-id"));
		moveToElementAndClick(firstApproval);
		
		// wait for page to load, use approval dates as hook
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".approval-dates")));

		//
		//
		
		if (tryByCss("[role='reassignModal']")) {
			// click to re-assign first asset
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[role='reassignModal']")));
			WebElement reassignButton = driver.findElement(By.cssSelector("[role='reassignModal']"));
			helper.moveToElementAndClick(reassignButton);
			
			// wait for search text box on modal
			WebElement searchBox = driver.findElement(By.cssSelector("[id^='textfield'] tbody"));
			wait.until(ExpectedConditions.visibilityOf(searchBox));
			
			// check for content
			boolean screenshot = false;
			//
			boolean icon = tryByCss(".icons-people");
			if (!icon) { screenshot = true; report.report("problem with people icon"); }
			boolean title = driver.findElement(By.cssSelector(".modal-title")).getText().contains("Reassign");
			if (!title) { screenshot = true; report.report("problem with title"); }
			boolean search = searchBox.isDisplayed();
			if (!search) { screenshot = true; report.report("problem with search box"); }
			// headers
			List<WebElement> headers = driver.findElements(By.cssSelector(".x-column-header-text"));
			boolean columnHeaders = headers.get(1).getText().contains("Name") &&
								    headers.get(2).getText().contains("User ID");
			if (!columnHeaders) { screenshot = true; report.report("problem with column headers should be Name and User ID"); }
			boolean tenRows = driver.findElements(By.cssSelector(".x-grid-row")).size() == 10;
			if (!tenRows) { screenshot = true; report.report("problem with rows should be 10"); }
			boolean radios = driver.findElements(By.cssSelector("[type='radio']")).size() == 10;
			if (!radios) { screenshot = true; report.report("problem with radio buttons should be 10"); }
			//
			// click one radio button for next boolean
			//
			this.moveToElementAndClick(driver.findElement(By.cssSelector("[type='radio']")));
			//
			boolean peopleSelected = tryByXpath("//*[text()=' People selected']");
			if (!peopleSelected) { screenshot = true; report.report("problem with people selected message bar"); }
			boolean selectedPeopleCount = driver.findElements(By.cssSelector(".x-boxselect-item")).size() == 1;
			boolean pager = tryByCss(".x-btn-group-notitle");
			if (!pager) { screenshot = true; report.report("problem with pager element css"); }
			boolean cancel = tryByXpath("//*[text()=' Cancel']");
			if (!cancel) { screenshot = true; report.report("problem with cancel button xpath"); }
			boolean reassign = tryByXpath("//span[text()=' Reassign']");
			if (!reassign) { screenshot = true; report.report("problem with reassign button xpath"); }
			
			if (screenshot) { takeScreenshot(); }
			
			boolean reassignModalContent = icon && title && search && columnHeaders && tenRows && radios && peopleSelected &&
					selectedPeopleCount && pager && cancel && reassign;
			
			// now click Reassign to bring up confirmation
			
			moveToElementAndClick(driver.findElement(By.xpath("//span[text()=' Reassign'][@class='x-btn-inner x-btn-inner-center']")));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[text()='Confirm Reassignment']")));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id^='textarea'] textarea.x-form-textarea")));
			
			boolean userPhoto = tryByCss("[alt='User Photo']");
			if (!userPhoto) { screenshot = true; report.report("problem with user photo css"); }
			boolean reassignName = tryByCss(".assets-name-comment");
			if (!reassignName) { screenshot = true; report.report("problem with reassingment name css"); }
			boolean commentField = tryByCss("[id^='textarea'] textarea.x-form-textarea");
			if (!commentField) { screenshot = true; report.report("problem with coment field css"); }
			boolean cancelButton = tryByXpath("//strong[text()='Cancel']");
			if (!cancelButton) { screenshot = true; report.report("problem with cancel button xpath"); }
			boolean submitButton = tryByXpath("//*[text()='Submit']");
			if (!submitButton) { screenshot = true; report.report("problem with submit button xpath"); }
			
			// type some text!
			driver.findElement(By.cssSelector("[id^='textarea'] textarea.x-form-textarea")).sendKeys("This is just a simple comment");
			
			boolean confirmModalContent = userPhoto && reassignName && commentField &&
					cancelButton && submitButton;
			
			// hit Submit
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Submit']")));
			moveToElementAndClick(driver.findElement(By.xpath("//*[text()='Submit']")));
			
			// should be back on Review Access Request page
			// User Photo should no longer be displayed
			
			try {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("[alt='User Photo']")));
				return reassignModalContent && confirmModalContent;
			} catch (TimeoutException e) {
				report.report("Confirmation modal didn't close, user photo still visible");
				return false;
			}
			
		} else {
			report.report("No assets in request.  Asset needed for test!", Reporter.FAIL);
			takeScreenshot();
			return false;  // asset needed for test
		}
	}
	
	//
	// END Tests
	//
	
	@Override
	public void assertInPage() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void navigateToPage() throws Exception {
		
		WebDriverWait wait = new WebDriverWait(driver, 30);
		// hook on Store element to confirm page load
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".store")));
		
		BasicNavigationPage nav = new BasicNavigationPage(driver);
		nav.clickHomeButtonAndWaitForPageToLoad();
	}
}
