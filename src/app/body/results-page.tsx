import React from 'react';
import SearchResult from '../backend/search/search-result'
import SearchManager from '../backend/search/search-manager';
import SourceResult from '../backend/search/source-result'
import SearchResultListing from './search/search-res-listing'
import { BodyState } from '../body'
const autobind = require('react-autobind')

interface Props {
    results: SourceResult[],
    lastSearch: string,
    callback: (state: BodyState) => void,
    lastSearchCallback
}

interface State {
    searchPhrase: string,
    showInfo: boolean,
    showDownload: boolean,
    mangaInfo?: MangaInfo
}

interface MangaInfo {
    image: string,
    title: string,
    author: string,
    description: string,
    source: string,
    chapterCount: number,
    chapters: Chapter[]
    alt?: string,
    genres?: string,
    artist?: string
}

interface Chapter {
    name: string,
    url: string
}

let testMangaInfo: MangaInfo = {
    image: "http://edasaki.com/i/test-page.png",
    source: "MangaTest",
    title: "Mockup Really Super Duper Long Title Just A Little Longer",
    author: "Edasaki",
    description: "A really cool thing about stuff. Super exciting stuff. Really great. Super great. Just the greatest. I'm just writing random stuff to try to push this to two lines so I can see how it looks lol. And now I'm just gonna write a whole bunchhhhhh of random stuff to push it beyond the boundary and see how wacky it looks after that. Because by default I think it kinda screwws up the formatting, but I want to make it resize the text automatically.",
    chapterCount: 10,
    chapters: [
        { name: "Chapter 1", url: "#" },
        { name: "Chapter 2", url: "#" },
        { name: "Chapter 3", url: "#" },
        { name: "Chapter 4", url: "#" },
        { name: "Chapter 5", url: "#" },
        { name: "Chapter 6", url: "#" },
        { name: "Chapter 7", url: "#" },
        { name: "Chapter 8", url: "#" },
        { name: "Chapter 9", url: "#" },
        { name: "Chapter 10", url: "#" }
    ],
    genres: "Fun, Cool, Happy",
    alt: "Test Alt Title",
    artist: "Art Vandelay"
}

export default class SearchResultsPage extends React.Component<Props, State> {

    constructor(props) {
        super(props)
        autobind(this)
        this.state = { showDownload: false, showInfo: false, searchPhrase: "" }
    }

    references: { input: HTMLInputElement, container: HTMLDivElement } = {
        input: null,
        container: null
    }

    loadURL(url: string): void {
        this.setState({ showInfo: true, mangaInfo: testMangaInfo })
    }

    checkCloseFull(e) {
        if (this.references.container) {
            let res = this.references.container.contains(e.target);
            if (!res) {
                this.closeFull()
            }
        }
    }

    closeFull() {
        this.setState({ showInfo: false, showDownload: false })
    }

    public render() {
        let sources = []
        let counter = 0
        this.props.results.forEach(source => {
            sources.push(
                <SearchResultListing callback={this.loadURL} results={source.results} sourceName={source.sourceName} key={++counter} />
            )
        });
        let infoContainer = undefined

        if (this.state.showInfo && this.state.mangaInfo) {

            let info = this.state.mangaInfo
            infoContainer = (
                <div id="manga-info-full-page-container" className="manga-info-full-page-container" onClick={this.checkCloseFull}>
                    <div ref={(c) => { this.references.container = c }} id="manga-info-container" className="manga-info-container">
                        <div className="top-wrapper">
                            <div className="cf">
                                <img id="manga-info-img" src={info.image} />
                                <div className="title">{info.title}</div>
                                {info.alt && <div className="alt">Alternate Names: {info.alt}</div>}
                                {info.genres && <div className="genre">Genres: {info.genres}</div>}
                                <div className="author">Author(s): Edasaki</div>
                                {info.artist && <div className="artist">Artist(s): {info.artist}</div>}
                                <div className="manga-description-container">
                                    <div className="desc">{info.description}</div>
                                </div>
                            </div>
                        </div>
                        <div className="divider"></div>
                        <div className="chapter-header">
                            {info.chapterCount} Chapters available from {info.source}
                            <div className="download-menu-button">
                                <span className="fa fa-download"></span>
                                <div className="arrow_box">Download!</div>
                            </div>
                        </div>
                        <div id="chapter-pane" className="chapters">{info.chapters.join(", ")}</div>
                    </div>
                </div>
            )
        } else if (this.state.showDownload) {

            infoContainer = (
                <div id="manga-info-full-page-container" className="manga-info-full-page-container" onClick={this.checkCloseFull}>
                    <div ref={(c) => { this.references.container = c }} id="manga-download-container" className="manga-download-container">
                        <div className="chapter-header">
                            <div className="download-back">
                                <span className="fa fa-arrow-left"></span>
                            </div>
                            <div className="download-panel-name">Manga Name Here</div>
                            <div className="select-all">Select All</div>
                        </div>
                        <div id="chapter-pane" className="chapters"></div>
                        <div id="download-selected-button" className="download-selected-button">Download!</div>
                    </div>
                </div>
            )
        }

        return (
            <div id="search-result-container" className="search-result-container">
                {/*<!--  search bar at top of page on search results page -->*/}
                <form id="re-search-form" className="re-search-form">
                    <input ref={(element) => { this.references.input = element; }} id="re-search-url" type="text" name="url" autoComplete="off" onChange={this.searchTextChange} />
                    <button type="submit" value="Load">Search</button>
                </form>
                <div id="search-results-title" className="search-results-title">Results for "{this.props.lastSearch}"</div>
                {sources}
                {infoContainer}
            </div>
        )
    }

    componentDidMount() {
        document.addEventListener("keydown", this.keyDown);
    }

    componentWillUnmount() {
        document.removeEventListener("keydown", this.keyDown);
    }

    keyDown(e: KeyboardEvent) {
        if (e.key == 'Tab') {
            console.log("tab key");
            e.preventDefault();
            this.references.input.focus();
        } if (e.key == 'Enter') {
            e.preventDefault();
            this.search();
        }
    }

    searchTextChange(e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({ searchPhrase: e.target.value });
    }

    search() {
        if (!this.state || !this.state.searchPhrase)
            return;
        let searchPhrase = this.state.searchPhrase
        this.props.callback(BodyState.Loading);
        this.props.lastSearchCallback(searchPhrase);
        SearchManager.search(this.props.callback, searchPhrase);
    }
}
