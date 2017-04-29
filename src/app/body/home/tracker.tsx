import React from 'react';
import TrackerItem from './tracker-item';

export default class Tracker extends React.Component<any, any> {
    public render() {
        return (
            <div className="tracker">
                <div id="tracker-container" className="container">
                    <div className="inner-container">
                        <div className="title">
                            <div className="tab active">Favorites</div>
                            <div className="tab">History</div>
                        </div>
                        <div id="tracker-list" className="section">
                            <TrackerItem title="Ika Musume" source="MangaHere" lastRead="Chapter 305" isFavorite/>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}