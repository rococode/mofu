import React from 'react';

export default class ReaderPage extends React.Component<{}, {}> {
    public render() {
        return (
            <div id="reader-container" className="reader-container">
                <div id="reader-title" className="reader-title">Gekkan Shoujo Nozaki</div>
                <div id="info-source" className="hidden-info"></div>
                <div id="info-chapter" className="hidden-info"></div>
                <div className="reader-nav-container">
                    <div className="reader-nav">
                        <div className="reader-nav-el">Prev Chapter</div>
                        <div className="reader-nav-div"></div>
                        <div className="reader-nav-el">Chapter 0/?</div>
                        <div className="reader-nav-div"></div>
                        <div className="reader-nav-el">Next Chapter</div>
                    </div>
                </div>
                <div id="reader-pages" className="reader-pages">
                    <div className="reader-page">
                        <img src="http://i.edasaki.com/test-page.png" />
                    </div>
                    <div className="reader-page">
                        <img src="http://i.edasaki.com/test-page.png" />
                    </div>
                </div>
                <div className="reader-nav-container">
                    <div className="reader-nav">
                        <div className="reader-nav-el">Prev Chapter</div>
                        <div className="reader-nav-div"></div>
                        <div className="reader-nav-el">Chapter 0/?</div>
                        <div className="reader-nav-div"></div>
                        <div className="reader-nav-el">Next Chapter</div>
                    </div>
                </div>
                <div id="top-bottom-buttons" className="top-bottom-buttons">
                    <span id="to-page-top" className="fa fa-arrow-up"></span>
                    <span id="to-page-bottom" className="fa fa-arrow-down"></span>
                </div>
                <div id="download-button" className="download-button">
                    <span id="download-button-icon" className="fa fa-download"></span>
                </div>
            </div>
        )
    }
}