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

    async joinClub(clubId, userId) {
        const club = await clubRepo.findById(clubId);
        if (!club) {
            throw new Error('Club not found');
        }

        const status = club.privacy_type === 'public' ? 'approved' : 'pending';

        try {
            clubRepo.addMember(clubId, userId, status);
            return { status };
        } catch (error) {
            if (error.code === 'SQLITE_CONSTRAINT_PRIMARYKEY') {
                throw new Error('Already a member or request pending');
            }
            throw error;
        }
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
};

export default clubService;