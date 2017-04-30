import { BodyState } from '../../body'
import MangaHere from '../sources/english/mangahere'

const electron = require('electron').remote

class SearchManager {

    constructor() {

    }

    public async search(callback: (state: BodyState) => void, s: string) {
        MangaHere.search(s);
        callback(BodyState.SearchResults);
    }

}


function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let instance = new SearchManager();
export default instance