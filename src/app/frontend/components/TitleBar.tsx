import React from 'react';
import autobind from 'autobind'
import electron from 'electron'
import open from 'open'

interface Props {
    zoomOut,
    zoomIn
}

export default class TitleBar extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
        autobind(this);
    }

    public render() {
        let isMax = electron.BrowserWindow.getFocusedWindow() ? electron.BrowserWindow.getFocusedWindow().isMaximized() : false
        return (
            <div className="title-bar">
                <div className="left">
                    <div className="button zoom-out" onClick={this.props.zoomOut}>-</div>
                    <div className="button zoom-in" onClick={this.props.zoomIn}>+</div>
                </div>
                <div className="title" onClick={this.mofumoe}></div>
                <div className="right">
                    <button className="minimize button" onClick={this.minimize}></button>
                    {isMax ? (<button className="unmaximize button" onClick={this.unmaximize}></button>) : (<button className="maximize button" onClick={this.maximize}></button>)}
                    <button className="exit button" onClick={this.exit}></button>
                </div>
            </div>
        )
    }

    private mofumoe() {
        open("http://mofu.moe");
    }

    private minimize() {
        electron.BrowserWindow.getFocusedWindow().minimize();
    }

    private unmaximize() {
        electron.BrowserWindow.getFocusedWindow().unmaximize();
        this.forceUpdate();
    }

    private maximize() {
        electron.BrowserWindow.getFocusedWindow().maximize();
        this.forceUpdate();
    }

    private exit() {
        electron.app.quit();
    }
}
