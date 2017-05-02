import { BodyState } from '../../body'
import MangaHere from '../sources/english/mangahere'
import SearchResult from './search-result'
import SourceResult from './source-result'

const electron = require('electron').remote

class SearchManager {

    constructor() {

    }

    public async search(callback: (state: BodyState, results?: SourceResult[]) => void, s: string) {
        let res: SourceResult[] = []
        res.push({
            sourceName: "MangaHere",
            results: await MangaHere.search(s)
        })
        callback(BodyState.SearchResults, res);
    }

}


function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let instance = new SearchManager();
export default instance