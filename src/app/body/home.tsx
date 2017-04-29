import React from 'react';
import Tracker from './home/tracker';
import SearchManager from '../backend/search-manager';

const autoBind = require('react-autobind');

interface StartSearchState {
    searchPhrase: string
}

export default class Home extends React.Component<{}, StartSearchState> {

    constructor(props) {
        super(props);
        autoBind(this);
    }

    componentDidMount() {
        document.addEventListener("keydown", this.keyDown);
    }

    componentWillUnmount() {
        document.addEventListener("keydown", this.keyDown);
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
        if (!this.state || !this.state.searchPhrase) return;
        let searchPhrase = this.state.searchPhrase
        console.log("Searching for " + searchPhrase);
        SearchManager.test(searchPhrase);
    }

}