import MangaSource from '../manga-source'
import get from '../../utils/accessor'
import Dictionary from '../../utils/dictionary'
import SearchResult from '../../search/search-result'

const cheerio: CheerioAPI = require('cheerio')


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
        let $ = cheerio.load(s);
        let mainLinks = $('.result_search > dl > dt > a.name_one')
        let res: SearchResult[] = []
        mainLinks.toArray().forEach(link => {
            let names: string[] = []
            let alt = $(link.parentNode.parentNode).children('dd')
            let altText = alt.text()
            altText = altText.substr(altText.indexOf(':') + 1).trim()
            names.push($(link).attr('rel'));
            altText.split(";").forEach(element => {
                element = element.trim()
                if (element.length > 0 && element.indexOf("...") == -1) {
                    names.push(element)
                } else if (element.indexOf("...") > -1) {
                    element.split("...").forEach(e => {
                        if (e.length > 0)
                            names.push(e.trim())
                    });
                }
            });
            let href: string = $(link).attr("href")
            res.push({
                title: names[0],
                altNames: names.length > 1 ? names.slice(1) : undefined,
                url: href
            })
        });
        return res
    }
}

let instance = new MangaHere()
export default instance