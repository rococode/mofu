import React from 'react';

export default class TitleBar extends React.Component<{}, {}> {
    public render() {
        return (
            <div className="title-bar">
                <div className="title">
                    mofu.moe
                </div>
                <div className="right">
                    <button className="minimize button" onClick={minimize}></button>
                    <button className="maximize button"></button>
                    <button className="exit button"></button>
                </div>
            </div>
        )
    }
}

function minimize() {
    // let windows = electron.BrowserWindow.getAllWindows();
    // console.log(windows);
}