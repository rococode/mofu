import React from 'react';
export default class Home extends React.Component<{}, {}>{
    public render() {
        return (
            <div id="home-container" className="home-container">
                {/*<img className="logo unselectable" src="assets/gfx/logo.png" />*/}
                <form id="loader-form" className="loader-form">
                    <input id="load-input-url" type="text" name="url" placeholder="" value="" autoComplete="off" />
                    <br />
                    <button type="submit" value="Load">Search</button>
                </form>
                <div className="changelog">
                    <h1>
                        Changelog <i id="changelog-toggle" className="fa fa-angle-double-down"></i>
                    </h1>
                    <div className="changelog-text" id="changelog-text"></div>
                </div>
                <div className="tracker">
                    <div id="tracker-button" className="button">Open Tracker</div>
                    <div id="tracker-container" className="container">
                        <div className="inner-container">
                            <div id="tracker-favorites" className="section favorites">
                                <div className="title">Favorites</div>
                                <div className="tracker-listing">
                                    <div className="name">
                                        <div className="fav active">
                                            <span className="fa fa-star"></span>
                                        </div>
                                        Ika Musume
								<div className="source">(MangaHere)</div>
                                    </div>
                                    <div className="lastread">
                                        Chapter 305
								<div className="alert">
                                            <span className="fa fa-bell-o"></span>
                                        </div>
                                    </div>
                                </div>
                                <div className="tracker-listing">
                                    <div className="name">
                                        <div className="fav active">
                                            <span className="fa fa-star"></span>
                                        </div>
                                        Yuru Yuri
								<div className="source">(KissManga)</div>
                                    </div>
                                    <div className="lastread">
                                        Ch.12
								<div className="alert active">
                                            <span className="fa fa-bell"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div id="tracker-history" className="section history">
                                <div className="title">History</div>
                                <div className="tracker-listing">
                                    <div className="name">
                                        <div className="fav">
                                            <span className="fa fa-star-o"></span>
                                        </div>
                                        Gekkan Shoujo Nozaki-kun
								<div className="source">(KissManga)</div>
                                    </div>
                                    <div className="lastread">
                                        Ch 89
								<div className="alert active">
                                            <span className="fa fa-bell"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}