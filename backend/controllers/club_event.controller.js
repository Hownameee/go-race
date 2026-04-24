import clubEventRepo from '../repo/clubEvent.repo.js';
import clubRepo from '../repo/club.repo.js';

const clubEventController = {
    async createEvent(req, res) {
        try {
            const userId = req.user.userId;
            const clubId = parseInt(req.params.clubId);
            const { title, description, target_distance, target_duration_seconds, start_time, end_time } = req.body;
            // fix call service
            const isLeader = clubRepo.checkIsLeader(clubId, userId);
            if (!isLeader) {
                return res.status(403).json({ success: false, message: 'Only club leader can create events' });
            }

            const eventId = clubEventRepo.createEvent(
                clubId,
                userId,
                title,
                description,
                target_distance || 0,
                target_duration_seconds || 0,
                start_time,
                end_time
            );

            res.status(201).json({ success: true, message: 'Event created successfully', data: { eventId } });
        } catch (error) {
            console.error('Create event error:', error);
            res.status(500).json({ success: false, message: 'Internal server error' });
        }
    },

    async getEvents(req, res) {
        try {
            const userId = req.user.userId;
            const clubId = parseInt(req.params.clubId);

            // Check if user has access to the club (can be a member or it's public)
            // But usually just fetching events for a club is allowed if they can view the club.
            const club = clubRepo.findById(clubId);
            if (!club) {
                return res.status(404).json({ success: false, message: 'Club not found' });
            }

            const events = clubEventRepo.findEventsByClubId(clubId, userId);

            res.status(200).json({ success: true, data: events });
        } catch (error) {
            console.error('Get events error:', error);
            res.status(500).json({ success: false, message: 'Internal server error' });
        }
    },

    async joinEvent(req, res) {
        try {
            const userId = req.user.userId;
            const clubId = parseInt(req.params.clubId);
            const eventId = parseInt(req.params.eventId);

            // 1. Verify event belongs to club
            const event = clubEventRepo.findEventByIdAndClubId(eventId, clubId);
            if (!event) {
                return res.status(404).json({ success: false, message: 'Event not found in this club' });
            }

            // 2. Verify user is approved member of club
            const clubWithMemberStatus = clubRepo.findByIdAndUserId(userId, clubId);
            if (!clubWithMemberStatus || clubWithMemberStatus.status !== 'approved') {
                return res.status(403).json({ success: false, message: 'You must be an approved member of the club to join its events' });
            }

            try {
                clubEventRepo.joinEvent(eventId, userId);
                res.status(200).json({ success: true, message: 'Successfully joined the event' });
            } catch (err) {
                if (err.code === 'SQLITE_CONSTRAINT_PRIMARYKEY') {
                    return res.status(400).json({ success: false, message: 'You have already joined this event' });
                }
                throw err;
            }
        } catch (error) {
            console.error('Join event error:', error);
            res.status(500).json({ success: false, message: 'Internal server error' });
        }
    },

    async getEventStats(req, res) {
        try {
            const clubId = parseInt(req.params.clubId);
            const eventId = parseInt(req.params.eventId);

            // Verify event belongs to club
            const event = clubEventRepo.findEventByIdAndClubId(eventId, clubId);
            if (!event) {
                return res.status(404).json({ success: false, message: 'Event not found in this club' });
            }

            const stats = clubEventRepo.getEventStats(eventId);
            if (!stats) {
                return res.status(404).json({ success: false, message: 'Event stats not found' });
            }

            res.status(200).json({ success: true, data: stats });
        } catch (error) {
            console.error('Get event stats error:', error);
            res.status(500).json({ success: false, message: 'Internal server error' });
        }
    }
};

export default clubEventController;
