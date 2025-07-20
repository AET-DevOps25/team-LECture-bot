import request from 'supertest';
import path from 'path';
import * as fs from 'fs';

const API_GATEWAY_URL = 'http://localhost:8080'; // Adjust if needed
const COURSE_SPACE_ID = 'test-course-space'; // Use a valid or test courseSpaceId

describe('API Gateway Document Integration', () => {
  it('should upload and list a document via the document microservice', async () => {
    // Prepare a sample PDF file for upload
    const filePath = path.join(__dirname, 'sample.pdf');
    fs.writeFileSync(filePath, 'PDF-1.4 sample'); // Create a dummy file if needed

    // Upload the document (multipart/form-data)
    const uploadRes = await request(API_GATEWAY_URL)
      .post(`/api/v1/documents/${COURSE_SPACE_ID}`)
      .attach('files', filePath)
      .expect(200);

    expect(Array.isArray(uploadRes.body)).toBe(true);
    expect(uploadRes.body[0]).toHaveProperty('id');
    const docId = uploadRes.body[0].id;

    // List documents for the course space
    const listRes = await request(API_GATEWAY_URL)
      .get(`/api/v1/documents/${COURSE_SPACE_ID}`)
      .expect(200);

    expect(Array.isArray(listRes.body)).toBe(true);
    expect(listRes.body.some((doc: any) => doc.id === docId)).toBe(true);

    // Clean up: remove the dummy file
    fs.unlinkSync(filePath);
  });
});