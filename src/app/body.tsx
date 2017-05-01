import React from 'react'
import Home from './body/home'
import Reader from './body/reader'
import SearchResultPage from './body/searchres'
import Loading from './body/loading'
import SearchResult from './backend/search/search-result'

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
    latestResults?: SearchResult[]
}

export default class Body extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = { bodyState: BodyState.Home };
        autobind(this)
    }

    changeState(state: BodyState, result?: SearchResult[]) {
        this.setState({ bodyState: state, latestResults: result });
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <Home callback={this.changeState} />
                break;
            case BodyState.SearchResults:
                res = <SearchResultPage results={this.state.latestResults}/>
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
