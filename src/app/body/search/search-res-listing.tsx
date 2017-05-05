import React from 'react'
import SearchResult from '../../backend/search/search-result'
const autobind = require('react-autobind');
import { getLowPriority } from '../../backend/utils/accessor'
import MangaSource from '../../backend/sources/manga-source'
import MangaInfo from '../../backend/abstracts/manga-info'
const cheerio: CheerioAPI = require('cheerio')

interface Props {
    results: SearchResult[],
    sourceName: string,
    callback: (info: MangaInfo) => void,
    source: MangaSource
}

export default class SearchResultListing extends React.Component<Props, any> {
    constructor(props) {
        super(props)
    }

    public render() {
        let listings = []
        let counter = 0;
        this.props.results.forEach(res => {
            listings.push(<IndividualListing title={res.title} url={res.url} source={this.props.source} altNames={res.altNames} key={++counter} callback={this.props.callback}/>)
        });

        return (
            <div className="search-results">
                <div className="search-source">
                    <div className="search-source-name">
                        <a href="#">{this.props.sourceName}</a>
                        <i className="search-source-toggle fa fa-angle-double-up"></i>
                    </div>
                    {listings}
                </div>
            </div>
        )
    }
}

let listingCounter = 1;

interface IndividualListingProps {
    title: string,
    altNames: string[],
    url: string, 
    callback: (info: MangaInfo) => void,
    source: MangaSource
}

interface IndividualListingState {
    imageURL: string,
    fetchedInfo: boolean,
    info?: MangaInfo
}

class IndividualListing extends React.Component<IndividualListingProps, IndividualListingState> {

    constructor(props) {
        super(props)
        console.log("here " + this.props.url);
        this.state = {
            imageURL: "http://edasaki.com/i/test-page.png",
            fetchedInfo: false
        }
        autobind(this)
    }

    async loadInfo() {
        await this.state.info
        this.props.callback(this.state.info)
    }

    async fetchInfo() {
        let res = await this.props.source.getInfo(this.props.url);
        this.setState({ imageURL: res.image, fetchedInfo: true, info: res});
    }

    public render() {
        if (!this.state.fetchedInfo) {
            this.fetchInfo();
        }
        let id = listingCounter++;
        return (
            <div className="search-result">
                <img id={"search-result-image-" + id} src={this.state.imageURL} />
                <div className="search-result-name">
                    <div className="search-result-title" onClick={this.loadInfo}>{this.props.title}</div>
                    <div className="alt-names">
                        {this.props.altNames && this.props.altNames.length > 0 ? "Alternate Names: " + this.props.altNames.join(", ") : ""}
                    </div>
                </div>
            </div>
        )
    }


}