# 0.1.14 - May 5 2017
- huge refactor in progress

# 0.1.13 - May 5 2017
- added chapter page loading - accessor.get needs optimizing, it's ultra slow right now haha
- added scrollbar to manga info panel

# 0.1.12 - May 3 2017
- updated npm dependencies
- manga info & chapter listing now works - a lot of stuff was added to make it work but I won't go into too much detail

# 0.1.11 - May 1 2017
- fixed a bug with unregistering key listeners
- added functionality to search bar on the search results page - it works now
- info popup now appears on clicking a manga title
- clicking outside of the popup closes it (surprisingly hard to do with React...)
- info and download popups are now visible by default (since they aren't on DOM at all unless needed, thanks to React)

# 0.1.10 - May 1 2017
- abstracted source names and listings to separate properties/components
- search results page now shows the original query at the time
- image loading for series covers now works! runs in the background using hidden electron windows

# 0.1.9 - Apr 30 2017
- added cheerio dependency for DOM parsing
- search callback now takes SearchResult[]
- MangaHere searching works (returns a Promise<SearchResult[]>)
- web accessor now runs entirely in the background with validator to check for completion
- change in body state now passes search results if new state is results page
- fixed default display/visibility on manga info/download popups
- added explicit typing declarations to tsconfig (maybe not necessary)
- added string-key dictionary; currently unused, turns out you can't iterate it very easily so it's not all that useful...
- added search result listing component (currently combines all listings from one source into a single component, may need to eventually split further)

# 0.1.8 - Apr 29 2017
- forced scrollbars to have no style for now (might use custom styling in the future since electron is webkit)
- got MangaHere backend searching working! (backend as in, it doesn't display anything yet)
- searching from home now goes to loading page, then search results
- tsc now targets es2015
- added accessor to fetch html using a hidden electron window
- added manga series abstract

# 0.1.7 - Apr 29 2017
- tweaked tracker styling to remove the ugly border around it
- fixed sourcemaps
- added debug launch task for vscode (requires chrome debugger extension)
- tracker listing modularized more and clicking to toggle fav/alert now works
- added pouchDB dependency for local db

# 0.1.6 - Apr 28 2017
- dev tools no longer open on launch
- redesigned home page - tracker now is automatically open and shows directly below the search button
- Enter and Tab now work on the home page to search and focus the search box, respectively
- started a bit of work on basic backend structure
- abstracted tracker and trackeritem into separate components

# 0.1.5 - Apr 25 2017
- loading panel is now unselectable as intended
- added new logo to title bar
- made all main containers visible by default - we're using react now so they aren't rendered together
- unmaximizing (whatever thats called) now works properly
- add zoom in/out on title bar

# 0.1.4 - Apr 24 2017
- added Home component (incomplete)

# 0.1.3 - Apr 24 2017
- after almost a full day, I FINALLY figured out how to import electron lmao

# 0.1.2 - Apr 24 2017
- various small fixes, still working on environment setup

# 0.1.1 - Apr 24 2017
- stuff seems to mostly work now? idk lol at least there don't appear to be any warnings/errors

# 0.1.0 - Apr 23 2017
- began complete rewrite of misakachan (now known as mofu)
- new tech stack is Electron+Typescript+React+Webpack+SASS+bit of jquery/js
- currently non-functional - if you want something to play around with, use the old jars under releases; I'll be working on getting this new version to the same level of functionality
- converted changelog to markdown

-----
-----

## 0.0.51 - Mar 19 2017
- added confirmation dialogue to phantomjs popup

## 0.0.50 - Mar 16 2017
- It's been a while! I've been ultra busy with exams (quarter system so finals start in March lol), but I'm all done now!
- add small notice about PhantomJS on launcher (so people don't get spooked...)
- added milliseconds to console output
- fixed up pom.xml source specification

## 0.0.49 - Feb 26 2017
- added LireScan source (French - credits to Yassine Farich @ https://github.com/yassinefarich)
- added BleachMx source (French - credits to Yassine Farich @ https://github.com/yassinefarich)

## 0.0.48 - Feb 25 2017
- added prototype of tracker interface
- misakachan now minimizes to tray (will make this optional in the future)
- added prototype for tray notifications (alerting for new chapters)

## 0.0.47 - Feb 25 2017
- Finished implementing most of image cache system for cover pages (still need to implement it for pages/downloading)
- manga info panel now displays cached image as well

## 0.0.46 - Feb 24 2017
- increased jsoup timeout to 10 seconds
- did a lot of refactoring - all routes are now classes
- still need to work on refactoring more - project is getting messy and i'm struggling to figure out how to add history/tracking b/c it's so messy lol 

## 0.0.45 - Feb 21 2017
- added new caching system (incomplete, only a prototype atm)

## 0.0.44 - Feb 18 2017
- fixed another null pointer issue
- wrote a ton of code to try to get past a cookie issue, only to get cheesed by same-origin policy (always forget it exists lol)
- since it was a lot of code (maybe stuff worth reusing in the future?), I'll push this commit now and restart in the next commit

## 0.0.43 - Feb 18 2017
- info & download panels now work much better on unusual window sizes
- manga page url conversion now takes collections instead of list
- library saving now detects "chapter" in chapter names and avoids folder names like "Chapter Ch.83"
- finished KissManga source!
- search request now checks for null body
- Artist label hides if Artist isn't found

## 0.0.42 - Feb 18 2017
- temporarily downgraded Jsoup dependency due to bug: https://github.com/jhy/jsoup/pull/831
- fixed NPE in Series
- added better error handling for missing Series fields
- added better error handling for failed lookups

## 0.0.41 - Feb 18 2017
- added MIT license to project
- readme updates
 
## 0.0.40 - Feb 17 2017
- realized that I only need to use phantomjs to get the cookies, not actually access pages afterwards lol
- greatly improved WebAccessor - now loads most things pretty quickly, especially after it finishes preloading
- finished kissmanga search results! Halfway there :D Once I finish KissManga I can wrap up some other features, I just wanted to make sure I had at least two sources working.

## 0.0.39 - Feb 16 2017
- finished first version of cloudflare bypass - fully functional, but it's not threadsafe and is blocking so it ain't that great :(
- cloudflare bypass now preloads on a separate thread
- added WebAccessor preloading - usually takes ~8 seconds on my computer, could be slower on worse internet connection so I'll work on improving it
- basically sites that use Cloudflare (like KissManga) make my life a lot harder lol

## 0.0.38 - Feb 16 2017
- added solo tag to test classes to run just that class easily
- mostly finished cloudflare bypass, bit stuck on a weird bug
- super-mini-patch ## 0.0.38.1: fixed the bug I think! Seems to work now :)

## 0.0.37 - Feb 13 2017
- fixed bug with non-integer chapter numberings
- whipped up a quick auto pom updater that changes the build version to match what's in this file

## 0.0.36 - Feb 13 2017
- finished first fully functional prototype of manga downloading!

## 0.0.35 - Feb 12 2017
- added scrollbar to chapter download panel
- finished backend reading the urls from download panel - just need to wrap up the code to actually download a chapter directly from URL now!

## 0.0.34 - Feb 11 2017
- chapters now show up properly in the new chapter download panel
- seems to be a bit laggy with lots of chapters (for example ika musume's 400+ chapters causes some lag) - will try to fix asap

## 0.0.33 - Feb 11 2017
- added explicit jetty dependencies to prevent dependency conflicts
- finally figured out how to set listeners for dynamically created elements (I am a jQuery noob hehe)
- on a side note, scripts.js is extremely messy right now - rest assured it'll be refactored at some point!
- made a lot of progress on download UI - basically done, now just need to do the program stuff (loading in chapters into the panel and downloading)

## 0.0.32 - Feb 10 2017
- Added ExperimentalTests for playing around with code
- added dependencies: Selenium and bonigarcia's web driver manager (for automated management of PhantomJS drivers on any OS)
- added working experimental code for bypassing kissmanga cloudflare protection with headless phantomjs browser thru selenium
- in layman's terms, basically on some websites you see those cloudflare "Please wait 5 seconds while we verify your browser" etc. etc. messages. Those cause some pretty difficult issues for bots like misakachan (well, that's kind of the point I guess). I managed to write a workaround that gets past that by imitating an actual browser, so now we should have no problems retrieving sites like KissManga, etc!

## 0.0.31 - Feb 8 2017
- made changelog fetching async and preloaded
- started working on download interface UI

## 0.0.30 - Feb 7 2017
- added some more debugging output options so I'm not spamming myself with debug messages lol
- improved code testing suite to be a lot nicer to use
- removed extra copy of manga code (forgot about it)
- started working on kissmanga source

## 0.0.29 - Feb 6 2017
- added download button to individual chapters in chapter listing
- cleaned up some code

## 0.0.28 - Feb 6 2017
- On midterm week right now so don't have as much time to code new stuff :P
- finished implementing single-click download on reader interface - still working on adding other download options
- added simple timer utility for debugging
- fixed multithreading issue with document cache initialization
- implemented multithreading for bakabt search - performs significantly faster (30-50%) for long results (5+ pages), about the same for short results (1-2 pages).

## 0.0.27 - Feb 3 2017
- fixed clearing pages on new chapter
- started work on chapter downloading (almost done with it I think, just a couple last bugs to iron out)

## 0.0.26 - Feb 2 2017
- top/bottom arrows are now slightly lower and sticky. the code to make them sticky but not overflow into the footer is actually pretty wacky, may try to improve in the future altho it seems to work quite well (surprisingly hard to make it not have weird snaps)
- added a super basic light theme (for personal use only at the moment - official builds will only have the dark theme until I add a theme switcher)
- added a download button (doesn't do anything yet, just sits there looking cute)
- fixed a bug with the above padded fixed divs (need to account for children for whatever reason) - now works super smoothly for both the arrows and the download button!

## 0.0.25 - Feb 2 2017
- added auto search result sorting by similarity to search term
- refactored manga info formatting (pluralization, etc.) from backend to frontend
- fixed issue with chapter overlay coming up over the image if there is no description using a micro clearfix
- fixed resizing issue on manga info display
- shifted manga display up a bit
- tweaked padding a bit on different parts of the info display

## 0.0.24 - Feb 2 2017
- fixed bug: search results not clearing
- fixed bug: generating unique ids for search result images

## 0.0.23 - Feb 1 2017
- made manga titles auto-resize
- added scrollbar for long manga descriptions
- implemented search bar on search results page

## 0.0.22 - Feb 1 2017
- implemented document caching for faster loading (5 minute cache)
- added new logo!
- refactored some stuff

## 0.0.21 - Jan 30 2017
- mostly wrapped up scanlator group stuff, will add it to info panel in next patch
- added images for each manga to search results - another surprisingly tricky to code thing, because it turns out some/most sites don't show images on the search results page so I have to fetch the image from each manga page, which needs to be multithreaded..
- fixed mangahere descriptions to pull the full description

## 0.0.20 - Jan 29 2017
- finished json serialization for Series objects
- mostly wrapped up manga popup display! just need to finish chapters and a bit of formatting stuff

## 0.0.19 - Jan 29 2017
- added automatic update checking - surprisingly difficult to code thanks to my laziness - I didn't want to have to remember to update a variable somewhere every build, so instead I made it so this changelog is automatically included in the compiled JAR. But I didn't really know how to do that so it took a while to figure out the right maven config. And then I had to make sure that I can run it locally still, while also having it able to get the resource stream from the jar. 
- fixed a typo with version initialization
- added version string display
- added message queueing to console to be able to ready messages before the GUI is loaded
- finished styling for manga info popup, working on the ajax stuff now

## 0.0.18 - Jan 27 2017
- added css class for data stored in hidden DOM content
- made the results count at the top of search results page work
- started work on manga info popup for search results page
- disabled TestSource for now

## 0.0.17 - Jan 27 2017
- lots of work on search UI, it's almost done! searching properly displays results now!

## 0.0.16 - Jan 26 2017
- more styling changes
- changelog is now toggle-to-view (hidden by default)
- added fancy scrollbar and styling to changelog. surprisingly hard to style lol, had to use some js library
- more work on AJAX stuff for manga searching
- started work on search result UI

## 0.0.15 - Jan 25 2017
- added some multithreading functionality
- started work on general search function, only have a prototype ready, i'm still working out how it should work for the user
- added some tests
- added Discord chat link

## 0.0.14 - Jan 23 2017
- added button to go to page top/bottom
- fixed search bar centering, it was slightly off
- added a cute loading page for when things are being downloaded from websites
- added automatic changelog fetching and display

## 0.0.13 - Jan 22 2017
- worked on some more styling
- added my own version of momentum scrolling on WASD/Arrow Keys - actually was a huge pain to code haha
- multithreaded page crawling - HUGE PERFORMANCE IMPROVEMENT!!!!!! I was super surprised at how much faster it got! A 36 page chapter of Railgun from MangaHere took ~13 seconds to load before; multithreaded, it loads all 36 pages in under a second!

## 0.0.12 - Jan 22 2017
- added auto SASS formatting builder to eclipse project - it doesn't run on auto build b/c it does some weird stuff if it does
- converted old style.css to SASS - first time using SASS, pretty cool stuff
- removed source mapping for SASS - not doing anything complex enough to need it lol
- fixed extra padding at bottom of image divs - was an issue with vertical-align
- switched to more modularized var naming for scss _colors
- as a side note, until release I will only be using the "PATCH" part of semantic versioning. No point doing "MINOR" patches when misaka isn't ready yet anyways and I'm working on this solo :P

## 0.0.11 - Jan 20 2017
- recoded BakaUpdateSearcher to search directly within the site instead of using google
- so far it seems to search with pretty great accuracy - first checks for direct phrase matches, and then compares between those by finding the shortest edit distance

## 0.0.10 - Jan 19 2017
- updated readme.md
- coded some stuff to search for manga on baka-updates
- currently prone to rate-limiting b/c it's parsing google. 
-- Will consider recoding to use baka-updates search interface (but will need to think about how to determine which link is the right one). Tired now though so I'm going to sleep lol

## 0.0.9 - Jan 19 2017
- added stuff for using SASS for CSS
- auto transpiler builder included for Eclipse - automatically updates generated CSS on auto builds
- excluded sass files from JAR builds

## 0.0.8 - Jan 14 2017
- made a persistent file framework to make it easier to code up stuff that needs to be saved in the future (images, options/preferences, history, etc.)
- some more CSS work
- added some convenience methods
- excluded local persistence folder (/misaka/) from git

## 0.0.7 - Jan 11 2017
- worked on js stuff, now loads in data from webserver
- added key captures for up/down/left/right nav (scroll up/down & next/prev chapter)
- CSS work on reader

## 0.0.6 - Jan 10 2017
- changed up logging - don't really need super fancy logging
- begin work on reader interface
- simplified AbstractSource type, adjusted existing implementations (well, there's just one) accordingly
- added test source - will make it excluded from production builds in future patch

## 0.0.5 - Jan 9 2017
- added fileutils to load files from within jar as resource streams (stored as temp files)
- added font manager for GUI prettiness
- did a lot of GUI work, improved GUI design/color scheme

## 0.0.4 - Jan 9 2017
- modularized code a bit
- added rudimentary versioning system
- started working on main GUI a bit
- added console output system to GUI

## 0.0.3 - Jan 8 2017
- changed patch numbering to start with major patch 0 (gonna switch to 1.x.x on general release to public)
- added basic testing for making .jar clickable with JFrame
- added cute misaka icon to JFrame
- restructured directories for tests

## 0.0.2 - Jan 7 2017
- now only show DEBUG level messages from within misakachan
- added source names
- added basic styling and layout for index page
- added org.json dependency
- removed templates/ and Thymeleaf dependency - probably won't need it
- started work on AJAX queries to load pages
- added favicon and temporary logo

# 0.0.1 - Jan 7 2017
- added (this) changelog
- added Thymeleaf dependency for HTML rendering