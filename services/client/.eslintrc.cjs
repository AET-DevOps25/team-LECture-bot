module.exports = {
  parser: '@typescript-eslint/parser',
  extends: [
    'eslint:recommended',                    // Basic JavaScript linting rules
    'plugin:react/recommended',             // Best practices for React
    'plugin:jsx-a11y/recommended',          // Accessibility rules for JSX (a11y)
    'plugin:react-hooks/recommended',       // Ensures rules of React hooks are followed
    'plugin:@typescript-eslint/recommended',// Adds TS-specific linting rules
    'prettier'                              // Disables ESLint rules that conflict with Prettier
  ],
  plugins: ['react', '@typescript-eslint'],
  settings: {
    react: {
      version: 'detect'                     // Automatically detect React version
    }
  }
};
