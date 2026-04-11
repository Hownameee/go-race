import db from '../utils/db/db.js';

const clubRepo = {
  getSuggestClubs: (currentUserId, limit) => {
    const sql = `
      SELECT 
          c.club_id, 
          c.name, 
          c.description, 
          c.avatar_s3_key, 
          c.privacy_type, 
          c.member_count,
          EXISTS(
              SELECT 1 FROM CLUB_MEMBERS cm 
              WHERE cm.club_id = c.club_id AND cm.user_id = ?
          ) as is_joined
      FROM CLUBS c
      WHERE c.club_id NOT IN (
          SELECT club_id FROM CLUB_MEMBERS WHERE user_id = ?
      )
      ORDER BY RANDOM()
      LIMIT ?
    `;

    return db.prepare(sql).all(currentUserId, currentUserId, limit);
  },

  searchClubsByName: (currentUserId, search, limit) => {
    const keyword = search
      .trim()
      .split(/\s+/)
      .filter(Boolean)
      .map((w) => w + '*')
      .join(' ');

    const sql = `
      SELECT 
          c.club_id, 
          c.name, 
          c.description, 
          c.avatar_s3_key, 
          c.privacy_type, 
          c.member_count,
          EXISTS(
              SELECT 1 FROM CLUB_MEMBERS cm 
              WHERE cm.club_id = c.club_id AND cm.user_id = ?
          ) as is_joined
      FROM CLUBS c
      JOIN CLUB_FTS fts ON c.club_id = fts.rowid
      WHERE CLUB_FTS MATCH ?
      ORDER BY bm25(CLUB_FTS)
      LIMIT ?
    `;

    return db.prepare(sql).all(currentUserId, keyword, limit);
  },

  joinClub: (clubId, userId, status) => {
    const sql = `
      INSERT INTO CLUB_MEMBERS (club_id, user_id, status)
      VALUES (?, ?, ?)
    `;
    return db.prepare(sql).run(clubId, userId, status);
  },
};

export default clubRepo;
