import OpenAIService from '../services/openai.service.js';

const aiController = {
  async chat(req, res, next) {
    try {
      const { prompt, history, location, currentWaypoints } = req.body;

      if (!prompt) {
        return res.violate(null, 'Prompt is required');
      }

      const aiResponse = await OpenAIService.generateRoute(prompt, history || [], location, currentWaypoints);

      return res.ok(aiResponse, 'Route plan generated successfully');
    } catch (error) {
      console.error('AI Chat Error:', error);
      if (error.name === 'ZodError') {
        return res.violate(null, 'AI returned invalid data format');
      }
      return next(error);
    }
  }
};

export default aiController;
