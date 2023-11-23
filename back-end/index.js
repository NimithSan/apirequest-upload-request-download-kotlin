const http = require('http');
const app = require('./app');
const server = http.createServer(app);

const { API_PORT } = process.env;


server.listen(API_PORT,"192.168.12.194",()=>{
    console.log("Server running");
})

