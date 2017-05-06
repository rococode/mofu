import React from 'react';
import { TrackerItemProps } from './TrackerItem';
import { TrackerItem } from 'frontend/components'
import autobind from 'autobind'

interface TrackerState {
    isOnRecentTab: boolean,
    listings: TrackerItemProps[]
}

export function favoriteCallback(item: TrackerItem) {
    console.log("detected change on " + item)
}

export function alertCallback(item: TrackerItem) {
    console.log("detected change on " + item)
}

export default class Tracker extends React.Component<any, TrackerState> {

    constructor(props) {
        super(props);
        this.state = { listings: [], isOnRecentTab: true };
        autobind(this);
    }

    public render() {
        let listings = []
        let props = this.state.isOnRecentTab ? this.getRecentListings() : this.getFavoriteListings();
        let counter = 0;
        props.forEach(e => {
            let item = <TrackerItem
                key={counter++}
                title={e.title} source={e.source} lastRead={e.lastRead} isFavorite={e.isFavorite} isAlert={e.isAlert}
            />
            listings.push(item)
        });
        return (
            <div className="tracker">
                <div id="tracker-container" className="container">
                    <div className="inner-container">
                        <div className="title">
                            <div className={"tab" + (this.state.isOnRecentTab ? " active" : "")} onClick={this.clickRecent}>Recent</div>
                            <div className={"tab" + (this.state.isOnRecentTab ? "" : " active")} onClick={this.clickFavorites}>Favorites</div>
                        </div>
                        <div id="tracker-list" className="section">
                            {listings}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    clickRecent() {
        if (this.state.isOnRecentTab) {
            return
        }
        this.setState({ isOnRecentTab: true })
    }

    clickFavorites() {
        if (!this.state.isOnRecentTab) {
            return
        }
        this.setState({ isOnRecentTab: false })
    }

    public getRecentListings(): TrackerItemProps[] {
        let arr: TrackerItemProps[] = [];
        arr.push(
            {
                title: "Ika Musume",
                source: "MangaHere",
                lastRead: "Chapter 305",
                isFavorite: true
            },
            {
                title: "Yuru Yuri",
                source: "Batoto",
                lastRead: "Chapter 79",
                isFavorite: true,
                isAlert: true
            },
            {
                title: "Gekkan Shoujo Nozaki-kun",
                source: "MangaFox",
                lastRead: "Chapter 35.5"
            }
        )
        return arr;
    }

    public getFavoriteListings(): TrackerItemProps[] {
        let all = this.getRecentListings();
        let res = []
        all.forEach(element => {
            if (element.isFavorite) {
                res.push(element)
            }
        });
        return res;
    }
}