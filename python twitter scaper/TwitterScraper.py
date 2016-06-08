from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions
import json
from datetime import date, timedelta
import sys

COMPANIES = ['3M', 'AmericanExpress', 'AppStore', 'Boeing', 'CaterpillarInc', 'Chevron', 'Cisco', 'CocaCola', 'Disney', 'DuPont_News',
             'exxonmobil', 'generalelectric', 'GoldmanSachs', 'HomeDepot', 'IBM', 'Intel', 'JNJNews', 'J.P. Morgan', 'McDonalds', 'Merck',
             'Microsoft', 'Nike', 'pfizer', 'ProcterGamble', 'Travelers', 'UTC', 'UnitedHealthGrp', 'verizon', 'Visa', 'Walmart']

def twitterScraper(keyword, cur_date):
  tweets = []
  one_day = timedelta(days = 1)
  browser = webdriver.Chrome()
  url = 'https://twitter.com/search?q=%40' + keyword + '%20since%3A' + cur_date.isoformat() + '%20until%3A' + (cur_date + one_day).isoformat() + '&src=typd'
  browser.get(url)

  try:
    back_to_top = browser.find_element_by_class_name('Icon--logo')
    while not back_to_top.is_displayed():
      browser.execute_script("window.scrollTo(0, document.body.scrollHeight)")
      #browser.implicitly_wait(2)
    #WebDriverWait(browser, 10).until(expected_conditions.visibility_of(back_to_top))
    if browser.execute_script("document.body.scrollHeight") < 800:
      browser.implicitly_wait(2)
    elements = browser.find_elements_by_class_name('tweet')
    for element in elements:
      #users = element.find_elements_by_class_name('fullname')
      dates = element.find_elements_by_class_name('_timestamp')
      texts = element.find_elements_by_class_name('tweet-text')
      favs_button = element.find_elements_by_class_name('js-actionFavorite')
      retwt_button = element.find_elements_by_class_name('js-actionRetweet')
      if len(dates) > 0 and len(texts) > 0 and len(favs_button) > 0 and len(retwt_button) > 0:
        #user = users[0].text.encode('utf-8')
        #id = element.get_attribute('data-item-id')
        time = dates[0].get_attribute('data-time')
        text = texts[0].text.encode('utf-8')
        fav = favs_button[0].find_elements_by_class_name('ProfileTweet-actionCountForPresentation')[0].text.encode('utf-8')
        if fav == '':
          fav = '0'
        retwt = retwt_button[0].find_elements_by_class_name('ProfileTweet-actionCountForPresentation')[0].text.encode('utf-8')
        if retwt == '':
          retwt = '0'
      tweet = {'company': keyword, 'date': time, 'text': text, 'favorite_count': fav, 'retweet_count': retwt}
      tweet_date = date.fromtimestamp(float(time)).isoformat()
      #print str(tweet_date) + ', ' + str(cur_date) + ', ' + str(tweet_date == cur_date)
      #print cur_date
      #print tweet
      if str(tweet_date) == str(cur_date):
        tweets.append(tweet)
  except:
    with open('empty_response.json', mode = 'r') as f:
      empties = json.load(f)
    with open('empty_response.json', mode = 'w') as f:
      empties.append({'date': cur_date.isoformat(), 'company': keyword})
      json.dump(empties, f)
    print sys.exc_info()[0]
  finally:
    browser.quit()
    print tweets
    if tweets == []:
      empties = []
      with open('empty_response.json', mode = 'r') as f:
        empties = json.load(f)
      with open('empty_response.json', mode = 'w') as f:
        e = {'date': cur_date.strftime('%Y-%m-%d'), 'company': keyword}
        empties.append({'date': cur_date.isoformat(), 'company': keyword})
        json.dump(empties, f)
    feeds = []
    with open(keyword + '.json', 'r') as infile:
      feeds = json.load(infile)
    with open(keyword + '.json', 'w') as outfile:
      feeds.extend(tweets)
      json.dump(feeds, outfile)

for company in COMPANIES:
  with open(company + '.json', mode = 'w') as f:
    json.dump([], f)

with open('empty_response.json', mode = 'w') as f:
  json.dump([], f)

start_date = date.today()
end_date = date(2010, 1, 1)
one_day = timedelta(days = 1)

while start_date > end_date:
  for company in COMPANIES:
    twitterScraper(company, start_date)
  start_date = start_date - one_day
