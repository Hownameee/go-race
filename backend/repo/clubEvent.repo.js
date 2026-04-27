import db from '../utils/db/db.js';

const clubEventRepo = {
    createEvent(clubId, creatorId, title, description, targetDistance, targetDurationSeconds, startTime, endTime) {
        const sql = `
            INSERT INTO CLUB_EVENTS (club_id, created_by, title, description, target_distance, target_duration_seconds, start_time, end_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        `;
        const info = db.prepare(sql).run(clubId, creatorId, title, description, targetDistance, targetDurationSeconds, startTime, endTime);
        return info.lastInsertRowid;
    },

    findEventsByClubId(clubId, userId) {
        const sql = `
            SELECT 
                e.event_id,
                e.club_id,
                e.title,
                e.description,
                e.target_distance,
                e.target_duration_seconds,
                e.start_time,
                e.end_time,
                CASE WHEN ep.user_id IS NOT NULL THEN 1 ELSE 0 END AS is_joined,
                COALESCE(ep.current_distance, 0) AS current_distance,
                COALESCE(ep.current_duration_seconds, 0) AS current_duration_seconds,
                e.participants_count,
                e.total_distance AS global_distance,
                e.total_duration_seconds AS global_duration_seconds
            FROM CLUB_EVENTS e
            LEFT JOIN CLUB_EVENT_PARTICIPANTS ep ON e.event_id = ep.event_id AND ep.user_id = ?
            WHERE e.club_id = ?
            ORDER BY e.created_at DESC
        `;
        return db.prepare(sql).all(userId, clubId);
    },

    joinEvent(eventId, userId) {
        const sql = `
            INSERT INTO CLUB_EVENT_PARTICIPANTS (event_id, user_id)
            VALUES (?, ?)
        `;
        return db.prepare(sql).run(eventId, userId);
    },

    findEventByIdAndClubId(eventId, clubId) {
        const sql = `SELECT * FROM CLUB_EVENTS WHERE event_id = ? AND club_id = ?`;
        return db.prepare(sql).get(eventId, clubId);
    },

    getEventStats(eventId) {
        const eventSql = `SELECT * FROM CLUB_EVENTS WHERE event_id = ?`;
        const event = db.prepare(eventSql).get(eventId);

        if (!event) return null;

        const participantsSql = `
            SELECT 
                u.user_id as member_id,
                u.fullname as member_name,
                u.avatar_url,
                ep.current_distance as distance,
                ep.current_duration_seconds as duration
            FROM CLUB_EVENT_PARTICIPANTS ep
            JOIN USERS u ON ep.user_id = u.user_id
            WHERE ep.event_id = ?
            ORDER BY ep.current_distance DESC, ep.current_duration_seconds DESC
            LIMIT 10
        `;
        const leaderboard = db.prepare(participantsSql).all(eventId);

        return {
            event_id: event.event_id,
            club_id: event.club_id,
            title: event.title,
            description: event.description,
            target_distance: event.target_distance,
            target_duration_seconds: event.target_duration_seconds,
            start_time: event.start_time,
            end_time: event.end_time,
            participants_count: event.participants_count,
            total_distance: event.total_distance,
            total_duration_seconds: event.total_duration_seconds,
            leaderboard: leaderboard
        };
    }
};

export default clubEventRepo;
