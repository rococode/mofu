import React from 'react';
import Footer from './footer';

export default class App extends React.Component<{}, {}> {
    public render() {
        return (
            <div>
            <TitleBar />
            <Footer />
            </div>
        );
    }
}

export class TitleBar extends React.Component<{}, {}> {
    public render() {
        return (
            <div className="title-bar">
                <div className="title">
                    mofu.moe
                </div>
            </div>
        )
    }
}