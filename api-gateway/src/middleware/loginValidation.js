const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

const validateLoginRequest = (req, res, next) => {
    const { login, senha } = req.body;

    if (!login || !senha) {
        return res.status(400).json({ 
            error: 'Missing required fields: login and senha are required' 
        });
    }

    if (!validateEmail(login)) {
        return res.status(400).json({ 
            error: 'Invalid email format' 
        });
    }

    next();
};

module.exports = {
    validateLoginRequest
}; 