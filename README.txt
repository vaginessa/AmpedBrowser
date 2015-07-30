Amped Browser
================================================================================================================================
This program is designed to be a quick and simple browser with no bloat while looking beautiful. There will be no features other
than basic web browsing. This allows for a quick no frills browser that anyone can enjoy. I wanted to make a trimmed down browser
that will work on slow connections and provide smooth scrolling and zooming unlike most modern (and heavily bloated) mobile 
browsers. I aimed to do this with a beautiful design. No tabs, no bloat; mobile browsing made simple.

Development priority is as follows:
  - Stability/Security
  - Performance
  - Design
  - Features
  
Settings Options
  - Set homepage +
  - Theme color
  - Save data (don't load images) +
  - Private browsing - deletes all browser data when app session ends +
  - Clear all browsing data on exit +
  - Send feedback +
  - About
  
Future Features
  - Find in page (use findAllAsync and findNext(true))
  - Additional font sizes
  

Known Issues
================================================================================================================================
  - [FIXED] Back button does not go back in WebView
  - No clear text functionality to empty EditText field
  - [FIXED] No settings menu
  - [FIXED] Cannot use Amped Browser as default browser
  - [Fixed] Layout is not perfect
  
Changelog
================================================================================================================================
Version 0.1.0 (beta) 7/22/2015
  - Basic browser functionality (http only)
  - [Added] Simple navigation drawer layout using Google's design elements
  - [Added] Custom list adapter to show icons in navigation drawer
  
Version 0.2.0 (beta) 7/23/2015
  - Can open links from other apps and can be set as default browser
  - Improved URL handling
  - Updated layout
  - Displays current URL in address bar
  - Text is highlighted when address bar is pressed for easy deletion
  - Handles javascript, link redirects, and https smoothly
  - Handles downloads
  
Version 1.0 Release 7/25/2015
  - URL handling should now work seamlessly
  - Implemented private mode
  - Theme colors change upon private mode being enabled
  - Implemented settings menu
  
Version 1.1 Release 7/26/2015
  - Fixed "Save Data" option
  - Fixed "Rate App" option being an empty link
  - Improved swipe to refresh functionality
  
Version 1.2 Release 7/29/2015
  - Added the ability to share links
  - Added the ability to choose a search engine (more can be added upon request)
  - Greatly improved actionbar behavior and scrolling
  - Bug fixes
