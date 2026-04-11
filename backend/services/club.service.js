import clubRepo from '../repo/club.repo.js';
import { getImageUrlS3 } from '../utils/s3/s3.js';

const clubService = {
    async getClubs(userId, offset, limit) {
        let clubs = await clubRepo.findMyClubs(userId, offset, limit);
        let type = 'my clubs';
        if (clubs.length === 0) {
            clubs = await clubRepo.findDiscoverClubs(offset, limit);
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
                        isJoined: type === 'my clubs',
                    };
                }
                return {
                    ...itemWithoutS3Key,
                    isJoined: type === 'my clubs',
                };
            }),
        );

        return { clubs: result, type };
    },
};

export default clubService;