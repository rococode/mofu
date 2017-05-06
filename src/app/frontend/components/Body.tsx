import React from 'react'
import { BodyState } from 'frontend/enums'
import { SearchResult, SourceResult } from 'backend/search'
import { SearchResultsPage, HomePage, ReaderPage, LoadingPage } from 'frontend/components'
import autobind from 'autobind'

interface Props {
    zoomFactor: number
}

interface State {
    bodyState: BodyState,
    latestResults?: SourceResult[],
    lastSearch?: string
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

    setLastSearch(s: string) {
        this.setState({ lastSearch: s });
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <HomePage callback={this.changeState} lastSearchCallback={this.setLastSearch} />
                break;
            case BodyState.SearchResults:
                res = <SearchResultsPage lastSearch={this.state.lastSearch} results={this.state.latestResults} callback={this.changeState} lastSearchCallback={this.setLastSearch} />
                break;
            case BodyState.Reader:
                res = <ReaderPage />
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
