import db from '../utils/db/db.js';

const clubRepo = {
    // Get clubs the user is a member of (approved)
    findMyClubs(userId, offset, limit) {
        const sql = `
            SELECT 
                c.club_id, 
                c.name, 
                c.description, 
                c.avatar_s3_key, 
                c.privacy_type,
                c.leader_id,
                u.fullname AS leader_name,
                c.member_count,
                c.post_count,
                cm.status
            FROM CLUBS c
            JOIN CLUB_MEMBERS cm ON c.club_id = cm.club_id
            JOIN USERS u ON c.leader_id = u.user_id
            WHERE cm.user_id = ? 
              AND cm.status = 'approved' 
            ORDER BY c.updated_at DESC
            LIMIT ? OFFSET ?;
        `;
        return db.prepare(sql).all(userId, limit, offset);
    },

    // Get public clubs the user has NOT joined
    findDiscoverClubs(userId, offset, limit) {
        const sql = `
            SELECT 
                c.club_id, 
                c.name, 
                c.description, 
                c.avatar_s3_key, 
                c.privacy_type,
                c.leader_id,
                u.fullname AS leader_name,
                c.member_count,
                c.post_count,
                cm.status
            FROM CLUBS c
            JOIN USERS u ON c.leader_id = u.user_id
            LEFT JOIN CLUB_MEMBERS cm ON c.club_id = cm.club_id AND cm.user_id = ?
            ORDER BY c.member_count DESC
            LIMIT ? OFFSET ?;
        `;
        return db.prepare(sql).all(userId, limit, offset);
    },

    findById(clubId) {
        const sql = `SELECT * FROM CLUBS WHERE club_id = ?`;
        return db.prepare(sql).get(clubId);
    },

    findByIdAndUserId(userId, clubId) {
        const sql = `            
                SELECT
                c.club_id, 
                c.name, 
                c.description, 
                c.avatar_s3_key, 
                c.privacy_type,
                c.leader_id,
                u.fullname AS leader_name,
                c.member_count,
                c.post_count,
                cm.status
            FROM CLUBS c
            JOIN USERS u ON c.leader_id = u.user_id
            LEFT JOIN CLUB_MEMBERS cm ON c.club_id = cm.club_id AND cm.user_id = ?
            WHERE c.club_id = ?;
            `;
        return db.prepare(sql).get(userId, clubId);
    },

    addMember(clubId, userId, status) {
        const sql = `
            INSERT INTO CLUB_MEMBERS (club_id, user_id, role, status)
            VALUES (?, ?, 'member', ?);
        `;
        return db.prepare(sql).run(clubId, userId, status);
    },

    removeMember(clubId, userId) {
        const sql = `DELETE FROM CLUB_MEMBERS WHERE club_id = ? AND user_id = ?;`;
        return db.prepare(sql).run(clubId, userId);
    },

    createClub(name, description, privacyType, leaderId) {
        return db.transaction(() => {
            const insertClubSql = `
                INSERT INTO CLUBS (name, description, privacy_type, leader_id)
                VALUES (?, ?, ?, ?);
            `;
            const info = db.prepare(insertClubSql).run(name, description, privacyType, leaderId);
            const clubId = info.lastInsertRowid;

            const insertLeaderSql = `
                INSERT INTO CLUB_MEMBERS (club_id, user_id, role, status)
                VALUES (?, ?, 'admin', 'approved');
            `;
            db.prepare(insertLeaderSql).run(clubId, leaderId);

            return clubId;
        })();
    },
};

export default clubRepo;