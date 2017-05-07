import React from 'react';
import autobind from 'autobind'
const rc_progress = require('rc-progress');

interface State {
    dots: number,
    progress?: number
}

interface Props {
    message?: string,
    progress?: number
}

export default class LoadingPage extends React.Component<Props, State> {
    timerID: NodeJS.Timer;
    constructor(props) {
        super(props)
        this.state = { dots: 0, progress: this.props.progress }
        autobind(this);
    }

    componentDidMount() {
        this.timerID = setInterval(() => this.tick(), 500)
    }

    componentWillUnmount() {
        clearInterval(this.timerID)
    }

    public render() {
        return (
            <div className="loading-container">
                <div className="wrapper">
                    {this.state && this.props.message &&
                        <div className="message">
                            {this.props.message}
                        </div>
                    }
                    {this.props.progress &&
                        <rc_progress.Line percent={this.state.progress * (this.state.progress < 1.01 ? 100 : 1)} strokeWidth="2" trailWidth="2" className="progress" strokeColor="#FF52FF" />
                    }
                </div>
            </div>
        )
    }

    tick() {
        let next = this.state.dots + 1
        if (next > 3)
            next = 0
        this.setState({ dots: next })
    }
}