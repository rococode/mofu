import React from 'react'
import Home from './body/home'
import Reader from './body/reader'
import SearchResultsPage from './body/results-page'
import Loading from './body/loading'
import SearchResult from './backend/search/search-result'
import SourceResult from './backend/search/source-result'

const autobind = require('react-autobind')

export enum BodyState {
    Home,
    SearchResults,
    Reader,
    Loading
}

interface Props {
    zoomFactor: number
}

interface State {
    bodyState: BodyState,
    latestResults?: SourceResult[],
    lastSearch? : string
}

export default class Body extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = { bodyState: BodyState.Home };
        autobind(this)
    }

    changeState(state: BodyState, result?: SourceResult[]) {
        this.setState({ bodyState: state, latestResults: result });
    }

    setLastSearch(s : string) {
        this.setState({lastSearch: s});
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <Home callback={this.changeState} lastSearchCallback={this.setLastSearch}/>
                break;
            case BodyState.SearchResults:
                res = <SearchResultsPage lastSearch={this.state.lastSearch} results={this.state.latestResults} callback={this.changeState} lastSearchCallback={this.setLastSearch}/>
                break;
            case BodyState.Reader:
                res = <Reader />
                break;
            case BodyState.Loading:
                res = <Loading />
                break;
        }
        const s = {
            zoom: this.props.zoomFactor
        }
        return <div style={s}>{res}</div>;
    }
}
