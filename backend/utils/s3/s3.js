import {
  GetObjectCommand,
  PutObjectCommand,
  S3Client,
} from '@aws-sdk/client-s3';
import s3Config from '../../config/s3.config.js';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';

const s3Client = new S3Client({
  region: s3Config.region,
  credentials: {
    accessKeyId: s3Config.accessKey,
    secretAccessKey: s3Config.secretAccessKey,
  },
});

export async function uploadImageS3(buffer, key, contentType = 'image/png') {
  try {
    const command = new PutObjectCommand({
      Bucket: s3Config.bucket,
      Key: key,
      Body: buffer,
      ContentType: contentType,
    });

    await s3Client.send(command);

    return {
      key,
      message: 'Upload successful',
    };
  } catch (error) {
    console.error('S3 Upload Error:', error);
    throw error;
  }
}

// expire in 12 hours
export async function getImageUrlS3(key, expiresIn = 3600 * 12) {
  try {
    const command = new GetObjectCommand({
      Bucket: s3Config.bucket,
      Key: key,
    });

    const url = await getSignedUrl(s3Client, command, {
      expiresIn,
    });

    return url;
  } catch (error) {
    console.error('S3 Get URL Error:', error);
    throw error;
  }
}
