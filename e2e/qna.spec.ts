
import { login } from './helpers';

import { test, expect } from '@playwright/test';

test.describe('Q&A', () => {
  test('should login, create a course space, and open Q&A tab', async ({ page }) => {
    // Login as test user
    await login(page, 'tester@user.com', '12345678ab');

    // Go to dashboard
    await page.goto('/dashboard');

    // Create a new course space
    await page.getByRole('button', { name: '✨ Create New Space' }).click();
    await page.getByRole('textbox', { name: 'Title' }).fill('QnA Test Course');
    await page.getByRole('textbox', { name: 'Description' }).fill('for qna e2e');
    await page.getByRole('button', { name: 'Create', exact: true }).click();

    // Wait for the new course space to appear and click it
    await expect(page.getByRole('link', { name: 'QnA Test Course Delete course' })).toBeVisible();
    await page.getByRole('link', { name: 'QnA Test Course Delete course' }).click();

    // Click the Q&A tab
    const qnaTab = page.getByRole('button', { name: 'Q&A' });
    await qnaTab.scrollIntoViewIfNeeded();
    await expect(qnaTab).toBeVisible();
    await qnaTab.click();

    // Assert the Q&A tab is active
    await expect(page.getByRole('heading', { name: 'Course Q&A' })).toBeVisible();
    await expect(page.locator('div').filter({ hasText: /^Ask a question about your course materials!$/ }).first()).toBeVisible();
    await expect(page.getByText('Ask a question about your')).toBeVisible();
    await expect(page.getByRole('textbox', { name: 'Type your question...' })).toBeVisible();
  });
});
