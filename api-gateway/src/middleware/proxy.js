const axios = require('axios');

const createProxyMiddleware = (baseURL) => {
  return async (req, res, next) => {
    try {
      const response = await axios({
        method: req.method,
        url: `${baseURL}${req.url}`,
        data: req.body,
        headers: {
          ...req.headers,
          host: new URL(baseURL).host,
        },
      });

      res.status(response.status).json(response.data);
    } catch (error) {
      if (error.response) {
        res.status(error.response.status).json(error.response.data);
      } else {
        res.status(500).json({ error: 'Internal Server Error' });
      }
    }
  };
};

module.exports = createProxyMiddleware; 