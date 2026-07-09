import js from "@eslint/js";
import globals from "globals";

export default [
  js.configs.recommended,

  {
    files: ["app/src/main/resources/static/js/**/*.js"],

    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",

      globals: {
        ...globals.browser
      }
    },

    rules: {
      "no-undef": "error",
      "no-unused-vars": "warn",
      "no-console": "off",
      "no-case-declarations": "off",
      "eqeqeq": "error",
      "curly": ["error", "multi-line"]
    }
  }
];
