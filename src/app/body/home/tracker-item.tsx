import React from 'react';

interface TrackerItemProps {
    isFavorite?: boolean,
    title: string,
    source: string,
    lastRead: string,
    isAlert?: boolean
}

export default class TrackerItem extends React.Component<TrackerItemProps, any> {

    constructor(props) {
        super(props);
    }

    public render() {
        return (
            <div className="tracker-listing">
                <div className="name">
                    <div className={"fav" + (this.props.isFavorite ? " active" : "")}>
                        <span className={"fa fa-star" + (this.props.isFavorite ? "" : "-o")}></span>
                    </div>
                    {this.props.title}
                    <div className="source">({this.props.source})</div>
                </div>
                <div className="lastread">
                    {this.props.lastRead}
                    <div className={"alert" + (this.props.isAlert ? " active" : "")}>
                        <span className={"fa fa-bell" + (this.props.isAlert ? "" : "-o")}></span>
                    </div>
                </div>
            </div>
        );
    }
}