import electron from 'moelectron'

let uniqueId = 0;

/**
 * low priority get, limited by a default of 10 active windows
 */
let lowPrioritySearches = 0;
const lowPriorityMax = 10;

let windows = []

class Accessor {

    async getLowPriority(url: string, validator?: (s: string) => boolean): Promise<string> {
        while (lowPrioritySearches >= lowPriorityMax) {
            await this.delay(250)
        }
        lowPrioritySearches++
        let res = this.get(url, validator)
        lowPrioritySearches--;
        return res;
    }

    async getAll(urls: string[], validator?: (s: string) => boolean): Promise<string[]> {
        let res : Promise<string>[] = []
        urls.forEach((element, index) => {
            res.push(this.get(element, validator));
        })
        return Promise.all(res)
    }

    async get(url: string, validator?: (s: string) => boolean): Promise<string> {
        let win = new electron.BrowserWindow({
            center: false,
            x: 0,
            y: 0,
            autoHideMenuBar: true,
            width: 100,
            height: 100,
            skipTaskbar: true,
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
        let firstValidate = false;
        let listener = function (event, message) {
            let id = message.id;
            let doc = message.doc;
            if (id != myId) {
                return
            }
            console.log("got id " + id);
            if (!doc || (validator && !validator(doc)))
                return
            if(!firstValidate) { 
                // an extra 100ms won't hurt, and a second check adds a great layer of redundancy
                firstValidate = true;
                return    
            }
            promise = doc;
            try {
                win.close();
            } catch (e) { }
            electron.ipcMain.removeListener('page-html', listener);
        }
        electron.ipcMain.on('page-html', listener)
        let js = `require('electron').ipcRenderer.send('page-html', {id: ` + myId + `, doc: document.documentElement.outerHTML});`
        while (promise === undefined) {
            win.webContents.executeJavaScript(js)
            console.log("Waiting for validation... " + url)
            await this.delay(100);
        }
        return promise
    }

    delay(ms: number) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}

let instance = new Accessor()
export default instance