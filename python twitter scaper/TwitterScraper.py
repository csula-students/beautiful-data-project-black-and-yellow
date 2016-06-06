from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions
import json
from datetime import date, timedelta

COMPANIES = ['3M', 'AmericanExpress', 'AppStore', 'Boeing', 'CaterpillarInc', 'Chevron', 'Cisco', 'CocaCola', 'Disney', 'DuPont_News',
             'exxonmobil', 'generalelectric', 'GoldmanSachs', 'HomeDepot', 'IBM', 'Intel', 'JNJNews', 'J.P. Morgan', 'McDonalds', 'Merck',
             'Microsoft', 'Nike', 'pfizer', 'ProcterGamble', 'Travelers', 'UTC', 'UnitedHealthGrp', 'verizon', 'Visa', 'Walmart']

def twitterScraper(keyword, cur_date):
  tweets = []
  one_day = timedelta(days = 1)
  browser = webdriver.Firefox()
  url = 'https://twitter.com/search?q=%40' + keyword + '%20since%3A' + cur_date.isoformat() + '%20until%3A' + (cur_date + one_day).isoformat() + '&src=typd'
  browser.get(url)

  try:
    back_to_top = browser.find_element_by_class_name('back-to-top')
    while not back_to_top.is_displayed():
      browser.execute_script("window.scrollTo(0, document.body.scrollHeight)")
      browser.implicitly_wait(2)
    WebDriverWait(browser, 60).until(expected_conditions.visibility_of(back_to_top))
    elements = browser.find_elements_by_class_name('tweet')
    for element in elements:
      users = element.find_elements_by_class_name('fullname')
      dates = element.find_elements_by_class_name('_timestamp')
      texts = element.find_elements_by_class_name('tweet-text')
      if len(users) > 0 and len(dates) > 0 and len(texts) > 0:
        user = users[0].text.encode('utf-8')
        id = element.get_attribute('data-item-id')
        time = dates[0].get_attribute('data-time').isoformat()
        text = texts[0].text.encode('utf-8')
      tweet = {'user': user, 'id': id, 'date': time, 'text': text}
      tweet_date = date.fromtimestamp(float(dates[0].get_attribute('data-time')))
      if tweet_date == cur_date:
        tweets.append(tweet)
  finally:
    browser.quit()
    print tweets
    feeds = []
    with open(keyword + '.json', 'r') as infile:
      feeds = json.load(infile)
    with open(keyword + '.json', 'w') as outfile:
      feeds.extend(tweets)
      json.dump(feeds, outfile)

for company in COMPANIES:
  with open(company + '.json', mode = 'w') as f:
    json.dump([], f)

start_date = date.today();
end_date = date(2010, 1, 1)
one_day = timedelta(days = 1)

while start_date > end_date:
  for company in COMPANIES:
    twitterScraper(company, start_date)
    start_date = start_date - one_day
