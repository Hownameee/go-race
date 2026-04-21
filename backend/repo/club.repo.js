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

    findAdmins(clubId) {
        const sql = `
            SELECT 
                u.user_id, 
                u.fullname, 
                u.avatar_url, 
                cm.role,
                CASE WHEN c.leader_id = u.user_id THEN 1 ELSE 0 END as is_leader
            FROM CLUB_MEMBERS cm
            JOIN USERS u ON cm.user_id = u.user_id
            JOIN CLUBS c ON cm.club_id = c.club_id
            WHERE cm.club_id = ? 
              AND cm.role = 'admin' 
              AND cm.status = 'approved'
            ORDER BY is_leader DESC, u.fullname ASC;
        `;
        return db.prepare(sql).all(clubId);
    },

    checkIsLeader(clubId, userId) {
        const sql = `SELECT 1 FROM CLUBS WHERE club_id = ? AND leader_id = ?`;
        return !!db.prepare(sql).get(clubId, userId);
    },

    updateClub(clubId, { name, description, avatarS3Key }) {
        const fields = [];
        const values = [];

        if (name !== undefined && name !== null) {
            fields.push('name = ?');
            values.push(name);
        }
        if (description !== undefined && description !== null) {
            fields.push('description = ?');
            values.push(description);
        }
        if (avatarS3Key !== undefined && avatarS3Key !== null) {
            fields.push('avatar_s3_key = ?');
            values.push(avatarS3Key);
        }

        if (fields.length === 0) return;

        fields.push('updated_at = CURRENT_TIMESTAMP');
        values.push(clubId);

        const sql = `UPDATE CLUBS SET ${fields.join(', ')} WHERE club_id = ?`;
        return db.prepare(sql).run(...values);
    },

    getClubStats(clubId) {
        // 1. Get totals for the entire club
        const clubTotalsSql = `
            SELECT 
                COALESCE(SUM(r.distance_km), 0) as total_distance,
                COUNT(r.record_id) as total_activities
            FROM CLUB_MEMBERS cm
            JOIN RECORD r ON cm.user_id = r.owner_id
            WHERE cm.club_id = ? AND cm.status = 'approved'
        `;
        const clubTotals = db.prepare(clubTotalsSql).get(clubId);

        // 2. Get aggregated stats per member to find records and leaderboard
        const memberStatsSql = `
            SELECT 
                u.user_id as member_id, 
                u.fullname as member_name, 
                u.avatar_url, 
                COALESCE(SUM(r.distance_km), 0) as total_distance,
                COALESCE(SUM(r.duration_seconds), 0) as total_duration
            FROM CLUB_MEMBERS cm
            JOIN USERS u ON cm.user_id = u.user_id
            LEFT JOIN RECORD r ON cm.user_id = r.owner_id
            WHERE cm.club_id = ? AND cm.status = 'approved'
            GROUP BY u.user_id
            ORDER BY total_distance DESC
        `;
        const leaderboard = db.prepare(memberStatsSql).all(clubId);

        // Calculate club record (the max among member sums)
        let clubRecordDistance = 0;
        let clubRecordDuration = 0;
        if (leaderboard.length > 0) {
            // Since it's sorted by total_distance DESC, the first one has the record distance
            clubRecordDistance = leaderboard[0].total_distance;
            // Record duration might be different member, but usually 'Leading duration' refers to the leader's duration or the max duration
            clubRecordDuration = Math.max(...leaderboard.map(l => l.total_duration));
        }

        return {
            totalDistance: clubTotals.total_distance,
            totalActivities: clubTotals.total_activities,
            clubRecordDistance: clubRecordDistance,
            clubRecordDuration: clubRecordDuration,
            leaderboard: leaderboard
        };
    },
};

export default clubRepo;