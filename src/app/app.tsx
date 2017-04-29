import React from 'react';
import Footer from './footer';
import Body from './body';
import TitleBar from './titlebar';
const autoBind = require('react-autobind');

interface State {
    zoomFactor: number
}

export default class App extends React.Component<{}, State> {

    constructor(props) {
        super(props)
        this.state = { zoomFactor: 1.0 }
        autoBind(this);
    }

    updateZoom(newZoom: number) {
        this.setState({ zoomFactor: newZoom });
    }

    zoomIn(e) {
        e.preventDefault();
        this.updateZoom(this.state.zoomFactor + 0.1);
    }

    zoomOut(e) {
        e.preventDefault();
        this.updateZoom(this.state.zoomFactor - 0.1);
    }

    public render() {
        return (
            <div>
                <TitleBar zoomIn={this.zoomIn} zoomOut={this.zoomOut} />
                <Body zoomFactor={this.state.zoomFactor} />
                <Footer />
            </div>
        );
    }
}