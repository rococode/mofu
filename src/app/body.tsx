import React from 'react'
import Home from './body/home'
import Reader from './body/reader'
import SearchRes from './body/searchres'
import Loading from './body/loading'

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
    bodyState: BodyState
}

export default class Body extends React.Component<Props, State> {

    constructor(props) {
        super(props);
        this.state = { bodyState: BodyState.Home };
        autobind(this)
    }

    changeState(state: BodyState) {
        this.setState({ bodyState: state });
    }

    public render() {
        let res;
        switch (this.state.bodyState) {
            case BodyState.Home:
                res = <Home callback={this.changeState} />
                break;
            case BodyState.SearchResults:
                res = <SearchRes />
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
