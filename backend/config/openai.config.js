import dotenv from 'dotenv';

dotenv.config();

const openaiConfig = {
  apiKey: process.env.OPENAI_API_KEY,
  model: process.env.OPENAI_MODEL,
};

export default openaiConfig;
