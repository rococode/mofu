import React from 'react'
import autobind from 'autobind'
import {MangaInfo} from 'backend/abstracts'
import {MangaSource} from 'backend/sources'

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

export default class SearchResultsSeriesListing extends React.Component<IndividualListingProps, IndividualListingState> {

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