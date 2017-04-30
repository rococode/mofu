const electron = require('electron').remote

export default async function get(url: string, validator?: (s: string) => boolean): Promise<string> {
    let win = new electron.BrowserWindow({
        center: false,
        x: 0,
        y: 0,
        skipTaskbar: true,
        autoHideMenuBar: true,
        width: 10,
        height: 10,
        title: "mofu searcher",
        webPreferences: {
            webSecurity: false,
            allowRunningInsecureContent: true,
            backgroundThrottling: false
        }
    })
    win.on('closed', () => { win = null })
    win.hide();
    win.loadURL(url);
    await delay(100)
    win.show();
    win.setTitle("mofu searcher")
    // win.webContents.executeJavaScript(`
    // require('electron').ipcRenderer.on('ping', function(event, message) {
    // require('electron').ipcRenderer.send('page-html', document.documentElement.outerHTML);
    // })`)
    let promise: Promise<string> = undefined
    electron.ipcMain.on('page-html', function (event, message) {
        console.log("received " + message);
        if (validator && !validator(message))
            return
        promise = message;
        win.close();
    });
    await delay(200)
    let js = `require('electron').ipcRenderer.send('page-html', document.documentElement.outerHTML);`
    while (promise === undefined) {
        win.webContents.executeJavaScript(js)
        console.log("waiting more...")
        await delay(250);
    }
    return promise
}

function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}