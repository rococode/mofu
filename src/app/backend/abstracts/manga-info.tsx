import MangaChapter from './manga-chapter'
import MangaSource from '../sources/manga-source'

interface MangaInfo {
    image: string,
    title: string,
    author: string,
    description: string,
    sourceName: string,
    source: MangaSource,
    chapterCount: number,
    chapters: MangaChapter[]
    alt?: string,
    genres?: string,
    artist?: string
}

export default MangaInfo