import React from 'react';
import { MangaChapter } from 'backend/abstracts'

interface Props {
    chapter: MangaChapter
}

export default class ReaderPage extends React.Component<Props, {}> {
    public render() {
        let chapter = this.props.chapter
        let pages = []

        chapter.pages.forEach((element, index) => {
            let page = (
                <div className="reader-page" >
                    <img src={element.url} />
                </div>
            )
            let num = (
                <div className="reader-page-number-container">
                    <div className="reader-page-number">{element.num + "/" + chapter.pages.length}</div>
                </div>
            )
            pages.push(page)
            pages.push(num)
        })
        return (
            <div id="reader-container" className="reader-container">

                <span id="to-page-top" className="fa fa-arrow-left"></span>
                <div id="reader-title" className="reader-title">{chapter.name}</div>
                <span id="to-page-top" className="fa fa-arrow-right"></span>
                <div id="reader-pages" className="reader-pages">
                    {pages}
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