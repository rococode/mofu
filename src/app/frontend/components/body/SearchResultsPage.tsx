import React from 'react';
import { SearchResult, SearchManager, SourceResult } from 'backend/search'
import { MangaSource } from 'backend/sources'
import { MangaInfo, MangaChapter } from 'backend/abstracts'
import { SearchResultsSourceListing, MangaChapterListing } from 'frontend/components'
import { BodyState } from 'frontend/enums'
import autobind from 'autobind'

interface Props {
    results: SourceResult[],
    lastSearch: string,
    callback: (result: SourceResult[]) => void,
    lastSearchCallback,
    readerCallback
}

interface State {
    searchPhrase: string,
    showInfo: boolean,
    showDownload: boolean,
    mangaInfo?: MangaInfo
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

    async loadInfo(info: MangaInfo) {
        this.setState({ showInfo: true, mangaInfo: info })
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
        if (this.props.results) {
            this.props.results.forEach(source => {
                sources.push(
                    <SearchResultsSourceListing callback={this.loadInfo} results={source.results} source={source.source} sourceName={source.sourceName} key={++counter} />
                )
            });
        }
        let infoContainer = undefined

        if (this.state.showInfo && this.state.mangaInfo) {
            let info = this.state.mangaInfo
            let chapters = []
            let c = 1;
            info.chapters.forEach(element => {
                chapters.push(<MangaChapterListing key={c++} chapter={element} readerCallback={this.props.readerCallback} />)
            });
            infoContainer = (
                <div id="manga-info-full-page-container" className="manga-info-full-page-container" onClick={this.checkCloseFull}>
                    <div ref={(c) => { this.references.container = c }} id="manga-info-container" className="manga-info-container">
                        <div className="top-wrapper">
                            <div className="cf">
                                <img id="manga-info-img" src={info.image} />
                                <div className="title">{info.title}</div>
                                {info.alt && <div className="alt">Alternate Names: {info.alt}</div>}
                                {info.genres && <div className="genre">Genres: {info.genres}</div>}
                                <div className="author">Author(s): {info.author}</div>
                                {info.artist && <div className="artist">Artist(s): {info.artist}</div>}
                                <div className="manga-description-container">
                                    <div className="desc">{info.description}</div>
                                </div>
                            </div>
                        </div>
                        <div className="divider"></div>
                        <div className="chapter-header">
                            {info.chapterCount} Chapters available from {info.sourceName}
                            <div className="download-menu-button">
                                <span className="fa fa-download"></span>
                                <div className="arrow_box">Download!</div>
                            </div>
                        </div>
                        <div id="chapter-pane" className="chapters">{chapters}</div>
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
        this.props.lastSearchCallback(searchPhrase);
        SearchManager.search(this.props.callback, searchPhrase);
    }
}
