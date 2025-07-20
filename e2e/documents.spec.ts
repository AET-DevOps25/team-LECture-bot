import { test, expect } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';
import { login } from './helpers';

test.describe('Documents', () => {
  let courseSpaceName = 'Doc Test Multi';

  test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('button', { name: '✨ Create New Space' }).click();
    await page.getByRole('textbox', { name: 'Title' }).fill(courseSpaceName);
    await page.getByRole('textbox', { name: 'Description' }).fill('doc test multi');
    await page.getByRole('button', { name: 'Create', exact: true }).click();
    await expect(page.getByRole('link', { name: courseSpaceName })).toBeVisible();
    await page.close();
  });

  test('should upload and list basic-text.pdf in the course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('link', { name: new RegExp(courseSpaceName) }).click();
    await page.getByRole('button', { name: 'Documents' }).click();
    const documentsTab = page.getByRole('button', { name: 'Documents' });
    await documentsTab.scrollIntoViewIfNeeded();
    await expect(documentsTab).toBeVisible();
    await documentsTab.click();

    const __filename = fileURLToPath(import.meta.url);
    const __dirname = path.dirname(__filename);
    const pdfPath = path.resolve(__dirname, 'fixtures/basic-text.pdf');
    await page.getByTestId('pdf-input').setInputFiles(pdfPath);
    await page.getByRole('button', { name: 'Upload' }).click();
    await expect(page.getByText('Upload successful!')).toBeVisible();
    await page.getByRole('cell', { name: 'basic-text.pdf' }).click();
  });

  test('should delete basic-text.pdf in the course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('link', { name: new RegExp(courseSpaceName) }).click();
    await page.getByRole('button', { name: 'Documents' }).click();
    const documentsTab = page.getByRole('button', { name: 'Documents' });
    await documentsTab.scrollIntoViewIfNeeded();
    await expect(documentsTab).toBeVisible();
    await documentsTab.click();

    // Delete the uploaded file and verify it is removed
    await page.getByRole('button', { name: "Delete", exact: false }).click();
    await expect(page.getByText('basic-text.pdf')).not.toBeVisible();
  });

  test('should upload and list large-pdf.pdf in the course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('link', { name: new RegExp(courseSpaceName) }).click();
    await page.getByRole('button', { name: 'Documents' }).click();
    const documentsTab = page.getByRole('button', { name: 'Documents' });
    await documentsTab.scrollIntoViewIfNeeded();
    await expect(documentsTab).toBeVisible();
    await documentsTab.click();

    const __filename = fileURLToPath(import.meta.url);
    const __dirname = path.dirname(__filename);
    const largePdfPath = path.resolve(__dirname, 'fixtures/large-doc.pdf');
    await page.getByTestId('pdf-input').setInputFiles(largePdfPath);
    await page.getByRole('button', { name: 'Upload' }).click();
    await expect(page.getByText('PDF cannot be processed. It')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Dismiss error' })).toBeVisible();
  });


});


