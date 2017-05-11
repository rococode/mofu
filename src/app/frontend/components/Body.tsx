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
    isLoading?: boolean,
    loadingProgress?: number,
    loadingMessage?: string
}

export let loading: (progress?: number, message?: string) => void;
export let stoploading: () => void;
export let changeState: (state: BodyState, sourceResult?: SourceResult[]) => void;

export default class Body extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = { bodyState: BodyState.Home };
        autobind(this)
        loading = this.loading
        stoploading = this.stoploading
        changeState = this.changeState;
    }

    componentDidMount() {
        document.addEventListener("keydown", this.keyDown);
    }

    componentWillUnmount() {
        document.removeEventListener("keydown", this.keyDown);
    }

    keyDown(e: KeyboardEvent) {
        switch (e.key) {
            case 'F5':
                e.preventDefault();
                location.reload();
                break;
            case 'F1':
                e.preventDefault();
                this.changeState(BodyState.Home);
                break;
            case 'F2':
                e.preventDefault();
                this.changeState(BodyState.Reader);
                break;
            case 'F3':
                e.preventDefault();
                this.changeState(BodyState.SearchResults);
                break;
            case 'F6':
                e.preventDefault();
                this.loading(0.5, "testing")
                break;
            case 'F7':
                e.preventDefault();
                this.stoploading()
                break;
        }
    }

    changeState(state: BodyState) {
        this.setState({ bodyState: state });
    }

    setSourceResults(result: SourceResult[]) {
        this.setState({latestResults: result, bodyState: BodyState.SearchResults});
    }

    setLastSearch(s: string) {
        this.setState({ lastSearch: s });
    }

    openReader(chapter: MangaChapter) {
        console.log(chapter)
        this.setState({ bodyState: BodyState.Reader, readingChapter: chapter })
    }

    loading(progress?: number, message?: string) {
        this.setState({ isLoading: true, loadingProgress: progress, loadingMessage: message })
    }

    stoploading() {
        this.setState({ isLoading: false, loadingMessage: undefined, loadingProgress: undefined })
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <HomePage callback={this.setSourceResults} lastSearchCallback={this.setLastSearch} />
                break;
            case BodyState.SearchResults:
                res = <SearchResultsPage lastSearch={this.state.lastSearch} results={this.state.latestResults} callback={this.setSourceResults} lastSearchCallback={this.setLastSearch} readerCallback={this.openReader} />
                break;
            case BodyState.Reader:
                res = <ReaderPage chapter={this.state.readingChapter} />
                break;
        }
        const s = {
            zoom: this.props.zoomFactor
        }
        return (
            <div style={s}>
                {res}
                {this.state.isLoading && <LoadingPage message={this.state.loadingMessage} progress={this.state.loadingProgress} />}
            </div>)
    }
}
