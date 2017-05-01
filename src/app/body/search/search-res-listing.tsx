import React from 'react'
import SearchResult from '../../backend/search/search-result'

interface Props {
    results: SearchResult[]
}

export default class SearchResultListing extends React.Component<Props, any> {

    constructor(props) {
        super(props)
    }

    public render() {
        let arr = []
        let keyCounter = 1
        let counter = 1
        this.props.results.forEach(res => {
            arr.push(
                <div className="search-result" key={keyCounter++}>
                    <img id={"search-result-image-" + counter++} src="http://edasaki.com/i/test-page.png" />
                    <div className="search-result-name">
                        <div className="search-result-title">{res.title}</div>
                        <div className="alt-names">
                            {res.altNames && res.altNames.length > 0 ? "Alternate Names: " + res.altNames.join(", ") : ""}
                        </div>
                        <div className="hidden-info">{res.url}</div>
                    </div>
                </div>
            )
        });
        return (
            <div className="search-results">
                <div className="search-source">
                    <div className="search-source-name">
                        <a href="#">MangaHere</a>
                        <i className="search-source-toggle fa fa-angle-double-up"></i>
                    </div>
                    {arr}
                </div>
            </div>
        )
    }
}
