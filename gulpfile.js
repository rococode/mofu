var gulp = require('gulp'),
    inSequence = require('run-sequence'),
    del = require('del'),
    inject = require('gulp-inject'),
    rename = require('gulp-rename'),
    concat = require('gulp-concat'),
    sourcemaps = require('gulp-sourcemaps'),
    typescript = require('gulp-typescript');

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

// Sass configuration
var gulp = require('gulp');
var sass = require('gulp-sass');

gulp.task('sass', function () {
    gulp.src('./src/sass/*.scss')
        .pipe(sass())
        .pipe(gulp.dest(function (f) {
            return "./src/assets/css/";
        }))
});

gulp.task('default', ['sass'], function () {
    gulp.watch('*.scss', ['sass']);
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

    return gulp.src([
        "node_modules/systemjs/dist/system.src.js",
    ])
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest('dist/js'));
});

gulp.task('build-app', function () {

    var project = typescript.createProject('tsconfig.json');

    var tsResult = project.src()
        .pipe(sourcemaps.init())
        .pipe(project());

    return tsResult.js
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('dist/js'));
});

gulp.task('build-html', function () {

    var sources = gulp.src(['dist/js/vendor.js', 'dist/js/app.js'], { read: false });

    return gulp.src('src/index.html')
        .pipe(inject(sources, { ignorePath: 'dist', addRootSlash: false }))
        .pipe(gulp.dest('dist'));
});