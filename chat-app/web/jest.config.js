module.exports = {
  verbose: true,
  preset: '@vue/cli-plugin-unit-jest/presets/no-babel',
  moduleNameMapper: {
   "^@/components(.*)$": "<rootDir>/../web/src/components$1.vue",
   "^@/common(.*)$": "<rootDir>/../web/src/common$1.js",
   "^@/store(.*)$": "<rootDir>/../web/src/store$1.js",
 }
}
