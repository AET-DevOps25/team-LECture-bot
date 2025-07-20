import { test, expect } from '@playwright/test';
import { login } from './helpers';

test.describe('Profile', () => {
  test('should redirect unauthenticated user to login', async ({ page }) => {
    await page.goto('/profile');
    await page.getByRole('heading', { name: 'Login to LECture-bot' }).click();
  });

  test('should show profile page for authenticated user', async ({ page }) => {
    await login(page);
    await page.goto('/profile');
    await page.getByRole('heading', { name: 'User Profile' }).click();
    await expect(page.getByRole('textbox', { name: 'Name' })).toHaveValue('Test User');
    await expect(page.getByRole('textbox', { name: 'Email' })).toHaveValue('test@user.com');
  });


  test('should allow user to change name', async ({ page }) => {
    await login(page);
    await page.goto('/profile');
    await page.getByRole('textbox', { name: 'Name' }).click();
    await page.getByRole('textbox', { name: 'Name' }).press('ControlOrMeta+Shift+ArrowLeft');
    await page.getByRole('textbox', { name: 'Name' }).press('ControlOrMeta+Shift+ArrowLeft');
    await page.getByRole('textbox', { name: 'Name' }).fill('Tester User');
    // Wait for dialog and click at the same time
    await Promise.all([
      page.waitForEvent('dialog').then(async dialog => {
        console.log(`Dialog message: ${dialog.message()}`);
        await dialog.dismiss();
      }),
      page.getByRole('button', { name: 'Update Profile' }).click()
    ]);
    await expect(page.getByRole('textbox', { name: 'Name' })).toHaveValue('Tester User');
  });

  test('should allow user to change email and be logged out', async ({ page }) => {
    await login(page);
    await page.goto('/profile');
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill('tester@user.com');
    // Wait for dialog and click at the same time
    await Promise.all([
      page.waitForEvent('dialog').then(async dialog => {
        console.log(`Dialog message: ${dialog.message()}`);
        await dialog.dismiss();
      }),
      page.getByRole('button', { name: 'Update Profile' }).click()
    ]);
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
    // Login with new email
    await login(page, 'tester@user.com');
    await page.getByRole('link', { name: 'LECture-bot' }).click();
    await page.getByRole('heading', { name: 'My Course Spaces' }).click
    await page.getByText('You haven\'t created any').click();
  });

  test('should allow user to change password', async ({ page }) => {
    await login(page, 'tester@user.com');
    await page.goto('/profile');
    await page.getByRole('textbox', { name: 'Current Password' }).click();
    await page.getByRole('textbox', { name: 'Current Password' }).fill('12345678');
    await page.getByRole('textbox', { name: 'New Password', exact: true }).click();
    await page.getByRole('textbox', { name: 'New Password', exact: true }).fill('12345678ab');
    await page.getByRole('textbox', { name: 'Confirm New Password' }).dblclick();
    await page.getByRole('textbox', { name: 'Confirm New Password' }).fill('12345678ab');
    // Wait for dialog and click at the same time
    await Promise.all([
      page.waitForEvent('dialog').then(async dialog => {
        console.log(`Dialog message: ${dialog.message()}`);
        await dialog.dismiss();
      }),
      page.getByRole('button', { name: 'Change Password' }).click()
    ]);
    // logout user
    await page.getByRole('button', { name: 'Logout' }).click();
    // Expect to be logged out (e.g., see login page)
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
    // Login with new password
    await login(page, 'tester@user.com', '12345678ab');
    await page.getByRole('link', { name: 'LECture-bot' }).click();
    await page.getByRole('heading', { name: 'My Course Spaces' }).click();
    await page.getByText('You haven\'t created any').click();
  });

  test('should show error for incorrect current password', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/profile');
    await page.getByRole('textbox', { name: 'Current Password' }).click();
    await page.getByRole('textbox', { name: 'Current Password' }).fill('wrongpassword');
    await page.getByRole('textbox', { name: 'New Password', exact: true }).click();
    await page.getByRole('textbox', { name: 'New Password', exact: true }).fill('newpassword123');
    await page.getByRole('textbox', { name: 'Confirm New Password' }).click();
    await page.getByRole('textbox', { name: 'Confirm New Password' }).fill('newpassword123');
    await page.getByRole('button', { name: 'Change Password' }).click();
    // logout user
    await page.getByRole('button', { name: 'Logout' }).click();
    // Expect to see login page
    await expect(page.getByRole('heading', { name: 'Login to LECture-bot' })).toBeVisible();
    // Try to login with new password
    await page.goto('/login');
    // Instead of: await login(page, 'tester@user.com', 'newpassword123');
    await page.getByRole('textbox', { name: 'Email' }).fill('tester@user.com');
    await page.getByRole('textbox', { name: 'Password' }).fill('newpassword123');
    await page.getByRole('button', { name: 'Login' }).click();
    await expect(page.getByText('Invalid credentials')).toBeVisible();
  });
});
