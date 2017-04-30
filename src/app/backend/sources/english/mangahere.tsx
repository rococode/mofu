import MangaSource from '../manga-source'
import get from '../../utils/accessor'

export class MangaHere extends MangaSource {

    searchPre = "http://www.mangahere.co/search.php?name_method=cw&author_method=cw&artist_method=cw&advopts=1&name="

    async load(url: string) {
        return undefined;
    }

    async search(phrase: string) {
        let url = this.searchPre + phrase;
        let s: string = await get(url, function (s) {
            return s.indexOf("result_search") >= 0
        });
        console.log("got " + s);
        return undefined
    }
}

let instance = new MangaHere()
export default instance