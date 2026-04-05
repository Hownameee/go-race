import dotenv from 'dotenv';

dotenv.config();

const mapConfig = {
  accessToken: process.env.MAPBOX_ACCESS_TOKEN,
};

export default mapConfig;
