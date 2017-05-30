<p align="center"><img src="src/assets/gfx/mofumoe.png" width="450px" /></p align="center">

## Notice - PLEASE READ ULTRA IMPORTANT!!!

mofu (previously known as misakachan) is currently being rewritten with a completely new codebase (Electron/Typescript/React). This means that **most features are currently broken**.

Versions 0.1.x are on the new codebase, and 0.0.x are the old one (Java core).

There is a working Java JAR file release on the releases page, but it will not be supported in the future. Consider it a sneak peek :)

**Until this notice disappears, I can't guarantee the accuracy of the stuff below!**

**In particular, the setup/installation instructions and features are almost CERTAINLY outdated and inaccurate.**

Once I've finished rewriting most of the features, this notice will disappear :)

## Notice #2

I often get caught up with other projects (I tend to be doing a lot of things at once), so if this seems inactive, it's probably only *kinda* inactive. I very rarely drop a project entirely, and when I do it'll be absolutely clear that I've stopped working on it. So even if mofu hasn't been updated in a while, expect it to be updated again at some point :)

---

# mofu

mofu is a modern manga reader that can be run on any computer with Java! 

Join us on Discord to see sneak peeks for upcoming features, give suggestions & feedback, or just hang out! 

I'm basically always online while I'm awake, and I try to respond to everyone who @mentions me or PMs me!

[<img src="https://discordapp.com/api/guilds/268574742032809985/widget.png?style=shield">](https://discord.gg/pMucKJ5)  

#### What is mofu?

**tl;dr mofu is a modern manga reader for desktop computers. Features (completed & planned) include tracking reading progress, notifications on new chapters, reader appearance settings, convenient hotkeys, customizable color themes, and much more!**

Reading manga on a smartphone or tablet nowadays is such a pleasure. There are so many great apps to choose from; just look at how much [tachiyomi](https://github.com/inorichi/tachiyomi) can do!  

Unfortunately, the same awesome browsing experience doesn't really exist (yet!) for computers. And that's pretty weird, since normally you'd think that desktops should be able to do everything that phones can, and more.

For whatever reason, the only programs available for Windows/Mac/Linux look and feel like they were designed 5+ years ago (because, well, many of them were). Most of them aren't even for manga, they're just kind of generic comic book readers that load in folders of images. Aside from having to use laggy, ad-ridden aggregate readers or resorting to clunky browser extensions, there aren't many options to choose from.

The goal of mofu is to close the gap and bring an awesome manga reading experience to desktops and laptops!

#### Got any screenshots?

Yup! [Just go to the pics directory or click here :)](./pics)

### Table of Contents

- [Features](#features)
 - [Supported Sources](#supported-sources)
- [Setup Instructions](#setup-instructions)
- [Download](#download)
- [FAQs](#faqs)
- [Tech Stack](#tech-stack)
- [License](#license)

## Features
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


## Setup Instructions
> **Important!** You may be asked for permissions to run PhantomJS. I know it sounds super sketchy, but I promise it isn't haha.  
> PhantomJS is this pretty large & reputable project: http://phantomjs.org. It's basically a browser that doesn't have any UI so it runs invisibly.  
> It's used in misakachan to get past pesky Cloudflare blocks (those "pls wait 5 seconds" things like on KissManga.com) without spamming you with new browser windows.  
> If you want to make sure it's not doing anything shady (better safe than sorry!), the relevant code is in `WebAccessor.java`.

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

Download the latest version of mofu from [the Releases page](https://github.com/edasaki/mofu/releases)

Of course you can also fork the repo and build from source.

## FAQs

### What's PhantomJS? A virus?

Please see "Setup Instructions" (relevant section copied here).

> **Important!** You may be asked for permissions to run PhantomJS. I know it sounds super sketchy, but I promise it isn't haha.  
> PhantomJS is this pretty large & reputable project: http://phantomjs.org. It's basically a browser that doesn't have any UI so it runs invisibly.  
> It's used in misakachan to get past pesky Cloudflare blocks (those "pls wait 5 seconds" things like on KissManga.com) without spamming you with new browser windows.  
> If you want to make sure it's not doing anything shady (better safe than sorry!), the relevant code is in `WebAccessor.java`.

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

### Do you accept pull requests?
Yes! But in the current stage the code is not very well structured and changes frequently, so please keep that in mind.

### Hey! This is mean to scanlators; people should always read off of scanlator's official websites. 
Everyone acknowledges that scanlators do all the hard work when it comes to translating manga. I say this as a translator myself - I (very infrequently) TL a couple Japanese web novels in my spare time at [Project Accelerator](http://project-accelerator.net/) and have worked on a few manga translations as well.

For the average reader, although scanlators' efforts are certainly appreciated, it's more convenient to use an aggregate site than to find the scanlator's official website and read from there. It's not ideal, but it's just the way the world works. Many, many people use aggregate sites exclusively (they wouldn't exist if it weren't a viable business model), but the majority of those sites don't give any credit to the scanlators. 

With that unfortunate truth in mind, mofu has code written specifically to combine convenience with proper credit. Whenever you load up a manga in mofu, mofu will try to find the manga on [MangaUpdates](https://www.mangaupdates.com/) to locate the scanlators who've done hard work translating that manga, and then credits them in the reader interface, along with a link to their site if one can be found.

## Tech Stack

Here's a quick overview of the specific tools and technologies used in misakachan, for anyone interested:

 - **Electron** - desktop app framework
 - **Typescript** - implementation language
 - **React** - UI stuff
 - **cheerio** - DOM parsing
 - **Javascript & jQuery** - sprinkled in here and there
 - **SASS** - CSS preprocessing
 - **PouchDB** - local data storage
 - **PhantomJS** - mostly used for bypassing cloudflare and getting dynamically loaded content

## License

mofu is available under [whatever license is currently in the root directory (probably the MIT license)](LICENSE).
