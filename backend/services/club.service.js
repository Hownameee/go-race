import clubRepo from '../repo/club.repo.js';
import { getImageUrlS3 } from '../utils/s3/s3.js';

const clubService = {
    async getClubs(userId, offset, limit) {
        let clubs = await clubRepo.findMyClubs(userId, offset, limit);
        let type = 'my clubs';
        if (clubs.length === 0) {
            clubs = await clubRepo.findDiscoverClubs(userId, offset, limit);
            type = 'discover clubs';
        }

        const result = await Promise.all(
            clubs.map(async (item) => {
                const { avatar_s3_key, ...itemWithoutS3Key } = item;
                if (avatar_s3_key) {
                    const avatar_url = await getImageUrlS3(avatar_s3_key);
                    return {
                        ...itemWithoutS3Key,
                        avatar_url,
                        status: item.status,
                    };
                }
                return {
                    ...itemWithoutS3Key,
                    status: item.status,
                };
            }),
        );

        return { clubs: result, type };
    },

    async getClubByIdAndUserId(userId, clubId) {
        const club = await clubRepo.findByIdAndUserId(userId, clubId);
        if (!club) {
            throw new Error('Club not found');
        }
        const { avatar_s3_key, ...itemWithoutS3Key } = club;
        let avatar_url = null;
        if (avatar_s3_key) {
            avatar_url = await getImageUrlS3(avatar_s3_key);
        }
        const res = { ...itemWithoutS3Key, avatar_url };
        return res;
    },

    async joinClub(clubId, userId) {
        const club = await clubRepo.findById(clubId);
        if (!club) {
            throw new Error('Club not found');
        }

        if (club.privacy_type === 'public') {
            await clubRepo.addMember(clubId, userId, 'approved');
            return { message: "Joined", status: "approved" };
        } else {
            await clubRepo.addMember(clubId, userId, 'pending');
            return { message: "Request sent", status: "pending" };
        }
    },

    async leaveClub(clubId, userId) {
        const club = await clubRepo.findById(clubId);
        if (!club) {
            throw new Error('Club not found');
        }
        await clubRepo.removeMember(clubId, userId);
        return { message: "Left" };
    },

    async createClub(name, description, privacyType, leaderId) {
        if (!name) throw new Error('Club name is required');
        try {
            const clubId = clubRepo.createClub(name, description, privacyType, leaderId);
            return { club_id: clubId, message: 'Club created successfully' };
        } catch (error) {
            throw error;
        }
    },

    async getClubAdmins(clubId) {
        const admins = await clubRepo.findAdmins(clubId);
        return admins;
    },
};

export default clubService;