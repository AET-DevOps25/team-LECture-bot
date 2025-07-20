import { expect } from '@playwright/test';
import { login } from './helpers';

import path from 'path';
import { fileURLToPath } from 'url';

import { test } from '@playwright/test';

test.describe('Flashcards', () => {
  test('should login, create a course space, upload a PDF, and open Flashcards tab', async ({ page }) => {
    // Login as test user
    await login(page, 'tester@user.com', '12345678ab');

    // Go to dashboard
    await page.goto('/dashboard');

    // Create a new course space
    await page.getByRole('button', { name: '✨ Create New Space' }).click();
    await page.getByRole('textbox', { name: 'Title' }).fill('Flashcards Test Course');
    await page.getByRole('textbox', { name: 'Description' }).fill('for flashcards e2e');
    await page.getByRole('button', { name: 'Create', exact: true }).click();

    // Wait for the new course space to appear and click it
    await expect(page.getByRole('link', { name: 'Flashcards Test Course Delete course' })).toBeVisible();
    await page.getByRole('link', { name: 'Flashcards Test Course Delete course' }).click();

    // Go to the Documents tab first
    await page.getByRole('button', { name: 'Documents' }).click();
    const documentsTab = page.getByRole('button', { name: 'Documents' });
    await documentsTab.scrollIntoViewIfNeeded();
    await expect(documentsTab).toBeVisible();
    await documentsTab.click();

    // Upload the basic-text.pdf file
    const __filename = fileURLToPath(import.meta.url);
    const __dirname = path.dirname(__filename);
    const pdfPath = path.resolve(__dirname, 'fixtures/basic-text.pdf');
    await page.getByTestId('pdf-input').setInputFiles(pdfPath);
    await page.getByRole('button', { name: 'Upload' }).click();
    await expect(page.getByText('Upload successful!')).toBeVisible();

    // Assert the file appears in the list (adjust selector as needed)
    await expect(page.getByRole('cell', { name: 'basic-text.pdf' })).toBeVisible();

    // Now go to the Flashcards tab (use the same approach as Documents)
    const flashcardsTab = page.getByRole('button', { name: 'Flashcards' });
    await flashcardsTab.scrollIntoViewIfNeeded();
    await expect(flashcardsTab).toBeVisible();
    await flashcardsTab.click();
    await expect(page.getByRole('button', { name: '✨ Generate Flashcards' })).toBeVisible();
    await expect(page.getByText('No flashcards generated yet.')).toBeVisible();
    await expect(page.getByText('Click the button to start.')).toBeVisible();
    await page.getByRole('button', { name: '✨ Generate Flashcards' }).click();

    // Wait for the flashcard question to appear (longer timeout for slow generation)
    const questionP = page.locator('div.flex.items-center.justify-center.cursor-pointer > p.text-2xl.font-semibold.text-gray-800');
    await expect(questionP).toBeVisible({ timeout: 30000 });
    await expect(await questionP.textContent()).not.toBe('');
    await expect(page.getByRole('button', { name: 'Shuffle Deck' })).toBeVisible();
    await page.getByRole('button', { name: 'Shuffle Deck' }).click();
    await expect(page.getByRole('button', { name: 'Next →' })).toBeVisible();
    await page.getByRole('button', { name: 'Next →' }).click();
    const questionP2 = page.locator('div.flex.items-center.justify-center.cursor-pointer > p.text-2xl.font-semibold.text-gray-800');
    await expect(questionP2).toBeVisible({ timeout: 30000 });
    await expect(await questionP2.textContent()).not.toBe('');
  });
});
