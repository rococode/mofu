import React from 'react';

export default class LoadingPage extends React.Component<{}, {}> {
    public render() {
        return (
            <div id="loading-container" className="loading-container">
                <div id="loading-bg-wrapper" className="loading-bg-wrapper">
                    <div className="loading-display">
                        <div className="sk-folding-cube">
                            <div className="sk-cube1 sk-cube"></div>
                            <div className="sk-cube2 sk-cube"></div>
                            <div className="sk-cube4 sk-cube"></div>
                            <div className="sk-cube3 sk-cube"></div>
                        </div>
                        <div className="loading-text">
                            Loading
					<div id="loading-dots" className="loading-dots">.</div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}