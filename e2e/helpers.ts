// e2e/helpers.ts
import { Page, expect } from '@playwright/test';

export async function login(page: Page, email = 'test@user.com', password = '12345678') {
  await page.goto('/login');
  await page.getByRole('textbox', { name: 'Email' }).fill(email);
  await page.getByRole('textbox', { name: 'Password' }).fill(password);
  await page.getByRole('button', { name: 'Login' }).click();
  await expect(page.getByRole('link', { name: 'LECture-bot' })).toBeVisible();
}
