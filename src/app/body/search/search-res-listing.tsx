import React from 'react'
import SearchResult from '../../backend/search/search-result'
const autobind = require('react-autobind');
import { getLowPriority } from '../../backend/utils/accessor'
const cheerio: CheerioAPI = require('cheerio')

interface Props {
    results: SearchResult[],
    sourceName: string
}

export default class SearchResultListing extends React.Component<Props, any> {
    constructor(props) {
        super(props)
    }

    public render() {
        let listings = []
        let counter = 0;
        this.props.results.forEach(res => {
            listings.push(<IndividualListing title={res.title} url={res.url} altNames={res.altNames} key={++counter} />)
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
    url: string
}

interface IndividualListingState {
    imageURL: string,
    fetchedImage: boolean
}

class IndividualListing extends React.Component<IndividualListingProps, IndividualListingState> {

    constructor(props) {
        super(props)
        console.log("here " + this.props.url);
        this.state = {
            imageURL: "http://edasaki.com/i/test-page.png",
            fetchedImage: false
        }
        autobind(this)
    }

    async fetchImage() {
        let res = await getLowPriority(this.props.url, function (x) {
            return x.indexOf('manga_detail_top') > -1
        });
        let $ = cheerio.load(res);
        let url = cheerio('.manga_detail_top', res).first().children('img.img').first().attr("src")
        console.log("got url " + url)
        this.setState({ imageURL: url, fetchedImage: true });
    }

    public render() {
        if (!this.state.fetchedImage) {
            this.fetchImage();
        }
        let id = listingCounter++;
        return (
            <div className="search-result">
                <img id={"search-result-image-" + id} src={this.state.imageURL} />
                <div className="search-result-name">
                    <div className="search-result-title">{this.props.title}</div>
                    <div className="alt-names">
                        {this.props.altNames && this.props.altNames.length > 0 ? "Alternate Names: " + this.props.altNames.join(", ") : ""}
                    </div>
                    <div className="hidden-info">{this.props.url}</div>
                </div>
            </div>
        )
    }

}