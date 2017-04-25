import React from 'react';
import Footer from './footer';
import TitleBar from './titlebar';

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