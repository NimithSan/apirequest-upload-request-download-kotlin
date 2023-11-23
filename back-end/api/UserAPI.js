const User = require("../model/user");
const bcrypt = require('bcryptjs')
const jwt = require('jsonwebtoken')
module.exports = {
    register: async(req,res)=>{
        // Our register logic starts here
        try {
            const { first_name, last_name, email, password } = req.body;
            // Validate user input
            if (!(email && password && first_name && last_name)) {
              return res.status(400).send("All input is required");
            }
        
            const oldUser = await User.findOne({ email });
        
            if (oldUser) {
              return res.status(409).send({error: "User Already Exist. Please Login"});
            }
        
            //Encrypt user password
            encryptedPassword = await bcrypt.hash(password, 10)
        
            // Create user in our database
            const user = await User.create({
              first_name,
              last_name,
              email: email.toLowerCase(), // sanitize: convert email to lowercase
              password: encryptedPassword,
            });
        
            // Create token
            const token = jwt.sign(
              { user_id: user._id, email },
              process.env.TOKEN_KEY,
              {
                expiresIn: "2h",
              }
            );
            // save user token
            user.token = token;
        
            // return new user
            res.status(201).json({id: user._id, email: user.email, first_name: user.first_name, last_name: user.last_name, token: token});
          } catch (err) {
            console.log(err);
            res.status(500).send({error: err});
          }
    },
    login: async(req, res)=>{
        try {
            console.log("Login", res)
            // Get user input
            const { email, password } = req.body;
        
            // Validate user input
            if (!(email && password)) {
              res.status(400).send({error: "All input is required"});
            }
            // Validate if user exist in our database
            const user = await User.findOne({ email });
        
            if (user && (await bcrypt.compare(password, user.password))) {
              // Create token
              const token = jwt.sign(
                { user_id: user._id, email }, 
                process.env.TOKEN_KEY,
                {
                  expiresIn: "365d",
                }
              );
        
              // save user token
              user.token = token;
        
              // user
              return res.status(200).json({id: user._id, email: user.email, first_name: user.first_name, last_name: user.last_name, token: token});
            }
            res.status(400).send({error: "Invalid Credentials"});
        } catch (err) {
            console.log(err);
            res.status(500).send({error: err});
        }
    }
}