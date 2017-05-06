import React from 'react';
import Tracker from './home/tracker';
import { SearchManager } from 'backend/search';
import { BodyState } from 'frontend/enums'

const autoBind = require('react-autobind');

interface HomeState {
    searchPhrase: string
}

interface HomeProps {
    callback: (state: BodyState) => void,
    lastSearchCallback
}

export default class HomePage extends React.Component<HomeProps, HomeState> {

    constructor(props) {
        super(props);
        autoBind(this);
    }

    componentDidMount() {
        document.addEventListener("keydown", this.keyDown);
    }

    componentWillUnmount() {
        document.removeEventListener("keydown", this.keyDown);
    }

    references: { input: HTMLInputElement } = {
        input: null
    }

    public render() {
        return (
            <div id="home-container" className="home-container">
                <div className="loader-form">
                    <input ref={(input) => { this.references.input = input; }} id="load-input-url" type="text" name="url" autoComplete="off" onChange={this.searchTextChange} />
                    <button type="submit" value="Load" onClick={this.search}>Search</button>
                </div>
                <Tracker />
            </div>
        )
    }

    keyDown(e: KeyboardEvent) {
        if (e.key == 'Tab') {
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