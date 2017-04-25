import React from 'react';
// <reference path="../../node_modules/@types/electron/index.d.ts" />
const electron = require('electron').remote;

export default class TitleBar extends React.Component<{}, {}> {
    public render() {
        return (
            <div className="title-bar">
                <div className="title">
                    mofu.moe
                </div>
                <div className="right">
                    <button className="minimize button" onClick={minimize}></button>
                    <button className="maximize button" onClick={maximize}></button>
                    <button className="exit button" onClick={exit}></button>
                </div>
            </div>
        )
    }
}

function minimize() {
    electron.BrowserWindow.getFocusedWindow().minimize();
}

function maximize() {
    electron.BrowserWindow.getFocusedWindow().maximize();
}

function exit() {
    electron.app.quit();
}