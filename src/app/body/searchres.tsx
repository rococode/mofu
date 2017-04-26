import React from 'react';

export default class SearchRes extends React.Component<{}, {}> {
    public render() {
        return (
            <div id="search-result-container" className="search-result-container">
                {/*<!--  search bar at top of page on search results page -->*/}
                <form id="re-search-form" className="re-search-form">
                    <input id="re-search-url" type="text" name="url" placeholder="" value="" autoComplete="off" />
                    <button type="submit" value="Load">Search</button>
                </form>
                <div id="search-results-title" className="search-results-title">Results</div>
                <div className="search-results"></div>

                <div id="manga-info-full-page-container" className="manga-info-full-page-container">
                    <div id="manga-info-container" className="manga-info-container">
                        <div className="top-wrapper">
                            <div className="cf">
                                <img id="manga-info-img" src="http://edasaki.com/i/test-page.png" />
                                <div className="title">Mockup Really Super Duper Long Title Just A Little Longer</div>
                                <div className="alt">Alternate Names: Test Alt Title</div>
                                <div className="genre">Genres: Fun, Cool, Happy</div>
                                <div className="author">Author(s): Edasaki</div>
                                <div className="artist">Artist(s): Edasaki</div>
                                <div className="manga-description-container">
                                    <div className="desc">A really cool thing about stuff. Super exciting stuff. Really great. Super great. Just the greatest. I'm just writing random stuff to try to push this to two lines so I can see how it looks lol. And now I'm just gonna
								write a whole bunchhhhhh of random stuff to push it beyond the boundary and see how wacky it looks after that. Because by default I think it kinda screwws up the formatting, but I want to make it resize the text automatically.</div>
                                </div>
                            </div>
                        </div>
                        <div className="divider"></div>
                        <div className="chapter-header">
                            10 Chapters available from MangoDango
					<div className="download-menu-button">
                                <span className="fa fa-download"></span>
                                <div className="arrow_box">Download!</div>
                            </div>
                        </div>
                        <div id="chapter-pane" className="chapters"></div>
                    </div>
                    <div id="manga-download-container" className="manga-download-container">
                        <div className="chapter-header">
                            <div className="download-back">
                                <span className="fa fa-arrow-left"></span>
                            </div>
                            <div className="download-panel-name">Manga Name Here</div>
                            <div className="select-all">Select All</div>
                        </div>
                        <div id="chapter-pane" className="chapters"></div>
                        <div id="download-selected-button" className="download-selected-button">Download!</div>
                    </div>
                </div>
            </div>
        )
    }
}