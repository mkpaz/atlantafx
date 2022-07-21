module.exports = function (grunt) {
    const SASS_COMPILER = require('sass');
    const CSS_OUTPUT_DIR = process.env.NODE_ENV !== 'dev' ? 'dist' : '../sampler/target/classes/atlantafx/sampler/theme-test';

    grunt.initConfig({
        cssOutputDir: CSS_OUTPUT_DIR,
        sass: {

            options: {
                implementation: SASS_COMPILER
            },
            dist: {
                files: {
                    '<%= cssOutputDir %>/primer-light.css': [ 'src/primer-light.scss' ],
                    '<%= cssOutputDir %>/primer-dark.css':  [ 'src/primer-dark.scss'  ]
                }
            }
        },
        cssmin: {
            dist: {
                files: {}
            }
        }
    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.registerTask('default', ['sass', 'cssmin']);
};
