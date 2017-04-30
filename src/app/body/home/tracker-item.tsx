import React from 'react';
import * as tracker from './tracker'
const autobind = require('react-autobind')

export interface TrackerItemProps {
    isFavorite?: boolean,
    title: string,
    source: string,
    lastRead: string,
    isAlert?: boolean
}

export interface TrackerItemState {
    isFavorite: boolean,
    isAlert: boolean
}

export default class TrackerItem extends React.Component<TrackerItemProps, TrackerItemState> {

    constructor(props) {
        super(props);
        autobind(this)
        this.state = {
            isFavorite : props.isFavorite,
            isAlert : props.isAlert
        }
    }

    clickStar() {
        let fav : boolean = this.state.isFavorite ? this.state.isFavorite : false
        this.setState({isFavorite : !fav})
        tracker.favoriteCallback(this);
    }

    clickAlert() {
        let alert : boolean = this.state.isAlert ? this.state.isAlert : false
        this.setState({isAlert : !alert})
        tracker.alertCallback(this);
    }

    public render() {
        return (
            <div className="tracker-listing">
                <div className="name">
                    <div className={"fav" + (this.state.isFavorite ? " active" : "")} onClick={this.clickStar}>
                        <span className={"fa fa-star" + (this.state.isFavorite ? "" : "-o")}></span>
                    </div>
                    {this.props.title}
                    <div className="source">({this.props.source})</div>
                </div>
                <div className="lastread">
                    {this.props.lastRead}
                    <div className={"alert" + (this.state.isAlert ? " active" : "")} onClick={this.clickAlert}>
                        <span className={"fa fa-bell" + (this.state.isAlert ? "" : "-o")}></span>
                    </div>
                </div>
            </div>
        );
    }
}