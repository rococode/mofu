import React from 'react'
import autobind from 'autobind'
import { MangaChapter } from 'backend/abstracts'

interface MangaChapterProps {
    chapter: MangaChapter,
    readerCallback
}

export default class MangaChapterListing extends React.Component<MangaChapterProps, any>{

    constructor(props) {
        super(props)
        autobind(this);
    }

    async loadChapter() {
        let chapter = await this.props.chapter.source.loadChapter(this.props.chapter)
        this.props.readerCallback(chapter)
    }

    public render() {
        return (
            <div className="chapter-wrapper" onClick={this.loadChapter}>
                <div className="chapter">
                    Chapter {this.props.chapter.name}
                </div>
            </div>
        )
    }
}