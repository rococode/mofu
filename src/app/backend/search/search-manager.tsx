import { BodyState } from 'frontend/enums'
import { MangaHere } from 'backend/sources'
import { SearchResult, SourceResult } from 'backend/search'
import { loading, stoploading } from 'frontend/loading'

class SearchManager {

    constructor() {

    }

    public async search(callback: (searchPhrase: string, results: SourceResult[]) => void, s: string) {
        console.log("Searching " + s)
        let curr = 0.0
        loading(0.50, "Searching MangaHere...");
        let res: SourceResult[] = []
        let r = await MangaHere.search(encodeURI(s))
        console.log("done to " + r)
        res.push({
            sourceName: "MangaHere",
            results: r,
            source: MangaHere
        })
        stoploading()
        callback(s, res);
    }
}

function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

let instance = new SearchManager();
export default instance