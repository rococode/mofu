import React from 'react'
import autobind from 'autobind'
import { MangaChapter } from 'backend/abstracts'

interface MangaChapterProps {
    chapter: MangaChapter
}

export default class MangaChapterListing extends React.Component<MangaChapterProps, any>{

    constructor(props) {
        super(props)
        autobind(this);
    }

    loadChapter() {
        this.props.chapter.source.loadChapter(this.props.chapter)
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