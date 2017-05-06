import electron from 'electron'

let uniqueId = 0;

/**
 * low priority get, limited by a default of 10 active windows
 */
let lowPrioritySearches = 0;
const lowPriorityMax = 10;

class Accessor {

    async  getLowPriority(url: string, validator?: (s: string) => boolean): Promise<string> {
        while (lowPrioritySearches >= lowPriorityMax) {
            await this.delay(250)
        }
        lowPrioritySearches++
        let res = this.get(url, validator)
        lowPrioritySearches--;
        return res;
    }

    async  get(url: string, validator?: (s: string) => boolean): Promise<string> {
        let win = new electron.BrowserWindow({
            center: false,
            x: 0,
            y: 0,
            skipTaskbar: true,
            autoHideMenuBar: true,
            width: 10,
            height: 10,
            show: false,
            title: "mofu searcher",
            webPreferences: {
                webSecurity: false,
                allowRunningInsecureContent: true,
                backgroundThrottling: false
            }
        })
        win.loadURL(url);
        // win.show();
        win.setTitle("mofu searcher")
        let myId = uniqueId++
        let promise: Promise<string> = undefined
        let listener = function (event, message) {
            let id = message.id;
            let doc = message.doc;
            if (id != myId) {
                return
            }
            console.log("got id " + id);
            if (!doc || (validator && !validator(doc)))
                return
            promise = doc;
            try {
                win.close();
            } catch (e) { }
            electron.ipcMain.removeListener('page-html', listener);
        }
        electron.ipcMain.on('page-html', listener)
        await this.delay(100)
        let js = `require('electron').ipcRenderer.send('page-html', {id: ` + myId + `, doc: document.documentElement.outerHTML});`
        while (promise === undefined) {
            win.webContents.executeJavaScript(js)
            console.log("Waiting for validation... " + url)
            await this.delay(250);
        }
        return promise
    }

    delay(ms: number) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}

let instance = new Accessor()
export default instance