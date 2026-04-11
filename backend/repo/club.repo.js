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
                c.post_count
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
    findDiscoverClubs(offset, limit) {
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
                c.post_count
            FROM CLUBS c
            JOIN USERS u ON c.leader_id = u.user_id
            ORDER BY c.member_count DESC
            LIMIT ? OFFSET ?;
        `;
        return db.prepare(sql).all(limit, offset);
    },
};

export default clubRepo;