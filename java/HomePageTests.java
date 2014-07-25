package ui8.home;

/* 
 * This code is where my junit test are called in such
 * a way as to make them available to JSystem.
 * 
 * I followed a basic pattern here as I was taught by
 * the original developers.
 */
import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;

import org.junit.Before;
import org.junit.Test;

import com.ca.auto.common.BaseTest;
import com.ca.auto.common.annotations.SupportTestTypes;
import com.ca.auto.enums.tests.TestType;
import com.ca.auto.testapi.ui8.home.IHome;

public class HomePageTests extends BaseTest {
	
	IHome home;
	private int expectedCount;
	private String searchText;
	
	
	@Before
	public void setupTests() throws Exception {
		home = (IHome)context.getBean("home_ui8");
	}
	
	// Home tests
	
	/**
	 * This tests drags and drops 1 item from the app list to the Favorites area.  If the drag and drop
	 * is successful, the test returns TRUE.
	 * 
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	//
	// The @TestProperties annotation is used by JSystem for naming and parameters
	//
	@TestProperties(name = "Home - My Apps - drag 1 app to Favorites ", paramsInclude = { "expectedCount, testType" })
	public void dragAndDrop1AppToFavorites() throws Exception {
		if (home.dragAndDrop1AppToFavorites(expectedCount) == true) {
			//
			// this is some of the test-specific reporting that appears in JSystem
			//
			report.report("Successfully dragged 1 app to Favorites: " + expectedCount, Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Failed to drag app to favorites, expected count incorrect: " + expectedCount,
					Reporter.FAIL);
		}
	}

	/**
	 * This test drags any existing apps from the Favorites area back to the apps area.  It is
	 * a minor test unto itself but can also be a utility to aid in constructing other tests.
	 * 
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	@TestProperties(name = "Home - My Apps - drag all Favorites away ", paramsInclude = { "testType" })
	public void dragAllFavoritesAway() throws Exception {
		if (home.dragAllFavoritesAway() == true) {
			report.report("Successfully dragged away all existing Favorites. ", Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Failed to drag away all existing Favorites, expected count incorrect: " +
					Reporter.FAIL);
		}
	}
	
	/**
	 * This test submits a search of <i><b>searchText</i></b> in the Home - My Apps search engine.
	 * The test returns TRUE if the number of results returned == <i><b>expectedCount</i></b>.
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	@TestProperties(name = "Home - My Apps - Search and hit RETURN to submit ", paramsInclude = { "searchText, expectedCount, testType" })
	public void homeMyAppsSearchAndHitReturnSuccessful() throws Exception {
		if (home.homeMyAppsSearchAndHitReturnSuccessful(searchText, expectedCount) == true) {
			report.report("Successfully found at least 1 match on: " + searchText, Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Failed to find match for: " + searchText,
					Reporter.FAIL);
		}
	}
	
	/**
	 * This test submits a search of <i><b>searchText</i></b> in the Home - My Store search engine.
	 * The test returns TRUE if the number of results returned == <i><b>expectedCount</i></b>.
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	@TestProperties(name = "Home - My Store - Search and hit RETURN to submit ", paramsInclude = { "searchText, expectedCount, testType" })
	public void homeMyStoreSearchAndHitReturnSuccessful() throws Exception {
		if (home.homeMyStoreSearchAndHitReturnSuccessful(searchText, expectedCount) == true) {
			report.report("Successfully found at least 1 match on: " + searchText, Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Failed to find match for: " + searchText,
					Reporter.FAIL);
		}
	}
	
	/**
	 * This test performs a basic content check of the Home - My Approvals page.  
	 * The <i><b>expectedCount</i></b> parameter indicates the number of approvals 
	 * we expect to see on the first page of the My Approvals screen.  This can
	 * later be expanded to page through all subsequent pages of content (if any)
	 * for a total count.
	 * 
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	@TestProperties(name = "Home - My Approvals - Basic content check, expected rows of data on first page ", paramsInclude = { "expectedCount, testType" })
	public void homeMyApprovalsContentCheck() throws Exception {
		if (home.homeMyApprovalsContentCheck(expectedCount) == true) {
			report.report("Content check successful ", Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Content check not successful " + searchText,
					Reporter.FAIL);
		}
	}
	
	/**
	 * This test performs a basic content check of the Reassign modal in an existing approval.
	 * 
	 * <b><font color="red">This test often fails when asset information simply fails to load.  This is probably
	 * a simulator environment issue, and can sometimes be remedied by running the test twice in a row.  The
	 * first run sometimes seems to 'clear the way' for a successful run the 2nd time around.</b></font>
	 * 
	 */
	@Test(timeout=TEST_TIMEOUT)
	@SupportTestTypes(testTypes = {TestType.Selenium2})
	@TestProperties(name = "Home - My Approvals - Reassign modal and confirmation content check ", paramsInclude = { "testType" })
	public void homeMyApprovalsReassignModalAndConfirmationTest() throws Exception {
		if (home.homeMyApprovalsReassignModalAndConfirmationTest() == true) {
			report.report("Content check successful ", Reporter.ReportAttribute.BOLD);
		} else {
			report.report("Content check not successful ",
					Reporter.FAIL);
		}
	}

	
	//*****    Getter & Setters
	public int getExpectedCount() {
		return expectedCount;
	}

	@ParameterProperties(description = "The number of results expected")
	public void setExpectedCount(int expectedCount) {
		this.expectedCount = expectedCount;
	}
	
	public String getSearchText() {
		return searchText;
	}

	@ParameterProperties(description = "Text string for search input")
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	
}

