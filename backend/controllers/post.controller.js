import postService from "../services/post.service.js";

const postController = {
    async createPost(req, res, next) {
        try {
            const newPost = await postService.createPost(req.body);
            return res.created(newPost, "Post created successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async getFeed(req, res, next) {
        try {
            const { cursor, limit } = req.query;
            const result = await postService.getFeed(cursor, limit);
            return res.ok(result, "Feed retrieved successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async getFollowingFeed(req, res, next) {
        try {
            const userId = parseInt(req.params.userId);
            const { cursor, limit } = req.query;
            const result = await postService.getFollowingFeed(userId, cursor, limit);
            return res.ok(result, "Following feed retrieved successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async likePost(req, res, next) {
        try {
            const postId = parseInt(req.params.postId);
            const userId = parseInt(req.query.userId);
            const result = await postService.likePost(postId, userId);
            return res.created(result, "Post liked successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async unlikePost(req, res, next) {
        try {
            const postId = parseInt(req.params.postId);
            const userId = parseInt(req.query.userId);
            const result = await postService.unlikePost(postId, userId);
            return res.ok(result, "Post unliked successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async createComment(req, res, next) {
        try {
            const postId = parseInt(req.params.postId);
            const userId = parseInt(req.query.userId);
            const { content } = req.body;
            const comment = await postService.createComment(postId, userId, content);
            return res.created(comment, "Comment created successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async getComments(req, res, next) {
        try {
            const postId = parseInt(req.params.postId);
            const { cursor, limit } = req.query;
            const result = await postService.getComments(postId, cursor, limit);
            return res.ok(result, "Comments retrieved successfully.");
        } catch (error) {
            if (error.status === 409) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },

    async deleteComment(req, res, next) {
        try {
            const postId = parseInt(req.params.postId);
            const commentId = parseInt(req.params.commentId);
            const userId = parseInt(req.query.userId);
            const result = await postService.deleteComment(postId, commentId, userId);
            return res.ok(result, "Comment deleted successfully.");
        } catch (error) {
            if (error.status === 409 || error.status === 404) {
                console.error(error);
                return res.violate(null, error.message);
            }
            return next(error);
        }
    },
};

export default postController;
