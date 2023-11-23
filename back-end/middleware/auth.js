const jwt = require('jsonwebtoken');
const config = process.env;
const verifyToken = (req, res, next)=>{
    const token = req.body.token || req.query.token || req.headers['x-acess-token'] || req.headers['authorization'] || req.headers['token'];
    if(!token){
        return res.status(401).send({error: "Unauthorized"});
    }
    try {
        const decoded = jwt.verify(token, config.TOKEN_KEY);
        req.user = decoded;
    } catch (error) {
        return res.status(401).send({error: "Unauthorized"});
    }
    return next();
};

module.exports = verifyToken;