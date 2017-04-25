import React from 'react';
const open = require('open');

export default class Footer extends React.Component<{}, {}> {
    public render() {
        return (
            <div className="footer unselectable">
                <div className="discord">
                    <a onClick={discord}> Join us on Discord! <img src="https://discordapp.com/api/guilds/268574742032809985/widget.png?style=shield" /></a>
                </div>
                <a onClick={edasaki}>&copy; Edasaki 2017</a> | Please remember to support the original authors and translation groups if you can!!
            </div>
        );
    }
}

function discord() {
    open("https://discord.gg/pMucKJ5")
}

function edasaki() {
    open("http://edasaki.com")
}