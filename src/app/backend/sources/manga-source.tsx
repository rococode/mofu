import SearchResult from '../search/search-result'
import MangaInfo from '../abstracts/manga-info'
import MangaChapter from '../abstracts/manga-chapter'

abstract class MangaSource {
    abstract async search(phrase: string): Promise<SearchResult[]>
    abstract async getInfo(url: string): Promise<MangaInfo>
    abstract async loadChapter(chapter: MangaChapter): Promise<MangaChapter>
}

export default MangaSource

