import fs from 'fs';

export function readFileByPath(path) {
  const value = fs.readFileSync(path);
  return value.toString();
}
