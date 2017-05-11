import React from 'react';
import autobind from 'autobind';
import { MangaChapter } from 'backend/abstracts'
import electron from 'moelectron'
import { changeState } from 'frontend/components/Body'
import {BodyState} from 'frontend/enums'

interface Props {
    chapter: MangaChapter
}

export default class ReaderPage extends React.Component<Props, {}> {

    constructor(props) {
        super(props)
        autobind(this)
    }

    public render() {
        let chapter = this.props.chapter
        let pages = []
        let c = 1;
        if (chapter && chapter.pages) {
            chapter.pages.forEach((element, index) => {
                let page = (
                    <div className="reader-page" key={c++}>
                        <img src={element.url} />
                    </div>
                )
                let num = (
                    <div className="reader-page-number-container" key={c++}>
                        <div className="reader-page-number">{element.num + "/" + chapter.pages.length}</div>
                    </div>
                )
                pages.push(page)
                pages.push(num)
            })
        } else {
            // for debugging styles
            for (let k = 0; k < 3; k++) {
                let page = (
                    <div className="reader-page" key={c++}>
                        <img src="http://edasaki.com/i/test-page.png" />
                    </div>
                )
                let num = (
                    <div className="reader-page-number-container" key={c++}>
                        <div className="reader-page-number">{(k + 1) + "/" + 3}</div>
                    </div>
                )
                pages.push(page)
                pages.push(num)
            }
        }
        return (
            <div id="reader-container" className="reader-container">

                <div className="reader-title">
                    <div className="back" onClick={this.backToSearch}>Back to Search</div>
                    <span id="to-page-top" className="button fa fa-arrow-left"></span>
                    <div className="text">{chapter && chapter.name ? chapter.name : "Test Chapter"}</div>
                    <span id="to-page-top" className="button fa fa-arrow-right"></span>
                    <div className="chapters">Chapters</div>
                    <div className="dropdown"></div>
                </div>
                <div id="reader-pages" className="reader-pages">
                    {pages}
                </div>
                <div className="top-bottom-buttons">
                    <span className="fa fa-arrow-up" onClick={this.scrollTop}></span>
                </div>
                <div className="download-button">
                    <span className="fa fa-download"></span>
                </div>
            </div>
        )
    }

    backToSearch() {
        changeState(BodyState.SearchResults)
    }

    scrollTop() {
        electron.getCurrentWindow().webContents.executeJavaScript("$('html, body').animate({ scrollTop: 0 }, 'fast');");
    }

}