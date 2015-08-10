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
  - Theme color +
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

Version 1.3 Release 8/5/2015
  - Proper hyperlink/image handling
  - Adjusted settings screen
  - Option to disable JavaScript for improved security and basic adblocking
  - Multiple window support
  - Theme color options
  - Fullscreen option

Version 1.4 Release 8/09/2015
  - NEW: Add bookmarks from the overflow menu! Swipe in from the right to view saved bookmarks. Long press a bookmark to delete it.
  - NEW: Option to define a custom user agent
  - NEW: Automatically set to not load images or JavaScript if on 2G or worse to save valuable bandwidth
  - FIXED: Request desktop site did not work for many websites
  - FIXED: Reduced the sensitivity of the swipe to refresh action to prevent accidental page refreshes
  - Bug fixes and improvements

Version 1.4.1 Maintenance 8/10/2015
  - FIXED: Bookmark related bug
  - Updated "About" section to include a changelog

Version 1.5 Release
  - NEW: Find in page support
  - NEW: Geolocation support for websites that want to access your location (can be turned off)
  - NEW: Keep screen from turning off
  - Bug fixes and other improvements
