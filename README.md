# misakachan
misakachan is a modern manga reader that can be run on any computer with Java! 

It's 2017, and there still isn't a nice, updated manga reader for folks who read manga on their computers. We're left using programs that look and feel like they were designed 5+ years ago (mostly because, well, they were) or just browsing laggy aggregate sites who use ugly and clunky readers to maximize their ad revenue.

Mobile apps are way better than anything available for computer users, but misakachan hopes to close that gap!

## Features
- Runs in your web browser so you can resize & zoom as much as you like!

### Planned Features
- Automatically loads full chapters in
- Vertical (one long page) & horizontal (page by page) reading modes
- One click to download loaded chapters to disk for future reading
- Track reading progress and automatically check for new chapters
- Load manga downloaded outside of misakachan (.zip, .rar, etc.)
- Attempt to automatically find the scanlators for a manga (and credit them if found)

## Supported Sources
- MangaHere.co
- more coming soon!


## Setup
1. Install **Java 8** if you don't already have it. **misakachan requires Java 8, and won't work properly with Java 7!** 
- [Click here to go to the Java 8 JRE download page](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
2. Download the JAR [no link at the moment - will add link to JAR when v1.0.0 is released]
3. On most systems, just double click the JAR. You should see a launcher. Press "Launch Browser" and you should see misakachan load up!
4. On some computers, you may not be able to run the JAR by double clicking it. Open up Command Prompt / Terminal / Bash instead, navigate to the directory that you have `misakachan.jar` saved in, and type `java -jar misakachan.jar`.
5. Need help? Post an issue here on Github and I'll respond as soon as I can! 


## FAQs

### Hey! This is mean to scanlators.
I think everyone acknowledges that scanlators do all the hard work when it comes to translating manga. I say this as a translator myself - I TL a couple Japanese web novels in my spare time at [Project Accelerator](http://project-accelerator.net/) and have worked on a few manga translations as well.

For the average reader, although scanlators' efforts are certainly appreciated, it's more convenient to use an aggregate site than have to find the scanlator's official website and read from there. It's not ideal, but it's just the way the world works. Many, many people use aggregate sites exclusively (they wouldn't exist if it weren't a viable business model), but the majority of those sites don't give any credit to the scanlators. 

With that unfortunate truth in mind, misakachan has code written specifically to combine convenience with respect. Whenever you load up a manga in misakachan, misaka will try to find the manga's title. If that's found, it'll then use [MangaUpdates](https://www.mangaupdates.com/) to locate the scanlators who've done hard work translating that manga and credits them in the reader interface.