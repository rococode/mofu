import { BodyState } from 'frontend/enums'
import { MangaHere } from 'backend/sources'
import { SearchResult, SourceResult } from 'backend/search'

class SearchManager {

    constructor() {

    }

    public async search(callback: (state: BodyState, results?: SourceResult[]) => void, s: string) {
        let res: SourceResult[] = []
        res.push({
            sourceName: "MangaHere",
            results: await this.timeout(MangaHere.search(s)),
            source: MangaHere
        })
        callback(BodyState.SearchResults, res);
    }

    // forces a timeout of 10 seconds
    timeout(x) {
        return Promise.race([x, new Promise(function (resolve, reject) {
            setTimeout(resolve, 10000, [])
        })]);
    }

}


function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let instance = new SearchManager();
export default instance