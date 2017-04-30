import SearchResult from '../search/search-result'
import MangaSeries from '../abstracts/manga-series'

abstract class MangaSource {
    abstract async search(phrase: string): Promise<SearchResult[]>
    abstract async load(url: string): Promise<MangaSeries>
}

export default MangaSource

