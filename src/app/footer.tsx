import React from 'react';

export default class Footer extends React.Component<{}, {}> {
    public render() {
        return (
            <div className="footer unselectable">
                <div className="discord">
                    <a href="https://discord.gg/pMucKJ5" target="_blank"> Join us on Discord! <img src="https://discordapp.com/api/guilds/268574742032809985/widget.png?style=shield" /></a>
                </div>
                <a href="http://edasaki.com" target="_blank">&copy; Edasaki 2017</a> | Please remember to support the original authors and translation groups if you can!!
            </div>
        );
    }
}