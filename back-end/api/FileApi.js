const FileModel = require("../model/file");
const path = require('path')
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');

module.exports = {
    uploadFiles: async(req, res)=>{
        try {
            const { folderId } = req.body;
            let file = req.files.file;
            let id = uuidv4()
            let dir = path.dirname(__dirname) + '/uploads/'+ id;
            let filePath = dir + "/" + file.name;
            await fs.mkdirSync(dir, { recursive: true });
            await file.mv(filePath)
            const fileModel = await FileModel.create({
                _id: id,
                folderId: folderId,
                name: file.name
            });
            // for (const file of req.files.file) {
            //     let id = uuidv4()
            //     let dir = path.dirname(__dirname) + '/uploads/'+ id;
            //     let filePath = dir + "/" + file.name
            //     await fs.mkdirSync(dir, { recursive: true });
            //     await file.mv(filePath)
            //     const fileModel = await FileModel.create({
            //         _id: id,
            //         folderId: folderId,
            //         name: file.name
            //     });
            //     files.push(fileModel)
            // }
            res.status(201).json({success: true,file: fileModel});
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    deleteFile: async(req,res)=>{
        try {
            const { _id } = req.body;
            if (!_id) {
                return res.status(400).send({error: "Id is required"});
            }
            const fileEntity = await FileModel.findOne({_id:_id});
            if(!fileEntity){
                return res.status(400).send({error: "File doesn't exist."});
            }
            let result = await FileModel.deleteOne({_id: _id})
            res.status(201).json({success: true});
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    getFile: async(req, res)=>{
        try {
            const { id,filename } = req.params;
            const fileEntity = await FileModel.findOne({_id:id});
            if(!fileEntity){
                return res.status(400).send({error: "File doesn't exist."});
            }
            let dir = path.dirname(__dirname) + `/uploads/${id}/${fileEntity.name}`
            res.download(dir)
        } catch (error) {
            res.status(500).send({error: error});
        }
    },
    getFiles: async(req, res)=>{
        try {
            const { before, after, perpage= 20, folderId } = req.query;
            let query = {}
            if(before){
                let beforeDate = new Date(before)
                query = { folderId: folderId , updatedAt: { $lt: beforeDate}}
            }else if(after){
                let afterDate = new Date(after)
                query = { folderId: folderId , updatedAt: { $gt: afterDate}}
            }else{
                query = { folderId: folderId }
            }
            let result = await FileModel.find(query).sort({updatedAt: -1}).limit(perpage)
            let page = {first: result[0]?.updatedAt,last: result[result.length -1]?.updatedAt, count: result.length}
            result = result.map((item)=>{ 
                return {
                        _id: item._id,
                        folderId: item.folderId,
                        name: item.name,
                        createdAt: item.createdAt, 
                        updatedAt: item.updatedAt
                    }
            });
            res.status(201).json({page: page,data: result});
        } catch (error) {
            res.status(500).send({error: error});
        }
    }
};