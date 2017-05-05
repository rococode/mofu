import SearchResult from './search-result'
import MangaSource from '../sources/manga-source'

interface SourceResult {
    results: SearchResult[],
    sourceName: string,
    source: MangaSource
}

export default SourceResult