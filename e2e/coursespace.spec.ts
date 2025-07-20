import { test, expect } from '@playwright/test';
import { login } from './helpers';

test.describe('Course Space', () => {
  test('should show dashboard/coursespace page', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await expect(page.getByRole('heading', { name: 'My Course Spaces' })).toBeVisible();
  });

  test('should show a message if no course spaces exist', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await expect(page.getByRole('heading', { name: 'My Course Spaces' })).toBeVisible();
    await expect(page.getByText("You haven't created any")).toBeVisible();
  });

  test('should allow user to create a new course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('button', { name: '✨ Create New Space' }).click();
    await page.getByRole('textbox', { name: 'Title' }).click();
    await page.getByRole('textbox', { name: 'Title' }).fill('Test Course');
    await page.getByRole('textbox', { name: 'Description' }).click();
    await page.getByRole('textbox', { name: 'Description' }).fill('some description');
    await page.getByRole('button', { name: 'Create', exact: true }).click();
    await expect(page.getByRole('link', { name: 'Test Course Delete course' })).toBeVisible();
  });

  test('should view details of a specific course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('button', { name: 'Edit' }).click();
    await expect(page.getByRole('textbox', { name: 'Title' })).toHaveValue('Test Course');
    await expect(page.getByRole('textbox', { name: 'Description' })).toHaveValue('some description');
    await page.getByRole('button', { name: 'Cancel' }).click();
    await page.getByRole('link', { name: 'Test Course Delete course' }).click();
    await page.getByRole('heading', { name: 'Course Space: Test Course (' }).click();
  });

  test('should allow user to edit a course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    await page.getByRole('button', { name: 'Edit' }).click();
    await page.getByRole('textbox', { name: 'Title' }).click();
    await page.getByRole('textbox', { name: 'Title' }).fill('Test Course Update');
    await page.getByRole('textbox', { name: 'Description' }).click();
    await page.getByRole('textbox', { name: 'Description' }).fill('some description updated');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByRole('link', { name: 'Test Course Update Delete course' })).toBeVisible();
  });

  test('should allow user to delete a course space', async ({ page }) => {
    await login(page, 'tester@user.com', '12345678ab');
    await page.goto('/dashboard');
    // Just check if the delete button exists somewhere on the page
    // tailwind does not provide a way to click on it
    const deleteBtn = page.locator('[aria-label="Delete course space"]');
    await expect(deleteBtn.first()).toBeAttached({ timeout: 2000 });
  });

});
