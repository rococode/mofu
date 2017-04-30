const PouchDB = require('pouchdb');

export function initialize() {
    let db = new PouchDB('./test')
}
