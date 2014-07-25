from robot.libraries.BuiltIn import BuiltIn
from robot.api import logger
import time
from Selenium2Library.keywords.keywordgroup import KeywordGroup
import os, time, sys
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.common.exceptions import NoSuchElementException
from datetime import datetime
from datetime import timedelta
from selenium.webdriver import Remote  # Remote imported only for code completion
import cProfile
import subprocess
# include some of the other LPA libraries


def get_current_browser():
    """This keyword returns the instance of current browser
    :rtype: Remote
    """
    return BuiltIn().get_library_instance('Selenium2Library')._current_browser()

class performanceKeywords(KeywordGroup):

    def profile_runner(self, batchSize, maxDiaries, diaryType):
        prof = cProfile.Profile
        self.track_diary_completion_and_offline_submission_times(batchSize, maxDiaries, diaryType)

    def log_results_to_console(self, message):
        """This keyword appends the current DiaryCount and RefreshTime to the console"""
        #logger.warn(message)

    def get_mem_usage_from_android(self):
        """This keyword returns the memory (as an int) currently in use by LPA on the Android device"""
        output = subprocess.check_output("adb shell dumpsys meminfo com.phtcorp.logpadapp | grep TOTAL", shell=True)
        return output.split()[1]

    def get_cpu_usage_from_android(self):
        """This keyword returns the memory (as a string) currently in use by LPA on the Android device"""
        output = subprocess.check_output("adb shell dumpsys cpuinfo |grep phtcorp", shell=True)
        return output.split()[0]

    def click_css(self, css):
        """This is a simple method to enable clicking a Css element"""
        driver = get_current_browser()
        wait = WebDriverWait(driver, 60)
        try:
            element = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, css)))
            element.click()
        except TimeoutException:
            driver.save_screenshot("missingElement.png")
            raise Exception("Timeout failed on css: %s" % css)

    def turn_on_android_wifi_service(self):
        """This keyword uses the adb shell command 'svc' to turn on wifi"""
        command_line= 'adb shell su -c svc wifi enable'
        try:
            os.system(command_line)
            # give time for wifi to fully connect
            time.sleep(20)
        except: pass

    def turn_off_android_wifi_service(self):
        """This keyword uses the adb shell command 'svc' to turn on wifi"""
        command_line= 'adb shell su -c svc wifi disable'
        try:
            os.system(command_line)
            time.sleep(10)
        except: pass

    def initialize_offline_log_file(self, diaryType):
        """This keyword initializes the comma-delimited output log with headers.  Browser
        should be on LPA Dashboard or some items may fail (see comments below)"""
        driver = get_current_browser()
        timestamp = datetime.now().strftime("%Y-%m%d-%Hh%Mm")
        log_file_name = "Offline_LPA_Diary_Performance_log_" + timestamp + ".csv"
        file = open(log_file_name, "w")
        file.write("Performance report to track LPA diary open, completion and off-line submission times.\n")
        file.write("Diary type: %s\n" % diaryType)
        file.write("Platform: %s\n" % driver.capabilities['platform'])
        file.write("Browser: %s %s\n" % (driver.capabilities['browserName'], driver.capabilities['version']))
        file.write("LPA version: %s\n" % self.get_product_version())  #  MUST BE ON DASHBOARD OR THIS WILL FAIL
        file.write("LPA Build: 1.6.1bld#2\n\n")   # TODO Don't hard code this!
        file.write("Stored Diary Count,Avg Open Time, Avg Time To Complete,Avg Offline Submit Time, \
Android mem, Android cpu, System Time\n")
        file.close()
        return log_file_name

    def initialize_online_log_file(self, diaryType):
        """This keyword initializes the comma-delimited output log with headers.  Browser
        should be on LPA Dashboard or some items may fail (see comments below)"""
        driver = get_current_browser()
        timestamp = datetime.now().strftime("%Y-%m%d-%Hh%Mm")
        log_file_name = "Online_LPA_Diary_Upload_Performance_log_" + timestamp + ".csv"
        file = open(log_file_name, "w")
        file.write("Performance report to track LPA diary open, completion and off-line submission times.\n")
        file.write("Diary type: %s\n" % diaryType)
        file.write("Platform: %s\n" % driver.capabilities['platform'])
        file.write("Browser: %s %s\n" % (driver.capabilities['browserName'], driver.capabilities['version']))
        file.write("LPA version: %s\n" % self.get_product_version())  #  MUST BE ON DASHBOARD OR THIS WILL FAIL
        file.write("LPA Build: 1.6.1bld#2\n\n")   # TODO Don't hard code this!
        file.write("Stored Diary Count,Offline Submit Time (seconds)\n")
        file.close()
        return log_file_name

    def average(self, numbers):
        sum = 0
        average = 0
        for x in numbers:
            sum += x

        average = sum / float(len(numbers))
        return average

    def wait_until_page_contains_text(self, text):
        """Helper method to nicely wait for LPA page to contain text"""
        driver = get_current_browser()
        wait = WebDriverWait(driver, 20)
        try:
            start = datetime.now()
            wait.until(EC.text_to_be_present_in_element((By.CSS_SELECTOR, ".ui-content"), text))
            end = datetime.now()
            wait_time = self.get_time_delta(start, end)
            #logger.warn("Waited %6.3f for '%s'" % (wait_time, text))
        except TimeoutException:
            driver.save_screenshot("missingText.png")
            end = datetime.now()
            wait_time = self.get_time_delta(start, end)
            #logger.warn("Waited %6.3f for '%s'" % (wait_time, text))

    def wait_until_element_clickable_css(self, css):
        driver = get_current_browser()
        wait = WebDriverWait(driver, 10)
        try:
            start = datetime.now()
            wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, css)))
            end = datetime.now()
            wait_time = self.get_time_delta(start, end)
            #logger.warn("Waited %6.3f for '%s'" % (wait_time, css))
        except TimeoutException:
            driver.save_screenshot("missingElement.png")
            raise Exception("Page text does not contain element '%s'" % css)

    def check_exists_by_css(self, css):
        driver = get_current_browser()
        try:
            driver.find_element_by_css_selector(css)
        except NoSuchElementException:
            return False
        return True

    def click_diary_next_and_confirm_click_successful(self):
        nextButtonCss = "#nextItem"
        start = datetime.now()
        driver = get_current_browser()
        srcBefore = driver.page_source
        click_status = False
        driver = get_current_browser()
        while click_status is False:
            self.wait_and_then_click_css(nextButtonCss)
            srcAfter = driver.page_source
            if not (srcBefore == srcAfter):
                click_status = True

        end = datetime.now()
        #logger.warn("Click Next and confirm time: %6.3f" % self.get_time_delta(start, end))

    def check_exists_by_xpath(self, xpath):
        driver = get_current_browser()
        try:
            driver.find_element_by_xpath(xpath)
        except NoSuchElementException:
            return False
        return True

    def reload_page(self):
        """This keyword refreshes the page in the browser"""
        driver = get_current_browser()
        driver.refresh()

    def get_product_version(self):
        """This keyword navigates from the Dashboard to Settings (and back) returning the product version"""
        driver = get_current_browser()
        settings_icon_css = "#application-toolbox"
        about_btn_css = "#about"
        version_text_css = "#about-list li:nth-of-type(9) .about-list-subheader"
        back_btn_css = "#back"
        #
        # assuming we're on the dashboard, go to settings and get the info and come back
        #
        # TODO get some waits in here now that implicit is set to 0
        # otherwise this may fail from time to time
        self.wait_until_element_clickable_css(settings_icon_css)
        self.click_css(settings_icon_css)
        self.wait_until_element_clickable_css(about_btn_css)
        self.click_css(about_btn_css)
        version_text = driver.find_element_by_css_selector(version_text_css).text
        self.click_css(back_btn_css)  # back to settings
        self.wait_until_element_clickable_css(back_btn_css)
        self.click_css(back_btn_css)  # back to dashboard
        self.reload_page()
        #
        # now return the version info
        #
        return version_text

    def wait_and_then_click_css(self, css):
        driver = get_current_browser()
        wait = WebDriverWait(driver, 20)
        try:
            start = datetime.now()
            button = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, css)))
            end = datetime.now()
            wait_time = self.get_time_delta(start, end)
            #logger.warn("Waited %6.3f for '%s'" % (wait_time, css))
            button.click()
            #logger.warn("Clicked %s" % css)
        except TimeoutException:
            driver.save_screenshot("missingText.png")
            end = datetime.now()
            wait_time = self.get_time_delta(start, end)
            #logger.warn("TimeoutException waiting %6.3f for '%s'" % (wait_time, css))

    def get_refresh_time(self):
        """This keyword measures the time between a screen refresh and returns the result as an integer"""
        driver = get_current_browser()
        pmcDiaryID = "Meds"
        wait = WebDriverWait(driver, 10)
        #
        # log time now
        #
        start = datetime.now()
        #
        # refresh
        #
        self.reload_page()
        #
        # wait for diary button to be clickable and log time again
        #
        wait.until(EC.element_to_be_clickable((By.ID, pmcDiaryID)))
        end = datetime.now()
        #
        # calculate time difference and return value
        #
        tdelta = end - start
        seconds = tdelta.total_seconds()
        return seconds

    def get_time_delta(self, start_time, end_time):
        tdelta = end_time - start_time
        return tdelta.total_seconds()

    def get_current_diary_count(self):
        """ This keyword counts the number of diaries stored on the dashboard screen"""
        driver  = get_current_browser()
        pt=driver.find_element_by_xpath('//*[@id="pending-transmission"]/span/span[1]')
        if pt.text: return int(pt.text)
        else: return   int(0)

    def generate_multiple_diaries(self, amount, reportType):
    	"""This method generates (amount) of diaries.  Possible diary type is 'pain' or 'daily'.
    	Returns average time to complete and average submit time for all diaries created"""
        open_times = []
        completion_times = []
        submit_times = []
        if reportType in "pmc":
            x = 0
            while (x < int(amount)):
                o_time, c_time = self.fill_in_pain_med_consumption_diary_alt()    # generate 1 diary and get
                # open and completion time
                open_times.append(o_time)
                completion_times.append(c_time)
                start = datetime.now()
                self.submit_diary_entry_and_wait_for_settings_icon_clickable()
                end = datetime.now()    # record end time
                submit_times.append(self.get_time_delta(start, end))   # calculate difference and add to list
                x = x + 1
        elif reportType in "daily":
            x = 0
            while (x < int(amount)):
                o_time, c_time = self.fill_in_daily_diary()    # generate 1 diary and get completion time
                open_times.append(o_time)
                completion_times.append(c_time)
                start = datetime.now()    # record start time
                self.submit_diary_entry_and_wait_for_settings_icon_clickable()
                end = datetime.now()    # record end time
                submit_times.append(self.get_time_delta(start, end))    # calculate difference and add to list
                x = x + 1
        elif reportType in "toss":
            x = 0
            while (x < int(amount)):
                o_time, c_time = self.fill_in_toss_diary()    # generate 1 diary and get completion time
                open_times.append(o_time)
                completion_times.append(c_time)
                start = datetime.now()    # record start time
                self.submit_diary_entry_and_wait_for_settings_icon_clickable()
                end = datetime.now()    # record end time
                submit_times.append(self.get_time_delta(start, end))    # calculate difference and add to list
                x = x + 1
        elif reportType in "sf36":
            x = 0
            while (x < int(amount)):
                o_time, c_time = self.fill_in_sf36_diary_alt()    # generate 1 diary and get completion time
                open_times.append(o_time)
                completion_times.append(c_time)
                start = datetime.now()    # record start time
                self.submit_diary_entry_and_wait_for_settings_icon_clickable()
                end = datetime.now()    # record end time
                submit_times.append(self.get_time_delta(start, end))    # calculate difference and add to list
                x = x + 1
        else:
            errorMsg = "ERROR: Report type specified was '%s'.  Must be 'pain' or 'daily'." % reportType
            raise Exception(errorMsg)

        clock_time = datetime.now().strftime('%Y-%m-%d %I:%M %p')
        open_mean = self.average(open_times)
        completion_mean = self.average(completion_times)
        submit_mean = self.average(submit_times)
        mem_cnt = self.get_mem_usage_from_android()
        cpu_cnt = self.get_cpu_usage_from_android()
        return open_mean, completion_mean, submit_mean, clock_time, mem_cnt, cpu_cnt   # return completion and
        # submit times for log

    def fill_in_sf36_diary(self):
        """This keyword assumes driver is on dashboard and generates a single sf36 Diary"""
        driver  = get_current_browser()
        driver.implicitly_wait(2)
        #
        # lets submit a diary via python webdriver
        #
        sf36DiaryCss = "#SF36v2-Standard"
        landingText = "Your Health and Well-Being"
        nextButtonCss = "#nextItem"
        second_optionCss = ".radio_1"
        third_optionCss = ".radio_2"
        ok_buttonCss = ".AFFIDAVIT .ui-checkbox"
        health_limit_text = "your health now limit you"
        physical_health_text = "as a result of your physical health"
        emotional_problems_text = "as a result of any emotional problems"
        how_feel_text = "how you feel"
        true_false_text = "TRUE or FALSE"
        rights_reserved_text = "All rights reserved"

        # start time
        start = datetime.now()    # record start time

        # open sf36 diary and wait for page text
        #self.wait_until_page_contains_text("SF-36")
        self.wait_and_then_click_css(sf36DiaryCss)
        self.wait_until_page_contains_text(landingText)
        diary_open_time = self.get_time_delta(start, datetime.now())  # this get subtracted from total for completion
        self.click_diary_next_and_confirm_click_successful()

        # answer 2 questions
        general = ('health is', 'how would you rate')
        for text in general:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(health_limit_text)
        self.click_diary_next_and_confirm_click_successful()

        # answer 10 questions
        health = ('running', 'table', 'groceries', 'flights', 'one flight', 'kneeling', 'a mile', 'several hundred',
                  'one hundred', 'dressing yourself')
        for text in health:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(physical_health_text)
        self.click_diary_next_and_confirm_click_successful()

        # answer 4 questions
        physical = ('cut down', 'accomplished less', 'kind of work', 'performing the work')
        for text in physical:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(emotional_problems_text)
        self.click_diary_next_and_confirm_click_successful()

        # answer 6 questions
        emotional = ('spent', 'accomplished', 'carefully', 'interfered', 'bodily', 'housework')
        for text in emotional:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(how_feel_text)
        self.click_css(nextButtonCss)

        # answer 10 questions
        feel = ('life', 'nervous', 'dumps', 'peaceful', 'energy', 'downhearted', 'worn', 'happy',
                'tired', 'relatives')
        for text in feel:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(true_false_text)
        self.click_css(nextButtonCss)

        # answer 4 questions
        trueFalse = ('easier', 'anybody', 'worse', 'excellent')
        for text in trueFalse:
            self.wait_until_page_contains_text(text)
            self.click_css(third_optionCss)
            self.click_diary_next_and_confirm_click_successful()

        # wait for text
        self.wait_until_page_contains_text(rights_reserved_text)
        self.click_diary_next_and_confirm_click_successful()

        # click affidavit (OK)
        self.click_css(ok_buttonCss)

        # end time
        end = datetime.now()

        # return diary open and completion time
        total_time = self.get_time_delta(start, end)
        diary_completion_time = total_time - diary_open_time
        return diary_open_time, diary_completion_time

    def fill_in_sf36_diary_alt(self):
        """This keyword assumes driver is on dashboard and generates a single sf36 Diary using an alternate process
           with lots of checks to make sure it doesn't slide off track.
           While quite stable, it unfortunately takes ~7 minutes to fill out a single diary"""
        driver  = get_current_browser()
        driver.implicitly_wait(0)
        driver.refresh()
        #
        # lets submit a diary via python webdriver
        #
        sf36DiaryCss = "#SF36v2-Standard"
        landingText = "Your Health and Well-Being"
        nextButtonCss = "#nextItem"
        radio2Css = ".radio_2"
        radio2SelectedCss = ".radio_2.selected"
        affidavit_buttonCss = ".AFFIDAVIT .ui-checkbox"
        affidavit_button_uncheckedCss = ".checkbox_0.ui-checkbox-off"
        affidavit_button_checkedCss = ".AFFIDAVIT .ui-checkbox-on"

        # start time
        start = datetime.now()    # record start time

        # open sf36 diary and wait for page text
        #self.wait_until_page_contains_text("SF-36")
        self.wait_and_then_click_css(sf36DiaryCss)
        self.wait_until_page_contains_text(landingText)
        diary_open_time = self.get_time_delta(start, datetime.now())  # this get subtracted from total for completion
        self.wait_and_then_click_css(nextButtonCss)

        diary_is_complete = False
        while diary_is_complete == False:
            # if choice button exists but not selected, then click button to select
            if self.check_exists_by_css(radio2Css) and not self.check_exists_by_css(radio2SelectedCss):
                self.click_css(radio2Css)
            # else if button exists and is selected, then click next
            elif self.check_exists_by_css(radio2Css) and self.check_exists_by_css(radio2SelectedCss):
                self.click_css(nextButtonCss)
            # else if page contains affidavit and is unchecked, then check it
            elif self.check_exists_by_css(affidavit_button_uncheckedCss):
                self.click_css(affidavit_buttonCss)
            # else if page contains affidavit and is checked, then break from loop
            elif self.check_exists_by_css(affidavit_button_checkedCss):
                diary_is_complete = True
            # otherwise, assume the page contains only text and click Next
            else:
                self.click_css(nextButtonCss)

        # diary is complete, so record end time
        end = datetime.now()
        # return diary open and completion time
        total_time = self.get_time_delta(start, end)
        diary_completion_time = total_time - diary_open_time
        return diary_open_time, diary_completion_time
        #return total_time

    def fill_in_pain_med_consumption_diary(self):
        """This keyword assumes driver is on dashboard and generates a single PMC Diary"""
        driver  = get_current_browser()
        #
        # lets submit a diary via python webdriver
        #
        pmcDiaryCss = "#Meds"
        nextButtonID = "nextItem"
        second_optionCss = ".radio_1"
        ok_buttonCss = ".AFFIDAVIT .ui-checkbox"

        # start time
        start = datetime.now()    # record start time

        # open pmc diary
        driver.find_element_by_css_selector(pmcDiaryCss).click()

        # answer the 1 question
        driver.find_element_by_css_selector(second_optionCss).click()
        driver.find_element_by_id(nextButtonID).click()
        driver.find_element_by_id(nextButtonID).click()

        # click affidavit (OK)
        driver.find_element_by_css_selector(ok_buttonCss).click()

        # end time
        end = datetime.now()

        # return diary completion time
        return self.get_time_delta(start, end)

    def fill_in_pain_med_consumption_diary_alt(self):
        """This keyword assumes driver is on dashboard and generates a single PMC Diary using an alternate process
           with lots of checks to make sure it doesn't slide off track."""
        driver  = get_current_browser()
        driver.implicitly_wait(0)
        driver.refresh()
        #
        # lets submit a diary via python webdriver
        #
        pmcDiaryCss = "#Meds"
        landingText = "How many pills did you consume?"
        nextButtonCss = "#nextItem"
        radio2Css = ".radio_2"
        radio2SelectedCss = ".radio_2.ui-radio-on"
        affidavit_buttonCss = ".AFFIDAVIT .ui-checkbox"
        affidavit_button_uncheckedCss = ".checkbox_0.ui-checkbox-off"
        affidavit_button_checkedCss = ".AFFIDAVIT .ui-checkbox-on"

        # start time
        start = datetime.now()    # record start time

        # open pmc diary and wait for page text
        self.wait_until_page_contains_text("Pain Medication Consumption")
        driver.find_element_by_css_selector(pmcDiaryCss).click()
        self.wait_until_page_contains_text(landingText)
        self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, nextButtonCss)))
        self.click_css(nextButtonCss)

        diary_is_complete = False
        while diary_is_complete == False:
            # if choice button exists but not selected, then click button to select
            if self.check_exists_by_css(radio2Css) and not self.check_exists_by_css(radio2SelectedCss):
                self.click_css(radio2Css)
            # else if button exists and is selected, then click next
            elif self.check_exists_by_css(radio2Css) and self.check_exists_by_css(radio2SelectedCss):
                self.click_css(nextButtonCss)
            # else if page contains affidavit and is unchecked, then check it
            elif self.check_exists_by_css(affidavit_button_uncheckedCss):
                self.click_css(affidavit_buttonCss)
            # else if page contains affidavit and is checked, then break from loop
            elif self.check_exists_by_css(affidavit_button_checkedCss):
                diary_is_complete = True
            # otherwise, assume the page contains only text and click Next
            else:
                self.click_css(nextButtonCss)

        # diary is complete, so record end time
        end = datetime.now()
        # return diary completion time
        return self.get_time_delta(start, end)

    def fill_in_toss_diary(self):
        """This keyword assumes driver is on dashboard and generates a single TOSS Diary"""
        driver  = get_current_browser()
        #
        # lets submit a diary via python webdriver
        #
        tossDiaryCss = "#Toss_Diary"
        nextButtonCss = "#nextItem"
        symptomsCss = ".ui-controlgroup-controls .ui-checkbox"  #  finds multiple, we want 0-6, not 7
        checkBoxMildCss = ".checkbox_1"
        checkBoxModerateCss = ".checkbox_2"
        checkBoxSevereCss = ".checkbox_3"
        ok_buttonCss = ".AFFIDAVIT .ui-checkbox"

        # start time
        start = datetime.now()    # record start time

        self.click_css(tossDiaryCss)

        # answer the questions

        symptoms = driver.find_elements_by_css_selector(symptomsCss)
        if len(symptoms) < 7:
            driver.save_screenshot("missingElement.png")
            # this was an issue, either due to performance or network dropping, not sure
            raise Exception("Missing elements in TOSS diary symptoms list")

        descriptions = []
        del symptoms[-1]  # the last option is 'None' so we don't want it
        for symptom in symptoms:
            descriptions.append(symptom.text.upper().strip())  # strip trailing spaces
            symptom.click()

        # then click Next
        self.click_css(nextButtonCss)

        # do this for each of the symptoms (itchy eyes, tearing eyes, red eyes, sneezing, TODO
        #   itchy nose, runny nose, blocked nose)
        for description in descriptions:
            self.wait_until_page_contains_text(description)
            # click all 3 because some don't seem to work in this diary
            self.click_css(checkBoxMildCss)
            self.click_css(checkBoxModerateCss)
            self.click_css(checkBoxSevereCss)
            self.click_css(nextButtonCss)

        # click affidavit (OK)
        self.click_css(ok_buttonCss)

        # end time
        end = datetime.now()

        # return diary completion time
        return self.get_time_delta(start, end)

    def fill_in_daily_diary(self):
        """This keyword assumes driver is on dashboard and generates a single Daily Diary"""
        driver  = get_current_browser()
        #
        # submit a diary via python webdriver
        #
        dailyDiaryCss = "#Daily_Diary"
        nextButtonCss = "#nextItem"
        second_optionCss = ".radio_1"
        very_goodCss= ".radio_0"
        pain_graphCss = ".vas-pointer"
        ok_buttonCss = ".AFFIDAVIT .ui-checkbox"

        # start time
        start = datetime.now()    # record start time

        self.click_css(dailyDiaryCss)

        # answer the 5 questions
        i = 0
        while i < 5:
            self.click_css(second_optionCss)
            self.click_css(nextButtonCss)
            i = i + 1

        # select 'very good'
        self.click_css(very_goodCss)
        self.click_css(nextButtonCss)

        # click the pain scale
        # TODO This works fine but crashes Selendroid which causes the program to fail later
        self.click_css(pain_graphCss)
        self.click_css(nextButtonCss)

        # click affidavit (OK)
        self.click_css(ok_buttonCss)

        # end time
        end = datetime.now()

        # return diary completion time
        return self.get_time_delta(start, end)

    def submit_diary_entry_and_wait_for_settings_icon_clickable(self):
        """This method will submit either diary and wait for the Settings icon to become clickable"""
        # click OK for affidavit
        driver = get_current_browser()
        wait = WebDriverWait(driver, 10)
        #
        #
        settings_iconID = "application-toolbox"
        nextButtonID = "nextItem"
        #
        #
        driver.find_element_by_id(nextButtonID).click()
        # wait for Settings icon to appear
        settings_icon = wait.until(EC.element_to_be_clickable((By.ID,settings_iconID)))

    def track_diary_completion_and_offline_submission_times(self, batchSize, maxDiaries, diaryType):
        """"This keyword generates batches of diaries in increasing increments, measuring
        and tracking how long it takes to edit the diary (completion) and how long it take
        to submit the diary to the offline stored report queue at each increment"""
        message = "Starting run with batchSize=%s, maxDiaries=%s, diaryType=%s" % (batchSize, maxDiaries, diaryType)
        #logger.warn(message)
        #
        # initialize log file
        #
        log_file_name = self.initialize_offline_log_file(diaryType)
        #logfile = open(filename, "a")
        #logfile.close()
        #logfile.write(message + "\n\n")
        #
        # run test and log results as we go
        #
        total_count = 0
        while (total_count < int(maxDiaries)):
            #
            # generate a round of diaries and log the avg submit time returned
            #
            run_info = self.generate_multiple_diaries(batchSize, diaryType)
            avg_open_time, avg_completion_time, avg_submit_time, clock_time, mem_cnt, cpu_cnt = run_info
            #
            # get diary count from LPA
            #
            diaryCount = self.get_current_diary_count()
            #
            # increase total_count
            total_count = total_count + int(batchSize)
            """output = "%s diaries, avg_open_time of %6.3f, avg_completion_time of %6.3f, avg submit time of %6.3f \
                and total_count is %s  %s" \
                % (diaryCount, avg_open_time, avg_completion_time, avg_submit_time, total_count, clock_time)"""
            log_line = "%s,%10.3f,%10.3f,%10.3f,%s,%s,%s\n" \
                       % (diaryCount,avg_open_time,avg_completion_time,avg_submit_time,mem_cnt,cpu_cnt,clock_time)
            logfile = open(log_file_name, "a")
            logfile.write(log_line)
            logfile.close()
            #self.log_results_to_console(output)

        #logfile.close()
        #logger.warn("Run complete.")

    def generate_diaries_offline_and_track_online_submission_times(self, batchSize, maxDiaries, diaryType):
        """This keyword accepts parameters for batchSize, maxDiaries and diaryType, and then creates a batch of
        diaries offline of size (batchSize).  It then goes online and submits the diaries, tracking the time spent
        performing the upload.  This repeats in increments of (batchSize) until the current size exceeds (maxDiaries)."""
        driver = get_current_browser()
        uploadButtonXpath = "//*[@id='pending-transmission']/span/span[1]"
        sf36DiaryCss = "#SF36v2-Standard"
        log_file_name = self.initialize_online_log_file(diaryType)
        total_count = 0
        while (total_count <= int(maxDiaries)):
            total_count = total_count + int(batchSize)
            #
            # turn off wifi
            #
            self.turn_off_android_wifi_service()
            #
            # wait for diary text on dashboard
            #
            self.wait_until_page_contains_text("SF-36v2 Standard")
            #
            # generate (totalCount) diaries and get stats (which we don't currently use but may want later)
            #
            stats = self.generate_multiple_diaries(total_count, diaryType)
            open_mean, completion_mean, submit_mean, clock_time, mem_cnt, cpu_cnt = stats
            # turn on wifi
            self.turn_on_android_wifi_service()
            #
            # wait for diary text on dashboard
            #
            self.wait_until_page_contains_text("SF-36v2 Standard")
            start_time = datetime.now()
            #
            # start upload to Study Works
            #
            self.check_exists_by_xpath(uploadButtonXpath)
            driver.find_element_by_xpath(uploadButtonXpath).click()
            #
            # wait until it's done
            #
            self.wait_until_page_contains_text("Please wait...")
            self.wait_until_page_contains_text("SF-36v2 Standard")
            self.wait_until_element_clickable_css(sf36DiaryCss)
            #
            # now it's done, so log count and duration and do it all again
            #
            end_time = datetime.now()
            total_time = self.get_time_delta(start_time, end_time)
            log_line = "%s,%6.3f\n" % (total_count, total_time)
            logfile = open(log_file_name, "a")
            logfile.write(log_line)
            logfile.close()


    def submit_multiple_pmc_by_javascript(self, count):
        """This should submit a single pmc diary by javascript, but is NOT WORKING"""
        driver = get_current_browser()
        #
        # initialize js code for 'add' function
        #
        self.initialize_pmc_javascript_add_function()
        #
        # here's a call to the add function
        #
        add_script = "$(add)"
        #
        # now we call it multiple times
        #
        x = 0
        while(x < count):
            driver.execute_script(add_script)
            x = x + 1

    def add_pmc_diary_by_javascript(self):
        """Add a single pmc diary by js"""
        driver = get_current_browser()
        #logger.warn("starting add...")
        driver.execute_script("$(add)")
        self.reload_page()

    def initialize_pmc_javascript_add_function(self):
        """This method submits a PMC diary via javascript for speed when needed"""
        driver  = get_current_browser()
        script = """var add = function () {
     var subjects = new LF.Collection.Subjects();
     subjects.fetch({
           onSuccess : function () {
                var subject = subjects.at(0),
                     now = new Date(),
                     dashboards = new LF.Collection.Dashboards(),
                     dashboard = new LF.Model.Dashboard({
                           SU                         : 'Meds',
                    change_phase        : 'false',
                    completed           : now.ISOStamp(),
                    completed_tz_offset : now.getOffset(),
                    core_version           : LF.coreVersion,
                    device_id           : subject.get('device_id'),
                           diary_id             : parseInt(now.getTime().toString() + now.getMilliseconds().toString(), 10),
                    instance_ordinal    : 1, // This must be unique
                    phase                  : 30,
                    phaseStartDateTZOffset  : LF.Utilities.timeStamp(new Date()),
                    questionnaire_id    : 'Meds',
                    report_date         : LF.Utilities.convertToDate(new Date()),
                    started             : now.ISOStamp(),
                    study_version       : LF.StudyDesign.studyVersion,
                    subject_id          : subject.get('subject_id')
                });

            dashboard.save({}, {
                     onSuccess : function () {
                           var transmission = new LF.Model.Transmission({
                                     method  : 'transmitQuestionnaire',
                                     params  : dashboard.get('id').toString(),
                                     created : now.getTime()
                                }).save({}, {
                                     onSuccess : function () {
                                           var ans1 = {
                                                subject_id          : subject.get('subject_id'),
                                    response            : '0',
                                    SW_Alias            : 'Meds.0.MEDS_Q_1',
                                    instance_ordinal    : 1, //should be same as written in dashboard model
                                    questionnaire_id    : 'Meds',
                                    question_id         : 'MEDS_Q_1'
                                                },
                                                ans2 = {
                                                     subject_id          : subject.get('subject_id'),
                                        response            : '1',
                                        SW_Alias            : 'AFFIDAVIT',
                                        instance_ordinal    : 1,
                                        questionnaire_id    : 'Meds',
                                        question_id         : 'AFFIDAVIT'
                                                },
                                                model = new LF.Model.Answer(ans1).save({}, {
                                                     onSuccess : function () {
                                                           var model2 = new LF.Model.Answer(ans2).save();
                                                     }
                                                });
                                     }
                                });
                     }
            });

           }
     });
};"""
        #logger.warn("starting init script")
        driver.execute_script(script)
        #logger.warn("finished init script")
        #add_script = "$(add)"
        #driver.execute_script(add_script)
