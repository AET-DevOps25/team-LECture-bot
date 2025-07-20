import { test, expect } from '@playwright/test';

test.describe('Login and Register', () => {
  test('should show login page (default)', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Register here' })).toBeVisible();
  });

  test('should show register page', async ({ page }) => {
    await page.goto('/register');
    await page.getByRole('heading', { name: 'Create an Account' }).click();
  });

  test('should allow user to register (happy path)', async ({ page }) => {
    await page.goto('/register');
    await expect(page.getByRole('heading', { name: 'Create an Account' })).toBeVisible();
    await page.getByRole('textbox', { name: 'Full Name' }).fill('Test User');
    await page.getByRole('textbox', { name: 'Email' }).fill('test@user.com');
    await page.getByRole('textbox', { name: 'Password' }).fill('12345678');
    await page.getByRole('button', { name: 'Register' }).click();
    await expect(page.getByText('Registration successful')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
  });

  test('should show error if email already exists', async ({ page }) => {
    await page.goto('/register');
    await page.getByRole('textbox', { name: 'Full Name' }).click();
    await page.getByRole('textbox', { name: 'Full Name' }).fill('Test User');
    await page.getByRole('textbox', { name: 'Full Name' }).press('Tab');
    await page.getByRole('textbox', { name: 'Email' }).fill('test@user.com');
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('12345678');
    await page.getByRole('button', { name: 'Register' }).click();
    await expect(page.getByText('Email already exists')).toBeVisible();
  });

  test('should allow user to login (happy path)', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('textbox', { name: 'Email' }).click();
  await page.getByRole('textbox', { name: 'Email' }).fill('test@user.com');
  await page.getByRole('textbox', { name: 'Email' }).press('Tab');
  await page.getByRole('textbox', { name: 'Password' }).fill('12345678');
  await page.getByRole('button', { name: 'Login' }).click();
  await expect(page.getByRole('link', { name: 'LECture-bot' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'My Course Spaces' })).toBeVisible();
  await expect(page.getByText("You haven't created any")).toBeVisible();
  });

  test('should show error for invalid email on login', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('test@use.com');
    await page.getByRole('textbox', { name: 'Email' }).press('Tab');
    await page.getByRole('textbox', { name: 'Password' }).fill('12345678');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByText('Invalid credentials').click();
  });

  test('should show error for invalid password on login', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('test@user.com');
    await page.getByRole('textbox', { name: 'Email' }).press('Tab');
    await page.getByRole('textbox', { name: 'Password' }).fill('1234567');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByText('Invalid credentials').click();
  });

  test('should logout user', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('textbox', { name: 'Email' }).fill('test@user.com');
    await page.getByRole('textbox', { name: 'Password' }).fill('12345678');
    await page.getByRole('button', { name: 'Login' }).click();
    await page.getByRole('link', { name: 'LECture-bot' }).click();
    await page.getByRole('heading', { name: 'My Course Spaces' }).click();
    await page.getByText('You haven\'t created any').click();
    await page.getByRole('button', { name: 'Logout' }).click();
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
  });

});
