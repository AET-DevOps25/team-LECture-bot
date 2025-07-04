//export default tseslint.config(
//  { ignores: ['dist'] },
//  {
//    extends: [js.configs.recommended, ...tseslint.configs.recommended],
//    files: ['**/*.{ts,tsx}'],
//    languageOptions: {
//      ecmaVersion: 2020,
//      globals: globals.browser,
//    },
//    plugins: {
//      'react-hooks': reactHooks,
//      'react-refresh': reactRefresh,
//    },
//    rules: {
//      ...reactHooks.configs.recommended.rules,
//      'react-refresh/only-export-components': [
//        'warn',
//        { allowConstantExport: true },
//      ],
//    },
//  },
//)
// eslint.config.js
import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';

export default tseslint.config(
  // 1. Global ignores
  {
    ignores: ['dist', 'node_modules', 'eslint.config.js'],
  },

  // 2. The default recommended configs
  js.configs.recommended,
  ...tseslint.configs.recommended,

  // 3. A specific config for your TypeScript/React files
  {
    files: ['src/**/*.{ts,tsx}'], // Be more specific, e.g., 'src/**/*.{ts,tsx}'
    languageOptions: {
      globals: {
        ...globals.browser,
      },
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      // You can add or override other rules here
      // e.g. '@typescript-eslint/no-explicit-any': 'off',
    },
  }
);
