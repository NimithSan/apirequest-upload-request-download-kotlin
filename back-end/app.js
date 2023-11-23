require('dotenv').config();
require("./config/database").connect();
const express = require('express');
const app = express();
const fileUpload = require('express-fileupload');
const auth = require('./middleware/auth')
app.use(express.json());
app.use(express.urlencoded({extended:true}))
app.use(fileUpload());


//API Router
//User
const {register, login} = require('./api/UserAPI')
app.post('/register', register);
app.post('/login', login);

//Note Folder
const { createFolder, updateFolder, deleteFolder, getFolder } = require('./api/FolderApi') 
app.post('/create_folder', auth, createFolder);
app.post('/update_folder', auth, updateFolder);
app.post('/delete_folder', auth, deleteFolder);
app.get('/get_folder', auth, getFolder);

//File
const { uploadFiles, deleteFile, getFile, getFiles} = require('./api/FileApi')
app.post('/upload_file', auth, uploadFiles);
app.post('/delete_file', auth, deleteFile);
app.get('/file/:id/:filename', auth, getFile);
app.get('/get_files', auth, getFiles);


app.get("/", (req,res)=>{
  res.status(200).send("User Note API");
});

module.exports = app;