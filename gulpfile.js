var gulp = require('gulp');
var inSequence = require('run-sequence');
var del = require('del');
var inject = require('gulp-inject');
var rename = require('gulp-rename');
var concat = require('gulp-concat');
var sourcemaps = require('gulp-sourcemaps');
var typescript = require('gulp-typescript');
var sass = require('gulp-sass');

gulp.task('build-all', function (done) {
    inSequence(
        [
            'clear',
            'sass',
            'build-vendor',
            'build-app',
            'build-html',
            'copy-app-package-file',
            'copy-app-main-file',
            'copy-assets'
        ]
    );
});

gulp.task('watch', function () {
    gulp.watch('./src/sass/*.scss', ['sass']);
    gulp.watch('./src/app/**/*', ['build-app']);
    gulp.watch('./src/app.package.json', ['copy-app-package-file']);
    gulp.watch('./src/main.js', ['copy-app-main-file']);
    gulp.watch('./src/assets/**/*', ['copy-assets']);
    gulp.watch('./src/index.html', ['build-html']);

});

gulp.task('sass', function () {
    gulp.src('./src/sass/*.scss')
        .pipe(sass())
        .pipe(gulp.dest(function (f) {
            return "./src/assets/css/";
        }))
});

gulp.task('clear', function (done) {
    del.sync(['dist/**/*'], { force: true });
    done();
});

gulp.task('copy-app-package-file', function () {
    return gulp.src('src/app.package.json')
        .pipe(rename('package.json'))
        .pipe(gulp.dest('dist'));
});

gulp.task('copy-app-main-file', function () {
    return gulp.src('src/main.js')
        .pipe(gulp.dest('dist'));
});

gulp.task('copy-assets', function () {
    return gulp.src('src/assets/**/*').pipe(gulp.dest('dist/assets'));
});

gulp.task('build-vendor', function () {
    return gulp.src(["node_modules/systemjs/dist/system.src.js"])
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest('dist/js'));
});

var project = typescript.createProject('tsconfig.json');
gulp.task('build-app', function () {
    // var tsResult = project.src()
    var tsResult = project.src()
        .pipe(sourcemaps.init())
        .pipe(project());
    return tsResult.js
        .pipe(sourcemaps.write('../maps', { sourceRoot: "../../src/app" }))
        .pipe(gulp.dest('dist/js'));
});

gulp.task('build-html', function () {
    var sources = gulp.src(['dist/js/vendor.js', 'dist/js/app.js'], { read: false });
    return gulp.src('src/index.html')
        .pipe(inject(sources, { ignorePath: 'dist', addRootSlash: false }))
        .pipe(gulp.dest('dist'));
});