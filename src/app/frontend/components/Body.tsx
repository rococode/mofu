import React from 'react'
import { BodyState } from 'frontend/enums'
import { SearchResult, SourceResult } from 'backend/search'
import { MangaChapter } from 'backend/abstracts'
import { SearchResultsPage, HomePage, ReaderPage, LoadingPage } from 'frontend/components'
import autobind from 'autobind'

interface Props {
    zoomFactor: number
}

interface State {
    bodyState: BodyState,
    latestResults?: SourceResult[],
    lastSearch?: string,
    readingChapter?: MangaChapter,
}

export default class Body extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = { bodyState: BodyState.Home };
        autobind(this)
    }

    componentDidMount() {
        document.addEventListener("keydown", this.keyDown);
    }

    componentWillUnmount() {
        document.removeEventListener("keydown", this.keyDown);
    }

    keyDown(e: KeyboardEvent) {
        if (e.key == 'F5') {
            e.preventDefault();
            location.reload();
        }
        console.log(e.key);
    }

    changeState(state: BodyState, result?: SourceResult[]) {
        this.setState({ bodyState: state, latestResults: result });
    }

    setLastSearch(s: string) {
        this.setState({ lastSearch: s });
    }

    openReader(chapter: MangaChapter) {
        console.log(chapter)
        this.setState({ bodyState: BodyState.Reader, readingChapter: chapter })
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <HomePage callback={this.changeState} lastSearchCallback={this.setLastSearch} />
                break;
            case BodyState.SearchResults:
                res = <SearchResultsPage lastSearch={this.state.lastSearch} results={this.state.latestResults} callback={this.changeState} lastSearchCallback={this.setLastSearch} readerCallback={this.openReader} />
                break;
            case BodyState.Reader:
                res = <ReaderPage chapter={this.state.readingChapter} />
                break;
            case BodyState.Loading:
                res = <LoadingPage />
                break;
        }
        const s = {
            zoom: this.props.zoomFactor
        }
        return <div style={s}>{res}</div>;
    }
}
