import dotenv from 'dotenv';

dotenv.config();

const s3Config = {
  accessKey: process.env.S3_ACCESS_KEY,
  secretAccessKey: process.env.S3_SECRET_ACCESS_KEY,
  bucket: process.env.S3_BUCKET,
  region: process.env.S3_REGION,
};

export default s3Config;
