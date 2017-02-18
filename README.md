<p align="center"><img src="src/main/resources/public/assets/gfx/logo.png" width="450px" /></p align="center">


# misakachan
misakachan is a modern manga reader that can be run on any computer with Java! 

Join us on Discord to see sneak peeks for upcoming features, give suggestions & feedback, or just hang out! 

I'm basically always online while I'm awake, and I try to respond to everyone who @mentions me or PMs me!

[<img src="https://discordapp.com/api/guilds/268574742032809985/widget.png?style=shield">](https://discord.gg/pMucKJ5)  

#### What is misakachan?

Reading manga on a smartphone or tablet nowadays is such a pleasure. There are so many great apps to choose from; just look at how much [tachiyomi](https://github.com/inorichi/tachiyomi) can do!  
Unfortunately, the same awesome browsing experience doesn't really exist for computers. The only programs available for Windows/Mac/Linux look and feel like they were designed 5+ years ago (mostly because, well, they were). Aside from having to use laggy, ad-ridden aggregate readers or resorting to clunky browser extensions, there aren't many options to choose from.  

misakachan hopes to close this gap and bring some of the great functionality available for Android/iOS to folks who also want to read manga on their computers!

## Features
- Runs in your web browser so you can resize & zoom as much as you like!
- Vertical (one long page) reading mode
- Search through supported sources for manga to read (currently limited to searching by title only)
- Loads full chapters in just a couple seconds
- Automatically find the scanlators for a manga and credit them if found
- One click to instantly download a chapter you're currently reading
- Download manager in search results page to download multiple chapters - it takes just 3 clicks to download an entire series!
- Many other features planned & in progress!

#### Supported Sources
- MangaHere.co
- KissManga.com
- more coming soon!


## Setup
1. Install **Java 8** if you don't already have it. **misakachan requires Java 8, and won't work properly with Java 7!** 
 - [Click here to go to the Java 8 JRE download page](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
2. Download the latest version from [the Releases page](https://github.com/edasaki/misakachan/releases)
3. If you have Java 8 installed correctly, you should be able to simply double click the JAR.
 - If that doesn't work for you, try opening Command Prompt (Windows), Terminal (Mac), or Bash (Linux).
 - Go to the directory that has `misakachan.jar` and type `java -jar misakachan.jar`.
 - If that still doesn't work, post an issue here or contact me on Discord and I'll try to help!
4. Click Launch Browser and you should see the misakachan main page! 
 - If Launch Browser doesn't work, just open your web browser and go to this URL: `http://127.0.0.1:10032/`. (It should almost always work, though.)

## Download
Just in case you missed it in "Setup" above lol.

Download the latest version of misakachan from [the Releases page](https://github.com/edasaki/misakachan/releases)

Of course you can also fork the repo and build from source.

## FAQs

### Is this ready for use?
I use a super relaxed version of [Semantic Versioning](http://semver.org/), which basically means I'm too lazy to stick to all those specific rules but misakachan's versioning will look vaguely like that.

*Basically, if misakachan is still on version 0.x.x, it's not technically ready for release and I can't make any guarantees on stability.*

Feel free to play around with it before v1.0.0 arrives though!

### How does this work?
    Please see "Setup" above for installation instructions! If you want details on the internal stuff, look through the code or see "Tech Stack" below.

### Can you add ____ source?

New sources are pretty painful to code, although they typically are similar to other sources. Post an issue here on GitHub and I'll do what I can. If you can code, making a new source is as simple as extending AbstractSource and filling in the methods. If you can write the code yourself, send a PR and I'll probably accept it. You can use the existing sources as reference for what each method is supposed to do; the project is not very well documented at the moment, unfortunately. 

### Can you add ____ feature?

Probably, if it's nothing too wild. The best way to make sure your suggestion isn't forgotten is to post an issue here on GitHub.

### Why Java?

I could make up some stuff about how Java is nice because it's crossplatform and stuff, but the real reason is that I just happen to be best at coding in Java lol. And UI design is a pain with most non-web languages (just my opinion), so having a nice library for creating a lightweight embedded webserver is super useful since you can make much prettier interfaces!

### Do you accept pull requests?
Not at the moment. The code is just way too disorganized at this stage, and I'm always refactoring things. I'll start accepting PRs when things are a bit more stable and cleaned up.

### How is the name "misakachan" stylized?
Just like that, lowercase m and no dash. I'm not really a fan of uppercase when it comes to romanizations, dunno why. Lowercase m looks cuter!

### Hey! This is mean to scanlators; people should always read off of scanlator's official websites. 
I think everyone acknowledges that scanlators do all the hard work when it comes to translating manga. I say this as a translator myself - I TL a couple Japanese web novels in my spare time at [Project Accelerator](http://project-accelerator.net/) and have worked on a few manga translations as well.

For the average reader, although scanlators' efforts are certainly appreciated, it's more convenient to use an aggregate site than to find the scanlator's official website and read from there. It's not ideal, but it's just the way the world works. Many, many people use aggregate sites exclusively (they wouldn't exist if it weren't a viable business model), but the majority of those sites don't give any credit to the scanlators. 

With that unfortunate truth in mind, misakachan has code written specifically to combine convenience with respect. Whenever you load up a manga in misakachan, misaka will try to find the manga on [MangaUpdates](https://www.mangaupdates.com/) to locate the scanlators who've done hard work translating that manga, and then credits them in the reader interface!

## Tech Stack

Here's a quick overview of the specific tools and technologies used in misakachan, for anyone interested:

 - **Java** - core stuff
 - **SparkJava** - embedded webserver
 - **HTML, CSS, JS/jQuery, SASS** - frontend design and AJAX to link to Java backend
 - **JSoup** - DOM fetching and parsing
 - **Selenium w/ PhantomJS** - mostly used for bypassing cloudflare and getting dynamically loaded content
 - **Google Truth** - assertion framework
 - **Maven** - compiling

## License

misakachan is available under whatever license is currently in the root directory (probably the MIT license).