import MangaPage from './manga-page'
import MangaSource from '../sources/manga-source'

interface MangaChapter {
    name: string,
    url: string,
    originalURL: string,
    source: MangaSource,
    prevChapter? : MangaChapter,
    nextChapter? : MangaChapter,
    pages? : MangaPage[]
}

export default MangaChapter