import { BodyState } from 'frontend/enums'
import { MangaHere } from 'backend/sources'
import { SearchResult, SourceResult } from 'backend/search'
import { loading, stoploading } from 'frontend/loading'

class SearchManager {

    constructor() {

    }

    public async search(callback: (state: BodyState, results?: SourceResult[]) => void, s: string) {
        let curr = 0.0
        loading(0.50, "Searching MangaHere...");
        let res: SourceResult[] = []
        res.push({
            sourceName: "MangaHere",
            results: await this.timeout(MangaHere.search(s), curr),
            source: MangaHere
        })
        stoploading()
        callback(BodyState.SearchResults, res);
    }

    // forces a timeout of 10 seconds
    timeout(x, prog : number) {
        return Promise.race([x, new Promise(function (resolve, reject) {
            setTimeout(resolve, 2000, [])
            setTimeout(resolve, 2000, [])
            setTimeout(resolve, 2000, [])
            setTimeout(resolve, 2000, [])
            setTimeout(resolve, 2000, [])
        })]);
    }

}


function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let instance = new SearchManager();
export default instance