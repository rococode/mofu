import React from 'react'
import autobind from 'autobind'
import { SearchResult } from 'backend/search'
import accessor from 'backend/utils/accessor'
import { MangaSource } from 'backend/sources'
import { MangaInfo } from 'backend/abstracts'
import { SearchResultsSeriesListing } from 'frontend/components'
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
            listings.push(<SearchResultsSeriesListing title={res.title} url={res.url} source={this.props.source} altNames={res.altNames} key={++counter} callback={this.props.callback} />)
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
