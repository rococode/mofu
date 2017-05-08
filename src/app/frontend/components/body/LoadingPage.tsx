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

    componentWillReceiveProps(props) {
        this.setState({ progress: props.progress });
    }

    public render() {
        let res = this.state.progress
        if (res < 1) {
            res *= 100;
        }
        if(res < 0) {
            res = 0;
        }
        res = Math.ceil(res)
        console.log(res)
        let progress
        if (this.props.progress) {
            progress = <rc_progress.Line percent={res} strokeWidth="2" trailWidth="2" className="progress" strokeColor="#FF52FF" />
        } else {
            progress = <rc_progress.Line percent={0} strokeWidth="2" trailWidth="2" className="progress" strokeColor="#FF52FF" />
        }
        return (
            <div className="loading-container">
                <div className="wrapper">
                    {this.state && this.props.message &&
                        <div className="message">
                            {this.props.message}
                        </div>
                    }
                    {progress}
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